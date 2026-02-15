package com.carsharing.mapper;

import com.carsharing.config.MapperConfig;
import com.carsharing.dto.user.UserRegRequestDto;
import com.carsharing.dto.user.UserResponseDto;
import com.carsharing.dto.user.UserResponseWithRolesDto;
import com.carsharing.dto.user.UserUpdateProfileRequestDto;
import com.carsharing.model.Role;
import com.carsharing.model.User;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    User toModel(UserRegRequestDto userRequest);

    UserResponseDto toDto(User user);

    @Mapping(target = "roles", source = "roles")
    UserResponseWithRolesDto toDtoWithRoles(User user);

    default Set<String> mapRoles(Set<Role> roles) {
        return roles.stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());
    }

    void updateUserProfile(@MappingTarget User user,
            UserUpdateProfileRequestDto userRequest);
}
