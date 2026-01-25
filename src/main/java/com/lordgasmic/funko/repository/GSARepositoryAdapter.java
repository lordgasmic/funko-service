package com.lordgasmic.funko.repository;

import com.lordgasmic.collections.Nucleus;
import com.lordgasmic.collections.repository.GSARepository;
import com.lordgasmic.collections.repository.RepositoryItem;
import com.lordgasmic.funko.config.FunkoConstants;
import com.lordgasmic.funko.config.FunkoExtraConstants;
import com.lordgasmic.funko.model.Funko;
import com.lordgasmic.funko.model.FunkoExtra;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Component
public class GSARepositoryAdapter {

    private static final String REPO_NAME = "FunkoRepository";

    private final GSARepository funkoRepository;

    public GSARepositoryAdapter() {
        funkoRepository = (GSARepository) Nucleus.getInstance().getGenericService(REPO_NAME);
    }

    public List<Funko> getAllFunkos() throws SQLException {
        final List<RepositoryItem> items = funkoRepository.getAllRepositoryItems(FunkoConstants.FUNKO_REPOSITORY_ITEM);
        return items.stream().map(GSARepositoryAdapter::convertRepositoryItemToFunkoResponse).collect(Collectors.toList());
    }

    public List<Funko> getAllFunkosWithExtras() throws ExecutionException, InterruptedException {
        final CompletableFuture<List<RepositoryItem>> funkoItems = CompletableFuture.supplyAsync(() -> {
            try {
                return funkoRepository.getAllRepositoryItems(FunkoConstants.FUNKO_REPOSITORY_ITEM);
            } catch (final SQLException e) {
                throw new RuntimeException(e);
            }
        });
        final CompletableFuture<List<RepositoryItem>> funkoExtraItems = CompletableFuture.supplyAsync(() -> {
            try {
                return funkoRepository.getAllRepositoryItems(FunkoExtraConstants.FUNKO_EXTRAS_REPOSITORY_ITEM);
            } catch (final SQLException e) {
                throw new RuntimeException(e);
            }
        });
        final CompletableFuture<Void> combinedFutures = CompletableFuture.allOf(funkoItems, funkoExtraItems);
        combinedFutures.get();

        final Map<Integer, Funko> funkoMap = funkoItems.get().stream().map(GSARepositoryAdapter::convertRepositoryItemToFunkoResponse).map(f -> Map.entry(f.getId(), f)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        final List<FunkoExtra> funkoExtras = funkoExtraItems.get().stream().map(GSARepositoryAdapter::convertRepositoryItemToFunkoExtrasResponse).toList();

        for (final FunkoExtra funkoExtra : funkoExtras) {
            final Funko funko = funkoMap.get(funkoExtra.getFunkoId());
            if (funko != null) {
                funko.getExtras().add(funkoExtra);
                funkoMap.put(funko.getId(), funko);
            }
        }

        return new ArrayList<>(funkoMap.values());
    }

    private static Funko convertRepositoryItemToFunkoResponse(final RepositoryItem item) {
        final Funko funkoResponse = new Funko();

        funkoResponse.setId((Integer) item.getPropertyValue(FunkoConstants.PROP_ID));
        funkoResponse.setTitle((String) item.getPropertyValue(FunkoConstants.PROP_TITLE));
        funkoResponse.setFandom((String) item.getPropertyValue(FunkoConstants.PROP_FANDOM));
        funkoResponse.setSeriesId((Integer) item.getPropertyValue(FunkoConstants.PROP_SERIES_ID));
        funkoResponse.setName((String) item.getPropertyValue(FunkoConstants.PROP_NAME));

        return funkoResponse;
    }

    private static FunkoExtra convertRepositoryItemToFunkoExtrasResponse(final RepositoryItem item) {
        final FunkoExtra funkoExtrasResponse = new FunkoExtra();

        funkoExtrasResponse.setId((Integer) item.getPropertyValue(FunkoExtraConstants.PROP_ID));
        funkoExtrasResponse.setFunkoId((Integer) item.getPropertyValue(FunkoExtraConstants.PROP_FUNKO_ID));
        funkoExtrasResponse.setText((String) item.getPropertyValue(FunkoExtraConstants.PROP_TEXT));

        return funkoExtrasResponse;
    }
}
