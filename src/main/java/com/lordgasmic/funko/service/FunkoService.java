package com.lordgasmic.funko.service;

import com.lordgasmic.collections.Nucleus;
import com.lordgasmic.collections.repository.GSARepository;
import com.lordgasmic.collections.repository.RepositoryItem;
import com.lordgasmic.funko.config.FunkoConstants;
import com.lordgasmic.funko.model.FunkoResponse;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FunkoService {
    private static final String REPO_NAME = "FunkoRepository";

    private final GSARepository funkoRepository;

    public FunkoService() {
        this.funkoRepository = (GSARepository) Nucleus.getInstance().getGenericService(REPO_NAME);
    }

    public List<FunkoResponse> getAllFunkos() throws SQLException {
        List<RepositoryItem> items = funkoRepository.getAllRepositoryItems(FunkoConstants.FUNKO_REPOSITORY_ITEM);
        return items.stream().map(FunkoService::convertRepositoryItemToFunkoResponse).collect(Collectors.toList());
    }

    private List<FunkoResponse> getAllFunkosWithExtras() {
        throw new UnsupportedOperationException();
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
}
