package com.lordgasmic.funko.service;

import com.lordgasmic.funko.model.FunkoResponse;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.Http2SolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class FunkoService {

    @Value("${apache.solr.address")
    private String solrAddress;

    private final Http2SolrClient client;

    public FunkoService() {
        client = new Http2SolrClient.Builder(solrAddress).build();
    }

    public List<FunkoResponse> getAllFunkos() throws SolrServerException, IOException {
        List<FunkoResponse> funkoResponses = new ArrayList<>();

        SolrQuery query = new SolrQuery();
        query.set("q", "price:599.99");
        QueryResponse response = client.query(query);

        SolrDocumentList docList = response.getResults();
        for (SolrDocument doc : docList) {
            FunkoResponse funkoResponse = new FunkoResponse();
            funkoResponse.setId((Integer) doc.getFieldValue("id"));
            funkoResponse.setTitle((String) doc.getFieldValue("title"));
            funkoResponse.setFandom((String) doc.getFieldValue("fandom"));
            funkoResponse.setSeriesId((Integer) doc.getFieldValue("seriesId"));
            funkoResponse.setName((String) doc.getFieldValue("name"));
            funkoResponses.add(funkoResponse);
        }

        return funkoResponses;
    }

    public List<FunkoResponse> getAllFunkosWithExtras() throws ExecutionException, InterruptedException {
//        return repositoryAdapter.getAllFunkosWithExtras();
        throw new UnsupportedOperationException();
    }
}
