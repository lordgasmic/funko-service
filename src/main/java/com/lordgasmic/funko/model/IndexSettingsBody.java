package com.lordgasmic.funko.model;

import lombok.Builder;
import lombok.Data;
import org.opensearch.client.opensearch.indices.IndexSettings;

@Data
@Builder
public class IndexSettingsBody {
    private IndexSettings settings;
}
