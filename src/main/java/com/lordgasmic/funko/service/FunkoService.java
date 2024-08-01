package com.lordgasmic.funko.service;

import com.lordgasmic.funko.model.FunkoResponse;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class FunkoService {

    private final SolrClient client;

    public FunkoService(SolrClient client) {
        this.client = client;
    }

    public List<FunkoResponse> getAllFunkos() throws SolrServerException, IOException {
        List<FunkoResponse> funkoResponses = new ArrayList<>();

        SolrQuery query = new SolrQuery();
        query.set("q", "*:*");
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
