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
import java.util.Map;

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
            Object rawExtras = doc.getFieldValue("extras");
            if (rawExtras != null) {
                if (rawExtras instanceof List extrasList) {
                    for (Object extra : extrasList) {
                        funko.getExtras().add(extractFromSolrDocument((SolrDocument) extra));
                    }
                } else if(rawExtras instanceof SolrDocument childExtras) {
                    funko.getExtras().add(extractFromSolrDocument(childExtras));
                }
            }
//            SolrDocument extrasDoc = (SolrDocument) doc.getFieldValue("extras");
//                System.out.println(doc.getFieldNames());
//                System.out.println(extrasDoc.getFieldNames());
//            funko.getExtras().addAll((List<FunkoExtra>) doc.getFieldValue("extras"));
            funkos.add(funko);
        }

        funkoResponse.setStart(docList.getStart());
        funkoResponse.setNumFound(docList.getNumFound());
        funkoResponse.setFunkos(funkos);

        return funkoResponse;
    }

    private static FunkoExtra extractFromSolrDocument(SolrDocument doc) {
        FunkoExtra extra = new FunkoExtra();
        extra.setId((Integer) doc.getFieldValue("extraId"));
        extra.setFunkoId((Integer) doc.getFieldValue("funkoId"));
        extra.setText((String) doc.getFieldValue("text"));
        return extra;
    }
}
