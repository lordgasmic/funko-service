package com.lordgasmic.funko.service;

import com.lordgasmic.funko.model.FunkoResponse;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.Http2SolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@Service
public class FunkoIndexService {

    private final SolrClient client;
    private final FunkoService service;

    public FunkoIndexService(FunkoService service) {
        this.service = service;

        client = new Http2SolrClient.Builder("http://localhost:8983/solr/funkos").build();
    }

    public void index() throws SQLException, SolrServerException, IOException {
        List<FunkoResponse> funkos = service.getAllFunkos();

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
}
