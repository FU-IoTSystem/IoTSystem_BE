package IotSystem.IoTSystem.Model.Response;

import IotSystem.IoTSystem.Model.Entities.Enum.Status.HTTPStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApiResponse<T> {

    //chuan hoa api reponse
    private HTTPStatus status;
    private String message;
    private T data;
    private LocalDateTime timestamp = LocalDateTime.now();
}

