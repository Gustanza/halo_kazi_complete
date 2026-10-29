package tz.co.halotel.halokazi.jobseeker.dto.cv;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

/**
 * Request to update the personal information section of a CV.
 */
public class CvPersonalInfoRequest {

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must be at most 100 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must be at most 100 characters")
    private String lastName;

    @NotBlank(message = "Contact email is required")
    @Email(message = "Contact email must be a valid email address")
    @Size(max = 254, message = "Contact email must be at most 254 characters")
    private String contactEmail;

    @Size(max = 20, message = "Phone must be at most 20 characters")
    private String phone;

    @Size(max = 20, message = "Alternate phone must be at most 20 characters")
    private String alternatePhone;

    @Size(max = 150, message = "Headline must be at most 150 characters")
    private String headline;

    @Size(max = 4000, message = "Professional summary must be at most 4000 characters")
    private String professionalSummary;

    @Size(max = 100, message = "Nationality must be at most 100 characters")
    private String nationality;

    @Size(max = 150, message = "Current location must be at most 150 characters")
    private String currentLocation;

    @Size(max = 100, message = "Region name must be at most 100 characters")
    private String regionName;

    @Size(max = 100, message = "District name must be at most 100 characters")
    private String districtName;

    @Size(max = 300, message = "Address line must be at most 300 characters")
    private String addressLine;

    @Size(max = 500, message = "Profile picture URL must be at most 500 characters")
    private String profilePictureUrl;

    @Size(max = 10, message = "Date of birth must be at most 10 characters")
    @Pattern(regexp = "^$|\\d{4}-\\d{2}-\\d{2}", message = "Date of birth must use YYYY-MM-DD")
    private String dateOfBirth;

    @Size(max = 20, message = "Gender must be at most 20 characters")
    private String gender;

    public CvPersonalInfoRequest() {}

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName != null ? firstName.trim() : null; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName != null ? lastName.trim() : null; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail != null ? contactEmail.trim() : null; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone != null ? phone.trim() : null; }

    public String getAlternatePhone() { return alternatePhone; }
    public void setAlternatePhone(String alternatePhone) { this.alternatePhone = alternatePhone != null ? alternatePhone.trim() : null; }

    public String getHeadline() { return headline; }
    public void setHeadline(String headline) { this.headline = headline != null ? headline.trim() : null; }

    public String getProfessionalSummary() { return professionalSummary; }
    public void setProfessionalSummary(String professionalSummary) { this.professionalSummary = professionalSummary != null ? professionalSummary.trim() : null; }

    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality != null ? nationality.trim() : null; }

    public String getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(String currentLocation) { this.currentLocation = currentLocation != null ? currentLocation.trim() : null; }

    public String getRegionName() { return regionName; }
    public void setRegionName(String regionName) { this.regionName = regionName != null ? regionName.trim() : null; }

    public String getDistrictName() { return districtName; }
    public void setDistrictName(String districtName) { this.districtName = districtName != null ? districtName.trim() : null; }

    public String getAddressLine() { return addressLine; }
    public void setAddressLine(String addressLine) { this.addressLine = addressLine != null ? addressLine.trim() : null; }

    public String getProfilePictureUrl() { return profilePictureUrl; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl != null ? profilePictureUrl.trim() : null; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth != null ? dateOfBirth.trim() : null; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender != null ? gender.trim() : null; }
}
