package IotSystem.IoTSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "IotSystem.IoTSystem.Repository")

public class IoTSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(IoTSystemApplication.class, args);
	}

}
