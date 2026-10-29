package tz.co.halotel.halokazi.vacancy.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "region")
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "region_id")
    private Long regionId;

    @Column(name = "code", nullable = false, length = 30)
    private String code;

    @Column(name = "name_en", nullable = false, length = 100)
    private String nameEn;

    @Column(name = "name_sw", nullable = false, length = 100)
    private String nameSw;

    @Column(name = "zone", nullable = false, length = 20)
    private String zone;

    @Column(name = "is_active", nullable = false)
    private Integer isActive;

    public Region() {
    }

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
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

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }
}
