package com.lordgasmic.funko.controller;

import com.lordgasmic.funko.model.Funko;
import com.lordgasmic.funko.model.FunkoResponse;
import com.lordgasmic.funko.model.IndexResponse;
import com.lordgasmic.funko.service.FunkoIndexService;
import com.lordgasmic.funko.service.FunkoService;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
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
    public FunkoResponse getFunkos(@RequestParam Optional<Integer> start, @RequestParam Optional<Integer> count) throws SolrServerException, IOException {
        return service.getFunkos(start.orElse(0), count.orElse(10));
    }

    @PutMapping("/api/v1/funkos")
    public ResponseEntity<IndexResponse> index() throws SolrServerException, IOException, ExecutionException, InterruptedException {
        indexService.index();

        return ResponseEntity.ok(new IndexResponse("success"));
    }
}
