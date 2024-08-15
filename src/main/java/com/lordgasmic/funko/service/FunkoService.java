package com.lordgasmic.funko.service;

import com.lordgasmic.funko.model.Funko;
import com.lordgasmic.funko.model.FunkoExtra;
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

@Service
public class FunkoService {

    private final SolrClient client;

    public FunkoService(SolrClient client) {
        this.client = client;
    }

    public FunkoResponse getFunkos(int start, int count) throws SolrServerException, IOException {
        FunkoResponse funkoResponse = new FunkoResponse();
        List<Funko> funkos = new ArrayList<>();

        SolrQuery query = new SolrQuery();
        query.set("q", "seriesId:[* TO *]");
        query.set("fl", "*,[child]");
        query.setStart(start);
        query.setRows(count);
        QueryResponse response = client.query(query);

        SolrDocumentList docList = response.getResults();
        for (SolrDocument doc : docList) {
            Funko funko = new Funko();
            funko.setId(Integer.parseInt((String) doc.getFieldValue("id")));
            funko.setTitle((String) doc.getFieldValue("title"));
            funko.setFandom((String) doc.getFieldValue("fandom"));
            funko.setSeriesId((Integer) doc.getFieldValue("seriesId"));
            funko.setName((String) doc.getFieldValue("name"));
            SolrDocument extrasDoc = (SolrDocument) doc.getFieldValue("extras");
            for(SolrDocument ed : extrasDoc.getChildDocuments()) {
                System.out.println(ed.getFieldValue("id"));
                System.out.println(ed.getFieldValue("funkoId"));
                System.out.println(ed.getFieldValue("text"));
            }
            funko.setExtras((List<FunkoExtra>) doc.getFieldValue("extras"));
            funkos.add(funko);
        }

        funkoResponse.setStart(docList.getStart());
        funkoResponse.setNumFound(docList.getNumFound());
        funkoResponse.setFunkos(funkos);

        return funkoResponse;
    }
}
