package vn.rikkei.exam.booking.service.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import vn.rikkei.exam.booking.dto.ChatRequest;
import vn.rikkei.exam.booking.dto.ChatResponse;
import vn.rikkei.exam.booking.tool.BookingTools;
//import vn.rikkei.exam.booking.tool.LogisticsToolService;

@Slf4j
@Service
@RequiredArgsConstructor
public class OperationsAgentService {

    private final ChatClient chatClient;
    private final BookingTools logisticsToolService;

    /**
     * System Prompt cho Logistics Agent — thiết kế để:
     * 1. Hướng dẫn LLM bóc tách thực thể chính xác
     * 2. Xác định thứ tự gọi tool (check → create → update)
     * 3. Phòng vệ khi thiếu thông tin
     */
    private static final String AGENT_SYSTEM_PROMPT = """
        Bạn là trợ lý vận hành tự động của RikkeiExpress — hệ thống logistics thông minh.
        Nhiệm vụ: Tiếp nhận phản ánh sự cố từ khách hàng và tự động xử lý trong hệ thống.
        
        ## QUY TRÌNH XỬ LÝ SỰ CỐ:
        
        **Bước 1 — Thu thập thông tin:**
        Bóc tách từ tin nhắn khách hàng:
        - Mã vận đơn (dạng RK-YYYY-NNN)
        - Loại sự cố: HỎNG_HÓC (hàng hư), GIAO_TRỄ (trễ hẹn), THẤT_LẠC (mất hàng)
        - Bưu cục: HN-01 (Hà Nội), SG-02 (Sài Gòn/TP.HCM), DN-03 (Đà Nẵng)
        - Mức độ: LOW (nhỏ), MEDIUM (trung bình), CRITICAL (khẩn cấp, nguy hiểm)
        
        **Bước 2 — Xác minh đơn hàng:**
        Dùng tool `getDeliveryStatus` để xác minh mã vận đơn tồn tại trong hệ thống.
        
        **Bước 3 — Tạo phiếu sự cố:**
        Nếu đơn tồn tại, dùng tool `createIncident` để ghi nhận sự cố vào hệ thống.
        
        **Bước 4 — Cập nhật trạng thái:**
        Dùng tool `updateDeliveryStatus` để cập nhật trạng thái đơn hàng:
        HỎNG_HÓC → DAMAGED | GIAO_TRỄ → DELAYED | THẤT_LẠC → DELAYED
        
        **Bước 5 — Phản hồi khách hàng:**
        Thông báo kết quả xử lý một cách thân thiện, chuyên nghiệp.
        
        ## QUY TẮC AN TOÀN:
        - Nếu tin nhắn thiếu mã vận đơn: Hỏi khách hàng cung cấp.
        - Nếu đơn hàng không tồn tại: Thông báo lịch sự, KHÔNG tạo sự cố.
        - Nếu không xác định được loại sự cố: Chọn phù hợp nhất dựa trên ngữ cảnh.
        - Luôn xưng hô lịch sự với khách hàng.
        """;

    /**
     * Xử lý tin nhắn phản ánh sự cố từ khách hàng.
     *
     * @param request ChatRequest chứa tin nhắn ngôn ngữ tự nhiên
     * @return ChatResponse với kết quả xử lý từ Agent
     */
    public ChatResponse processIncidentReport(ChatRequest request) {
        log.info("[AGENT] Nhận tin nhắn: '{}'", request.getMessage());

        try {
            // Gọi ChatClient với tools đã đăng ký
            // Spring AI tự động: phát hiện tool calls → thực thi → trả kết quả về LLM
            // Max iterations được cấu hình trong application.yml hoặc options
            String agentReply = chatClient.prompt()
                    .system(AGENT_SYSTEM_PROMPT)
                    .user(request.getMessage())
                    // Đăng ký tools từ LogisticsToolService
                    // Spring AI 2.x tự phát hiện @Tool annotated methods
                    .tools(logisticsToolService)
                    .call()
                    .content();

            log.info("[AGENT]  Xử lý xong, reply: {} chars", agentReply.length());

            return ChatResponse.builder()
                    .reply(agentReply)
                    .actionTaken("AGENT_PROCESSED")
                    .build();

        } catch (Exception e) {
            // ZERO CRASH POLICY — không để exception lan ra controller
            log.error("[AGENT]  Lỗi Agent: {}", e.getMessage(), e);
            return ChatResponse.builder()
                    .reply("Xin lỗi, hệ thống đang gặp sự cố kỹ thuật. " +
                            "Chúng tôi đã ghi nhận yêu cầu của bạn và sẽ xử lý trong thời gian sớm nhất. " +
                            "Vui lòng liên hệ hotline: 1800-1234 để được hỗ trợ ngay.")
                    .actionTaken("ERROR")
                    .errorMessage(e.getMessage())
                    .build();
        }
    }
}
