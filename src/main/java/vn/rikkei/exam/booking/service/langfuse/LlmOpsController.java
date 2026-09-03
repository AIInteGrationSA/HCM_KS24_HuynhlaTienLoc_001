package vn.rikkei.exam.booking.service.langfuse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/llmops")
public class LlmOpsController {

    @GetMapping("/status")
    public Map<String, String> getStatus() {
        return Map.of(
                "status", "UP",
                "telemetry", "Enabled",
                "description", "LLMOps observability is configured via OpenTelemetry. Profiles: cloud/local."
        );
    }

    @GetMapping("/langfuse-guide")
    public String langfuseGuide() {
        return "Để xem trace và cost metrics trên Langfuse, " +
                "đảm bảo bạn chạy ứng dụng với profile 'cloud' và biến môi trường LANGFUSE_AUTH_HEADER.";
    }
}
