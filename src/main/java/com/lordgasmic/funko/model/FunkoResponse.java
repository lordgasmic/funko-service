package com.lordgasmic.funko.model;

import lombok.Data;

import java.util.List;

@Data
public class FunkoResponse {
    private boolean hasMore;
    private List<Funko> funkos;
}
