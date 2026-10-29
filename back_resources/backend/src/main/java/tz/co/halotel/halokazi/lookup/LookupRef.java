package tz.co.halotel.halokazi.lookup;

/** A managed list value (region, employment type, category...) as clients see it: stable code + both languages. */
public record LookupRef(String code, String nameEn, String nameSw) {

    /** Null when the joined lookup row is absent (optional columns such as category). */
    public static LookupRef ofNullable(String code, String nameEn, String nameSw) {
        return code == null ? null : new LookupRef(code, nameEn, nameSw);
    }
}
