package ru.tournament.fsp_sevastopol.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;
import ru.tournament.fsp_sevastopol.config.KeycloakJwtAuthenticationConverter;
import ru.tournament.fsp_sevastopol.config.SecurityConfig;
import ru.tournament.fsp_sevastopol.dto.auth.CurrentUserResponseDto;
import ru.tournament.fsp_sevastopol.service.CurrentUserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, KeycloakJwtAuthenticationConverter.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CurrentUserService currentUserService;

    @MockBean
    private JwtDecoder jwtDecoder;

    @Test
    void meShouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void meShouldReturnProvisionedLocalUser() throws Exception {
        when(currentUserService.getCurrentUser(any())).thenReturn(
                CurrentUserResponseDto.builder()
                        .userId(5L)
                        .subjectId("kc-123")
                        .username("alice")
                        .email("alice@example.com")
                        .avatarUrl("/images/avatars/default.png")
                        .currentRating(0)
                        .roles(List.of("ROLE_USER", "ROLE_ADMIN"))
                        .build()
        );

        mockMvc.perform(get("/api/auth/me")
                        .with(jwt().jwt(jwt -> jwt
                                .subject("kc-123")
                                .claim("preferred_username", "alice")
                                .claim("email", "alice@example.com")
                                .claim("realm_access", java.util.Map.of("roles", List.of("user", "admin"))))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(5))
                .andExpect(jsonPath("$.subjectId").value("kc-123"))
                .andExpect(jsonPath("$.username").value("alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.avatarUrl").value("/images/avatars/default.png"))
                .andExpect(jsonPath("$.currentRating").value(0))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_USER"));
    }
}
