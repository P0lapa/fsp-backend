package ru.tournament.fsp_sevastopol.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.tournament.fsp_sevastopol.dto.contest.ContestFullResponseDto;
import ru.tournament.fsp_sevastopol.dto.contest.ContestRequestDto;
import ru.tournament.fsp_sevastopol.dto.contest.ContestShortResponseDto;
import ru.tournament.fsp_sevastopol.entity.ContestEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ContestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdByUserId", ignore = true)
    ContestEntity toEntity(ContestRequestDto dto);

    ContestShortResponseDto toShortResponseDto(ContestEntity entity);

    List<ContestShortResponseDto> toShortResponseDtoList(List<ContestEntity> entities);

    ContestFullResponseDto toFullResponseDto(ContestEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdByUserId", ignore = true)
    void updateEntityFromDto(ContestRequestDto dto, @MappingTarget ContestEntity entity);
}

