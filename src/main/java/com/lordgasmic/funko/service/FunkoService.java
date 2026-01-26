package com.lordgasmic.funko.service;

import com.lordgasmic.funko.model.Funko;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.lordgasmic.funko.config.OpenSearchConstants.INDEX_NAME;

@Service
@Slf4j
public class FunkoService {

    private final OpenSearchClient client;

    public FunkoService(final OpenSearchClient client) {
        this.client = client;
    }

    public void search() throws IOException {
        //Search for the document
        log.info("search");
        final SearchResponse<Funko> searchResponse = client.search(s -> s.index(INDEX_NAME), Funko.class);
        log.info("search total hits: {}", searchResponse.hits().total().value());
        log.info("search hits size: {}", searchResponse.hits().hits().size());
        for (int i = 0; i < searchResponse.hits().hits().size(); i++) {
            log.info("LGC[SearchResponse]: {}", searchResponse.hits().hits().get(i).source());
        }
    }

    public void add() throws IOException {
//        final Funko indexData = new Funko("first_name", "Derp");
//        final IndexRequest<Funko> indexRequest = new IndexRequest.Builder<Funko>().index(INDEX_NAME).document(indexData).build();
//        client.index(indexRequest);
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
