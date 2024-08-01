package com.lordgasmic.funko.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.solr.client.solrj.beans.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IndexRequest {

    @Field
    private int id;

    @Field
    private int seriesId;

    @Field
    private String fandom;

    @Field
    private String title;

    @Field
    private String name;
}
