package com.lordgasmic.funko.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Funko {
    private long id;
    private String title;
    private String fandom;
    private long seriesId;
    private String name;
    private List<FunkoExtra> extras = new ArrayList<>();
}
