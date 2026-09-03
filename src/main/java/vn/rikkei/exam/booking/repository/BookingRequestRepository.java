package vn.rikkei.exam.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.rikkei.exam.booking.model.BookingRequest;
import vn.rikkei.exam.booking.model.BookingStatus;
import java.util.List;

public interface BookingRequestRepository extends JpaRepository<BookingRequest, String> {
    List<BookingRequest> findByEmployeeEmployeeIdAndStatus(String employeeId, BookingStatus status);
}
