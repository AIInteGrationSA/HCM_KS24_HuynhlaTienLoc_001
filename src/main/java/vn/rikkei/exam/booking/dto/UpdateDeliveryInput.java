package vn.rikkei.exam.booking.dto;

import lombok.Data;

@Data
public class UpdateDeliveryInput {

    /** Mã vận đơn cần cập nhật (bắt buộc) */
    private String trackingCode;

    /**
     * Trạng thái mới (bắt buộc). Các giá trị hợp lệ:
     * - DAMAGED  → khi incident_type là HỎNG_HÓC
     * - DELAYED  → khi incident_type là GIAO_TRỄ
     * - IN_TRANSIT, DELIVERED (trạng thái chuẩn)
     */
    private String newStatus;

    /** Ghi chú bổ sung (tuỳ chọn) */
    private String note;
}
