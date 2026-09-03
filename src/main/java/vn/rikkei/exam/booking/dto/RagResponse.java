package vn.rikkei.exam.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;


@Data
@Builder
public class RagResponse {

    private String answer;

    private List<SourceDocument> sourceDocuments;

    private String question;

    private int retrievedChunks;



    @Data
    @Builder
    public static class SourceDocument {
        private String fileName;
        private String pageOrSection;
        private String excerpt;
        private Double similarityScore;
    }
}
