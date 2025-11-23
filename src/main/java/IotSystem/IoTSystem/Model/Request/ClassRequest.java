package IotSystem.IoTSystem.Model.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassRequest {
    private String classCode;   // mã lớp
    private String semester;    // học kỳ
    private boolean status;    // trạng thái (đang mở / đã đóng)
    private UUID teacherId;     // id giảng viên phụ trách (FK đến Account)
}
