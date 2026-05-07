package ru.tournament.fsp_sevastopol.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.tournament.fsp_sevastopol.dto.problemset.ProblemSetRequestDto;
import ru.tournament.fsp_sevastopol.dto.problemset.ProblemSetResponseDto;
import ru.tournament.fsp_sevastopol.dto.problemset.ProblemSetShortResponseDto;
import ru.tournament.fsp_sevastopol.entity.ProblemSetEntity;

@Mapper(componentModel = "spring")
public interface ProblemSetMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdByUserId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ProblemSetEntity toEntity(ProblemSetRequestDto dto);

    @Mapping(target = "tasks", ignore = true)
    ProblemSetResponseDto toResponseDto(ProblemSetEntity entity);

    @Mapping(target = "taskCount", ignore = true)
    ProblemSetShortResponseDto toShortResponseDto(ProblemSetEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdByUserId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(ProblemSetRequestDto dto, @MappingTarget ProblemSetEntity entity);
}
