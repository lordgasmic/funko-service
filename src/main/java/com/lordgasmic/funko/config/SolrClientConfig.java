package com.lordgasmic.funko.config;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.Http2SolrClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SolrClientConfig {

    @Value("${apache.solr.address}")
    private String solrAddress;

    @Bean
    public SolrClient solrClient() {
        return new Http2SolrClient.Builder(solrAddress).build();
    }
}
