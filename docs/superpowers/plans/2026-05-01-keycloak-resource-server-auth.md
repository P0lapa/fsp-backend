# Keycloak Resource Server Auth Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add JWT-based authentication with Keycloak, auto-provision local users by `sub`, extract roles from JWT, and use the local user id in contest creation.

**Architecture:** Spring Boot acts as an OAuth2 Resource Server and validates bearer tokens from the frontend. A local `UserEntity` stores the Keycloak `sub`, default avatar URL, and rating state; authenticated write requests resolve or create the local user before entering business logic.

**Tech Stack:** Spring Boot 3.5, Spring Security OAuth2 Resource Server, Spring Data JPA, PostgreSQL, JUnit 5, MockMvc, spring-security-test

---

## File Structure

**Create**
- `src/main/java/ru/tournament/fsp_sevastopol/entity/UserEntity.java`
- `src/main/java/ru/tournament/fsp_sevastopol/entity/UserRatingHistoryEntity.java`
- `src/main/java/ru/tournament/fsp_sevastopol/repository/UserRepository.java`
- `src/main/java/ru/tournament/fsp_sevastopol/repository/UserRatingHistoryRepository.java`
- `src/main/java/ru/tournament/fsp_sevastopol/config/SecurityProperties.java`
- `src/main/java/ru/tournament/fsp_sevastopol/config/JwtRoleConverter.java`
- `src/main/java/ru/tournament/fsp_sevastopol/service/CurrentUserService.java`
- `src/main/java/ru/tournament/fsp_sevastopol/service/UserProvisioningService.java`
- `src/main/java/ru/tournament/fsp_sevastopol/dto/auth/CurrentUserResponseDto.java`
- `src/test/java/ru/tournament/fsp_sevastopol/service/UserProvisioningServiceTest.java`
- `src/test/java/ru/tournament/fsp_sevastopol/controller/AuthControllerTest.java`
- `src/test/java/ru/tournament/fsp_sevastopol/controller/ContestSecurityIntegrationTest.java`

**Modify**
- `src/main/java/ru/tournament/fsp_sevastopol/config/SecurityConfig.java`
- `src/main/java/ru/tournament/fsp_sevastopol/controller/AuthController.java`
- `src/main/java/ru/tournament/fsp_sevastopol/controller/ContestController.java`
- `src/main/java/ru/tournament/fsp_sevastopol/service/ContestService.java`
- `src/main/resources/application.yml`

### Task 1: Add local user persistence model

**Files:**
- Create: `src/main/java/ru/tournament/fsp_sevastopol/entity/UserEntity.java`
- Create: `src/main/java/ru/tournament/fsp_sevastopol/entity/UserRatingHistoryEntity.java`
- Create: `src/main/java/ru/tournament/fsp_sevastopol/repository/UserRepository.java`
- Create: `src/main/java/ru/tournament/fsp_sevastopol/repository/UserRatingHistoryRepository.java`
- Test: `src/test/java/ru/tournament/fsp_sevastopol/service/UserProvisioningServiceTest.java`

- [ ] **Step 1: Write the failing test**

```java
@ExtendWith(MockitoExtension.class)
class UserProvisioningServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserProvisioningService userProvisioningService;

    @Test
    void shouldCreateLocalUserWhenSubIsMissing() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("sub", "kc-123")
                .claim("preferred_username", "alice")
                .build();

        when(userRepository.findByKeycloakSub("kc-123")).thenReturn(Optional.empty());
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity entity = invocation.getArgument(0);
            entity.setId(7L);
            return entity;
        });

        UserEntity user = userProvisioningService.getOrCreateUser(jwt);

        assertThat(user.getId()).isEqualTo(7L);
        assertThat(user.getKeycloakSub()).isEqualTo("kc-123");
        assertThat(user.getAvatarUrl()).isEqualTo("/images/avatars/default.png");
        assertThat(user.getCurrentRating()).isEqualTo(0);
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew.bat test --tests "ru.tournament.fsp_sevastopol.service.UserProvisioningServiceTest"`
Expected: FAIL because `UserProvisioningService`, `UserRepository`, and `UserEntity` do not exist yet.

- [ ] **Step 3: Write minimal implementation**

```java
@Getter
@Setter
@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(name = "uk_users_keycloak_sub", columnNames = "keycloak_sub"))
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "keycloak_sub", nullable = false)
    private String keycloakSub;

    @Column(name = "avatar_url", nullable = false)
    private String avatarUrl;

    @Column(name = "current_rating", nullable = false)
    private Integer currentRating;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
```

```java
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByKeycloakSub(String keycloakSub);
}
```

```java
@Service
@RequiredArgsConstructor
public class UserProvisioningService {

    private static final String DEFAULT_AVATAR_URL = "/images/avatars/default.png";

    private final UserRepository userRepository;

    @Transactional
    public UserEntity getOrCreateUser(Jwt jwt) {
        String sub = jwt.getSubject();

        return userRepository.findByKeycloakSub(sub)
                .orElseGet(() -> {
                    UserEntity user = new UserEntity();
                    user.setKeycloakSub(sub);
                    user.setAvatarUrl(DEFAULT_AVATAR_URL);
                    user.setCurrentRating(0);
                    return userRepository.save(user);
                });
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `./gradlew.bat test --tests "ru.tournament.fsp_sevastopol.service.UserProvisioningServiceTest"`
Expected: PASS

### Task 2: Configure JWT validation and role extraction

**Files:**
- Modify: `src/main/java/ru/tournament/fsp_sevastopol/config/SecurityConfig.java`
- Create: `src/main/java/ru/tournament/fsp_sevastopol/config/SecurityProperties.java`
- Create: `src/main/java/ru/tournament/fsp_sevastopol/config/JwtRoleConverter.java`
- Modify: `src/main/resources/application.yml`
- Test: `src/test/java/ru/tournament/fsp_sevastopol/controller/ContestSecurityIntegrationTest.java`

- [ ] **Step 1: Write the failing security test**

```java
@SpringBootTest
@AutoConfigureMockMvc
class ContestSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void postContestShouldRequireAuthentication() throws Exception {
        mockMvc.perform(post("/api/contests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Spring Cup",
                                  "description": "desc",
                                  "format": "ICPC",
                                  "participationType": "TEAM",
                                  "isPublic": true,
                                  "status": "DRAFT",
                                  "startAt": "2026-05-10T10:00:00",
                                  "endAt": "2026-05-10T15:00:00",
                                  "level": "REGIONAL",
                                  "supportedLanguages": ["JAVA"]
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew.bat test --tests "ru.tournament.fsp_sevastopol.controller.ContestSecurityIntegrationTest"`
Expected: FAIL because the current config permits all requests.

- [ ] **Step 3: Write minimal security implementation**

```java
@ConfigurationProperties(prefix = "app.security")
public record SecurityProperties(
        String defaultAvatarUrl,
        String clientId
) {
}
```

```java
@Component
public class JwtRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Set<String> roles = new LinkedHashSet<>();

        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess != null && realmAccess.get("roles") instanceof Collection<?> realmRoles) {
            realmRoles.forEach(role -> roles.add(role.toString()));
        }

        return roles.stream()
                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role.toUpperCase(Locale.ROOT))
                .map(SimpleGrantedAuthority::new)
                .toList();
    }
}
```

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtRoleConverter jwtRoleConverter) throws Exception {
    JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
    authenticationConverter.setJwtGrantedAuthoritiesConverter(jwtRoleConverter);

    return http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(HttpMethod.GET, "/api/**").permitAll()
                    .requestMatchers("/", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                    .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(authenticationConverter)))
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .build();
}
```

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: ${SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI}

app:
  security:
    default-avatar-url: ${APP_SECURITY_DEFAULT_AVATAR_URL:/images/avatars/default.png}
    client-id: ${APP_SECURITY_CLIENT_ID:}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `./gradlew.bat test --tests "ru.tournament.fsp_sevastopol.controller.ContestSecurityIntegrationTest"`
Expected: PASS with `401 Unauthorized` for anonymous `POST /api/contests`

### Task 3: Add current user endpoint and local user auto-provisioning

**Files:**
- Modify: `src/main/java/ru/tournament/fsp_sevastopol/controller/AuthController.java`
- Create: `src/main/java/ru/tournament/fsp_sevastopol/dto/auth/CurrentUserResponseDto.java`
- Create: `src/main/java/ru/tournament/fsp_sevastopol/service/CurrentUserService.java`
- Test: `src/test/java/ru/tournament/fsp_sevastopol/controller/AuthControllerTest.java`

- [ ] **Step 1: Write the failing controller test**

```java
@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, JwtRoleConverter.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CurrentUserService currentUserService;

    @Test
    void meShouldReturnProvisionedLocalUserAndRoles() throws Exception {
        when(currentUserService.getCurrentUser(any(Jwt.class))).thenReturn(
                CurrentUserResponseDto.builder()
                        .userId(5L)
                        .subjectId("kc-123")
                        .avatarUrl("/images/avatars/default.png")
                        .currentRating(0)
                        .roles(List.of("ROLE_USER", "ROLE_ADMIN"))
                        .build()
        );

        mockMvc.perform(get("/api/auth/me").with(jwt().jwt(jwt -> jwt
                        .subject("kc-123")
                        .claim("realm_access", Map.of("roles", List.of("user", "admin"))))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(5))
                .andExpect(jsonPath("$.subjectId").value("kc-123"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_USER"));
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew.bat test --tests "ru.tournament.fsp_sevastopol.controller.AuthControllerTest"`
Expected: FAIL because the controller is commented out and DTO/service do not exist.

- [ ] **Step 3: Write minimal implementation**

```java
@Builder
@Getter
public class CurrentUserResponseDto {
    private Long userId;
    private String subjectId;
    private String avatarUrl;
    private Integer currentRating;
    private List<String> roles;
}
```

```java
@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserProvisioningService userProvisioningService;
    private final JwtRoleConverter jwtRoleConverter;

    @Transactional
    public CurrentUserResponseDto getCurrentUser(Jwt jwt) {
        UserEntity user = userProvisioningService.getOrCreateUser(jwt);
        List<String> roles = jwtRoleConverter.convert(jwt).stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return CurrentUserResponseDto.builder()
                .userId(user.getId())
                .subjectId(user.getKeycloakSub())
                .avatarUrl(user.getAvatarUrl())
                .currentRating(user.getCurrentRating())
                .roles(roles)
                .build();
    }
}
```

```java
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final CurrentUserService currentUserService;

    @GetMapping("/me")
    public CurrentUserResponseDto getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        return currentUserService.getCurrentUser(jwt);
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `./gradlew.bat test --tests "ru.tournament.fsp_sevastopol.controller.AuthControllerTest"`
Expected: PASS

### Task 4: Use authenticated local user in contest writes

**Files:**
- Modify: `src/main/java/ru/tournament/fsp_sevastopol/service/ContestService.java`
- Modify: `src/main/java/ru/tournament/fsp_sevastopol/controller/ContestController.java`
- Test: `src/test/java/ru/tournament/fsp_sevastopol/controller/ContestSecurityIntegrationTest.java`

- [ ] **Step 1: Write the failing behavior test**

```java
@Test
void createContestShouldUseAuthenticatedUserId() throws Exception {
    mockMvc.perform(post("/api/contests")
                    .with(jwt().jwt(jwt -> jwt.subject("kc-123")
                            .claim("realm_access", Map.of("roles", List.of("user")))))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(validContestJson()))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.createdByUserId").value(42));
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew.bat test --tests "ru.tournament.fsp_sevastopol.controller.ContestSecurityIntegrationTest"`
Expected: FAIL because `ContestService` still hardcodes `1L`.

- [ ] **Step 3: Write minimal implementation**

```java
@Transactional
public ContestFullResponseDto createContest(ContestRequestDto dto, Long currentUserId) {
    ContestEntity contest = contestMapper.toEntity(dto);
    contest.setCreatedByUserId(currentUserId);
    ContestEntity savedContest = contestRepository.save(contest);
    return contestMapper.toFullResponseDto(savedContest);
}
```

```java
@PostMapping
@ResponseStatus(HttpStatus.CREATED)
public ContestFullResponseDto createContest(
        @AuthenticationPrincipal Jwt jwt,
        @Valid @RequestBody ContestRequestDto dto
) {
    Long currentUserId = currentUserService.getCurrentUserEntity(jwt).getId();
    return contestService.createContest(dto, currentUserId);
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `./gradlew.bat test --tests "ru.tournament.fsp_sevastopol.controller.ContestSecurityIntegrationTest"`
Expected: PASS with `createdByUserId` equal to the provisioned local user id.

### Task 5: Verify the whole Spring context

**Files:**
- Modify: `src/test/java/ru/tournament/fsp_sevastopol/FspSevastopolApplicationTests.java`

- [ ] **Step 1: Add focused context configuration**

```java
@SpringBootTest(properties = {
        "spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8081/realms/fsp",
        "app.security.default-avatar-url=/images/avatars/default.png"
})
class FspSevastopolApplicationTests {

    @Test
    void contextLoads() {
    }
}
```

- [ ] **Step 2: Run the full test suite**

Run: `./gradlew.bat test`
Expected: PASS

## Self-Review

- Spec coverage:
  - JWT validation by Keycloak: Task 2
  - Role extraction: Task 2 and Task 3
  - Auto-create local user by `sub`: Task 1 and Task 3
  - Store default avatar and zero rating: Task 1
  - Use local user id in tournament creation: Task 4
  - Future rating history table foundation: Task 1
- Placeholder scan: no `TODO`, `TBD`, or vague “add tests later” steps remain.
- Type consistency: `keycloakSub`, `currentRating`, `CurrentUserResponseDto`, and `createContest(..., Long currentUserId)` names are consistent across tasks.
