package com.lordgasmic.funko.mappers;

import com.lordgasmic.funko.entities.FunkoExtrasEntity;
import com.lordgasmic.funko.model.FunkoExtra;
import com.lordgasmic.funko.model.FunkoRequest;

import java.util.ArrayList;
import java.util.List;

public final class FunkoExtrasMapper {

    private FunkoExtrasMapper() {
        // intentionally blank
    }

    public static FunkoExtra toFunkoExtra(final FunkoExtrasEntity entity) {
        return FunkoExtra.builder()
                .id(entity.getId())
                .funkoId(entity.getFunkoId())
                .text(entity.getText())
                .build();
    }

    public static List<FunkoExtrasEntity> toEntity(final long funkoId, final List<FunkoRequest.Extras> extras) {
        final List<FunkoExtrasEntity> entities = new ArrayList<>();

        for (final FunkoRequest.Extras extra : extras) {
            final FunkoExtrasEntity entity = new FunkoExtrasEntity();

            entity.setFunkoId(funkoId);
            entity.setText(extra.getText());

            entities.add(entity);
        }

        return entities;
    }
}
