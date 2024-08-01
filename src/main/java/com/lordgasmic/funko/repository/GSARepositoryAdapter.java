package com.lordgasmic.funko.repository;


import com.lordgasmic.collections.Nucleus;
import com.lordgasmic.collections.repository.GSARepository;
import com.lordgasmic.collections.repository.RepositoryItem;
import com.lordgasmic.funko.config.FunkoConstants;
import com.lordgasmic.funko.config.FunkoExtraConstants;
import com.lordgasmic.funko.model.FunkoExtrasResponse;
import com.lordgasmic.funko.model.FunkoResponse;
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

    public List<FunkoResponse> getAllFunkos() throws SQLException {
        List<RepositoryItem> items = funkoRepository.getAllRepositoryItems(FunkoConstants.FUNKO_REPOSITORY_ITEM);
        return items.stream().map(GSARepositoryAdapter::convertRepositoryItemToFunkoResponse).collect(Collectors.toList());
    }

    public List<FunkoResponse> getAllFunkosWithExtras() throws ExecutionException, InterruptedException {
        CompletableFuture<List<RepositoryItem>> funkoItems = CompletableFuture.supplyAsync(() -> {
            try {
                return funkoRepository.getAllRepositoryItems(FunkoConstants.FUNKO_REPOSITORY_ITEM);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        CompletableFuture<List<RepositoryItem>> funkoExtraItems = CompletableFuture.supplyAsync(() -> {
            try {
                return funkoRepository.getAllRepositoryItems(FunkoExtraConstants.FUNKO_EXTRAS_REPOSITORY_ITEM);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        CompletableFuture<Void> combinedFutures = CompletableFuture.allOf(funkoItems, funkoExtraItems);
        combinedFutures.get();

        Map<Integer, FunkoResponse> funkoMap = funkoItems.get().stream().map(GSARepositoryAdapter::convertRepositoryItemToFunkoResponse).map(f -> Map.entry(f.getId(), f)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        List<FunkoExtrasResponse> funkoExtras = funkoExtraItems.get().stream().map(GSARepositoryAdapter::convertRepositoryItemToFunkoExtrasResponse).toList();

        for (FunkoExtrasResponse funkoExtra : funkoExtras) {
            FunkoResponse funko = funkoMap.get(funkoExtra.getFunkoId());
            if (funko != null) {
                funko.getExtras().add(funkoExtra);
                funkoMap.put(funko.getId(), funko);
            }
        }

        return new ArrayList<>(funkoMap.values());
    }

    private static FunkoResponse convertRepositoryItemToFunkoResponse(RepositoryItem item) {
        FunkoResponse funkoResponse = new FunkoResponse();

        funkoResponse.setId((Integer) item.getPropertyValue(FunkoConstants.PROP_ID));
        funkoResponse.setTitle((String) item.getPropertyValue(FunkoConstants.PROP_TITLE));
        funkoResponse.setFandom((String) item.getPropertyValue(FunkoConstants.PROP_FANDOM));
        funkoResponse.setSeriesId((Integer) item.getPropertyValue(FunkoConstants.PROP_SERIES_ID));
        funkoResponse.setName((String) item.getPropertyValue(FunkoConstants.PROP_NAME));

        return funkoResponse;
    }

    private static FunkoExtrasResponse convertRepositoryItemToFunkoExtrasResponse(RepositoryItem item) {
        FunkoExtrasResponse funkoExtrasResponse = new FunkoExtrasResponse();

        funkoExtrasResponse.setId((Integer) item.getPropertyValue(FunkoExtraConstants.PROP_ID));
        funkoExtrasResponse.setFunkoId((Integer) item.getPropertyValue(FunkoExtraConstants.PROP_FUNKO_ID));
        funkoExtrasResponse.setText((String) item.getPropertyValue(FunkoExtraConstants.PROP_TEXT));

        return funkoExtrasResponse;
    }
}
