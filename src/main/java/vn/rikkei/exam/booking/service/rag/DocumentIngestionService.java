package vn.rikkei.exam.booking.service.rag;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentIngestionService {

    private final VectorStore vectorStore;


    private static final int CHUNK_SIZE     = 500;
    private static final int MIN_CHUNK_SIZE = 50;
    private static final int MAX_CHUNK_SIZE = 10000;


    public int ingestDocument(String fileName) {
        log.info("[RAG-INGEST] Bắt đầu nạp tài liệu: {}", fileName);

        Resource resource = new ClassPathResource("documents/" + fileName);
        return ingestResource(resource, fileName);
    }

    public int ingestUploadedFile(MultipartFile file) throws IOException {
        log.info("[RAG-INGEST] Nạp file upload: {}, size: {} bytes",
                file.getOriginalFilename(), file.getSize());

        // Tạo Spring Resource từ MultipartFile
        Resource resource = file.getResource();
        return ingestResource(resource, file.getOriginalFilename());
    }


    private int ingestResource(Resource resource, String sourceName) {
        try {
            // ---- BƯỚC 1: LOAD — Đọc tài liệu ----
            List<Document> rawDocuments = loadDocument(resource, sourceName);
            log.info("[RAG-INGEST] Đọc xong {} trang/đoạn từ '{}'",
                    rawDocuments.size(), sourceName);

            // ---- BƯỚC 2: SPLIT — Phân mảnh tài liệu (Chunking) ----
            TokenTextSplitter splitter = new TokenTextSplitter();

            List<Document> chunks = splitter.apply(rawDocuments);
            log.info("[RAG-INGEST] Phân mảnh xong: {} chunks (size={})",
                    chunks.size(), CHUNK_SIZE);

            // Gắn metadata bổ sung cho từng chunk để phục vụ trích dẫn nguồn
            enrichChunkMetadata(chunks, sourceName);

            // ---- BƯỚC 3+4: EMBED + STORE ----
            // Spring AI tự động gọi EmbeddingModel rồi lưu vào vector_store
            vectorStore.add(chunks);

            log.info("[RAG-INGEST]  Nạp thành công {} chunks từ '{}'",
                    chunks.size(), sourceName);
            return chunks.size();

        } catch (Exception e) {
            log.error("[RAG-INGEST]  Lỗi khi nạp tài liệu '{}': {}", sourceName, e.getMessage(), e);
            throw new RuntimeException("Không thể nạp tài liệu: " + sourceName, e);
        }
    }


    private List<Document> loadDocument(Resource resource, String sourceName) {
        String name = sourceName.toLowerCase();

        if (name.endsWith(".pdf")) {
            PagePdfDocumentReader reader = new PagePdfDocumentReader(resource);
            return reader.get();
        } else {
            // TextReader cho Markdown (.md), plain text (.txt), và các format văn bản khác
            TextReader reader = new TextReader(resource);
            reader.getCustomMetadata().put("source", sourceName);
            reader.getCustomMetadata().put("format", name.endsWith(".md") ? "markdown" : "text");
            return reader.get();
        }
    }


    private void enrichChunkMetadata(List<Document> chunks, String sourceName) {
        for (int i = 0; i < chunks.size(); i++) {
            Document chunk = chunks.get(i);
            Map<String, Object> meta = new HashMap<>(chunk.getMetadata());
            meta.put("source_file", sourceName);
            meta.put("chunk_index", i + 1);
            meta.put("total_chunks", chunks.size());
            meta.put("document_type", "logistics_policy");
            // Giữ metadata cũ (số trang nếu có từ PDF reader)
        }
    }


    public int ingestDefaultPolicyDocument() {
        return ingestDocument("rikei-express-policy.md");
    }
}

