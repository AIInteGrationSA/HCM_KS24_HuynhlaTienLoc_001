package vn.rikkei.exam.booking.dto;


import lombok.Data;


@Data
public class CreateIncidentInput {

    /** Mã vận đơn (bắt buộc). Dạng: RK-YYYY-NNN */
    private String trackingCode;

    /**
     * Loại sự cố (bắt buộc). Các giá trị hợp lệ:
     * - HỎNG_HÓC: hàng hóa bị hư hỏng vật lý
     * - GIAO_TRỄ: giao hàng muộn hơn cam kết
     * - THẤT_LẠC: không tìm thấy đơn hàng
     */
    private String incidentType;

    /** Mã bưu cục xảy ra sự cố (bắt buộc). Ví dụ: HN-01, SG-02, DN-03 */
    private String hubCode;

    /**
     * Mức độ nghiêm trọng (bắt buộc). Các giá trị hợp lệ:
     * - LOW: ảnh hưởng nhỏ, không khẩn cấp
     * - MEDIUM: cần xử lý trong ngày làm việc
     * - CRITICAL: khẩn cấp, cần xử lý ngay lập tức
     */
    private String severity;

    /** Mô tả chi tiết sự cố do AI bóc tách từ tin nhắn (bắt buộc) */
    private String description;
}

