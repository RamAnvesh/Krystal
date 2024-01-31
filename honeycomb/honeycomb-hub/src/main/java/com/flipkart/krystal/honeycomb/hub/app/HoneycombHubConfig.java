package com.flipkart.krystal.honeycomb.hub.app;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flipkart.gjex.core.GJEXConfiguration;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public final class HoneycombHubConfig extends GJEXConfiguration {}
