package vn.rikkei.exam.booking.service.rag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import vn.rikkei.exam.booking.dto.RagResponse;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class RagService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    private static final int    TOP_K               = 5;
    private static final double SIMILARITY_THRESHOLD = 0.5;


    private static final String RAG_SYSTEM_PROMPT = """
        Bạn là trợ lý tra cứu quy chế vận chuyển của Enterprise Booking Assistan.
        
        ## QUY TẮC TRẢ LỜI BẮT BUỘC:
        
        1. **CHỈ** trả lời dựa trên nội dung TÀI LIỆU QUY CHẾ được cung cấp trong context.
           KHÔNG sử dụng kiến thức bên ngoài hoặc tự suy đoán.
        
        2. **BẮT BUỘC trích dẫn nguồn**: Sau mỗi thông tin quan trọng, ghi rõ nguồn tài liệu.
           Ví dụ: "Theo Điều Chính sách công tác và thanh toán)"
        
        3. **THỪA NHẬN giới hạn**: Nếu câu hỏi không có trong tài liệu, trả lời:
           "Tôi không tìm thấy thông tin về [chủ đề] trong tài liệu quy chế hiện tại.
            Vui lòng liên hệ bộ phận hỗ trợ khách hàng để được tư vấn chính xác."
        
        4. **Ngôn ngữ**: Trả lời bằng tiếng Việt, rõ ràng, súc tích, có cấu trúc.
        
        5. **Không bịa đặt**: Tuyệt đối không tạo ra điều khoản, biểu phí, hay mốc thời gian
           không có trong tài liệu.
        
        ## CONTEXT TỪ TÀI LIỆU:
        %s
        """;

    public RagResponse ask(String question) {
        log.info("[RAG-QUERY] Câu hỏi: '{}'", question);

        try {
            // ---- BƯỚC 1: Tìm kiếm chunk tương đồng trong VectorStore ----
            SearchRequest searchRequest = SearchRequest.builder()
                    .query(question)
                    .topK(TOP_K)
                    .similarityThreshold(SIMILARITY_THRESHOLD)
                    .build();

            List<Document> retrievedDocs = vectorStore.similaritySearch(searchRequest);
            log.info("[RAG-QUERY] Truy xuất {} chunks (threshold={})",
                    retrievedDocs.size(), SIMILARITY_THRESHOLD);

            // ---- BƯỚC 2: Format context từ retrieved documents ----
            String contextText = retrievedDocs.isEmpty()
                    ? "Không tìm thấy nội dung liên quan trong tài liệu quy chế."
                    : retrievedDocs.stream()
                      .map(doc -> "- " + doc.getFormattedContent())
                      .collect(Collectors.joining("\n\n"));

            String formattedSystemPrompt = String.format(RAG_SYSTEM_PROMPT, contextText);

            // ---- BƯỚC 3: Gọi LLM với System Prompt chứa context ----
            String answer = chatClient.prompt()
                    .system(formattedSystemPrompt)
                    .user(question)
                    .call()
                    .content();

            log.info("[RAG-QUERY]  Trả lời thành công ({} chars)", answer != null ? answer.length() : 0);

            // ---- BƯỚC 4: Xây dựng response với trích dẫn nguồn ----
            List<RagResponse.SourceDocument> sources = buildSourceDocuments(retrievedDocs);

            return RagResponse.builder()
                    .question(question)
                    .answer(answer)
                    .sourceDocuments(sources)
                    .retrievedChunks(retrievedDocs.size())
                    .build();

        } catch (Exception e) {
            log.error("[RAG-QUERY] Lỗi khi xử lý câu hỏi: {}", e.getMessage(), e);
            throw new RuntimeException("Không thể xử lý câu hỏi RAG", e);
        }
    }

    private List<RagResponse.SourceDocument> buildSourceDocuments(List<Document> docs) {
        return docs.stream().map(doc -> {
            Map<String, Object> meta = doc.getMetadata();
            String content = doc.getFormattedContent();
            // Lấy excerpt ngắn (150 chars đầu)
            String excerpt = content.length() > 150
                    ? content.substring(0, 150) + "..."
                    : content;

            return RagResponse.SourceDocument.builder()
                    .fileName(getMetaString(meta, "source_file", "rikei-express-policy.md"))
                    .pageOrSection(getMetaString(meta, "page_number",
                            "Chunk " + getMetaString(meta, "chunk_index", "N/A")))
                    .excerpt(excerpt)
                    .similarityScore(getMetaDouble(meta, "distance"))
                    .build();
        }).toList();
    }

    private Double getMetaDouble(Map<String, Object> meta, String key) {
        Object val = meta.get(key);
        if (val instanceof Number n) {
            return n.doubleValue();
        }
        return null;
    }

    private String getMetaString(Map<String, Object> meta, String key, String defaultValue) {
        Object val = meta.get(key);
        return val != null ? val.toString() : defaultValue;
    }
}