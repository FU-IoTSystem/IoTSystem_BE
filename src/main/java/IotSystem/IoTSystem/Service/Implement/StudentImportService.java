package IotSystem.IoTSystem.Service.Implement;

import IotSystem.IoTSystem.Model.Entities.*;
import IotSystem.IoTSystem.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class StudentImportService {

    @Autowired
    private ClassesRepository classesRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private ClassAssignemntRepository classAssignemntRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

    public int importStudentsFromSpreadsheetML(MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);

            Element table = (Element) doc.getElementsByTagNameNS("urn:schemas-microsoft-com:office:spreadsheet", "Table").item(0);
            if (table == null) return 0;

            NodeList rows = table.getElementsByTagNameNS("urn:schemas-microsoft-com:office:spreadsheet", "Row");
            if (rows.getLength() == 0) return 0;

            int processed = 0;

            // Resolve header indices
            int classIdx = -1, rollIdx = -1, emailIdx = -1, nameIdx = -1;
            int currentCol = 0;
            Element headerRow = (Element) rows.item(0);
            NodeList headerCells = headerRow.getElementsByTagNameNS("urn:schemas-microsoft-com:office:spreadsheet", "Cell");
            currentCol = 0;
            for (int c = 0; c < headerCells.getLength(); c++) {
                Element cell = (Element) headerCells.item(c);
                int index = currentCol;
                if (cell.hasAttributeNS("urn:schemas-microsoft-com:office:spreadsheet", "Index")) {
                    index = Integer.parseInt(cell.getAttributeNS("urn:schemas-microsoft-com:office:spreadsheet", "Index")) - 1;
                }
                String value = extractCellString(cell);
                if ("Class".equalsIgnoreCase(value)) classIdx = index;
                if ("RollNumber".equalsIgnoreCase(value)) rollIdx = index;
                if ("Email".equalsIgnoreCase(value)) emailIdx = index;
                if ("FullName".equalsIgnoreCase(value)) nameIdx = index;
                currentCol = index + 1;
            }

            // Iterate data rows
            for (int r = 1; r < rows.getLength(); r++) {
                Element row = (Element) rows.item(r);
                String classCode = getCellValueByIndex(row, classIdx);
                String rollNumber = getCellValueByIndex(row, rollIdx);
                String email = getCellValueByIndex(row, emailIdx);
                String fullName = getCellValueByIndex(row, nameIdx);

                if (isBlank(classCode) || isBlank(rollNumber)) {
                    continue;
                }

                // Class: ensure exists
                Classes clazz = classesRepository.findByClassCode(classCode).orElseGet(() -> {
                    Classes c = new Classes();
                    c.setClassCode(classCode);
                    c.setSemester(null);
                    c.setStatus(true);
                    
                    // Set a random lecturer as the account for this class
                    Account lecturer = getRandomLecturer();
                    if (lecturer == null) {
                        throw new RuntimeException("No active lecturers found. Cannot create class without a lecturer.");
                    }
                    c.setAccount(lecturer);
                    
                    return classesRepository.save(c);
                });

                // Student: if existed by studentCode => skip row entirely
                if (accountRepository.existsByStudentCode(rollNumber)) {
                    continue;
                }

                // Check if account exists by email - if so, update it instead of creating new
                Optional<Account> existingAccount = accountRepository.findByEmail(email);
                Account account;
                
                if (existingAccount.isPresent()) {
                    // Update existing account
                    account = existingAccount.get();
                    account.setFullName(fullName);
                    account.setStudentCode(rollNumber);
                    // Don't change password or role if account already exists
                } else {
                    // Create new account with STUDENT role
                    Roles studentRole = rolesRepository.findByName("STUDENT")
                            .orElseThrow(() -> new RuntimeException("Default role STUDENT not found"));

                    account = new Account();
                    account.setId(null);
                    account.setFullName(fullName);
                    account.setEmail(email);
                    account.setStudentCode(rollNumber);
                    account.setIsActive(true);
                    account.setRole(studentRole);
                    // Default password for testing: "123456"
                    account.setPasswordHash(passwordEncoder.encode("123456"));
                }

                // Create wallet for student if it doesn't exist
                if (account.getWallet() == null) {
                    Wallet wallet = new Wallet();
                    wallet.setBalance(BigDecimal.ZERO);
                    wallet.setCurrency("VND");
                    wallet.setActive(true);
                    wallet.setAccount(account);
                    account.setWallet(wallet);
                    account = accountRepository.save(account);
                    walletRepository.save(wallet);
                } else {
                    account = accountRepository.save(account);
                }

                // Enroll: create ClassAssignment link if it doesn't exist
                Optional<ClassAssignment> existingAssignment = classAssignemntRepository.findByClazzAndAccount(clazz, account);
                if (existingAssignment.isEmpty()) {
                    ClassAssignment assignment = new ClassAssignment();
                    assignment.setClazz(clazz);
                    assignment.setAccount(account);
                    assignment.setRole(null);
                    classAssignemntRepository.save(assignment);
                }

                processed++;
            }

            return processed;
        } catch (Exception ex) {
            throw new RuntimeException("Failed to import students: " + ex.getMessage(), ex);
        }
    }

    private String getCellValueByIndex(Element row, int targetIndex) {
        if (targetIndex < 0) return null;
        NodeList cells = row.getElementsByTagNameNS("urn:schemas-microsoft-com:office:spreadsheet", "Cell");
        int logicalIndex = 0;
        for (int i = 0; i < cells.getLength(); i++) {
            Element cell = (Element) cells.item(i);
            int index = logicalIndex;
            if (cell.hasAttributeNS("urn:schemas-microsoft-com:office:spreadsheet", "Index")) {
                index = Integer.parseInt(cell.getAttributeNS("urn:schemas-microsoft-com:office:spreadsheet", "Index")) - 1;
            }
            if (index == targetIndex) {
                return extractCellString(cell);
            }
            logicalIndex = index + 1;
        }
        return null;
    }

    private String extractCellString(Element cell) {
        NodeList dataNodes = cell.getElementsByTagNameNS("urn:schemas-microsoft-com:office:spreadsheet", "Data");
        if (dataNodes.getLength() == 0) return null;
        Node data = dataNodes.item(0);
        String text = data.getTextContent();
        return isBlank(text) ? null : text.trim();
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}


