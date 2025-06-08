package IotSystem.IoTSystem.Entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Base {
    @CreationTimestamp
    private Date createdOn;
    @UpdateTimestamp
    private Date lastModifiedOn;
    @CreatedBy
    private String createdBy;
    @LastModifiedBy
    private String lastModifiedBy;
}

