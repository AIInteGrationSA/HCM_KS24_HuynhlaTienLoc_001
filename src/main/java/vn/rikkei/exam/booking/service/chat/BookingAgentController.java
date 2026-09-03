package vn.rikkei.exam.booking.service.chat;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/v1/booking")
public class BookingAgentController {

    private static final Logger log = LoggerFactory.getLogger(BookingAgentController.class);
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final ChatClient bookingAgentChatClient;

    public BookingAgentController(ChatClient bookingAgentChatClient) {
        this.bookingAgentChatClient = bookingAgentChatClient;
    }

    @GetMapping("/chat")
    public String chat(
            @RequestParam(name = "chatId",  defaultValue = "default-session") String chatId,
            @RequestParam(name = "message") String message) {

        log.info("[Booking Agent] Nhận request — chatId='{}', message='{}'", chatId, message);

        String today = LocalDate.now().format(DISPLAY_DATE_FORMAT);
        String todayIso = LocalDate.now().toString(); // YYYY-MM-DD cho tool

        log.debug("[Booking Agent] Ngày hôm nay: {} (ISO: {})", today, todayIso);

        String response = bookingAgentChatClient
                .prompt()

                .system(systemSpec -> systemSpec
                        .param("current_date", today + " (ISO: " + todayIso + ")")
                )

                .advisors(advisorSpec -> advisorSpec
                        .param("chat_memory_conversation_id", chatId)
                )

                .user(message)

                .call()
                .content();

        log.info("[Booking Agent] Phản hồi cho chatId='{}': {}", chatId, response);

        return response;
    }
}

