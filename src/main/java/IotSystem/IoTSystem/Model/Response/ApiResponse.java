package IotSystem.IoTSystem.Model.Response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApiResponse<T> {

    //chuan hoa api reponse
    private int status;
    private String message;
    private T data;
    private LocalDateTime timestamp = LocalDateTime.now();
}

