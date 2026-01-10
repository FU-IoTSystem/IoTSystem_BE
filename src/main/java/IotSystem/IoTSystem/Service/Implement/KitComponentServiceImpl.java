package IotSystem.IoTSystem.Service.Implement;

import IotSystem.IoTSystem.Exception.ResourceNotFoundException;
import IotSystem.IoTSystem.Model.Entities.Kit_Component;
import IotSystem.IoTSystem.Model.Entities.Kits;
import IotSystem.IoTSystem.Model.Entities.Enum.Status.KitComponentType;
import IotSystem.IoTSystem.Model.Mappers.KitComponentMapper;
import IotSystem.IoTSystem.Model.Mappers.KitResponseMapper;
import IotSystem.IoTSystem.Model.Request.KitComponentRequest;
import IotSystem.IoTSystem.Model.Response.ExcelImportResponse;
import IotSystem.IoTSystem.Model.Response.KitComponentResponse;
import IotSystem.IoTSystem.Model.Response.KitResponse;
import IotSystem.IoTSystem.Repository.KitComponentRepository;
import IotSystem.IoTSystem.Repository.KitsRepository;
import IotSystem.IoTSystem.Service.IKitComponentService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KitComponentServiceImpl implements IKitComponentService {

    @Autowired
    KitComponentRepository kitComponentRepository;
    @Autowired
    KitsRepository kitsRepository;


    @Override
    public KitComponentResponse createKitComponent(KitComponentRequest kitComponentRequest) {
        // Support both kit-scoped components and global components (kitId can be null)
        Kits kit = null;
        if (kitComponentRequest.getKitId() != null) {
            Optional<Kits> result = kitsRepository.findById(kitComponentRequest.getKitId());
            if (result.isEmpty()) {
                throw new ResourceNotFoundException("KitId not found: " + kitComponentRequest.getKitId());
            }
            kit = result.get();
        }

        Kit_Component kitComponent = KitComponentMapper.toEntity(kitComponentRequest, kit);
        kitComponentRepository.save(kitComponent);

        // If component is attached to a kit, recalculate kit amount; skip for global components
        if (kit != null) {
            List<Kit_Component> allComponents = kitComponentRepository.findByKitId(kit.getId());
            float kitAmount = (float) allComponents.stream()
                    .mapToDouble(c -> c.getPricePerCom() != null ? c.getPricePerCom() : 0.0)
                    .sum();
            kit.setAmount(kitAmount);
            kitsRepository.save(kit);
        }

        return KitComponentMapper.toResponse(kitComponent);
    }

    @Override
    public Object getKitComponentId(Long id) {
        return null;
    }

    @Autowired
    private IotSystem.IoTSystem.Repository.RequestKitComponentRepository requestKitComponentRepository;

    @Autowired
    private IotSystem.IoTSystem.Repository.BorrowingRequestRepository borrowingRequestRepository;

    @Override
    public List<KitComponentResponse> getAllKitComponents() {
        return kitComponentRepository.findAll()
                .stream()
                .filter(c -> c.getStatus() == null || !c.getStatus().equals("DELETED"))
                .map(KitComponentMapper::toResponse)
                .toList();
    }

    @Override
    public KitResponse deleteKitComponent(UUID id) {
        Kit_Component entity = kitComponentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Kit Component ID not found: " + id));
        Kits kit = entity.getKit();

        // Check if component is in use
        List<IotSystem.IoTSystem.Model.Entities.RequestKitComponent> usageList = requestKitComponentRepository.findByKitComponentsId(id);

        if (!usageList.isEmpty()) {
            // Check for any active requests (PENDING, APPROVED)
            boolean hasActiveRequests = usageList.stream().anyMatch(usage -> {
                IotSystem.IoTSystem.Model.Entities.BorrowingRequest req = borrowingRequestRepository.findById(usage.getRequestId()).orElse(null);
                if (req != null) {
                    String status = req.getStatus();
                    return "PENDING".equalsIgnoreCase(status) || "APPROVED".equalsIgnoreCase(status);
                }
                return false;
            });

            if (hasActiveRequests) {
                throw new RuntimeException("Cannot delete component that is currently in use or requested. Please resolve related requests first.");
            }

            // Soft delete: Update status to DELETED
            entity.setStatus("DELETED");
            entity.setQuantityAvailable(0); // Optional: ensure no further borrowing
            kitComponentRepository.save(entity);

            // Return response (similar to successful delete but entity remains)
            if (kit != null) {
                // Recalculate kit amount (excluding deleted components if desired, or keep them?
                // Usually soft deleted items should probably not contribute to price if they are 'gone')
                List<Kit_Component> allComponents = kitComponentRepository.findByKitId(kit.getId());
                float kitAmount = (float) allComponents.stream()
                        .filter(c -> !"DELETED".equals(c.getStatus()))
                        .mapToDouble(c -> c.getPricePerCom() != null ? c.getPricePerCom() : 0.0)
                        .sum();
                kit.setAmount(kitAmount);
                kitsRepository.save(kit);

                return KitResponseMapper.toResponse(kit, kit.getComponents());
            } else {
                return KitResponse.builder().build();
            }
        }

        // If no usage, hard delete
        kitComponentRepository.delete(entity);

        // For kit-scoped components, recalculate kit amount; for global components (kit == null), just return empty response
        if (kit != null) {
            List<Kit_Component> allComponents = kitComponentRepository.findByKitId(kit.getId());
            float kitAmount = (float) allComponents.stream()
                    // Filter out deleted ones just in case mixed state
                    .filter(c -> !"DELETED".equals(c.getStatus()))
                    .mapToDouble(c -> c.getPricePerCom() != null ? c.getPricePerCom() : 0.0)
                    .sum();
            kit.setAmount(kitAmount);
            kitsRepository.save(kit);

            return KitResponseMapper.toResponse(kit, kit.getComponents());
        } else {
            // Global component delete: no kit to recalculate
            return KitResponse.builder().build();
        }
    }

    @Override
    public KitComponentResponse updateKitComponent(UUID id, KitComponentRequest kitComponentRequest) {
        Kit_Component component = kitComponentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Kit Component ID not found: " + id));
        Kits existingKit = component.getKit();

        KitComponentMapper.updateEntity(kitComponentRequest, component, existingKit);
        Kit_Component updatedComponent = kitComponentRepository.save(component);

        // Recalculate kit amount only when the component belongs to a kit
        Kits kit = updatedComponent.getKit();
        if (kit != null) {
            List<Kit_Component> allComponents = kitComponentRepository.findByKitId(kit.getId());
            float kitAmount = (float) allComponents.stream()
                    .mapToDouble(c -> c.getPricePerCom() != null ? c.getPricePerCom() : 0.0)
                    .sum();
            kit.setAmount(kitAmount);
            kitsRepository.save(kit);
        }

        return KitComponentMapper.toResponse(updatedComponent);
    }

    @Override
    public ExcelImportResponse importComponentsFromExcel(UUID kitId, String fileContent, String fileName) {
        return importComponentsFromExcel(kitId, fileContent, fileName, null);
    }

    public ExcelImportResponse importComponentsFromExcel(UUID kitId, String fileContent, String fileName, String sheetName) {
        List<String> errors = new ArrayList<>();
        int successCount = 0;
        int totalRows = 0;

        try {
            // Decode base64 file content
            byte[] fileBytes = Base64.getDecoder().decode(fileContent);

            // Validate file is not empty
            if (fileBytes == null || fileBytes.length == 0) {
                throw new IOException("File content is empty");
            }

            // Validate file extension
            if (fileName == null || (!fileName.toLowerCase().endsWith(".xlsx") && !fileName.toLowerCase().endsWith(".xls"))) {
                throw new IOException("Invalid file format. Please upload a .xls or .xlsx file.");
            }

            // Determine file type and create workbook
            Workbook workbook;
            try {
                if (fileName.toLowerCase().endsWith(".xlsx")) {
                    workbook = new XSSFWorkbook(new ByteArrayInputStream(fileBytes));
                } else {
                    workbook = new HSSFWorkbook(new ByteArrayInputStream(fileBytes));
                }
            } catch (Exception e) {
                throw new IOException("Failed to read Excel file. Please ensure the file is a valid Excel format: " + e.getMessage());
            }

            // Get sheet by name if specified, otherwise use first sheet
            Sheet sheet;
            if (sheetName != null && !sheetName.trim().isEmpty()) {
                sheet = workbook.getSheet(sheetName.trim());
                if (sheet == null) {
                    workbook.close();
                    return ExcelImportResponse.builder()
                            .success(false)
                            .message("Sheet '" + sheetName + "' not found in Excel file. Available sheets: " +
                                    String.join(", ", java.util.stream.IntStream.range(0, workbook.getNumberOfSheets())
                                            .mapToObj(i -> workbook.getSheetName(i))
                                            .collect(java.util.stream.Collectors.toList())))
                            .build();
                }
            } else {
                sheet = workbook.getSheetAt(0); // Get first sheet if no sheet name specified
            }

            if (sheet == null) {
                workbook.close();
                throw new IOException("Excel file does not contain any sheets");
            }

            // Validate file format by checking header row
            if (!validateComponentFileFormat(sheet)) {
                workbook.close();
                throw new IOException("Invalid file format. The file does not appear to be a component import file. " +
                        "Please check that the header row contains expected columns (name, quantity, etc.).");
            }

            // If kitId is provided, validate kit and import as kit-scoped components
            Kits kit = null;
            if (kitId != null) {
                Optional<Kits> kitOptional = kitsRepository.findById(kitId);
                if (kitOptional.isEmpty()) {
                    workbook.close();
                    return ExcelImportResponse.builder()
                            .success(false)
                            .message("Kit not found: " + kitId)
                            .build();
                }
                kit = kitOptional.get();
            }

            // Process each row (skip header row)
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header row

                // Check if row is empty (all cells are empty or null)
                boolean isEmptyRow = true;
                for (int i = 0; i < row.getLastCellNum(); i++) {
                    String cellValue = getCellValueAsString(row.getCell(i));
                    if (cellValue != null && !cellValue.trim().isEmpty()) {
                        isEmptyRow = false;
                        break;
                    }
                }

                if (isEmptyRow) {
                    continue; // Skip empty rows
                }

                totalRows++;

                try {
                    // Excel columns (by index):
                    // Column A: name (required)
                    // Column B: type (optional, Box/Set/Unit)
                    // Column C: quantity (required)
                    // Column D: pricePerComp (optional, default 0.0)
                    // Column E: description (optional)
                    // Column F: image URL (optional)
                    // Column G: link (optional)

                    String componentName = getCellValueAsString(row.getCell(0)); // Column A: name
                    String typeStr       = getCellValueAsString(row.getCell(1)); // Column B: type
                    String quantityStr   = getCellValueAsString(row.getCell(2)); // Column C: quantity
                    String pricePerCompStr = getCellValueAsString(row.getCell(3)); // Column D: pricePerComp
                    String description   = getCellValueAsString(row.getCell(4)); // Column E: description
                    String imageUrl      = getCellValueAsString(row.getCell(5)); // Column F: image URL
                    String link          = getCellValueAsString(row.getCell(6)); // Column G: link

                    // Validate required fields
                    if (componentName == null || componentName.trim().isEmpty()) {
                        throw new RuntimeException("Component name is required");
                    }

                    Integer quantity = null;
                    if (quantityStr != null && !quantityStr.trim().isEmpty()) {
                        try {
                            quantity = Integer.parseInt(quantityStr.trim());
                            if (quantity <= 0) {
                                throw new RuntimeException("Quantity must be greater than 0");
                            }
                        } catch (NumberFormatException e) {
                            throw new RuntimeException("Invalid quantity format: " + quantityStr);
                        }
                    } else {
                        throw new RuntimeException("Quantity is required");
                    }

                    // Parse pricePerComp (optional)
                    Double pricePerComp = 0.0; // Default price
                    if (pricePerCompStr != null && !pricePerCompStr.trim().isEmpty()) {
                        try {
                            pricePerComp = Double.parseDouble(pricePerCompStr.trim());
                            if (pricePerComp < 0) {
                                throw new RuntimeException("Price per component cannot be negative");
                            }
                        } catch (NumberFormatException e) {
                            throw new RuntimeException("Invalid priceperComp format: " + pricePerCompStr);
                        }
                    }

                    // Validate URL format for image_url (optional)
                    if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                        imageUrl = imageUrl.trim();
                        if (!isValidUrl(imageUrl)) {
                            throw new RuntimeException("Invalid image_url format. Must be a valid URL: " + imageUrl);
                        }
                    }

                    // Validate URL format for link (optional)
                    if (link != null && !link.trim().isEmpty()) {
                        link = link.trim();
                        if (!isValidUrl(link)) {
                            throw new RuntimeException("Invalid link format. Must be a valid URL: " + link);
                        }
                    }

                    // Determine component type from typeStr (optional, default UNIT)
                    KitComponentType componentType = KitComponentType.UNIT;
                    if (typeStr != null && !typeStr.trim().isEmpty()) {
                        String normalizedType = typeStr.trim().toLowerCase();
                        // Hỗ trợ cả tiếng Anh và dạng viết thường/hoa
                        if (normalizedType.equals("box") || normalizedType.equals("hộp")) {
                            // Map Box type
                            componentType = KitComponentType.BOX;
                        } else if (normalizedType.equals("set") || normalizedType.equals("bộ")) {
                            componentType = KitComponentType.SET;
                        } else if (normalizedType.equals("unit") || normalizedType.equals("đơn") || normalizedType.equals("single")) {
                            componentType = KitComponentType.UNIT;
                        } else {
                            throw new RuntimeException("Invalid component type: " + typeStr + ". Expected: Box, Set, Unit");
                        }
                    }

                    // Create component
                    Kit_Component component = new Kit_Component();
                    component.setComponentName(componentName.trim());
                    component.setComponentType(componentType);
                    component.setQuantityTotal(quantity);
                    component.setQuantityAvailable(quantity);
                    component.setDescription(description != null && !description.trim().isEmpty() ? description.trim() : null);
                    component.setLink(link != null ? link.trim() : null);
                    component.setImageUrl(imageUrl != null && !imageUrl.trim().isEmpty() ? imageUrl.trim() : null);
                    component.setStatus("AVAILABLE");
                    component.setPricePerCom(pricePerComp);
                    // If kit is not null, associate component with kit, otherwise keep it global (kit = null)
                    if (kit != null) {
                        component.setKit(kit);
                    } else {
                        component.setKit(null);
                    }

                    kitComponentRepository.save(component);
                    successCount++;
                } catch (Exception e) {
                    errors.add("Row " + (row.getRowNum() + 1) + ": " + e.getMessage());
                }
            }

            workbook.close();

            // If kit is specified, recalculate kit amount based on its components
            if (kit != null) {
                List<Kit_Component> allComponents = kitComponentRepository.findByKitId(kit.getId());
                float kitAmount = (float) allComponents.stream()
                        .mapToDouble(c -> c.getPricePerCom() != null ? c.getPricePerCom() : 0.0)
                        .sum();
                kit.setAmount(kitAmount);
                kitsRepository.save(kit);
            }

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

    /**
     * Validate file format by checking header row
     * @param sheet Excel sheet to validate
     * @return true if header row matches expected format, false otherwise
     */
    private boolean validateComponentFileFormat(Sheet sheet) {
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            return false;
        }

        // Check if header contains expected keywords (case insensitive)
        String colA = getCellValueAsString(headerRow.getCell(0)); // Column A: name
        String colB = getCellValueAsString(headerRow.getCell(1)); // Column B: type
        String colC = getCellValueAsString(headerRow.getCell(2)); // Column C: quantity
        String colD = getCellValueAsString(headerRow.getCell(3)); // Column D: pricePerComp
        String colE = getCellValueAsString(headerRow.getCell(4)); // Column E: description
        String colF = getCellValueAsString(headerRow.getCell(5)); // Column F: image URL
        String colG = getCellValueAsString(headerRow.getCell(6)); // Column G: link

        // At minimum, name and quantity columns should be present
        boolean hasName = colA != null && (colA.toLowerCase().contains("name") ||
                colA.toLowerCase().contains("tên"));
        boolean hasQuantity = colC != null && (colC.toLowerCase().contains("quantity") ||
                colC.toLowerCase().contains("số lượng") ||
                colC.toLowerCase().contains("qty"));
        // Optional but recommended: type, price, description, image, link
        // Không bắt buộc để tránh làm hỏng file cũ, chỉ kiểm nhẹ nếu có
        return hasName && hasQuantity;
    }

    /**
     * Validate URL format using simple pattern
     * @param url URL string to validate
     * @return true if URL format is valid, false otherwise
     */
    private boolean isValidUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }
        // Simple URL validation pattern
        String urlPattern = "^(https?|ftp)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        return url.trim().matches(urlPattern);
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
                    // Return as integer if it's a whole number, otherwise as double
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == (long) numericValue) {
                        return String.valueOf((long) numericValue);
                    } else {
                        return String.valueOf(numericValue);
                    }
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
