package vn.rikkei.exam.booking.dto;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class ChatResponse {

    private String reply;

    private String actionTaken;

    private Long incidentId;

    private String trackingCode;

    private String newDeliveryStatus;

    private String errorMessage;
}
