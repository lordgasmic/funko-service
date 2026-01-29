package com.lordgasmic.funko.mappers;

import com.lordgasmic.funko.entities.FunkoEntity;
import com.lordgasmic.funko.model.Funko;
import com.lordgasmic.funko.model.FunkoRequest;

import java.util.ArrayList;

public final class FunkoMapper {

    private FunkoMapper() {
        // intentionally blank
    }

    public static Funko toFunko(final FunkoEntity entity) {
        return Funko.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .fandom(entity.getFandom())
                .seriesId(entity.getSeriesId())
                .name(entity.getName())
                .extras(new ArrayList<>())
                .build();
    }

    public static FunkoEntity toEntity(final FunkoRequest request) {
        final FunkoEntity entity = new FunkoEntity();

        entity.setTitle(request.getTitle());
        entity.setFandom(request.getFandom());
        entity.setSeriesId(request.getSeriesId());
        entity.setName(request.getName());

        return entity;
    }
}
