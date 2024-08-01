package com.lordgasmic.funko.service;

import com.lordgasmic.funko.model.FunkoResponse;
import com.lordgasmic.funko.model.IndexRequest;
import com.lordgasmic.funko.repository.GSARepositoryAdapter;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class FunkoIndexService {

    private final GSARepositoryAdapter repositoryAdapter;
    private final SolrClient client;

    public FunkoIndexService(GSARepositoryAdapter repositoryAdapter, SolrClient client) {
        this.repositoryAdapter = repositoryAdapter;
        this.client = client;
    }

    public void index() throws SolrServerException, IOException, ExecutionException, InterruptedException {
        List<FunkoResponse> funkos = repositoryAdapter.getAllFunkosWithExtras();

//        client.deleteByQuery("*:*");


        for (FunkoResponse funko : funkos) {
            IndexRequest indexRequest = new IndexRequest();
            indexRequest.setId(funko.getId());
            indexRequest.setTitle(funko.getTitle());
            indexRequest.setFandom(funko.getFandom());
            indexRequest.setSeriesId(funko.getSeriesId());
            indexRequest.setName(funko.getName());
            client.addBean(indexRequest);
        }

        client.commit();
    }
}
