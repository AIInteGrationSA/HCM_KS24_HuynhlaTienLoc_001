package vn.rikkei.exam.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.rikkei.exam.booking.model.RoomInventory;
import java.time.LocalDate;
import java.util.List;

public interface RoomInventoryRepository extends JpaRepository<RoomInventory, Long> {
    List<RoomInventory> findByRoomTypeRoomTypeAndAvailableDateGreaterThanEqualAndAvailableDateLessThan(
            String roomType, LocalDate checkIn, LocalDate checkOut);
}
