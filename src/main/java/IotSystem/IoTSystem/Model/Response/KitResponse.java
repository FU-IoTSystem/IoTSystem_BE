package IotSystem.IoTSystem.Model.Response;

import IotSystem.IoTSystem.Model.Entities.Enum.KitType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KitResponse {
    private UUID id;
    private String kitName;
    private KitType type;
    private String status;
    private String description;
    private String imageUrl;
    private Integer quantityTotal;
    private Integer quantityAvailable;
    /**
     * Tổng pricePerCom của tất cả components (tính động trên mapper; không map trực tiếp DB).
     */
    private Float amount;

    private List<KitComponentResponse> components;
}