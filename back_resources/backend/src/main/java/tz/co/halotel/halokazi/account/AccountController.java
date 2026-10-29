package tz.co.halotel.halokazi.account;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import tz.co.halotel.halokazi.account.SettingsDtos.ChangePasswordRequest;
import tz.co.halotel.halokazi.account.SettingsDtos.LanguageRequest;
import tz.co.halotel.halokazi.account.SettingsDtos.NotificationsRequest;
import tz.co.halotel.halokazi.account.SettingsDtos.PrivacyRequest;
import tz.co.halotel.halokazi.account.SettingsDtos.SettingsView;
import tz.co.halotel.halokazi.common.web.ClientInfo;
import tz.co.halotel.halokazi.session.AuthenticatedUser;
import tz.co.halotel.halokazi.session.CurrentUser;

/** The signed-in user's own account. Every route needs a session (see SessionWebConfig). */
@RestController
@RequestMapping("/api/me")
class AccountController {

    private final AccountSettingsService settings;

    AccountController(AccountSettingsService settings) {
        this.settings = settings;
    }

    @GetMapping("/settings")
    SettingsView settings(@CurrentUser AuthenticatedUser user) {
        return settings.settings(user);
    }

    @PutMapping("/settings/notifications")
    SettingsView notifications(@CurrentUser AuthenticatedUser user, @Valid @RequestBody NotificationsRequest request,
            ClientInfo client) {
        return settings.updateNotifications(user, request.toChoice(), client);
    }

    @PutMapping("/settings/privacy")
    SettingsView privacy(@CurrentUser AuthenticatedUser user, @Valid @RequestBody PrivacyRequest request,
            ClientInfo client) {
        return settings.updatePrivacy(user, request.toChoice(), client);
    }

    @PutMapping("/settings/language")
    SettingsView language(@CurrentUser AuthenticatedUser user, @Valid @RequestBody LanguageRequest request,
            ClientInfo client) {
        return settings.updateLanguage(user, request.language(), client);
    }

    @PostMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void changePassword(@CurrentUser AuthenticatedUser user, @Valid @RequestBody ChangePasswordRequest request,
            ClientInfo client) {
        settings.changePassword(user, request, client);
    }
}
