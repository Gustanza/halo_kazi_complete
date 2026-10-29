package tz.co.halotel.halokazi.common.util;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/** Null-safe column readers for JdbcClient row mappers. Timestamps are returned in UTC. */
public final class SqlRows {

    private SqlRows() {
    }

    public static OffsetDateTime utc(ResultSet rs, String column) throws SQLException {
        OffsetDateTime value = rs.getObject(column, OffsetDateTime.class);
        return value == null ? null : value.withOffsetSameInstant(ZoneOffset.UTC);
    }

    public static BigDecimal decimal(ResultSet rs, String column) throws SQLException {
        return rs.getBigDecimal(column);
    }

    public static Integer integer(ResultSet rs, String column) throws SQLException {
        int value = rs.getInt(column);
        return rs.wasNull() ? null : value;
    }
}
