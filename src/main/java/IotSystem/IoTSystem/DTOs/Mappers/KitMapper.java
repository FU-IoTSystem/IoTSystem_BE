package IotSystem.IoTSystem.DTOs.Mappers;

import IotSystem.IoTSystem.DTOs.Response.KitResponseDTO;
import IotSystem.IoTSystem.Entities.Kits;

public class KitMapper {
    public static KitResponseDTO toDTO(Kits kit) {
        KitResponseDTO dto = new KitResponseDTO();
        dto.setId(kit.getId());
        dto.setType(kit.getType());
        dto.setStatus(kit.getStatus());
        dto.setQrCode(kit.getQrCode());
        dto.setDescription(kit.getDescription());
        return dto;
    }
}
