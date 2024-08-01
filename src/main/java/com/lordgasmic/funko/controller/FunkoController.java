package com.lordgasmic.funko.controller;

import com.lordgasmic.funko.model.FunkoResponse;
import com.lordgasmic.funko.model.IndexResponse;
import com.lordgasmic.funko.service.FunkoIndexService;
import com.lordgasmic.funko.service.FunkoService;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
public class FunkoController {

    private final FunkoService service;
    private final FunkoIndexService indexService;

    public FunkoController(FunkoService service, FunkoIndexService indexService) {
        this.service = service;
        this.indexService = indexService;
    }

    @GetMapping("/api/v1/funkos")
    public List<FunkoResponse> getAllFunkos() throws SolrServerException, IOException {
        return service.getAllFunkos();
    }

    @GetMapping("/api/v1/funkos/extras")
    public List<FunkoResponse> getFunkosExtras() throws ExecutionException, InterruptedException {
        return service.getAllFunkosWithExtras();
    }

    @PutMapping("/api/v1/funkos")
    public ResponseEntity<IndexResponse> index() throws SQLException, SolrServerException, IOException {
        indexService.index();

        return ResponseEntity.ok(new IndexResponse("success"));
    }
}
