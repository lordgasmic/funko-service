package com.lordgasmic.funko.controller;

import com.lordgasmic.funko.model.FunkoResponse;
import com.lordgasmic.funko.service.FunkoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
public class FunkoController {

    private FunkoService service;

    public FunkoController(FunkoService service) {
        this.service = service;
    }

    @GetMapping("/api/v1/funkos")
    public List<FunkoResponse> getAllFunkos() throws SQLException {
        return service.getAllFunkos();
    }

    @GetMapping("/api/v1/funkos/extras")
    public List<FunkoResponse> getFunkosExtras() throws ExecutionException, InterruptedException {
        return service.getAllFunkosWithExtras();
    }
}
