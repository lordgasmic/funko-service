package com.lordgasmic.funko.service;

import com.lordgasmic.funko.model.Funko;
import com.lordgasmic.funko.model.IndexData;
import com.lordgasmic.funko.repository.GSARepositoryAdapter;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManager;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.ClientTlsStrategyBuilder;
import org.apache.hc.core5.function.Factory;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.nio.ssl.TlsStrategy;
import org.apache.hc.core5.reactor.ssl.TlsDetails;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.indices.CreateIndexRequest;
import org.opensearch.client.opensearch.indices.IndexSettings;
import org.opensearch.client.opensearch.indices.PutIndicesSettingsRequest;
import org.opensearch.client.transport.OpenSearchTransport;
import org.opensearch.client.transport.httpclient5.ApacheHttpClient5TransportBuilder;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class FunkoIndexService {

    private final GSARepositoryAdapter repositoryAdapter;

    public FunkoIndexService(final GSARepositoryAdapter repositoryAdapter) {
        this.repositoryAdapter = repositoryAdapter;
    }

    public void index() throws ExecutionException, InterruptedException, IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        final List<Funko> funkos = repositoryAdapter.getAllFunkosWithExtras();


        final HttpHost host = new HttpHost("https", "localhost", 9200);
        final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        // Only for demo purposes. Don't specify your credentials in code.
        credentialsProvider.setCredentials(new AuthScope(host), new UsernamePasswordCredentials("admin", "admin".toCharArray()));

        final SSLContext sslcontext = SSLContextBuilder
                .create()
                .loadTrustMaterial(null, (chains, authType) -> true)
                .build();

        final ApacheHttpClient5TransportBuilder builder = ApacheHttpClient5TransportBuilder.builder(host);
        builder.setHttpClientConfigCallback(httpClientBuilder -> {
            final TlsStrategy tlsStrategy = ClientTlsStrategyBuilder.create()
                    .setSslContext(sslcontext)
                    // See https://issues.apache.org/jira/browse/HTTPCLIENT-2219
                    .setTlsDetailsFactory(new Factory<SSLEngine, TlsDetails>() {
                        @Override
                        public TlsDetails create(final SSLEngine sslEngine) {
                            return new TlsDetails(sslEngine.getSession(), sslEngine.getApplicationProtocol());
                        }
                    })
                    .build();

            final PoolingAsyncClientConnectionManager connectionManager = PoolingAsyncClientConnectionManagerBuilder
                    .create()
                    .setTlsStrategy(tlsStrategy)
                    .build();

            return httpClientBuilder
                    .setDefaultCredentialsProvider(credentialsProvider)
                    .setConnectionManager(connectionManager);
        });

        final OpenSearchTransport transport = builder.build();
        final OpenSearchClient client = new OpenSearchClient(transport);

        //Create the index
        final String index = "sample-index";
        final CreateIndexRequest createIndexRequest = new CreateIndexRequest.Builder().index(index).build();
        client.indices().create(createIndexRequest);

        //Add some settings to the index
        final IndexSettings indexSettings = new IndexSettings.Builder().autoExpandReplicas("0-all").build();
        final PutIndicesSettingsRequest putSettingsRequest = new PutIndicesSettingsRequest.Builder().index(index).settings(indexSettings).build();
        client.indices().putSettings(putSettingsRequest);

        //Index some data
        final IndexData indexData = new IndexData("first_name", "Bruce");
        final IndexRequest<IndexData> indexRequest = new IndexRequest.Builder<IndexData>().index(index).id("1").document(indexData).build();
        client.index(indexRequest);

        //Search for the document
        final SearchResponse<IndexData> searchResponse = client.search(s -> s.index(index), IndexData.class);
        for (int i = 0; i < searchResponse.hits().hits().size(); i++) {
            log.info("LGC[SearchResponse]: {}", searchResponse.hits().hits().get(i).source());
        }

        //Delete the document
//        client.delete(b -> b.index(index).id("1"));

        // Delete the index
//        DeleteIndexRequest deleteIndexRequest = new DeleteRequest.Builder().index(index).build();
//        DeleteIndexResponse deleteIndexResponse = client.indices().delete(deleteIndexRequest);
    }
}
