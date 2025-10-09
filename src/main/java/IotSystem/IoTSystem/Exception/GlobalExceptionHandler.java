package IotSystem.IoTSystem.Exception;

import IotSystem.IoTSystem.Model.Entities.Enum.Status.ErrorCode;
import IotSystem.IoTSystem.Model.Entities.Enum.Status.HTTPStatus;
import IotSystem.IoTSystem.Model.Response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleNotFound(ResourceNotFoundException ex) {
        ApiResponse<?> response = new ApiResponse<>();
        response.setStatus(HTTPStatus.NotFound);
        response.setErrorCode(ErrorCode.ResourceNotFound);
        response.setSuccess(false);
        response.setStatusText("Not Found");
        response.setMessage(ex.getMessage());
        response.setTotal(0);
        response.setData(null);
        return ResponseEntity.status(404).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGeneral(Exception ex) {
        ApiResponse<?> response = new ApiResponse<>();
        response.setStatus(HTTPStatus.InternalServerError);
        response.setErrorCode(ErrorCode.InternalServerError);
        response.setSuccess(false);
        response.setStatusText("Error");
        response.setMessage("Lỗi hệ thống: " + ex.getMessage());
        response.setTotal(0);
        response.setData(null);
        return ResponseEntity.status(500).body(response);
    }

}

