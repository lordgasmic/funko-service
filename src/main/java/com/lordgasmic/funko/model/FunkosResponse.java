package com.lordgasmic.funko.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FunkosResponse {

    private int from;
    private int size;
    private long numFound;
    private List<Funko> funkos;
}
