package com.example.demo.service;

import com.example.demo.dto.StockNews;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockPriceServiceImplTest {

    @Mock
    private org.springframework.web.client.RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    private StockPriceServiceImpl createServiceWithConfig(String baseUrl, String apiKey) throws Exception {
        StockPriceServiceImpl svc = new StockPriceServiceImpl(restTemplate, objectMapper);
        // set private fields via reflection
        Field fBase = StockPriceServiceImpl.class.getDeclaredField("baseUrl");
        fBase.setAccessible(true);
        fBase.set(svc, baseUrl);
        Field fApi = StockPriceServiceImpl.class.getDeclaredField("apiKey");
        fApi.setAccessible(true);
        fApi.set(svc, apiKey);
        return svc;
    }

    @Nested
    @DisplayName("getCurrentPrice")
    class GetCurrentPriceTests {
        @Test
        @DisplayName("Given known symbol when getCurrentPrice then return price and cache it")
        void givenKnownSymbol_whenGetCurrentPrice_thenReturnPriceAndCache() throws Exception {
            var realMapper = new ObjectMapper();
            String json = "{\"c\":150.25}";
            JsonNode node = realMapper.readTree(json);

            when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(json);
            when(objectMapper.readTree(json)).thenReturn(node);

            StockPriceServiceImpl svc = createServiceWithConfig("https://api.test", "key");

            BigDecimal first = svc.getCurrentPrice("aapl");
            assertEquals(new BigDecimal("150.25"), first);

            // second call should hit cache: restTemplate called once
            BigDecimal second = svc.getCurrentPrice("AAPL");
            assertEquals(new BigDecimal("150.25"), second);

            verify(restTemplate, times(1)).getForObject(anyString(), eq(String.class));
            verify(objectMapper, times(1)).readTree(json);
        }

        @Test
        @DisplayName("Given invalid symbol when getCurrentPrice then return null")
        void givenInvalidSymbol_whenGetCurrentPrice_thenReturnNull() throws Exception {
            var realMapper = new ObjectMapper();
            String json = "{\"c\":0}";
            JsonNode node = realMapper.readTree(json);

            when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(json);
            when(objectMapper.readTree(json)).thenReturn(node);

            StockPriceServiceImpl svc = createServiceWithConfig("https://api.test", "key");

            assertNull(svc.getCurrentPrice("xxxx"));
            verify(restTemplate).getForObject(anyString(), eq(String.class));
        }

        @Test
        @DisplayName("Given null symbol when getCurrentPrice then return null and do not call restTemplate")
        void givenNull_whenGetCurrentPrice_thenReturnNull() throws Exception {
            StockPriceServiceImpl svc = createServiceWithConfig("https://api.test", "key");
            assertNull(svc.getCurrentPrice(null));
            verifyNoInteractions(restTemplate, objectMapper);
        }
    }

    @Nested
    @DisplayName("getCurrentPrices")
    class GetCurrentPricesTests {
        @Test
        @DisplayName("Given multiple symbols when getCurrentPrices then return map")
        void givenMultipleSymbols_whenGetCurrentPrices_thenReturnMap() throws Exception {
            var realMapper = new ObjectMapper();
            String j1 = "{\"c\":150.0}";
            String j2 = "{\"c\":300.0}";

            // Delegate objectMapper.readTree to real parsing so responses are handled
            when(restTemplate.getForObject(anyString(), eq(String.class)))
                    .thenReturn(j1)
                    .thenReturn(j2);
            when(objectMapper.readTree(anyString())).thenAnswer(inv -> realMapper.readTree((String)inv.getArgument(0)));

            StockPriceServiceImpl svc = createServiceWithConfig("https://api.test", "key");

            Map<String, BigDecimal> map = svc.getCurrentPrices("aapl", "msft");
            assertEquals(2, map.size());
            assertEquals(new BigDecimal("150.0"), map.get("AAPL"));
            assertEquals(new BigDecimal("300.0"), map.get("MSFT"));

            verify(restTemplate, times(2)).getForObject(anyString(), eq(String.class));
        }

        @Test
        @DisplayName("Given empty input when getCurrentPrices then return empty map")
        void givenEmptyInput_whenGetCurrentPrices_thenReturnEmptyMap() throws Exception {
            StockPriceServiceImpl svc = createServiceWithConfig("https://api.test", "key");
            Map<String, BigDecimal> map = svc.getCurrentPrices();
            assertTrue(map.isEmpty());
            verifyNoInteractions(restTemplate);
        }
    }

    @Nested
    @DisplayName("isValidSymbol")
    class IsValidSymbolTests {
        @Test
        @DisplayName("Given valid symbol when isValidSymbol then true")
        void givenValidSymbol_whenIsValidSymbol_thenTrue() throws Exception {
            var realMapper = new ObjectMapper();
            String json = "{\"c\":10.0}";
            JsonNode node = realMapper.readTree(json);

            when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(json);
            when(objectMapper.readTree(json)).thenReturn(node);

            StockPriceServiceImpl svc = createServiceWithConfig("https://api.test", "key");
            assertTrue(svc.isValidSymbol("tsla"));
        }

        @Test
        @DisplayName("Given invalid symbol when isValidSymbol then false")
        void givenInvalid_whenIsValidSymbol_thenFalse() throws Exception {
            var realMapper = new ObjectMapper();
            String json = "{\"c\":0}";
            JsonNode node = realMapper.readTree(json);

            when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(json);
            when(objectMapper.readTree(json)).thenReturn(node);

            StockPriceServiceImpl svc = createServiceWithConfig("https://api.test", "key");
            assertFalse(svc.isValidSymbol("bad"));
        }
    }

    @Nested
    @DisplayName("news methods")
    class NewsTests {
        @Test
        @DisplayName("getMarketNews delegates to objectMapper and returns list")
        void getMarketNews_delegatesAndReturnsList() throws Exception {
            String json = "[{\"headline\":\"h\",\"summary\":\"s\",\"url\":\"u\"}]";
            StockNews sn = new StockNews();
            sn.setHeadline("h");
            sn.setSummary("s");
            sn.setUrl("u");
            List<StockNews> expected = List.of(sn);

            when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(json);
            when(objectMapper.readValue(eq(json), any(com.fasterxml.jackson.core.type.TypeReference.class)))
                    .thenReturn(expected);

            StockPriceServiceImpl svc = createServiceWithConfig("https://api.test", "key");
            List<StockNews> result = svc.getMarketNews("general");

            assertEquals(expected, result);
            verify(objectMapper).readValue(eq(json), any(com.fasterxml.jackson.core.type.TypeReference.class));
        }

        @Test
        @DisplayName("getCompanyNews delegates and returns list with date range")
        void getCompanyNews_delegatesAndReturnsList() throws Exception {
            String json = "[{\"headline\":\"h2\",\"summary\":\"s2\",\"url\":\"u2\"}]";
            StockNews sn2 = new StockNews();
            sn2.setHeadline("h2");
            sn2.setSummary("s2");
            sn2.setUrl("u2");
            List<StockNews> expected = List.of(sn2);

            when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(json);
            when(objectMapper.readValue(eq(json), any(com.fasterxml.jackson.core.type.TypeReference.class)))
                    .thenReturn(expected);

            StockPriceServiceImpl svc = createServiceWithConfig("https://api.test", "key");
            LocalDate from = LocalDate.now().minusDays(5);
            LocalDate to = LocalDate.now();
            List<StockNews> result = svc.getCompanyNews("AAPL", from, to);

            assertEquals(expected, result);
            verify(objectMapper).readValue(eq(json), any(com.fasterxml.jackson.core.type.TypeReference.class));
        }
    }
}
