package IotSystem.IoTSystem.Config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.awt.Desktop;
import java.net.URI;

@Component
public class SwaggerBrowserOpener implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            // Lấy port đang chạy (mặc định 8080, hoặc có thể đọc từ Environment nếu bạn cấu hình server.port khác)
            String port = event.getApplicationContext()
                    .getEnvironment()
                    .getProperty("local.server.port", "8080");

            String url = "http://localhost:" + port + "/swagger-ui/index.html"; // ✅


            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                System.out.println("Please open your browser and navigate to: " + url);
            }
        } catch (Exception e) {
            // nếu bật headless (ci/cd server không có GUI) thì sẽ văng lỗi, bạn có thể log rồi bỏ qua
            e.printStackTrace();
        }
    }
}
