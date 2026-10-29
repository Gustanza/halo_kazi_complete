package tz.co.halotel.halokazi.vacancy.repository;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import tz.co.halotel.halokazi.vacancy.dto.VacancyFilterParams;
import tz.co.halotel.halokazi.vacancy.entity.Vacancy;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class VacancySpecification {

    public static Specification<Vacancy> filterOpenVacancies(VacancyFilterParams params) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Rule 1: Must be ACTIVE
            predicates.add(cb.equal(root.get("status"), "ACTIVE"));

            // Rule 2: Deadline must be in the future
            predicates.add(cb.greaterThan(root.get("applicationDeadline"), OffsetDateTime.now()));

            // Rule 3: Must not be soft-deleted
            predicates.add(cb.isNull(root.get("deletedAt")));

            // Rule 4: Employer must be APPROVED
            predicates.add(cb.equal(root.join("employer").get("verificationStatus"), "APPROVED"));

            // Filter: Keyword search
            if (params.getKeyword() != null && !params.getKeyword().trim().isEmpty()) {
                String kw = "%" + params.getKeyword().trim().toLowerCase() + "%";
                Predicate titleMatch = cb.like(cb.lower(root.get("jobTitle")), kw);
                Predicate expMatch = cb.like(cb.lower(root.get("workExperience")), kw);
                Predicate locMatch = cb.like(cb.lower(root.get("locationDetails")), kw);
                predicates.add(cb.or(titleMatch, expMatch, locMatch));
            }

            // Filter: Category
            if (params.getCategoryId() != null) {
                predicates.add(cb.equal(root.get("category").get("categoryId"), params.getCategoryId()));
            }

            // Filter: Region
            if (params.getRegionId() != null) {
                predicates.add(cb.equal(root.get("region").get("regionId"), params.getRegionId()));
            }

            // Filter: Employment Type
            if (params.getEmploymentTypeId() != null) {
                predicates.add(cb.equal(root.get("employmentType").get("employmentTypeId"), params.getEmploymentTypeId()));
            }

            // Filter: Max minExperienceYears
            if (params.getMinExperienceYears() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("minExperienceYears"), params.getMinExperienceYears()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
