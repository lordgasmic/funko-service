package com.lordgasmic.funko.service;

import com.lordgasmic.funko.model.FunkoResponse;
import com.lordgasmic.funko.repository.GSARepositoryAdapter;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@Service
@Slf4j
public class FunkoIndexService {

    private final GSARepositoryAdapter repositoryAdapter;
    private final SolrClient client;

    public FunkoIndexService(GSARepositoryAdapter repositoryAdapter, SolrClient client) {
        this.repositoryAdapter = repositoryAdapter;
        this.client = client;
    }

    public void index() throws SQLException, SolrServerException, IOException {
        List<FunkoResponse> funkos = repositoryAdapter.getAllFunkos();

//        client.deleteByQuery("*:*");

        for (FunkoResponse funko : funkos) {
            SolrInputDocument document = new SolrInputDocument();
            document.addField("id", funko.getId());
            document.addField("title", funko.getTitle());
            document.addField("fandom", funko.getFandom());
            document.addField("seriesId", funko.getSeriesId());
            document.addField("name", funko.getName());
            client.add(document);
        }

        client.commit();
    }
}
