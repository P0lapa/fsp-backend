package ru.tournament.fsp_sevastopol.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tournament.fsp_sevastopol.dto.contest.ContestFullResponseDto;
import ru.tournament.fsp_sevastopol.dto.contest.ContestRequestDto;
import ru.tournament.fsp_sevastopol.dto.contest.ContestShortResponseDto;
import ru.tournament.fsp_sevastopol.entity.ContestEntity;
import ru.tournament.fsp_sevastopol.exception.ContestNotFoundException;
import ru.tournament.fsp_sevastopol.mapper.ContestMapper;
import ru.tournament.fsp_sevastopol.repository.ContestRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContestServiceTest {

    @Mock
    private ContestRepository contestRepository;

    private final ContestMapper contestMapper = Mappers.getMapper(ContestMapper.class);

    private ContestService contestService;

    @BeforeEach
    void setUp() {
        contestService = new ContestService(contestRepository, contestMapper);
    }

    @Test
    void createContestUsesAuthenticatedUserId() {
        ContestRequestDto requestDto = new ContestRequestDto();
        requestDto.setTitle("Auth Cup");
        requestDto.setDescription("Secured create");
        requestDto.setIsPublic(true);
        requestDto.setSupportedLanguages(Set.of());

        ContestEntity savedContest = new ContestEntity();
        savedContest.setId(10L);
        savedContest.setTitle("Auth Cup");
        savedContest.setCreatedByUserId(42L);

        when(contestRepository.save(org.mockito.ArgumentMatchers.any(ContestEntity.class))).thenReturn(savedContest);

        ContestFullResponseDto response = contestService.createContest(requestDto, 42L);

        var contestCaptor = forClass(ContestEntity.class);
        verify(contestRepository).save(contestCaptor.capture());
        assertThat(contestCaptor.getValue().getCreatedByUserId()).isEqualTo(42L);
        assertThat(response.getId()).isEqualTo(10L);
    }

    @Test
    void getAllContestsReturnsOnlyPublicContests() {
        ContestEntity publicContest = new ContestEntity();
        publicContest.setId(1L);
        publicContest.setTitle("Public Cup");
        publicContest.setIsPublic(true);

        when(contestRepository.findByIsPublicTrue()).thenReturn(List.of(publicContest));

        List<ContestShortResponseDto> contests = contestService.getAllContests();

        verify(contestRepository).findByIsPublicTrue();
        assertThat(contests).hasSize(1);
        assertThat(contests.getFirst().getId()).isEqualTo(1L);
        assertThat(contests.getFirst().getTitle()).isEqualTo("Public Cup");
    }

    @Test
    void getContestByIdDoesNotFilterPrivateContest() {
        ContestEntity privateContest = new ContestEntity();
        privateContest.setId(2L);
        privateContest.setTitle("Private Cup");
        privateContest.setIsPublic(false);

        when(contestRepository.findById(2L)).thenReturn(Optional.of(privateContest));

        ContestFullResponseDto contest = contestService.getContestById(2L);

        verify(contestRepository).findById(2L);
        assertThat(contest.getId()).isEqualTo(2L);
        assertThat(contest.getTitle()).isEqualTo("Private Cup");
    }

    @Test
    void getContestByIdThrowsWhenContestMissing() {
        when(contestRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contestService.getContestById(99L))
                .isInstanceOf(ContestNotFoundException.class);
    }
}
