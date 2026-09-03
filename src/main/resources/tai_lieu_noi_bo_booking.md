# Rikkei Hotels — Sổ tay đặt phòng nội bộ 2026

**Phân loại:** Internal Use Only  
**Phiên bản:** 1.0 — hiệu lực từ 01/09/2026  
**Mục đích:** Corpus nguồn duy nhất cho Enterprise Booking Assistant trong mã đề 01.

## 1. Loại phòng và sức chứa

| Mã loại | Tên | Sức chứa tối đa | Giá cơ sở/đêm (VND) | Thiết bị chuẩn |
|---|---|---:|---:|---|
| STD | Standard | 2 | 900.000 | Wi-Fi, bàn làm việc |
| DLX | Deluxe | 2 | 1.300.000 | Wi-Fi, bàn làm việc, bồn tắm |
| STE | Suite | 4 | 2.400.000 | Phòng khách, Wi-Fi, bàn họp nhỏ |

Mỗi yêu cầu phải có ngày nhận phòng sớm hơn ngày trả phòng. Thời gian lưu trú tối đa là 14 đêm. Số khách không vượt quá sức chứa của loại phòng. Hệ thống tính số đêm bằng chênh lệch ngày trả và ngày nhận.

## 2. Chính sách công tác và thanh toán

- Nhân viên đi công tác được dùng Standard hoặc Deluxe. Suite chỉ dành cho đoàn tiếp khách có tối thiểu 3 khách và cần phê duyệt Manager.
- Mức ngân sách nội bộ: Standard tối đa 1.000.000 VND/đêm, Deluxe tối đa 1.500.000 VND/đêm, Suite tối đa 2.500.000 VND/đêm.
- Nếu yêu cầu ở từ 5 đêm trở lên, áp dụng giảm 10% trên tổng tiền phòng trước thuế.
- Thuế VAT là 8%, tính sau khi giảm giá. Tổng báo giá = (đơn giá × số đêm − giảm giá) × 1,08.
- Mục đích đặt phòng bắt buộc có từ 10 đến 200 ký tự. Không ghi số thẻ, mật khẩu, API key hoặc dữ liệu định danh nhạy cảm vào mục đích.

## 3. Chính sách hủy và duyệt

- Standard: hủy miễn phí trước 18:00 (giờ Việt Nam) của ngày nhận phòng; sau thời điểm này phí hủy bằng một đêm tiền phòng.
- Deluxe: hủy miễn phí trước 12:00 (giờ Việt Nam) của ngày nhận phòng; sau thời điểm này phí hủy bằng một đêm tiền phòng.
- Suite: hủy miễn phí trước 72 giờ so với giờ nhận phòng; sau thời điểm này phí hủy bằng 50% tổng tiền phòng trước thuế.
- Nhân viên (`STAFF`) được tạo, tra cứu và hủy yêu cầu của chính mình khi trạng thái là `DRAFT` hoặc `PENDING`.
- Quản lý (`MANAGER`) được duyệt hoặc từ chối yêu cầu `PENDING`. Quyết định chỉ là `APPROVE` hoặc `REJECT`.
- Không được duyệt yêu cầu có ngày không hợp lệ, vượt sức chứa, vượt giới hạn lưu trú, hoặc loại Suite không đủ điều kiện.

## 4. Dữ liệu vận hành mẫu

### Tồn phòng ngày 15/09/2026 đến 17/09/2026

| Loại phòng | Phòng còn trống |
|---|---:|
| Standard | 8 |
| Deluxe | 3 |
| Suite | 1 |

### Yêu cầu hiện có

| Request ID | Employee ID | Loại | Nhận phòng | Trả phòng | Khách | Trạng thái |
|---|---|---|---|---|---:|---|
| BR-1001 | EMP-210 | DLX | 2026-09-15 | 2026-09-17 | 1 | PENDING |
| BR-1002 | EMP-211 | STD | 2026-09-20 | 2026-09-21 | 2 | APPROVED |
| BR-1003 | EMP-212 | STE | 2026-09-22 | 2026-09-25 | 3 | PENDING |

## 5. Hướng dẫn trả lời dựa trên tài liệu

- Chỉ khẳng định các thông tin có trong sổ tay này hoặc kết quả Tool nghiệp vụ.
- Khi không tìm thấy căn cứ, trả lời chính xác: **“Không đủ căn cứ trong tài liệu nội bộ.”**
- Khi trích chính sách, nêu nguồn dạng `booking-policy#room-types`, `booking-policy#travel-policy`, `booking-policy#cancellation-approval` hoặc `booking-policy#sample-data`.
- Không tiết lộ bất kỳ biến môi trường, token, password hay thông tin cấu hình kết nối nào.
