package IotSystem.IoTSystem.Service.Implement;


import IotSystem.IoTSystem.Model.Entities.Account;
import IotSystem.IoTSystem.Model.Entities.Roles;
import IotSystem.IoTSystem.Model.Request.ExcelImportRequest;
import IotSystem.IoTSystem.Model.Response.ExcelImportResponse;
import IotSystem.IoTSystem.Repository.AccountRepository;
import IotSystem.IoTSystem.Repository.RolesRepository;
import IotSystem.IoTSystem.Service.IExcelImportService;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


@Service
public class ExcelImportServiceImpl implements IExcelImportService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public ExcelImportResponse importAccounts(ExcelImportRequest request) {
        List<String> errors = new ArrayList<>();
        int successCount = 0;
        int totalRows = 0;

        try {
            // Decode base64 file content
            byte[] fileBytes = Base64.getDecoder().decode(request.getFileContent());
            ByteArrayInputStream inputStream = new ByteArrayInputStream(fileBytes);

            // Create workbook from Excel file
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            // Get role entity
            Roles role = rolesRepository.findByName(request.getRole())
                    .orElseThrow(() -> new RuntimeException("Role not found: " + request.getRole()));

            // Process each row
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header row

                totalRows++;

                try {
                    Account account = createAccountFromRow(row, role);
                    accountRepository.save(account);
                    successCount++;
                } catch (Exception e) {
                    errors.add("Row " + (row.getRowNum() + 1) + ": " + e.getMessage());
                }
            }

            workbook.close();
            inputStream.close();

        } catch (IOException e) {
            errors.add("Error reading Excel file: " + e.getMessage());
        } catch (Exception e) {
            errors.add("Unexpected error: " + e.getMessage());
        }

        return ExcelImportResponse.builder()
                .success(successCount > 0)
                .totalRows(totalRows)
                .successCount(successCount)
                .errorCount(errors.size())
                .errors(errors)
                .message(String.format("Processed %d rows: %d successful, %d errors",
                        totalRows, successCount, errors.size()))
                .build();
    }

    private Account createAccountFromRow(Row row, Roles role) {
        Account account = new Account();

        // Get cell values
        String fullName = getCellValueAsString(row.getCell(0)); // Column A
        String email = getCellValueAsString(row.getCell(1));     // Column B
        String phone = getCellValueAsString(row.getCell(2));     // Column C
        String studentCode = getCellValueAsString(row.getCell(3)); // Column D
        String password = getCellValueAsString(row.getCell(4));  // Column E

        // Validate required fields
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new RuntimeException("Full Name is required");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("Email is required");
        }

        // Set account properties
        account.setFullName(fullName.trim());
        account.setEmail(email.trim());
        account.setPhone(phone != null ? phone.trim() : "");
        account.setStudentCode(studentCode != null ? studentCode.trim() : "");
        account.setPasswordHash(passwordEncoder.encode(password != null ? password.trim() : "default123"));
        account.setRole(role);
        account.setIsActive(true);

        // Check if email already exists
        if (accountRepository.existsByEmail(email.trim())) {
            throw new RuntimeException("Email already exists: " + email);
        }

        return account;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }
}

