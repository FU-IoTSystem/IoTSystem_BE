package IotSystem.IoTSystem.Model.Request;

import java.util.UUID;

public class ClassRequest {
    private String classCode;   // mã lớp
    private String semester;    // học kỳ
    private boolean status;     // trạng thái (đang mở / đã đóng)
    private UUID teacherId;     // id giảng viên phụ trách (FK đến Account)
}
