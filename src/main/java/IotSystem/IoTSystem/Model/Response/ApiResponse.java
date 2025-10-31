package IotSystem.IoTSystem.Model.Response;

import IotSystem.IoTSystem.Model.Entities.Enum.Status.ErrorCode;
import IotSystem.IoTSystem.Model.Entities.Enum.Status.HTTPStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema
public class ApiResponse<T> {

    private HTTPStatus status;         // HTTP status như 200, 404
    private ErrorCode errorCode;       // Mã lỗi nghiệp vụ như ResourceNotFound
    private String message;            // Mô tả lỗi hoặc thông báo
    private String statusText;         // "Success", "Error", "Not Found", ...
    private boolean success;           // true/false
    private int total;                 // số lượng dữ liệu trả về
    private T data;                    // dữ liệu thực tế
    private LocalDateTime timestamp = LocalDateTime.now();
}

