package com.lordgasmic.funko.service;

import com.lordgasmic.funko.entities.FunkoEntity;
import com.lordgasmic.funko.entities.FunkoExtrasEntity;
import com.lordgasmic.funko.mappers.FunkoExtrasMapper;
import com.lordgasmic.funko.mappers.FunkoMapper;
import com.lordgasmic.funko.model.Funko;
import com.lordgasmic.funko.model.FunkoExtra;
import com.lordgasmic.funko.model.FunkoRequest;
import com.lordgasmic.funko.model.FunkosResponse;
import com.lordgasmic.funko.repository.FunkoExtrasRepository;
import com.lordgasmic.funko.repository.FunkoRepository;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.Refresh;
import org.opensearch.client.opensearch.core.*;
import org.opensearch.client.opensearch.core.bulk.BulkOperation;
import org.opensearch.client.opensearch.core.bulk.IndexOperation;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.lordgasmic.funko.config.OpenSearchConstants.INDEX_NAME;

@Service
@Slf4j
public class FunkoService {

    private final OpenSearchClient client;
    private final FunkoRepository funkoRepository;
    private final FunkoExtrasRepository funkoExtrasRepository;

    public FunkoService(final OpenSearchClient client, final FunkoRepository funkoRepository, final FunkoExtrasRepository funkoExtrasRepository) {
        this.client = client;
        this.funkoRepository = funkoRepository;
        this.funkoExtrasRepository = funkoExtrasRepository;
    }

    public void index() throws IOException {
        final List<FunkoEntity> funkoEntities = funkoRepository.findAll();
        final List<FunkoExtrasEntity> funkoExtrasEntities = funkoExtrasRepository.findAll();

        final Map<Long, Funko> funkoMap = funkoEntities.stream().map(FunkoMapper::toFunko).map(f -> Map.entry(f.getId(), f)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        final List<FunkoExtra> funkoExtras = funkoExtrasEntities.stream().map(FunkoExtrasMapper::toFunkoExtra).toList();

        for (final FunkoExtra funkoExtra : funkoExtras) {
            final Funko funko = funkoMap.get(funkoExtra.getFunkoId());
            if (funko != null) {
                funko.getExtras().add(funkoExtra);
                funkoMap.put(funko.getId(), funko);
            }
        }

        final List<Funko> funkos = new ArrayList<>(funkoMap.values());

        //Index some data
        log.info("data");
        final List<BulkOperation> ops = new ArrayList<>();
        for (final Funko funko : funkos) {
            ops.add(new BulkOperation.Builder()
                    .index(IndexOperation.of(io -> io.index(INDEX_NAME).id(Long.valueOf(funko.getId()).toString()).document(funko))
                    ).build());
        }

        final BulkRequest.Builder bulkReq = new BulkRequest.Builder()
                .index(INDEX_NAME)
                .operations(ops)
                .refresh(Refresh.WaitFor);
        final BulkResponse bulkResponse = client.bulk(bulkReq.build());
    }

    public FunkosResponse findAll(final int from, final int size) throws IOException {
        final FunkosResponse response = FunkosResponse.builder().from(from).size(size).funkos(new ArrayList<>()).build();

        final SearchResponse<Funko> searchResponse = client.search(s -> s.index(INDEX_NAME).from(from).size(size), Funko.class);
        response.setNumFound(searchResponse.hits().total().value());
        for (int i = 0; i < searchResponse.hits().hits().size(); i++) {
            log.info("LGC[SearchResponse]: {}", searchResponse.hits().hits().get(i).source());
            response.getFunkos().add(searchResponse.hits().hits().get(i).source());
        }

        return response;
    }

    public Funko search(final String id) throws IOException {
        final GetRequest request = GetRequest.of(g -> g.index(INDEX_NAME).id(id));
        final GetResponse<Funko> response = client.get(request, Funko.class);
        if (response.found()) {
            return response.source();
        }

        return null;
    }

    public void add(final FunkoRequest request) throws IOException {
        final FunkoEntity funkoEntity = funkoRepository.save(FunkoMapper.toEntity(request));
        final Funko funko = FunkoMapper.toFunko(funkoEntity);
        if (!request.getExtras().isEmpty()) {
            final List<FunkoExtrasEntity> entities = funkoExtrasRepository.saveAll(FunkoExtrasMapper.toEntity(funkoEntity.getId(), request.getExtras()));
            final List<FunkoExtra> extras = entities.stream().map(FunkoExtrasMapper::toFunkoExtra).toList();
            funko.setExtras(extras);
        }

        final IndexRequest<Funko> indexRequest = new IndexRequest.Builder<Funko>().index(INDEX_NAME).document(funko).build();
        client.index(indexRequest);
    }
}
