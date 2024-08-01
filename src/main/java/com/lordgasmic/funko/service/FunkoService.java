package com.lordgasmic.funko.service;

import com.lordgasmic.collections.Nucleus;
import com.lordgasmic.collections.repository.GSARepository;
import com.lordgasmic.collections.repository.RepositoryItem;
import com.lordgasmic.funko.config.FunkoConstants;
import com.lordgasmic.funko.config.FunkoExtraConstants;
import com.lordgasmic.funko.model.FunkoExtrasResponse;
import com.lordgasmic.funko.model.FunkoResponse;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.Http2SolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class FunkoService {
    private static final String REPO_NAME = "FunkoRepository";

    private final GSARepository funkoRepository;
    private final SolrClient client;

    public FunkoService() {
        this.funkoRepository = (GSARepository) Nucleus.getInstance().getGenericService(REPO_NAME);

        client = new Http2SolrClient.Builder("http://172.16.0.105:8983/solr/funkos").build();
    }

    public List<FunkoResponse> getAllFunkos() throws SQLException {
        List<RepositoryItem> items = funkoRepository.getAllRepositoryItems(FunkoConstants.FUNKO_REPOSITORY_ITEM);
        return items.stream().map(FunkoService::convertRepositoryItemToFunkoResponse).collect(Collectors.toList());
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

        Map<Integer, FunkoResponse> funkoMap = funkoItems.get().stream().map(FunkoService::convertRepositoryItemToFunkoResponse).map(f -> Map.entry(f.getId(), f)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        List<FunkoExtrasResponse> funkoExtras = funkoExtraItems.get().stream().map(FunkoService::convertRepositoryItemToFunkoExtrasResponse).toList();

        for (FunkoExtrasResponse funkoExtra : funkoExtras) {
            FunkoResponse funko = funkoMap.get(funkoExtra.getFunkoId());
            if (funko != null) {
                funko.getExtras().add(funkoExtra);
                funkoMap.put(funko.getId(), funko);
            }
        }

        return new ArrayList<>(funkoMap.values());
    }

    public void index() throws SQLException, SolrServerException, IOException {
        List<FunkoResponse> funkos = getAllFunkos();

        for (FunkoResponse funko : funkos) {
            SolrInputDocument document = new SolrInputDocument();
            document.addField("title", funko.getTitle());
            document.addField("fandom", funko.getFandom());
            document.addField("seriesId", funko.getSeriesId());
            document.addField("name", funko.getName());
            client.add(document);
        }

        client.commit();
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
