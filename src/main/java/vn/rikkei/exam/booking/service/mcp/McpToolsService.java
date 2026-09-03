package vn.rikkei.exam.booking.service.mcp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.ToolCallback;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.springframework.context.annotation.Description;
import vn.rikkei.exam.booking.Util.SafeSqlValidator;

@Configuration
public class McpToolsService {

    private final JdbcTemplate jdbcTemplate;
    private final SafeSqlValidator sqlValidator;

    public McpToolsService(JdbcTemplate jdbcTemplate, SafeSqlValidator sqlValidator) {
        this.jdbcTemplate = jdbcTemplate;
        this.sqlValidator = sqlValidator;
    }

    public record SqlQueryRequest(String query) {}

    @Bean
    @Description("Thực thi câu lệnh SQL SELECT để phân tích dữ liệu logistic (deliveries, incidents). Hệ thống tự động giới hạn 100 dòng.")
    public Function<SqlQueryRequest, String> executeAnalyticsQuery() {
        return request -> {
            try {
                String safeSql = sqlValidator.validateAndEnforceSelectOnly(request.query());
                List<Map<String, Object>> results = jdbcTemplate.queryForList(safeSql);

                if (results.isEmpty()) {
                    return "Không có dữ liệu phù hợp.";
                }
                return results.toString();
            } catch (Exception e) {
                return "Lỗi thực thi truy vấn: " + e.getMessage();
            }
        };
    }
}
