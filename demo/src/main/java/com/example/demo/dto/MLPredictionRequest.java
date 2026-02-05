package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MLPredictionRequest {
    // Matches the "data" field in your Python Pydantic model
    private List<List<Double>> data;
}