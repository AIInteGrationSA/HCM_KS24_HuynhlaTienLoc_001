# Enterprise Booking Assistant — Base Project (Đề 001)

Starter project dành cho bài thi thực hành **AI Integrated in Action**. Project đã có sẵn Gradle Wrapper, dependency, entity/model, Spring Data JPA repository, tài liệu chính sách và dữ liệu mẫu. Sinh viên chỉ triển khai phần nghiệp vụ và tích hợp AI theo đề thi.

Đề thi đầy đủ được phát riêng tại file `../DE_THI_THUC_HANH_DE_001.md`.

## Đã được cung cấp

- Java 17, Spring Boot, Spring AI, PostgreSQL/Supabase Pgvector và MCP client dependencies.
- `BookingAssistantApplication`.
- Model/entity: `Employee`, `RoomType`, `RoomInventory`, `BookingRequest`, `BookingStatus`.
- Repository: `EmployeeRepository`, `RoomTypeRepository`, `RoomInventoryRepository`, `BookingRequestRepository`.
- Cấu hình kết nối phòng thi tại `src/main/resources/application.yml`.
- Corpus RAG: `src/main/resources/tai_lieu_noi_bo_booking.md`.
- Dữ liệu nghiệp vụ mẫu: `src/main/resources/seed_data_booking.sql`.

## Phần sinh viên phải tự triển khai

Không có sẵn Service, DTO, Controller, `@Tool`, RAG ingestion/retrieval, ChatMemory, Langfuse trace, MCP capability handler, exception handler hoặc lời giải nghiệp vụ.

Các package trống đã được tạo trước để thống nhất cấu trúc bài nộp:

```text
config/                  # cấu hình Spring AI và ChatMemory
controller/              # API admin, assistant và operations
dto/                     # request/response contracts
exception/               # exception nghiệp vụ và global handler
service/                 # booking service
service/rag/             # chunking, ingest, retrieval Pgvector
service/chat/            # orchestration ChatClient
service/langfuse/        # trace/observability
service/mcp/             # Antigravity client integration
tool/                    # Spring AI @Tool
```

Các package trên chỉ chứa `.gitkeep` rỗng, không có code mẫu.

1. Khởi động ứng dụng để JPA tạo/cập nhật schema.
2. Chạy `seed_data_booking.sql` trên database được cấp.
3. Hoàn thành các API, Tool và test theo yêu cầu đề thi.

## Chạy project

```powershell
.\gradlew.bat bootRun
```

```powershell
.\gradlew.bat test
```

Không commit thông tin môi trường phòng thi khi nộp bài. Nếu cần thay đổi môi trường, chỉ thay các biến môi trường tương ứng, không đổi contract cấu hình.
## Langfuse

Docker Langfuse được cấu hình sẵn trong `docker-compose-langfuse.yml` và dùng host mặc định `http://localhost:3000`. Sinh viên chỉ cần khởi động Docker, tạo project Langfuse cá nhân rồi điền `LANGFUSE_PUBLIC_KEY` và `LANGFUSE_SECRET_KEY` qua environment.

## Cấu hình AI linh hoạt

Project có hai profile AI. Chỉ chọn profile qua environment, không cần sửa code hoặc `application.yml`.

- **Cloud (mặc định):** đặt `SPRING_PROFILES_ACTIVE=cloud`; khai báo `OPENAI_BASE_URL`, `OPENAI_API_KEY`, `OPENAI_CHAT_MODEL`, `OPENAI_EMBEDDING_MODEL`. Dimension Pgvector mặc định là `3072`; đổi `EMBEDDING_DIMENSIONS` nếu embedding model dùng dimension khác.
- **Local (Ollama):** đặt `SPRING_PROFILES_ACTIVE=local`; khai báo tùy chọn `OLLAMA_BASE_URL` (mặc định `http://localhost:11434`), `OLLAMA_CHAT_MODEL` (mặc định `qwen2.5:7b`) và `OLLAMA_EMBEDDING_MODEL` (mặc định `nomic-embed-text`). Dimension Pgvector mặc định là `768`.

Trước khi chạy local, cài Ollama và tải model tương ứng, ví dụ `ollama pull qwen2.5:7b` và `ollama pull nomic-embed-text`. Khi đổi embedding model hoặc đổi giữa cloud/local, dùng `PGVECTOR_TABLE_NAME` khác hoặc đảm bảo `EMBEDDING_DIMENSIONS` khớp dimension của model để tránh lỗi vector store.
"# HCM_KS24_HuynhlaTienLoc_001" 
