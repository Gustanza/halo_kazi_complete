package tz.co.halotel.halokazi.common.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DatabaseTestController {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseTestController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/db-test")
    public ResponseEntity<Map<String, Object>> testDatabaseConnection() {
        try {
            Integer result = jdbcTemplate.queryForObject("SELECT 1 FROM DUAL", Integer.class);
            if (result != null && result == 1) {
                return ResponseEntity.ok(Map.of("status", "connected"));
            } else {
                Map<String, Object> err = new HashMap<>();
                err.put("status", "error");
                err.put("message", "Unexpected query result: " + result);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
            }
        } catch (Exception ex) {
            Map<String, Object> err = new HashMap<>();
            err.put("status", "error");
            err.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        }
    }
}
