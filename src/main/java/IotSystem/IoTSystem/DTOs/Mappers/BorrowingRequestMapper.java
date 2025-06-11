package IotSystem.IoTSystem.DTOs.Mappers;

import IotSystem.IoTSystem.DTOs.Response.BorrowingResponseDTO;
import IotSystem.IoTSystem.Entities.BorrowingRequest;

public class BorrowingRequestMapper {
    public static BorrowingResponseDTO toDTO(BorrowingRequest request) {
        return new BorrowingResponseDTO(
                request.getId(),
                request.getKit().getId(),
                request.getKit().getType().toString(),
                request.getKit().getStatus().toString(),
                request.getRequestedBy().getFullName(),
                request.getBorrowDate(),
                request.getReturnDate(),
                request.getStatus().toString()
        );
    }
}
