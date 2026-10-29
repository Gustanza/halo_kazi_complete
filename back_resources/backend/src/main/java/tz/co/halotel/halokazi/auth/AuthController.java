package tz.co.halotel.halokazi.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import tz.co.halotel.halokazi.auth.dto.AuthResponse;
import tz.co.halotel.halokazi.auth.dto.ForgotPasswordRequest;
import tz.co.halotel.halokazi.auth.dto.LoginRequest;
import tz.co.halotel.halokazi.auth.dto.RegisterRequest;
import tz.co.halotel.halokazi.auth.dto.RegisterResponse;
import tz.co.halotel.halokazi.auth.dto.ResendOtpRequest;
import tz.co.halotel.halokazi.auth.dto.ResendOtpResponse;
import tz.co.halotel.halokazi.auth.dto.ResetPasswordRequest;
import tz.co.halotel.halokazi.auth.dto.UserView;
import tz.co.halotel.halokazi.auth.dto.VerifyOtpRequest;
import tz.co.halotel.halokazi.common.web.ClientInfo;
import tz.co.halotel.halokazi.session.AuthenticatedUser;
import tz.co.halotel.halokazi.session.CurrentUser;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RegistrationService registration;
    private final AuthService auth;
    private final PasswordResetService passwordReset;

    public AuthController(RegistrationService registration, AuthService auth, PasswordResetService passwordReset) {
        this.registration = registration;
        this.auth = auth;
        this.passwordReset = passwordReset;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterResponse register(@Valid @RequestBody RegisterRequest request, ClientInfo client) {
        return registration.register(request, client);
    }

    @PostMapping("/otp/verify")
    public AuthResponse verifyOtp(@Valid @RequestBody VerifyOtpRequest request, ClientInfo client) {
        return registration.verifyPhone(request, client);
    }

    @PostMapping("/otp/resend")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResendOtpResponse resendOtp(@Valid @RequestBody ResendOtpRequest request, ClientInfo client) {
        return registration.resendVerificationCode(request, client);
    }

    @PostMapping("/password/forgot")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResendOtpResponse forgotPassword(@Valid @RequestBody ForgotPasswordRequest request, ClientInfo client) {
        return passwordReset.requestReset(request, client);
    }

    @PostMapping("/password/reset")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetPassword(@Valid @RequestBody ResetPasswordRequest request, ClientInfo client) {
        passwordReset.resetPassword(request, client);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request, ClientInfo client) {
        return auth.login(request, client);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@CurrentUser AuthenticatedUser user, ClientInfo client) {
        auth.logout(user, client);
    }

    @GetMapping("/me")
    public UserView me(@CurrentUser AuthenticatedUser user) {
        return auth.currentUser(user);
    }
}
