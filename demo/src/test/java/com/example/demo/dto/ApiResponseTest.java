package com.example.demo.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ApiResponseTest {

    private static final long TIMESTAMP_ALLOWANCE_MS = 5_000; // 5 seconds

    @Nested
    @DisplayName("success(T data)")
    class SuccessWithDataTests {
        @Test
        @DisplayName("Given data when success(data) then response is successful with default message and timestamp")
        void givenData_whenSuccessWithData_thenResponseOk() {
            String payload = "payload";
            ApiResponse<String> resp = ApiResponse.success(payload);

            assertTrue(resp.isSuccess());
            assertEquals("Success", resp.getMessage());
            assertEquals(payload, resp.getData());
            assertNotNull(resp.getTimestamp());
            long ageMs = Math.abs(Duration.between(resp.getTimestamp(), LocalDateTime.now()).toMillis());
            assertTrue(ageMs < TIMESTAMP_ALLOWANCE_MS, "timestamp should be recent");
        }
    }

    @Nested
    @DisplayName("success(String message, T data)")
    class SuccessWithMessageAndDataTests {
        @Test
        @DisplayName("Given custom message and data when success(message, data) then response uses provided message and data")
        void givenMessageAndData_whenSuccessWithMessageAndData_thenResponseOk() {
            Integer value = 42;
            String customMsg = "All good";
            ApiResponse<Integer> resp = ApiResponse.success(customMsg, value);

            assertTrue(resp.isSuccess());
            assertEquals(customMsg, resp.getMessage());
            assertEquals(value, resp.getData());
            assertNotNull(resp.getTimestamp());
            long ageMs = Math.abs(Duration.between(resp.getTimestamp(), LocalDateTime.now()).toMillis());
            assertTrue(ageMs < TIMESTAMP_ALLOWANCE_MS, "timestamp should be recent");
        }
    }

    @Nested
    @DisplayName("error(String message)")
    class ErrorTests {
        @Test
        @DisplayName("Given message when error(message) then response is failure with no data and timestamp")
        void givenMessage_whenError_thenResponseFailure() {
            String errMsg = "Something went wrong";
            ApiResponse<Object> resp = ApiResponse.error(errMsg);

            assertFalse(resp.isSuccess());
            assertEquals(errMsg, resp.getMessage());
            assertNull(resp.getData());
            assertNotNull(resp.getTimestamp());
            long ageMs = Math.abs(Duration.between(resp.getTimestamp(), LocalDateTime.now()).toMillis());
            assertTrue(ageMs < TIMESTAMP_ALLOWANCE_MS, "timestamp should be recent");
        }
    }
}
