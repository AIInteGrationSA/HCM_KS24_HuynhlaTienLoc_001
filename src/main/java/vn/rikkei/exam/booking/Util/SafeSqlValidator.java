package vn.rikkei.exam.booking.Util;

import org.springframework.stereotype.Component;

@Component
public class SafeSqlValidator {

    public String validateAndEnforceSelectOnly(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            throw new IllegalArgumentException("SQL query cannot be empty");
        }

        String upperSql = sql.trim().toUpperCase();

        // Zero Trust: Chỉ cho phép lệnh SELECT
        if (!upperSql.startsWith("SELECT")) {
            throw new IllegalArgumentException("Chỉ cho phép thực thi câu lệnh SELECT.");
        }

        // Chống phá hoại
        if (upperSql.contains("DROP") || upperSql.contains("DELETE") ||
                upperSql.contains("UPDATE") || upperSql.contains("INSERT") ||
                upperSql.contains("TRUNCATE") || upperSql.contains("ALTER")) {
            throw new IllegalArgumentException("Phát hiện từ khóa cấm trong câu lệnh SQL.");
        }

        // Auto LIMIT 100 nếu chưa có
        if (!upperSql.contains("LIMIT")) {
            return sql + " LIMIT 100";
        }

        return sql;
    }
}
