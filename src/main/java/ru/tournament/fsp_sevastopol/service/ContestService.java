package ru.tournament.fsp_sevastopol.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tournament.fsp_sevastopol.dto.contest.ContestFullResponseDto;
import ru.tournament.fsp_sevastopol.dto.contest.ContestRequestDto;
import ru.tournament.fsp_sevastopol.dto.contest.ContestShortResponseDto;
import ru.tournament.fsp_sevastopol.entity.ContestEntity;
import ru.tournament.fsp_sevastopol.exception.ContestNotFoundException;
import ru.tournament.fsp_sevastopol.mapper.ContestMapper;
import ru.tournament.fsp_sevastopol.repository.ContestRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContestService {

    private final ContestRepository contestRepository;
    private final ContestMapper contestMapper;

    @Transactional
    public ContestFullResponseDto createContest(ContestRequestDto dto, Long currentUserId) {
        ContestEntity contest = contestMapper.toEntity(dto);
        contest.setCreatedByUserId(currentUserId);
        ContestEntity savedContest = contestRepository.save(contest);

        return contestMapper.toFullResponseDto(savedContest);
    }

    @Transactional(readOnly = true)
    public List<ContestShortResponseDto> getAllContests() {
        List<ContestEntity> contests = contestRepository.findByIsPublicTrue();

        return contestMapper.toShortResponseDtoList(contests);
    }

    @Transactional(readOnly = true)
    public ContestFullResponseDto getContestById(Long id) {
        ContestEntity contest = findContestEntityById(id);

        return contestMapper.toFullResponseDto(contest);
    }

    @Transactional
    public ContestFullResponseDto updateContest(Long id, ContestRequestDto dto) {
        ContestEntity contest = findContestEntityById(id);

        contestMapper.updateEntityFromDto(dto, contest);

        ContestEntity updatedContest = contestRepository.save(contest);

        return contestMapper.toFullResponseDto(updatedContest);
    }

    @Transactional
    public void deleteContest(Long id) {
        ContestEntity contest = findContestEntityById(id);

        contestRepository.delete(contest);
    }

    private ContestEntity findContestEntityById(Long id) {
        return contestRepository.findById(id)
                .orElseThrow(() -> new ContestNotFoundException(id));
    }
}
