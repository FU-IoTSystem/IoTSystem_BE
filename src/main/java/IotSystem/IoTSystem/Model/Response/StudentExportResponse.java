package IotSystem.IoTSystem.Model.Response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentExportResponse {

    // Class code of the student
    @JsonProperty("ClassCode")
    private String classCode;

    // Student code (roll number)
    @JsonProperty("StudentCode")
    private String studentCode;

    // Full name of the student
    @JsonProperty("name")
    private String name;

    // Email of the student
    @JsonProperty("email")
    private String email;

    // Role name (e.g. STUDENT)
    @JsonProperty("Role")
    private String role;

    // Account status (active/inactive)
    @JsonProperty("Status")
    private Boolean status;

    // Account created date
    @JsonProperty("CreateDate")
    private LocalDateTime createDate;
}


