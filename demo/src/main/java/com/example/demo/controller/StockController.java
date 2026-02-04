package com.example.demo.controller;

import com.example.demo.dto.CandleStickData;
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

    @Autowired
    public StockController(YahooFinanceService yahooFinanceService) {
        this.yahooFinanceService = yahooFinanceService;
    }

    @GetMapping("/{ticker}/history")
    public ResponseEntity<List<CandleStickData>> getStockHistory(@PathVariable String ticker) {
        List<CandleStickData> history = yahooFinanceService.getStockHistory(ticker);
        return ResponseEntity.ok(history);
    }
}