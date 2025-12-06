package IotSystem.IoTSystem;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.TimeZone;

@SpringBootApplication(exclude = {
		org.springframework.boot.autoconfigure.http.client.HttpClientAutoConfiguration.class
})
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "IotSystem.IoTSystem.Repository")

public class IoTSystemApplication {

	@PostConstruct
	public void init() {
		// Set default timezone to UTC+7 (Asia/Ho_Chi_Minh)
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
	}

	public static void main(String[] args) {
		// Set timezone before Spring Boot starts
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
		System.setProperty("user.timezone", "Asia/Ho_Chi_Minh");

		SpringApplication.run(IoTSystemApplication.class, args);
	}

}
