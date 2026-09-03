package vn.rikkei.exam.booking.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.rikkei.exam.booking.dto.RagResponse;
import vn.rikkei.exam.booking.service.rag.DocumentIngestionService;
import vn.rikkei.exam.booking.service.rag.RagService;

import java.io.IOException;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/api/v1/rag")
@RequiredArgsConstructor
@Tag(name = "Module 1 — RAG", description = "Tra cứu quy chế vận chuyển với Retrieval-Augmented Generation")
public class RagController {

    private final RagService ragService;
    private final DocumentIngestionService ingestionService;


    @GetMapping("/ask")
    @Operation(
            summary = "Tra cứu quy chế vận chuyển",
            description = """
            Đặt câu hỏi bằng ngôn ngữ tự nhiên về quy chế vận chuyển RikkeiExpress.
            
            **Pipeline RAG:**
            1. Chuyển câu hỏi → vector embedding
            2. Tìm kiếm top-5 chunk tương đồng trong PgVector (cosine similarity ≥ 0.7)
            3. Inject context vào System Prompt
            4. LLM sinh câu trả lời kèm trích dẫn nguồn
            
            **Chống hallucination:** AI sẽ từ chối trả lời nếu thông tin không có trong tài liệu.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Câu trả lời thành công kèm trích dẫn nguồn"),
                    @ApiResponse(responseCode = "400", description = "Câu hỏi không hợp lệ"),
                    @ApiResponse(responseCode = "500", description = "Lỗi xử lý AI")
            }
    )
    public ResponseEntity<RagResponse> ask(
            @Parameter(description = "Câu hỏi về quy chế vận chuyển",
                    example = "Mức bồi thường tối đa khi hàng bị hỏng hóc là bao nhiêu?",
                    required = true)
            @RequestParam String question
    ) {
        if (question == null || question.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        log.info("[RAG-API] Nhận câu hỏi: '{}'", question);
        RagResponse response = ragService.ask(question.trim());
        return ResponseEntity.ok(response);
    }


    @PostMapping("/ingest/default")
    @Operation(
            summary = "Nạp tài liệu quy chế mặc định",
            description = "Nạp file rikei-express-policy.md vào PgVector (chunk 500 tokens, overlap 50)."
    )
    public ResponseEntity<Map<String, Object>> ingestDefault() {
        log.info("[RAG-API] Nạp tài liệu quy chế mặc định");
        try {
            int chunks = ingestionService.ingestDefaultPolicyDocument();
            return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "document", "rikei-express-policy.md",
                    "chunksIngested", chunks,
                    "message", "Tài liệu đã được nạp thành công vào VectorStore"
            ));
        } catch (Exception e) {
            log.error("[RAG-API] Lỗi nạp tài liệu mặc định: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "ERROR",
                    "message", e.getMessage()
            ));
        }
    }


    @PostMapping(value = "/ingest", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Nạp tài liệu tùy chọn vào VectorStore",
            description = "Upload file tài liệu (PDF, MD, TXT) để đưa vào hệ thống tra cứu RAG."
    )
    public ResponseEntity<Map<String, Object>> ingestFile(
            @Parameter(description = "File tài liệu cần nạp (PDF, MD, TXT, DOCX)")
            @RequestParam("file") MultipartFile file
    ) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "ERROR",
                    "message", "File không được để trống"
            ));
        }

        log.info("[RAG-API] Upload file: '{}' ({} bytes)",
                file.getOriginalFilename(), file.getSize());
        try {
            int chunks = ingestionService.ingestUploadedFile(file);
            return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "fileName", file.getOriginalFilename(),
                    "fileSize", file.getSize(),
                    "chunksIngested", chunks,
                    "message", "File đã được nạp thành công vào VectorStore"
            ));
        } catch (IOException e) {
            log.error("[RAG-API] Lỗi đọc file: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "ERROR",
                    "message", "Không thể đọc file: " + e.getMessage()
            ));
        } catch (Exception e) {
            log.error("[RAG-API] Lỗi nạp file: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "ERROR",
                    "message", e.getMessage()
            ));
        }
    }
}
