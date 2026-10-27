package tz.co.halotel.halokazi.employer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** EMPLOYER - only what registration and /me need; other columns keep their database defaults. */
@Entity
@Table(name = "employer")
public class Employer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employer_id")
    private Long id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    @Column(name = "business_name", nullable = false)
    private String businessName;

    @Column(name = "company_email")
    private String companyEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false)
    private EmployerVerificationStatus verificationStatus;

    protected Employer() {
    }

    /** Employers may register before verification (AUTH-07); TIN/licence come later. */
    public static Employer register(Long userId, String businessName, String companyEmail) {
        Employer employer = new Employer();
        employer.userId = userId;
        employer.businessName = businessName;
        employer.companyEmail = companyEmail;
        employer.verificationStatus = EmployerVerificationStatus.NOT_SUBMITTED;
        return employer;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getBusinessName() {
        return businessName;
    }

    public EmployerVerificationStatus getVerificationStatus() {
        return verificationStatus;
    }
}
