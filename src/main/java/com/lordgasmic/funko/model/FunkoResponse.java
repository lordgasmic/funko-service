package com.lordgasmic.funko.model;

import lombok.Data;

import java.util.List;

@Data
public class FunkoResponse {

    private long start;
    private long numFound;
    private List<Funko> funkos;
}
