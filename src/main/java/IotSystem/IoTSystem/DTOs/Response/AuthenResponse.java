package IotSystem.IoTSystem.DTOs.Response;

public class AuthenResponse {
    private String token;

    public AuthenResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
