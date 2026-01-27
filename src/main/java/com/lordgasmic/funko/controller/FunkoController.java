package com.lordgasmic.funko.controller;

import com.lordgasmic.funko.model.Funko;
import com.lordgasmic.funko.model.FunkoRequest;
import com.lordgasmic.funko.model.FunkoResponse;
import com.lordgasmic.funko.model.FunkosResponse;
import com.lordgasmic.funko.service.FunkoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

@RestController
public class FunkoController {

    private final FunkoService service;

    public FunkoController(final FunkoService service) {
        this.service = service;
    }

    @GetMapping("/api/v1/funkos")
    public ResponseEntity<FunkosResponse> getFunkos(@RequestParam final Optional<Integer> from, @RequestParam final Optional<Integer> size) throws IOException {
        return ResponseEntity.ok(service.findAll(from.orElse(0), size.orElse(10)));
    }

    @GetMapping("/api/v1/funkos/{id}")
    public ResponseEntity<FunkoResponse> getFunko(@RequestParam final String id) throws IOException {
        final Funko funko = service.search(id);

        return ResponseEntity.ok(FunkoResponse.builder().funko(funko).build());
    }

    @PostMapping("/api/v1/funkos")
    public ResponseEntity<Void> addEntity(@RequestBody final FunkoRequest request) throws IOException {
        service.add(request);

        return ResponseEntity.accepted().build();
    }

    @PutMapping("/api/v1/funkos")
    public ResponseEntity<Void> index() throws IOException {
        service.index();

        return ResponseEntity.accepted().build();
    }
}
