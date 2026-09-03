package vn.rikkei.exam.booking.dto;

import lombok.Data;

/**
 * Request DTO cho Agent chat API.
 * Nhận tin nhắn ngôn ngữ tự nhiên bất cấu trúc từ khách hàng.
 */
@Data
public class ChatRequest {

    /**
     * Tin nhắn ngôn ngữ tự nhiên từ khách hàng.
     * Ví dụ: "Đơn RK-2026-001 bị ướt sũng hỏng đồ ở kho Hà Nội, đề nghị kiểm tra"
     */
    private String message;

    /** Session ID để theo dõi cuộc hội thoại (tuỳ chọn) */
    private String sessionId;
}
