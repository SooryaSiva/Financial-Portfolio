package com.example.demo.controller;

import com.example.demo.dto.CandleStickData;
import com.example.demo.dto.MLPredictionResponse;
import com.example.demo.service.StockPredictionService;
import com.example.demo.service.YahooFinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stocks")
@CrossOrigin(origins = "*") // Allow frontend to access this
public class StockController {

    private final YahooFinanceService yahooFinanceService;
    private final StockPredictionService stockPredictionService;

    @Autowired
    public StockController(YahooFinanceService yahooFinanceService, StockPredictionService stockPredictionService) {
        this.yahooFinanceService = yahooFinanceService;
        this.stockPredictionService = stockPredictionService;
    }

    @GetMapping("/{ticker}/history")
    public ResponseEntity<List<CandleStickData>> getStockHistory(@PathVariable String ticker) {
        List<CandleStickData> history = yahooFinanceService.getStockHistory(ticker);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/{ticker}/predict")
    public ResponseEntity<MLPredictionResponse> getStockPrediction(@PathVariable String ticker) {
        MLPredictionResponse prediction = stockPredictionService.getPrediction(ticker);
        return ResponseEntity.ok(prediction);
    }
}