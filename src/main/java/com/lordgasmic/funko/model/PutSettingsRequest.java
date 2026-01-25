package com.lordgasmic.funko.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.opensearch.client.opensearch.indices.PutIndicesSettingsRequest;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class PutSettingsRequest extends PutIndicesSettingsRequest.Builder {
    private String index;
    private IndexSettingsBody value;
}
