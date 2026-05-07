package ru.tournament.fsp_sevastopol.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.tournament.fsp_sevastopol.dto.problemset.ProblemSetTaskRequestDto;
import ru.tournament.fsp_sevastopol.dto.problemset.ProblemSetTaskResponseDto;
import ru.tournament.fsp_sevastopol.entity.ProblemSetTaskEntity;

@Mapper(componentModel = "spring")
public interface ProblemSetTaskMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "problemSet", ignore = true)
    @Mapping(target = "task", ignore = true)
    ProblemSetTaskEntity toEntity(ProblemSetTaskRequestDto dto);

    @Mapping(target = "problemSetId", source = "problemSet.id")
    @Mapping(target = "taskId", source = "task.id")
    @Mapping(target = "task", source = "task")
    ProblemSetTaskResponseDto toResponseDto(ProblemSetTaskEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "problemSet", ignore = true)
    @Mapping(target = "task", ignore = true)
    void updateEntityFromDto(ProblemSetTaskRequestDto dto, @MappingTarget ProblemSetTaskEntity entity);
}
