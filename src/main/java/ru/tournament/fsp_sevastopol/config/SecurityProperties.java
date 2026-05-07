package ru.tournament.fsp_sevastopol.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security")
public record SecurityProperties(
        String realmAccessClaim,
        String rolesClaim,
        String rolePrefix
) {
}
