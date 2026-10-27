package tz.co.halotel.halokazi.settings;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import tz.co.halotel.halokazi.common.config.HalokaziProperties;

/**
 * Typed access to SYSTEM_SETTING. The whole table is small, so it is loaded in one query and
 * cached for {@code halokazi.settings.cache-ttl}; admin changes apply within that delay.
 */
@Service
public class SettingsService {

    private static final Logger log = LoggerFactory.getLogger(SettingsService.class);

    private final SystemSettingRepository repository;
    private final Clock clock;
    private final Duration cacheTtl;

    private volatile Snapshot snapshot;

    public SettingsService(SystemSettingRepository repository, Clock clock, HalokaziProperties properties) {
        this.repository = repository;
        this.clock = clock;
        this.cacheTtl = properties.settings().cacheTtl();
    }

    public int getInt(Setting setting) {
        String raw = values().get(setting.name());
        if (raw == null) {
            return setting.defaultValue();
        }
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            log.warn("SYSTEM_SETTING {}='{}' is not a number; using default {}", setting, raw, setting.defaultValue());
            return setting.defaultValue();
        }
    }

    public Duration getSeconds(Setting setting) {
        return Duration.ofSeconds(getInt(setting));
    }

    public Duration getMinutes(Setting setting) {
        return Duration.ofMinutes(getInt(setting));
    }

    private Map<String, String> values() {
        Snapshot current = snapshot;
        Instant now = clock.instant();
        if (current == null || current.loadedAt().plus(cacheTtl).isBefore(now)) {
            Map<String, String> loaded = repository.findAll().stream()
                    .collect(Collectors.toUnmodifiableMap(SystemSetting::getKey, SystemSetting::getValue));
            current = new Snapshot(loaded, now);
            snapshot = current;
        }
        return current.values();
    }

    private record Snapshot(Map<String, String> values, Instant loadedAt) {
    }
}
