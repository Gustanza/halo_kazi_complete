package tz.co.halotel.halokazi.common.web;

import java.util.List;

/** One page of results. {@code page} is 1-based, matching what the UI shows. */
public record PageResponse<T>(List<T> items, int page, int size, long totalItems, int totalPages) {

    public static <T> PageResponse<T> of(List<T> items, PageQuery query, long totalItems) {
        int totalPages = (int) ((totalItems + query.size() - 1) / query.size());
        return new PageResponse<>(items, query.page(), query.size(), totalItems, totalPages);
    }
}
