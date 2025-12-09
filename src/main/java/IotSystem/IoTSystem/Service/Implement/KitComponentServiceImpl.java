package IotSystem.IoTSystem.Service.Implement;

import IotSystem.IoTSystem.Exception.ResourceNotFoundException;
import IotSystem.IoTSystem.Model.Entities.Kit_Component;
import IotSystem.IoTSystem.Model.Entities.Kits;
import IotSystem.IoTSystem.Model.Entities.Enum.Status.KitComponentType;
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
        Optional<Kits> result = kitsRepository.findById(kitComponentRequest.getKitId());
        if (result.isPresent()) {
            Kits kit = result.get();
            Kit_Component kitComponent = KitComponentMapper.toEntity(kitComponentRequest, kit);
            kitComponentRepository.save(kitComponent);

            // Recalculate kit amount after adding component
            List<Kit_Component> allComponents = kitComponentRepository.findByKitId(kit.getId());
            float kitAmount = (float) allComponents.stream()
                    .mapToDouble(c -> c.getPricePerCom() != null ? c.getPricePerCom() : 0.0)
                    .sum();
            kit.setAmount(kitAmount);
            kitsRepository.save(kit);

            return KitComponentMapper.toResponse(kitComponent);
        } else {
            throw new RuntimeException("KitId Not Found");
        }
    }

    @Override
    public Object getKitComponentId(Long id) {
        return null;
    }

    @Override
    public List<KitComponentResponse> getAllKitComponents() {
        return List.of();
    }

    @Override
    public KitResponse deleteKitComponent(UUID id) {
        Kit_Component entity = kitComponentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Kit Component ID not found: " + id));
        Kits kit = entity.getKit();
        kitComponentRepository.delete(entity);

        // Recalculate kit amount after deleting component
        List<Kit_Component> allComponents = kitComponentRepository.findByKitId(kit.getId());
        float kitAmount = (float) allComponents.stream()
                .mapToDouble(c -> c.getPricePerCom() != null ? c.getPricePerCom() : 0.0)
                .sum();
        kit.setAmount(kitAmount);
        kitsRepository.save(kit);

        return KitResponseMapper.toResponse(kit, kit.getComponents());
    }

    @Override
    public KitComponentResponse updateKitComponent(UUID id, KitComponentRequest kitComponentRequest) {
        Kit_Component component = kitComponentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Kit Component ID not found: " + id));

        KitComponentMapper.updateEntity(kitComponentRequest, component, component.getKit());
        Kit_Component updatedComponent = kitComponentRepository.save(component);

        // Recalculate kit amount after updating component
        Kits kit = updatedComponent.getKit();
        List<Kit_Component> allComponents = kitComponentRepository.findByKitId(kit.getId());
        float kitAmount = (float) allComponents.stream()
                .mapToDouble(c -> c.getPricePerCom() != null ? c.getPricePerCom() : 0.0)
                .sum();
        kit.setAmount(kitAmount);
        kitsRepository.save(kit);

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

            // Determine file type and create workbook
            Workbook workbook;
            if (fileName != null && fileName.toLowerCase().endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(new ByteArrayInputStream(fileBytes));
            } else {
                workbook = new HSSFWorkbook(new ByteArrayInputStream(fileBytes));
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

            // Get kit
            Optional<Kits> kitOptional = kitsRepository.findById(kitId);
            if (kitOptional.isEmpty()) {
                workbook.close();
                return ExcelImportResponse.builder()
                        .success(false)
                        .message("Kit not found: " + kitId)
                        .build();
            }
            Kits kit = kitOptional.get();

            // Process each row (skip header row)
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header row

                totalRows++;

                try {
                    // Excel columns: index, name, link, quantity
                    // Column A: index (optional, can be used for ordering)
                    // Column B: name (required)
                    // Column C: link (optional)
                    // Column D: quantity (required)

                    String componentName = getCellValueAsString(row.getCell(1)); // Column B: name
                    String link = getCellValueAsString(row.getCell(2)); // Column C: link
                    String quantityStr = getCellValueAsString(row.getCell(3)); // Column D: quantity

                    // Validate required fields
                    if (componentName == null || componentName.trim().isEmpty()) {
                        throw new RuntimeException("Component name is required");
                    }

                    Integer quantity = null;
                    if (quantityStr != null && !quantityStr.trim().isEmpty()) {
                        try {
                            quantity = Integer.parseInt(quantityStr.trim());
                        } catch (NumberFormatException e) {
                            throw new RuntimeException("Invalid quantity format: " + quantityStr);
                        }
                    } else {
                        throw new RuntimeException("Quantity is required");
                    }

                    // Create component
                    Kit_Component component = new Kit_Component();
                    component.setComponentName(componentName.trim());
                    component.setComponentType(KitComponentType.RED); // Default type, can be changed later
                    component.setQuantityTotal(quantity);
                    component.setQuantityAvailable(quantity);
                    component.setLink(link != null ? link.trim() : null);
                    component.setStatus("AVAILABLE");
                    component.setPricePerCom(0.0); // Default price, can be updated later
                    component.setKit(kit);

                    kitComponentRepository.save(component);
                    successCount++;
                } catch (Exception e) {
                    errors.add("Row " + (row.getRowNum() + 1) + ": " + e.getMessage());
                }
            }

            workbook.close();

            // Recalculate kit amount
            List<Kit_Component> allComponents = kitComponentRepository.findByKitId(kit.getId());
            float kitAmount = (float) allComponents.stream()
                    .mapToDouble(c -> c.getPricePerCom() != null ? c.getPricePerCom() : 0.0)
                    .sum();
            kit.setAmount(kitAmount);
            kitsRepository.save(kit);

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
