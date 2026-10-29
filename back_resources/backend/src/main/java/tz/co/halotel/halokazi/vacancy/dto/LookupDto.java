package tz.co.halotel.halokazi.vacancy.dto;

public class LookupDto {

    private Long id;
    private String code;
    private String nameEn;
    private String nameSw;

    public LookupDto() {
    }

    public LookupDto(Long id, String code, String nameEn, String nameSw) {
        this.id = id;
        this.code = code;
        this.nameEn = nameEn;
        this.nameSw = nameSw;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
