package com.lordgasmic.funko.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FunkoExtra {
    private long id;
    private long funkoId;
    private String text;
}
