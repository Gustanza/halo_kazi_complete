package tz.co.halotel.halokazi.common.web;

import tz.co.halotel.halokazi.common.error.ApiException;
import tz.co.halotel.halokazi.common.error.ErrorCode;

/** Validated paging request: 1-based page, bounded size. */
public record PageQuery(int page, int size) {

    public static final int DEFAULT_SIZE = 10;
    public static final int MAX_SIZE = 50;

    public static PageQuery of(Integer page, Integer size) {
        int p = page == null ? 1 : page;
        int s = size == null ? DEFAULT_SIZE : size;
        if (p < 1) {
            throw new ApiException(ErrorCode.VALIDATION_FAILED).field("page", "Must be 1 or greater.");
        }
        if (s < 1 || s > MAX_SIZE) {
            throw new ApiException(ErrorCode.VALIDATION_FAILED)
                    .field("size", "Must be between 1 and " + MAX_SIZE + ".");
        }
        return new PageQuery(p, s);
    }

    public long offset() {
        return (long) (page - 1) * size;
    }
}
