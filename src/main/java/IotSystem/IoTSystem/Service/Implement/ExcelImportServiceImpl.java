package IotSystem.IoTSystem.Service.Implement;

import IotSystem.IoTSystem.Model.Entities.Account;
import IotSystem.IoTSystem.Model.Entities.ClassAssignment;
import IotSystem.IoTSystem.Model.Entities.Classes;
import IotSystem.IoTSystem.Model.Entities.Roles;
import IotSystem.IoTSystem.Model.Entities.Wallet;
import IotSystem.IoTSystem.Model.Request.AccountRequest;
import IotSystem.IoTSystem.Model.Request.ExcelImportRequest;
import IotSystem.IoTSystem.Model.Response.ExcelImportResponse;
import IotSystem.IoTSystem.Repository.AccountRepository;
import IotSystem.IoTSystem.Repository.ClassAssignemntRepository;
import IotSystem.IoTSystem.Repository.ClassesRepository;
import IotSystem.IoTSystem.Repository.RolesRepository;
import IotSystem.IoTSystem.Repository.WalletRepository;
import IotSystem.IoTSystem.Service.IExcelImportService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;

@Service
public class ExcelImportServiceImpl implements IExcelImportService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ClassesRepository classesRepository;

    @Autowired
    private ClassAssignemntRepository classAssignemntRepository;

    @Autowired
    private WalletRepository walletRepository;

    private final Random random = new Random();

    /**
     * Get a random lecturer account to assign to new classes
     * @return A lecturer account, or null if no lecturers exist
     */
    private Account getRandomLecturer() {
        List<Account> lecturers = accountRepository.findAllLecturers();
        if (lecturers.isEmpty()) {
            return null;
        }
        return lecturers.get(random.nextInt(lecturers.size()));
    }

    @Override
    public ExcelImportResponse importAccounts(ExcelImportRequest request) {
        List<String> errors = new ArrayList<>();
        int successCount = 0;
        int totalRows = 0;

        try {
            // Decode base64 file content
            byte[] fileBytes = Base64.getDecoder().decode(request.getFileContent());

            // Validate file is not empty
            if (fileBytes == null || fileBytes.length == 0) {
                throw new IOException("File content is empty");
            }

            // Determine file type and create workbook
            Workbook workbook = null;
            String fileName = request.getFileName() != null ? request.getFileName().toLowerCase() : "";

            // Validate file extension
            if (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls")) {
                throw new IOException("Invalid file format. Please upload a .xls or .xlsx file.");
            }

            // Try to open the file first - this is the most reliable way to validate
            try {
                // Try to detect file type from extension first
                if (fileName.endsWith(".xlsx")) {
                    try {
                        workbook = new XSSFWorkbook(new ByteArrayInputStream(fileBytes));
                    } catch (Exception e) {
                        // If XSSF fails, try WorkbookFactory
                        workbook = WorkbookFactory.create(new ByteArrayInputStream(fileBytes));
                    }
                } else if (fileName.endsWith(".xls")) {
                    try {
                        workbook = new HSSFWorkbook(new ByteArrayInputStream(fileBytes));
                    } catch (Exception e) {
                        // If HSSF fails, try WorkbookFactory
                        workbook = WorkbookFactory.create(new ByteArrayInputStream(fileBytes));
                    }
                } else {
                    // Try WorkbookFactory as fallback (auto-detect)
                    workbook = WorkbookFactory.create(new ByteArrayInputStream(fileBytes));
                }
            } catch (Exception e) {
                // If all attempts fail, provide detailed error message
                String errorMsg = "Cannot read Excel file. ";

                // Check file signature for better error message
                if (fileBytes.length >= 2) {
                    if (fileBytes[0] == 0x50 && fileBytes[1] == 0x4B) {
                        errorMsg += "File appears to be a ZIP file (XLSX format), but cannot be opened. ";
                    } else if (fileBytes[0] == (byte)0xD0 && fileBytes[1] == (byte)0xCF) {
                        errorMsg += "File appears to be an OLE2 file (XLS format), but cannot be opened. ";
                    } else {
                        errorMsg += "File does not appear to be a valid Excel file format. ";
                    }
                }

                errorMsg += "Please ensure the file is a valid .xls or .xlsx file and is not corrupted. ";
                errorMsg += "Original error: " + e.getMessage();

                throw new IOException(errorMsg);
            }

            if (workbook == null) {
                throw new IOException("Failed to create workbook from file");
            }

            // Get sheet by name if specified, otherwise use first sheet
            Sheet sheet;
            if (request.getSheetName() != null && !request.getSheetName().trim().isEmpty()) {
                sheet = workbook.getSheet(request.getSheetName().trim());
                if (sheet == null) {
                    // Get available sheet names before closing workbook
                    final Workbook finalWorkbook = workbook;
                    List<String> availableSheets = java.util.stream.IntStream.range(0, finalWorkbook.getNumberOfSheets())
                            .mapToObj(i -> finalWorkbook.getSheetName(i))
                            .collect(java.util.stream.Collectors.toList());
                    workbook.close();
                    throw new IOException("Sheet '" + request.getSheetName() + "' not found in Excel file. Available sheets: " +
                            String.join(", ", availableSheets));
                }
            } else {
                sheet = workbook.getSheetAt(0); // Get first sheet if no sheet name specified
            }

            if (sheet == null) {
                workbook.close();
                throw new IOException("Excel file does not contain any sheets");
            }

            // Get role entity
            Roles role = rolesRepository.findByName(request.getRole())
                    .orElseThrow(() -> new RuntimeException("Role not found: " + request.getRole()));

            // Process each row
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header row

                totalRows++;

                try {
                    Account account = createAccountFromRow(row, role);

                    // Create wallet for STUDENT or LECTURER accounts
                    if ("STUDENT".equals(role.getName()) || "LECTURER".equals(role.getName())) {
                        Wallet wallet = new Wallet();
                        wallet.setBalance(java.math.BigDecimal.ZERO);
                        wallet.setCurrency("VND");
                        wallet.setActive(true);
                        wallet.setAccount(account);
                        account.setWallet(wallet);
                    }

                    Account savedAccount = accountRepository.save(account);

                    // Handle class assignment based on role
                    if ("STUDENT".equals(role.getName())) {
                        // For students: Column E is StudentClass
                        String studentClass = getCellValueAsString(row.getCell(4)); // Column E
                        if (studentClass != null && !studentClass.trim().isEmpty()) {
                            assignStudentToClass(savedAccount, studentClass.trim(), row.getRowNum() + 1, errors);
                        }
                    } else if ("LECTURER".equals(role.getName())) {
                        // For lecturers: Column C is class_code, Column D is Semester
                        String classCode = getCellValueAsString(row.getCell(3)); // Column D: class_code
                        String semester = getCellValueAsString(row.getCell(4)); // Column E: Semester
                        if (classCode != null && !classCode.trim().isEmpty()) {
                            createOrAssignClassForLecturer(savedAccount, classCode.trim(),
                                    semester != null ? semester.trim() : null, row.getRowNum() + 1, errors);
                        }
                    }

                    successCount++;
                } catch (Exception e) {
                    errors.add("Row " + (row.getRowNum() + 1) + ": " + e.getMessage());
                }
            }

            workbook.close();

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
        String email;
        String fullName;
        String phone;
        String studentCode = null;

        if ("STUDENT".equals(role.getName())) {
            // For students: Excel columns: studentCode, StudentName, email, phone, StudentClass
            studentCode = getCellValueAsString(row.getCell(0)); // Column A: studentCode
            fullName = getCellValueAsString(row.getCell(1));    // Column B: StudentName
            email = getCellValueAsString(row.getCell(2));       // Column C: email
            phone = getCellValueAsString(row.getCell(3));       // Column D: phone
            // Column E is StudentClass - handled separately after account creation
        } else if ("LECTURER".equals(role.getName())) {
            // For lecturers: Excel columns: email, fullname, phone, class_code, Semester
            email = getCellValueAsString(row.getCell(0));       // Column A: email
            fullName = getCellValueAsString(row.getCell(1));    // Column B: fullname
            phone = getCellValueAsString(row.getCell(2));       // Column C: phone
            // Column D is class_code, Column E is Semester - handled separately after account creation
        } else {
            throw new RuntimeException("Unsupported role: " + role.getName());
        }

        // Validate required fields
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new RuntimeException("Full Name is required");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("Email is required");
        }

        // Set account properties
        account.setEmail(email.trim());
        account.setFullName(fullName.trim());
        account.setPhone(phone != null ? phone.trim() : "");
        if (studentCode != null) {
            account.setStudentCode(studentCode.trim());
        } else {
            account.setStudentCode("");
        }
        // Password default is "1"
        account.setPasswordHash(passwordEncoder.encode("1"));
        account.setRole(role);
        account.setIsActive(true);

        // Check if email already exists
        if (accountRepository.existsByEmail(email.trim())) {
            throw new RuntimeException("Email already exists: " + email);
        }

        return account;
    }

    private void createOrAssignClassForLecturer(Account lecturer, String classCode, String semester, int rowNumber, List<String> errors) {
        try {
            // Find class by class code, or create it if it doesn't exist
            Classes clazz = classesRepository.findByClassCode(classCode).orElseGet(() -> {
                // Create new class if it doesn't exist
                Classes newClass = new Classes();
                newClass.setClassCode(classCode);
                newClass.setSemester(semester); // Use provided semester or null
                newClass.setStatus(true); // Default to active
                newClass.setAccount(lecturer); // Set lecturer as teacher for this class

                return classesRepository.save(newClass);
            });

            // Check if assignment already exists for lecturer
            java.util.Optional<ClassAssignment> existingAssignment =
                    classAssignemntRepository.findByClazzAndAccount(clazz, lecturer);

            if (existingAssignment.isEmpty()) {
                // Create new class assignment for lecturer
                ClassAssignment assignment = new ClassAssignment();
                assignment.setClazz(clazz);
                assignment.setAccount(lecturer);
                assignment.setRole(lecturer.getRole());
                classAssignemntRepository.save(assignment);
            }
        } catch (Exception e) {
            // Add warning but don't fail the entire row
            errors.add("Row " + rowNumber + ": Could not create/assign class '" + classCode + "' for lecturer: " + e.getMessage());
        }
    }

    private void assignStudentToClass(Account account, String classCode, int rowNumber, List<String> errors) {
        try {
            // Find class by class code, or create it if it doesn't exist
            Classes clazz = classesRepository.findByClassCode(classCode).orElseGet(() -> {
                // Create new class if it doesn't exist
                Classes newClass = new Classes();
                newClass.setClassCode(classCode);
                newClass.setSemester(null); // Default to null if not specified
                newClass.setStatus(true); // Default to active

                // Set a random lecturer as the teacher for this class
                Account randomLecturer = getRandomLecturer();
                if (randomLecturer == null) {
                    throw new RuntimeException("No active lecturers found. Cannot create class without a lecturer.");
                }
                newClass.setAccount(randomLecturer);

                Classes savedClass = classesRepository.save(newClass);

                // Create ClassAssignment for lecturer when creating new class
                java.util.Optional<ClassAssignment> existingLecturerAssignment =
                        classAssignemntRepository.findByClazzAndAccount(savedClass, randomLecturer);

                if (existingLecturerAssignment.isEmpty()) {
                    ClassAssignment lecturerAssignment = new ClassAssignment();
                    lecturerAssignment.setClazz(savedClass);
                    lecturerAssignment.setAccount(randomLecturer);
                    lecturerAssignment.setRole(randomLecturer.getRole());
                    classAssignemntRepository.save(lecturerAssignment);
                }

                return savedClass;
            });

            // Check if assignment already exists for student
            java.util.Optional<ClassAssignment> existingAssignment =
                    classAssignemntRepository.findByClazzAndAccount(clazz, account);

            if (existingAssignment.isEmpty()) {
                // Create new class assignment for student
                ClassAssignment assignment = new ClassAssignment();
                assignment.setClazz(clazz);
                assignment.setAccount(account);
                assignment.setRole(account.getRole());
                classAssignemntRepository.save(assignment);
            }
        } catch (Exception e) {
            // Add warning but don't fail the entire row
            errors.add("Row " + rowNumber + ": Could not assign student to class '" + classCode + "': " + e.getMessage());
        }
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
