package vn.rikkei.exam.booking.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "room_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomType {
    @Id
    @Column(name = "room_type", length = 3)
    private String roomType;

    @Column(name = "display_name", nullable = false, length = 50)
    private String displayName;

    @Column(name = "max_guests", nullable = false)
    private Integer maxGuests;

    @Column(name = "base_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal basePrice;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;
}
