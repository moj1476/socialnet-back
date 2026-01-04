package org.socialnet.socialnet.user.infrastructure.persistence.jpa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.socialnet.socialnet.user.core.model.SettingsEntity;
import org.socialnet.socialnet.user.core.model.Theme;
import org.socialnet.socialnet.user.core.model.VisibilityType;
import org.socialnet.socialnet.user.infrastructure.persistence.jpa.entity.UserSettingsEntity;
import org.socialnet.socialnet.user.infrastructure.persistence.jpa.entity.UserSettingsEntity.AppTheme;

@Mapper(componentModel = "spring")
public interface SettingsPersistenceMapper {

    @Mapping(target = "theme", source = "theme", qualifiedByName = "mapToDomainTheme")
    @Mapping(target = "whoCanSeePosts", source = "whoCanSeePosts", qualifiedByName = "mapToDomainVisibility")
    SettingsEntity toDomain(UserSettingsEntity entity);


    @Mapping(target = "theme", source = "theme", qualifiedByName = "mapToEntityTheme")
    @Mapping(target = "whoCanSeePosts", source = "whoCanSeePosts", qualifiedByName = "mapToEntityVisibility")

    @Mapping(target = "updatedAt", ignore = true)
    UserSettingsEntity toEntity(SettingsEntity settings);

    @Named("mapToDomainTheme")
    default Theme mapToDomainTheme(AppTheme appTheme) {
        if (appTheme == null) return Theme.SYSTEM;
        return Theme.valueOf(appTheme.name());
    }

    @Named("mapToEntityTheme")
    default AppTheme mapToEntityTheme(Theme theme) {
        if (theme == null) return AppTheme.SYSTEM;
        return AppTheme.valueOf(theme.name());
    }

    @Named("mapToDomainVisibility")
    default VisibilityType mapToDomainVisibility(UserSettingsEntity.VisibilityType entityVisibility) {
        if (entityVisibility == null) return VisibilityType.EVERYONE;
        return VisibilityType.valueOf(entityVisibility.name());
    }

    @Named("mapToEntityVisibility")
    default UserSettingsEntity.VisibilityType mapToEntityVisibility(VisibilityType domainVisibility) {
        if (domainVisibility == null) return UserSettingsEntity.VisibilityType.EVERYONE;
        return UserSettingsEntity.VisibilityType.valueOf(domainVisibility.name());
    }
}