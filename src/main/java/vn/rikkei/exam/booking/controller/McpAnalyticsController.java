package vn.rikkei.exam.booking.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/analytics")
public class McpAnalyticsController {

    private final ChatClient chatClient;

    public McpAnalyticsController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .defaultSystem("Bạn là chuyên gia phân tích dữ liệu. Hãy sử dụng MCP tool 'executeAnalyticsQuery' để truy vấn CSDL và trả lời câu hỏi của người dùng.")
                .build();
    }

    @PostMapping("/chat")
    public Map<String, String> chatAnalytics(@RequestBody Map<String, String> request) {
        String userMessage = request.get("message");
        String response = chatClient.prompt()
                .user(userMessage)
                .call()
                .content();
        return Map.of("response", response);
    }
}
