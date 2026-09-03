package vn.rikkei.exam.booking.tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BookingTools {

    @Tool(description = """
            Kiểm tra số lượng phòng còn trống tại khách sạn Rikkei Hotel theo loại phòng và ngày check-in.
            Gọi tool này khi khách hàng hỏi về tình trạng phòng trống, muốn đặt phòng, hoặc hỏi có phòng không.
            Loại phòng hỗ trợ: Standard (phòng tiêu chuẩn), Deluxe (phòng cao cấp), Suite (phòng hạng nhất).
            Tham số date cần ở định dạng YYYY-MM-DD.
            """)
    public String getRoomAvailability(
            @ToolParam(description = "Loại phòng cần kiểm tra: Standard, Deluxe, hoặc Suite")
            String roomType,

            @ToolParam(description = "Ngày check-in cần kiểm tra, định dạng YYYY-MM-DD. Nếu user nói 'ngày mai' hãy tính từ ngày hiện tại.")
            String date) {

        log.info("[FUNCTION CALLING] LLM đã gọi getRoomAvailability(roomType='{}', date='{}')", roomType, date);

        if (roomType == null || roomType.trim().isEmpty()) {
            String result = "Vui lòng cung cấp loại phòng cần kiểm tra (Standard, Deluxe, hoặc Suite).";
            log.info("[FUNCTION CALLING] getRoomAvailability trả về: {}", result);
            return result;
        }

        if (date == null || date.trim().isEmpty()) {
            String result = "Vui lòng cung cấp ngày check-in cần kiểm tra (định dạng YYYY-MM-DD).";
            log.info("[FUNCTION CALLING] getRoomAvailability trả về: {}", result);
            return result;
        }

        String normalizedRoomType = roomType.toLowerCase().trim();

        if (!ROOM_AVAILABILITY.containsKey(normalizedRoomType)) {
            String result = String.format(
                    "Khách sạn Rikkei Hotel không có loại phòng '%s'. " +
                            "Các loại phòng hiện có: Standard, Deluxe, Suite.",
                    roomType
            );
            log.info("[FUNCTION CALLING] getRoomAvailability trả về: {}", result);
            return result;
        }

        int availableRooms = ROOM_AVAILABILITY.get(normalizedRoomType);
        long pricePerNight = ROOM_PRICES.get(normalizedRoomType);

        String result;
        if (availableRooms == 0) {
            result = String.format(
                    "Rất tiếc! Phòng %s ngày %s đã HẾT PHÒNG. " +
                            "Vui lòng chọn ngày khác hoặc loại phòng khác.",
                    roomType, date
            );
        } else {
            result = String.format(
                    "Phòng %s ngày %s: còn %d phòng trống. " +
                            "Giá: %,d VNĐ/đêm.",
                    roomType, date, availableRooms, pricePerNight
            );
        }

        log.info("[FUNCTION CALLING] getRoomAvailability trả về: {}", result);
        return result;
    }

}

