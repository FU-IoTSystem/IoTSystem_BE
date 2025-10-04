package IotSystem.IoTSystem.Model.Entities.Enum.Status;

public enum HTTPStatus {
    Ok(200),
    BadRequest(400),
    Unauthorized(401),
    Forbidden(403),
    NotFound(404),
    InternalServerError(500),
    Created(201),
    NoContent(204),
    Conflict(409);

    private final int code;

    HTTPStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
