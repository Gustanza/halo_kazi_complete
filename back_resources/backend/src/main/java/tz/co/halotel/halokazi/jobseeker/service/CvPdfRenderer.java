package tz.co.halotel.halokazi.jobseeker.service;

import org.springframework.stereotype.Component;
import tz.co.halotel.halokazi.jobseeker.model.*;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Small dependency-free PDF renderer for the current CV snapshot model. */
@Component
public class CvPdfRenderer {

    public byte[] render(Cv cv) {
        List<String> lines = makeLines(cv);
        List<List<String>> pages = new ArrayList<>();
        for (int from = 0; from < lines.size(); from += 48) {
            pages.add(lines.subList(from, Math.min(lines.size(), from + 48)));
        }
        if (pages.isEmpty()) pages.add(List.of("Curriculum Vitae"));

        int fontId = 3 + pages.size() * 2;
        Map<Integer, byte[]> objects = new LinkedHashMap<>();
        objects.put(1, ascii("<< /Type /Catalog /Pages 2 0 R >>"));
        StringBuilder kids = new StringBuilder();
        for (int i = 0; i < pages.size(); i++) {
            int contentId = 3 + i * 2;
            int pageId = contentId + 1;
            kids.append(pageId).append(" 0 R ");
            objects.put(contentId, stream(contentFor(pages.get(i))));
            objects.put(pageId, ascii("<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] /Resources << /Font << /F1 "
                    + fontId + " 0 R >> >> /Contents " + contentId + " 0 R >>"));
        }
        objects.put(2, ascii("<< /Type /Pages /Kids [" + kids + "] /Count " + pages.size() + " >>"));
        objects.put(fontId, ascii("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>"));
        return assemble(objects);
    }

    private List<String> makeLines(Cv cv) {
        List<String> lines = new ArrayList<>();
        lines.add(cv.getCvName() == null ? "Curriculum Vitae" : cv.getCvName());
        CvPersonalInfo p = cv.getPersonalInfo();
        if (p != null) {
            add(lines, join(p.getFirstName(), p.getLastName()));
            add(lines, p.getHeadline());
            add(lines, p.getContactEmail());
            add(lines, p.getPhone());
            add(lines, p.getCurrentLocation());
            add(lines, p.getProfessionalSummary());
        }
        addSection(lines, "Work Experience");
        if (cv.getExperiences() != null) for (CvExperience e : cv.getExperiences()) {
            add(lines, join(e.getJobTitle(), e.getCompanyName()));
            add(lines, join(date(e.getStartDate()), e.isCurrent() ? "Present" : date(e.getEndDate())));
            add(lines, e.getDescription());
        }
        addSection(lines, "Education");
        if (cv.getEducations() != null) for (CvEducation e : cv.getEducations()) {
            add(lines, join(e.getQualification(), e.getInstitutionName()));
            add(lines, e.getFieldOfStudy());
        }
        addSection(lines, "Skills");
        if (cv.getSkills() != null) for (CvSkill s : cv.getSkills()) add(lines, join(s.getSkillName(), s.getProficiency()));
        addSection(lines, "Certifications");
        if (cv.getCertifications() != null) for (CvCertification c : cv.getCertifications()) add(lines, join(c.getCertificationName(), c.getIssuingOrganization()));
        addSection(lines, "Languages");
        if (cv.getLanguages() != null) for (CvLanguageEntry l : cv.getLanguages()) add(lines, join(l.getLanguageName(), l.getProficiency()));
        addSection(lines, "Projects");
        if (cv.getProjects() != null) for (CvProject pjt : cv.getProjects()) {
            add(lines, join(pjt.getTitle(), pjt.getRole()));
            add(lines, pjt.getDescription());
        }
        addSection(lines, "References");
        if (cv.getReferences() != null) for (CvReference r : cv.getReferences()) add(lines, join(r.getFullName(), r.getOrganization()));
        return lines;
    }

    private void addSection(List<String> lines, String title) {
        if (lines.size() > 1) lines.add("");
        lines.add(title.toUpperCase());
    }

    private void add(List<String> lines, String value) {
        if (value == null || value.isBlank()) return;
        String safe = value.replace('\r', ' ').replace('\n', ' ');
        StringBuilder current = new StringBuilder();
        for (String word : safe.split("\\s+")) {
            String asciiWord = toAscii(word);
            if (current.length() > 0 && current.length() + asciiWord.length() + 1 > 88) {
                lines.add(current.toString());
                current.setLength(0);
            }
            if (current.length() > 0) current.append(' ');
            current.append(asciiWord);
        }
        if (current.length() > 0) lines.add(current.toString());
    }

    private String date(java.time.LocalDate date) {
        return date == null ? "" : date.toString();
    }

    private String join(String first, String second) {
        if (first == null || first.isBlank()) return second == null ? "" : second;
        if (second == null || second.isBlank()) return first;
        return first + " | " + second;
    }

    private String toAscii(String input) {
        StringBuilder result = new StringBuilder(input.length());
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            result.append(c >= 32 && c <= 126 ? c : '?');
        }
        return result.toString();
    }

    private byte[] contentFor(List<String> lines) {
        StringBuilder content = new StringBuilder("BT\n/F1 10 Tf\n50 744 Td\n14 TL\n");
        for (String line : lines) content.append('(').append(escape(line)).append(") Tj\nT*\n");
        content.append("ET\n");
        return content.toString().getBytes(StandardCharsets.US_ASCII);
    }

    private String escape(String text) {
        return text.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
    }

    private byte[] stream(byte[] bytes) {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        result.writeBytes(ascii("<< /Length " + bytes.length + " >>\nstream\n"));
        result.writeBytes(bytes);
        result.writeBytes(ascii("endstream"));
        return result.toByteArray();
    }

    private byte[] assemble(Map<Integer, byte[]> objects) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        output.writeBytes(ascii("%PDF-1.4\n%CV\n"));
        int maxId = objects.keySet().stream().mapToInt(Integer::intValue).max().orElse(0);
        long[] offsets = new long[maxId + 1];
        for (int id = 1; id <= maxId; id++) {
            offsets[id] = output.size();
            output.writeBytes(ascii(id + " 0 obj\n"));
            output.writeBytes(objects.get(id));
            output.writeBytes(ascii("\nendobj\n"));
        }
        long xref = output.size();
        output.writeBytes(ascii("xref\n0 " + (maxId + 1) + "\n0000000000 65535 f \n"));
        for (int id = 1; id <= maxId; id++) {
            output.writeBytes(ascii(String.format(java.util.Locale.ROOT, "%010d 00000 n \n", offsets[id])));
        }
        output.writeBytes(ascii("trailer\n<< /Size " + (maxId + 1) + " /Root 1 0 R >>\nstartxref\n" + xref + "\n%%EOF\n"));
        return output.toByteArray();
    }

    private byte[] ascii(String text) {
        return text.getBytes(StandardCharsets.US_ASCII);
    }
}
