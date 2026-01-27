package com.lordgasmic.funko.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FunkoResponse {
    private Funko funko;
}
