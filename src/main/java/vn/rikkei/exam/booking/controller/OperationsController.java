package vn.rikkei.exam.booking.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.rikkei.exam.booking.service.chat.OperationsAgentService;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/operations")
public class OperationsController {

    private final OperationsAgentService agentService;

    public OperationsController(OperationsAgentService agentService) {
        this.agentService = agentService;
    }

    @PostMapping("/chat")
    public Map<String, String> processIncident(@RequestBody Map<String, String> request) {
        String userMessage = request.get("message");
        String response = agentService.processIncident(userMessage);
        return Map.of("response", response);
    }
}
