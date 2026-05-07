package ru.tournament.fsp_sevastopol.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;
import ru.tournament.fsp_sevastopol.dto.contest.ContestFullResponseDto;
import ru.tournament.fsp_sevastopol.dto.contest.ContestShortResponseDto;
import ru.tournament.fsp_sevastopol.entity.UserEntity;
import ru.tournament.fsp_sevastopol.enums.ContestLevelEnum;
import ru.tournament.fsp_sevastopol.enums.ProgrammingLanguageEnum;
import ru.tournament.fsp_sevastopol.repository.UserRepository;
import ru.tournament.fsp_sevastopol.service.ContestService;
import ru.tournament.fsp_sevastopol.service.UserProvisioningService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=" +
                "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration," +
                "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration," +
                "org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration"
})
@AutoConfigureMockMvc
class ContestSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContestService contestService;

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserProvisioningService userProvisioningService;

    @Test
    void anonymousPostContestShouldBeRejected() throws Exception {
        mockMvc.perform(post("/api/contests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Spring Cup"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void contestsGetEndpointsShouldRemainPublic() throws Exception {
        ContestShortResponseDto contest = new ContestShortResponseDto();
        contest.setId(1L);
        contest.setTitle("Spring Cup");
        contest.setStartAt(LocalDateTime.of(2026, 5, 10, 10, 0));
        contest.setRegistrationEndAt(LocalDateTime.of(2026, 5, 9, 10, 0));
        contest.setLevel(ContestLevelEnum.MEDIUM);
        contest.setSupportedLanguages(Set.of(ProgrammingLanguageEnum.JAVA));

        ContestFullResponseDto fullContest = new ContestFullResponseDto();
        fullContest.setId(1L);
        fullContest.setTitle("Spring Cup");

        when(contestService.getAllContests()).thenReturn(List.of(contest));
        when(contestService.getContestById(1L)).thenReturn(fullContest);

        mockMvc.perform(get("/api/contests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        mockMvc.perform(get("/api/contests/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void authenticatedPostContestShouldSucceedWithJwt() throws Exception {
        ContestFullResponseDto response = new ContestFullResponseDto();
        response.setId(99L);
        response.setTitle("Spring Cup");

        UserEntity currentUser = new UserEntity();
        currentUser.setId(42L);
        currentUser.setKeycloakSub("kc-123");
        when(userProvisioningService.getOrCreateUser(any())).thenReturn(currentUser);
        when(contestService.createContest(any(), eq(42L))).thenReturn(response);

        mockMvc.perform(post("/api/contests")
                        .with(jwt().jwt(jwt -> jwt
                                .subject("kc-123")
                                .claim("realm_access", Map.of("roles", List.of("user")))))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Spring Cup",
                                  "description": "Season opener",
                                  "format": "ICPC",
                                  "participationType": "TEAM",
                                  "level": "MEDIUM",
                                  "supportedLanguages": ["JAVA"],
                                  "isPublic": true,
                                  "status": "DRAFT",
                                  "startAt": "2026-05-10T10:00:00",
                                  "endAt": "2026-05-10T15:00:00",
                                  "maxTeamSize": 3
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(99));

        verify(contestService).createContest(any(), eq(42L));
    }
}
