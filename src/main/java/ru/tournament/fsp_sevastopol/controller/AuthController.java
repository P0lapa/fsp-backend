package ru.tournament.fsp_sevastopol.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tournament.fsp_sevastopol.dto.auth.CurrentUserResponseDto;
import ru.tournament.fsp_sevastopol.service.CurrentUserService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "API для текущего авторизованного пользователя")
public class AuthController {

    private final CurrentUserService currentUserService;

    @GetMapping("/me")
    @Operation(
            summary = "Получить текущего пользователя",
            description = "Возвращает локального пользователя и базовые данные из JWT",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public CurrentUserResponseDto getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        return currentUserService.getCurrentUser(jwt);
    }
}
