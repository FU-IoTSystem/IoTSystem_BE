package IotSystem.IoTSystem.Model.Response;

public class AuthenResponse {
    private String token;

    public AuthenResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
