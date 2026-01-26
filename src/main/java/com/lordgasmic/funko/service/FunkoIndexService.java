package com.lordgasmic.funko.service;

import com.lordgasmic.funko.model.Funko;
import com.lordgasmic.funko.model.IndexData;
import com.lordgasmic.funko.repository.GSARepositoryAdapter;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.Refresh;
import org.opensearch.client.opensearch.core.BulkRequest;
import org.opensearch.client.opensearch.core.BulkResponse;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.opensearch.client.opensearch.core.bulk.BulkOperation;
import org.opensearch.client.opensearch.core.bulk.IndexOperation;
import org.opensearch.client.opensearch.indices.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.lordgasmic.funko.config.OpenSearchConstants.INDEX_NAME;

@Service
@Slf4j
public class FunkoIndexService {

    private final GSARepositoryAdapter repositoryAdapter;
    private final OpenSearchClient client;

    public FunkoIndexService(final GSARepositoryAdapter repositoryAdapter, final OpenSearchClient client) {
        this.repositoryAdapter = repositoryAdapter;
        this.client = client;
    }

    public void index() throws ExecutionException, InterruptedException, IOException {
        final List<Funko> funkos = repositoryAdapter.getAllFunkosWithExtras();

        // Delete the index
        log.info("delete");
        final DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest.Builder().index(INDEX_NAME).build();
        final DeleteIndexResponse deleteIndexResponse = client.indices().delete(deleteIndexRequest);
        log.info("delete response: {}", deleteIndexResponse.toJsonString());

        //Create the index
        log.info("create");
        final CreateIndexRequest createIndexRequest = new CreateIndexRequest.Builder().index(INDEX_NAME).build();
        client.indices().create(createIndexRequest);

        //Add some settings to the index
        log.info("settings");
        final IndexSettings indexSettings = new IndexSettings.Builder().autoExpandReplicas("0-all").build();
        final PutIndicesSettingsRequest putSettingsRequest = new PutIndicesSettingsRequest.Builder().index(INDEX_NAME).settings(indexSettings).build();
        client.indices().putSettings(putSettingsRequest);

        //Index some data
        log.info("data");
        final IndexData indexData = new IndexData("first_name", "Bruce");
        final IndexRequest<IndexData> indexRequest = new IndexRequest.Builder<IndexData>().index(INDEX_NAME).id("1").document(indexData).build();
        client.index(indexRequest);

        final List<BulkOperation> ops = new ArrayList<>();

        for (int i = 0; i < 10_000; i++) {
            final IndexData doc1 = new IndexData("Document " + i, "The text of document " + i);
            final String id = "id" + i;
            ops.add(new BulkOperation.Builder().index(
                    IndexOperation.of(io -> io.index(INDEX_NAME).id(id).document(doc1))
            ).build());
        }

        final BulkRequest.Builder bulkReq = new BulkRequest.Builder()
                .index(INDEX_NAME)
                .operations(ops)
                .refresh(Refresh.WaitFor);
        final BulkResponse bulkResponse = client.bulk(bulkReq.build());

        //Delete the document
//        client.delete(b -> b.index(index).id("1"));
    }
}
