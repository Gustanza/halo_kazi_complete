package tz.co.halotel.halokazi.jobseeker.dto;

import java.util.ArrayList;
import java.util.List;

public class ProfileStrengthDto {
    private double overallPercentage;
    private String statusLabel; // e.g. "Good", "Excellent", "Needs Improvement"
    private List<SectionScoreDto> sections = new ArrayList<>();
    private List<String> missingRecommendations = new ArrayList<>();

    public ProfileStrengthDto() {}

    public ProfileStrengthDto(double overallPercentage, String statusLabel) {
        this.overallPercentage = overallPercentage;
        this.statusLabel = statusLabel;
    }

    public double getOverallPercentage() {
        return overallPercentage;
    }

    public void setOverallPercentage(double overallPercentage) {
        this.overallPercentage = overallPercentage;
    }

    public String getStatusLabel() {
        return statusLabel;
    }

    public void setStatusLabel(String statusLabel) {
        this.statusLabel = statusLabel;
    }

    public List<SectionScoreDto> getSections() {
        return sections;
    }

    public void setSections(List<SectionScoreDto> sections) {
        this.sections = sections;
    }

    public List<String> getMissingRecommendations() {
        return missingRecommendations;
    }

    public void setMissingRecommendations(List<String> missingRecommendations) {
        this.missingRecommendations = missingRecommendations;
    }

    public static class SectionScoreDto {
        private String sectionCode;
        private String sectionName;
        private double weight;
        private boolean completed;
        private boolean notApplicable;

        public SectionScoreDto() {}

        public SectionScoreDto(String sectionCode, String sectionName, double weight, boolean completed, boolean notApplicable) {
            this.sectionCode = sectionCode;
            this.sectionName = sectionName;
            this.weight = weight;
            this.completed = completed;
            this.notApplicable = notApplicable;
        }

        public String getSectionCode() {
            return sectionCode;
        }

        public void setSectionCode(String sectionCode) {
            this.sectionCode = sectionCode;
        }

        public String getSectionName() {
            return sectionName;
        }

        public void setSectionName(String sectionName) {
            this.sectionName = sectionName;
        }

        public double getWeight() {
            return weight;
        }

        public void setWeight(double weight) {
            this.weight = weight;
        }

        public boolean isCompleted() {
            return completed;
        }

        public void setCompleted(boolean completed) {
            this.completed = completed;
        }

        public boolean isNotApplicable() {
            return notApplicable;
        }

        public void setNotApplicable(boolean notApplicable) {
            this.notApplicable = notApplicable;
        }
    }
}
