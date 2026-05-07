package ru.tournament.fsp_sevastopol.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.tournament.fsp_sevastopol.dto.task.TaskRequestDto;
import ru.tournament.fsp_sevastopol.dto.task.TaskResponseDto;
import ru.tournament.fsp_sevastopol.dto.task.TaskShortResponseDto;
import ru.tournament.fsp_sevastopol.entity.TaskEntity;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorUserId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    TaskEntity toEntity(TaskRequestDto dto);

    TaskResponseDto toResponseDto(TaskEntity entity);

    TaskShortResponseDto toShortResponseDto(TaskEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorUserId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(TaskRequestDto dto, @MappingTarget TaskEntity entity);
}