package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CandleStickData {
    private long time;  // Unix timestamp
    private double open;
    private double high;
    private double low;
    private double close;
    private long volume;
}