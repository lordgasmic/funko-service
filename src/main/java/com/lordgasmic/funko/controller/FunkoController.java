package com.lordgasmic.funko.controller;

import com.lordgasmic.funko.model.FunkoResponse;
import com.lordgasmic.funko.model.IndexResponse;
import com.lordgasmic.funko.service.FunkoIndexService;
import com.lordgasmic.funko.service.FunkoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

@RestController
public class FunkoController {

    private final FunkoService service;
    private final FunkoIndexService indexService;

    public FunkoController(final FunkoService service, final FunkoIndexService indexService) {
        this.service = service;
        this.indexService = indexService;
    }

    @GetMapping("/api/v1/funkos")
    public FunkoResponse getFunkos() throws IOException {
        service.search();
        return null;
    }

    @PostMapping("/api/v1/funkos")
    public ResponseEntity<Void> addEntity() throws IOException {
        service.add();
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/api/v1/funkos")
    public ResponseEntity<IndexResponse> index() throws IOException, ExecutionException, InterruptedException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        indexService.index();

        return ResponseEntity.ok(new IndexResponse("success"));
    }
}
