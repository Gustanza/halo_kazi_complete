package tz.co.halotel.halokazi.cv;

import java.util.List;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** The CV templates offered by the CV builder (CV-02). */
@RestController
@RequestMapping("/api/public/cv-templates")
class CvTemplateController {

    /**
     * {@code scheme} ("light" | "dark") and {@code accent} (hex colour) drive the template thumbnail.
     * They live in layout_definition under "preview" until real template previews are stored as files.
     */
    record CvTemplate(Long id, String code, String nameEn, String nameSw, String descriptionEn,
            String descriptionSw, String scheme, String accent) {
    }

    private final JdbcClient jdbc;

    CvTemplateController(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping
    List<CvTemplate> active() {
        return jdbc.sql("""
                SELECT template_id, code, name_en, name_sw, description_en, description_sw,
                       JSON_VALUE(layout_definition, '$.preview.scheme') scheme,
                       JSON_VALUE(layout_definition, '$.preview.accent') accent
                  FROM cv_template
                 WHERE status = 'ACTIVE'
                 ORDER BY sort_order, template_id
                """)
                .query((rs, n) -> new CvTemplate(rs.getLong("template_id"), rs.getString("code"),
                        rs.getString("name_en"), rs.getString("name_sw"), rs.getString("description_en"),
                        rs.getString("description_sw"), rs.getString("scheme"), rs.getString("accent")))
                .list();
    }
}
