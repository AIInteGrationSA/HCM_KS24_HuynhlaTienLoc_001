package vn.rikkei.exam.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.rikkei.exam.booking.model.RoomType;

public interface RoomTypeRepository extends JpaRepository<RoomType, String> {}
