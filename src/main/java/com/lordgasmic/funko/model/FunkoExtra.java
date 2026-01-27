package com.lordgasmic.funko.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FunkoExtra {
    private long id;
    private long funkoId;
    private String text;
}
