package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MLPredictionResponse {
    @JsonProperty("predicted_price")
    private Double predictedPrice;

    @JsonProperty("raw_scaled_output")
    private Double rawScaledOutput;
}