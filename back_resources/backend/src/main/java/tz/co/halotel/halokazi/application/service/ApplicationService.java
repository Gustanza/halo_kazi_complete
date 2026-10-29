package tz.co.halotel.halokazi.application.service;

import tz.co.halotel.halokazi.application.dto.ApplicationDetailDto;
import tz.co.halotel.halokazi.application.dto.ApplicationSubmitRequest;
import tz.co.halotel.halokazi.application.dto.ApplicationSummaryDto;
import tz.co.halotel.halokazi.common.api.PageResponse;

public interface ApplicationService {

    PageResponse<ApplicationSummaryDto> getMyApplications(Long profileId, int page, int size);

    ApplicationDetailDto getApplicationById(Long applicationId, Long profileId);

    ApplicationSummaryDto submitApplication(Long profileId, Long userId, ApplicationSubmitRequest request);

    boolean checkHasApplied(Long profileId, Long vacancyId);
}
