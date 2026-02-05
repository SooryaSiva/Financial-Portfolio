package com.example.demo.dto;

import com.example.demo.entity.AssetType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AssetDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    @DisplayName("builder and accessors")
    class BuilderAndAccessors {
        @Test
        @DisplayName("Given builder values when building AssetDTO then getters return same values")
        void givenBuilderValues_whenBuild_thenAccessorsWork() {
            AssetDTO dto = AssetDTO.builder()
                    .id(1L)
                    .symbol("AAPL")
                    .name("Apple Inc.")
                    .type(AssetType.STOCK)
                    .quantity(new BigDecimal("10"))
                    .buyPrice(new BigDecimal("150.00"))
                    .purchaseDate(LocalDate.of(2023, 1, 1))
                    .currentPrice(new BigDecimal("155.00"))
                    .currentValue(new BigDecimal("1550.00"))
                    .costBasis(new BigDecimal("1500.00"))
                    .gainLoss(new BigDecimal("50.00"))
                    .gainLossPercentage(new BigDecimal("3.33"))
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            assertEquals(1L, dto.getId());
            assertEquals("AAPL", dto.getSymbol());
            assertEquals("Apple Inc.", dto.getName());
            assertEquals(AssetType.STOCK, dto.getType());
            assertEquals(new BigDecimal("10"), dto.getQuantity());
            assertEquals(new BigDecimal("150.00"), dto.getBuyPrice());
            assertEquals(LocalDate.of(2023, 1, 1), dto.getPurchaseDate());
            assertEquals(new BigDecimal("155.00"), dto.getCurrentPrice());
            assertEquals(new BigDecimal("1550.00"), dto.getCurrentValue());
            assertEquals(new BigDecimal("1500.00"), dto.getCostBasis());
            assertEquals(new BigDecimal("50.00"), dto.getGainLoss());
            assertNotNull(dto.getCreatedAt());
            assertNotNull(dto.getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("validation constraints")
    class ValidationTests {
        @Test
        @DisplayName("Valid DTO should have no constraint violations")
        void validDto_noViolations() {
            AssetDTO dto = AssetDTO.builder()
                    .symbol("GOOG")
                    .name("Google LLC")
                    .type(AssetType.STOCK)
                    .quantity(new BigDecimal("1.0001"))
                    .buyPrice(new BigDecimal("10.00"))
                    .build();

            Set<ConstraintViolation<AssetDTO>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Blank symbol triggers NotBlank violation")
        void blankSymbol_violates() {
            AssetDTO dto = AssetDTO.builder()
                    .symbol(" ")
                    .name("Name")
                    .type(AssetType.STOCK)
                    .quantity(new BigDecimal("1"))
                    .buyPrice(new BigDecimal("1"))
                    .build();

            Set<ConstraintViolation<AssetDTO>> violations = validator.validate(dto);
            assertTrue(violations.stream().anyMatch(v -> "symbol".equals(v.getPropertyPath().toString())));
        }

        @Test
        @DisplayName("Too long symbol triggers Size violation")
        void tooLongSymbol_violates() {
            String longSym = "S".repeat(21);
            AssetDTO dto = AssetDTO.builder()
                    .symbol(longSym)
                    .name("Name")
                    .type(AssetType.CASH)
                    .quantity(new BigDecimal("1"))
                    .buyPrice(new BigDecimal("1"))
                    .build();

            Set<ConstraintViolation<AssetDTO>> violations = validator.validate(dto);
            assertTrue(violations.stream().anyMatch(v -> "symbol".equals(v.getPropertyPath().toString())));
        }

        @Test
        @DisplayName("Blank name triggers NotBlank violation")
        void blankName_violates() {
            AssetDTO dto = AssetDTO.builder()
                    .symbol("SYM")
                    .name(" ")
                    .type(AssetType.BOND)
                    .quantity(new BigDecimal("1"))
                    .buyPrice(new BigDecimal("1"))
                    .build();

            Set<ConstraintViolation<AssetDTO>> violations = validator.validate(dto);
            assertTrue(violations.stream().anyMatch(v -> "name".equals(v.getPropertyPath().toString())));
        }

        @Test
        @DisplayName("Too long name triggers Size violation")
        void tooLongName_violates() {
            String longName = "N".repeat(101);
            AssetDTO dto = AssetDTO.builder()
                    .symbol("SYM")
                    .name(longName)
                    .type(AssetType.BOND)
                    .quantity(new BigDecimal("1"))
                    .buyPrice(new BigDecimal("1"))
                    .build();

            Set<ConstraintViolation<AssetDTO>> violations = validator.validate(dto);
            assertTrue(violations.stream().anyMatch(v -> "name".equals(v.getPropertyPath().toString())));
        }

        @Test
        @DisplayName("Null type triggers NotNull violation")
        void nullType_violates() {
            AssetDTO dto = AssetDTO.builder()
                    .symbol("SYM")
                    .name("Name")
                    .type(null)
                    .quantity(new BigDecimal("1"))
                    .buyPrice(new BigDecimal("1"))
                    .build();

            Set<ConstraintViolation<AssetDTO>> violations = validator.validate(dto);
            assertTrue(violations.stream().anyMatch(v -> "type".equals(v.getPropertyPath().toString())));
        }

        @Test
        @DisplayName("Null quantity triggers NotNull violation")
        void nullQuantity_violates() {
            AssetDTO dto = AssetDTO.builder()
                    .symbol("SYM")
                    .name("Name")
                    .type(AssetType.STOCK)
                    .quantity(null)
                    .buyPrice(new BigDecimal("1"))
                    .build();

            Set<ConstraintViolation<AssetDTO>> violations = validator.validate(dto);
            assertTrue(violations.stream().anyMatch(v -> "quantity".equals(v.getPropertyPath().toString())));
        }

        @Test
        @DisplayName("Quantity below minimum triggers DecimalMin violation")
        void smallQuantity_violates() {
            AssetDTO dto = AssetDTO.builder()
                    .symbol("SYM")
                    .name("Name")
                    .type(AssetType.STOCK)
                    .quantity(new BigDecimal("0.00001"))
                    .buyPrice(new BigDecimal("1"))
                    .build();

            Set<ConstraintViolation<AssetDTO>> violations = validator.validate(dto);
            assertTrue(violations.stream().anyMatch(v -> "quantity".equals(v.getPropertyPath().toString())));
        }

        @Test
        @DisplayName("Null buyPrice triggers NotNull violation")
        void nullBuyPrice_violates() {
            AssetDTO dto = AssetDTO.builder()
                    .symbol("SYM")
                    .name("Name")
                    .type(AssetType.STOCK)
                    .quantity(new BigDecimal("1"))
                    .buyPrice(null)
                    .build();

            Set<ConstraintViolation<AssetDTO>> violations = validator.validate(dto);
            assertTrue(violations.stream().anyMatch(v -> "buyPrice".equals(v.getPropertyPath().toString())));
        }

        @Test
        @DisplayName("Buy price below minimum triggers DecimalMin violation")
        void smallBuyPrice_violates() {
            AssetDTO dto = AssetDTO.builder()
                    .symbol("SYM")
                    .name("Name")
                    .type(AssetType.STOCK)
                    .quantity(new BigDecimal("1"))
                    .buyPrice(new BigDecimal("0.001"))
                    .build();

            Set<ConstraintViolation<AssetDTO>> violations = validator.validate(dto);
            assertTrue(violations.stream().anyMatch(v -> "buyPrice".equals(v.getPropertyPath().toString())));
        }
    }
}
