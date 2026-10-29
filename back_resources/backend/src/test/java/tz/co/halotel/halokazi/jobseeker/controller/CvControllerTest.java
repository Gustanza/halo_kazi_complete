package tz.co.halotel.halokazi.jobseeker.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import tz.co.halotel.halokazi.common.exception.GlobalExceptionHandler;
import tz.co.halotel.halokazi.common.exception.ResourceNotFoundException;
import tz.co.halotel.halokazi.jobseeker.dto.cv.CvTemplateDto;
import tz.co.halotel.halokazi.jobseeker.service.CvPdfRenderer;
import tz.co.halotel.halokazi.jobseeker.service.CvService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CvControllerTest {

    @Mock private CvService cvService;
    @Mock private CvPdfRenderer pdfRenderer;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mvc = MockMvcBuilders.standaloneSetup(new CvController(cvService, pdfRenderer))
                .setValidator(validator)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void templatesRouteReturnsApiResponse() throws Exception {
        when(cvService.getTemplates()).thenReturn(List.of(new CvTemplateDto()));

        mvc.perform(get("/api/v1/jobseeker/cvs/templates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    void invalidPersonalInfoBodyReturns400InApiResponseFormat() throws Exception {
        mvc.perform(put("/api/v1/jobseeker/cvs/3/personal-info")
                        .header("If-Match", "1")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data.firstName").exists());
        verifyNoInteractions(cvService);
    }

    @Test
    void missingCvUses404ApiResponse() throws Exception {
        when(cvService.getCv(999L)).thenThrow(new ResourceNotFoundException("CV not found"));

        mvc.perform(get("/api/v1/jobseeker/cvs/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("CV not found"));
    }

    @Test
    void missingIfMatchIs400NotA500() throws Exception {
        mvc.perform(put("/api/v1/jobseeker/cvs/3/title")
                        .contentType("application/json")
                        .content("{\"title\":\"Updated title\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}
