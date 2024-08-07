package com.lordgasmic.funko.service;

import com.lordgasmic.funko.model.FunkoExtrasResponse;
import com.lordgasmic.funko.model.FunkoResponse;
import com.lordgasmic.funko.repository.GSARepositoryAdapter;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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

        client.deleteByQuery("*:*");

        for (FunkoResponse funko : funkos) {
            SolrInputDocument document = new SolrInputDocument();
            document.addField("id", funko.getId());
            document.addField("title", funko.getTitle());
            document.addField("fandom", funko.getFandom());
            document.addField("seriesId", funko.getSeriesId());
            document.addField("name", funko.getName());
//            List<Integer> extras = new ArrayList<>();
            List<SolrInputDocument> extras = new ArrayList<>();
            for (FunkoExtrasResponse funkoExtras : funko.getExtras()) {
                SolrInputDocument doc = new SolrInputDocument();
                doc.addField("id", UUID.randomUUID().toString());
                doc.addField("extraId", funkoExtras.getId());
                doc.addField("funkoId", funkoExtras.getFunkoId());
                doc.addField("text", funkoExtras.getText());
//                document.addChildDocument(doc);
                extras.add(doc);


//                extras.add(funkoExtras.getFunkoId());
            }
            document.addField("extras", extras);
            client.add(document);
        }

        client.commit();
    }
}
