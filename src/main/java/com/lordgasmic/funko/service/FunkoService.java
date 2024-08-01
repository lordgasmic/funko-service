package com.lordgasmic.funko.service;

import com.lordgasmic.funko.model.FunkoResponse;
import com.lordgasmic.funko.repository.GSARepositoryAdapter;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class FunkoService {

    private final GSARepositoryAdapter repositoryAdapter;

    public FunkoService(GSARepositoryAdapter repositoryAdapter) {
        this.repositoryAdapter = repositoryAdapter;
    }

    public List<FunkoResponse> getAllFunkos() throws SQLException {
        return repositoryAdapter.getAllFunkos();
    }

    public List<FunkoResponse> getAllFunkosWithExtras() throws ExecutionException, InterruptedException {
        return repositoryAdapter.getAllFunkosWithExtras();
    }
}
