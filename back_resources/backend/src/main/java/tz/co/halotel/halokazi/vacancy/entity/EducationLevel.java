package tz.co.halotel.halokazi.vacancy.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "education_level")
public class EducationLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "education_level_id")
    private Long educationLevelId;

    @Column(name = "code", nullable = false, length = 30)
    private String code;

    @Column(name = "name_en", nullable = false, length = 80)
    private String nameEn;

    @Column(name = "name_sw", nullable = false, length = 80)
    private String nameSw;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "is_active", nullable = false)
    private Integer isActive;

    public EducationLevel() {
    }

    public Long getEducationLevelId() {
        return educationLevelId;
    }

    public void setEducationLevelId(Long educationLevelId) {
        this.educationLevelId = educationLevelId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getNameSw() {
        return nameSw;
    }

    public void setNameSw(String nameSw) {
        this.nameSw = nameSw;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }
}
