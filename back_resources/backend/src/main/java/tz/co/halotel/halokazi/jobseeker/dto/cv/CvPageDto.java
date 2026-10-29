package tz.co.halotel.halokazi.jobseeker.dto.cv;

import java.util.List;

/** Paginated, metadata-only result for the My CVs screen. */
public class CvPageDto {
    private List<CvSummaryDto> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    public CvPageDto() {}

    public CvPageDto(List<CvSummaryDto> content, int page, int size, long totalElements, int totalPages) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }

    public List<CvSummaryDto> getContent() { return content; }
    public void setContent(List<CvSummaryDto> content) { this.content = content; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
    public long getTotalElements() { return totalElements; }
    public void setTotalElements(long totalElements) { this.totalElements = totalElements; }
    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
}
