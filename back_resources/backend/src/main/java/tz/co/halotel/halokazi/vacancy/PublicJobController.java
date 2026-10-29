package tz.co.halotel.halokazi.vacancy;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tz.co.halotel.halokazi.common.error.ApiException;
import tz.co.halotel.halokazi.common.error.ErrorCode;
import tz.co.halotel.halokazi.common.web.PageQuery;
import tz.co.halotel.halokazi.common.web.PageResponse;
import tz.co.halotel.halokazi.vacancy.JobViews.JobDetail;
import tz.co.halotel.halokazi.vacancy.JobViews.JobSummary;

/** Public job board: open vacancies only, no login needed. */
@RestController
@RequestMapping("/api/public/jobs")
class PublicJobController {

    private final JobQueries jobs;

    PublicJobController(JobQueries jobs) {
        this.jobs = jobs;
    }

    @GetMapping
    PageResponse<JobSummary> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String experience,
            @RequestParam(required = false) String salary,
            @RequestParam(required = false) String posted,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        return jobs.search(JobSearch.of(q, region, type, category, experience, salary, posted),
                PageQuery.of(page, size));
    }

    @GetMapping("/{id}")
    JobDetail get(@PathVariable long id) {
        return jobs.findOpen(id).orElseThrow(() -> new ApiException(ErrorCode.JOB_NOT_FOUND));
    }
}
