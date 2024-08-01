package com.lordgasmic.funko.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.Http2SolrClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class SolrClientConfig {

    @Value("${apache.solr.address}")
    private String solrAddress;

    @Bean
    public SolrClient solrClient() {
        log.info("solr address: {}", solrAddress);
        return new Http2SolrClient.Builder(solrAddress).build();
    }
}
