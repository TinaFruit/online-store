package com.example.springProject.test;

import com.example.springproject.service.ProductSer;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.springproject.exeption.AppException;
import com.example.springproject.model.ProductSummaryDTO;
import com.example.springproject.repository.ProductRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductTest {

    @Mock
    private ProductRepo productRepo;

    @Mock
    private StringRedisTemplate redis;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private ProductSer productSer;

    @BeforeEach
    void setUp() {
        lenient().when(redis.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    @DisplayName("getAndDeserializeFromCache: returns null on cache miss")
    void getAndDeserializeFromCacheNULLTest() {
        // arrange
        when(valueOperations.get("product:1:10")).thenReturn(null);

        // act
        List<ProductSummaryDTO> result = productSer.getAndDeserializeFromCache("product:1:10");

        // assert
        assertNull(result);
    }

    @Test
    @DisplayName("getAndDeserializeFromCache: returns deserialized list on cache hit with valid JSON")
    void getAndDeserializeFromCacheSuccessTest() {
        // arrange
        String json = "[{\"id\":1,\"productName\":\"Apple\",\"description\":\"Fresh apple\","
                + "\"price\":9.99,\"category\":\"Fruit\",\"imageUrl\":\"http://img/1.png\"}]";
        when(valueOperations.get("product:1:10")).thenReturn(json);

        // act
        List<ProductSummaryDTO> result = productSer.getAndDeserializeFromCache("product:1:10");

        // assert
        assertEquals(1, result.size());
        assertEquals("Apple", result.get(0).getProductName());
    }

    @Test
    @DisplayName("getAndDeserializeFromCache: throws RuntimeException on cache hit with invalid JSON")
    void getAndDeserializeFromCache_whenJsonInvalid_throwsRuntimeException() {
        // arrange
        when(valueOperations.get("product:1:10")).thenReturn("this is not valid json{{{");

        // act + assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> productSer.getAndDeserializeFromCache("product:1:10")
        );
        assertEquals("cached issue", exception.getMessage());
    }

    // ========================================================
    // fetchAndMapFromDb tests
    // ========================================================

    @Test
    @DisplayName("fetchAndMapFromDb: correctly maps DB rows to DTOs, including Integer->Long id conversion")
    void fetchAndMapFromDb_whenDbHasData_returnsMappedDTOs() {
        // arrange
        Map<String, Object> row = new HashMap<>();
        row.put("id", 1); // intentionally Integer, to verify Number -> Long conversion logic
        row.put("product_name", "Apple");
        row.put("description", "Fresh apple");
        row.put("price", new BigDecimal("9.99"));
        row.put("category", "Fruit");
        row.put("image_url", "http://img/1.png");

        when(productRepo.displayRepo(1, 10)).thenReturn(List.of(row));

        // act
        List<ProductSummaryDTO> result = productSer.fetchAndMapFromDb(1, 10);

        // assert
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Apple", result.get(0).getProductName());
        assertEquals(0, result.get(0).getPrice().compareTo(new BigDecimal("9.99")));
    }

    @Test
    @DisplayName("fetchAndMapFromDb: throws 404 when DB returns empty list")
    void fetchAndMapFromDb_whenDbReturnsEmptyList_throwsAppException() {
        // arrange
        when(productRepo.displayRepo(1, 10)).thenReturn(Collections.emptyList());

        // act + assert
        AppException exception = assertThrows(
                AppException.class,
                () -> productSer.fetchAndMapFromDb(1, 10)
        );
        assertEquals("no found itesms", exception.getMessage());
    }

    @Test
    @DisplayName("fetchAndMapFromDb: throws 404 when DB returns null")
    void fetchAndMapFromDb_whenDbReturnsNull_throwsAppException() {
        // arrange
        when(productRepo.displayRepo(1, 10)).thenReturn(null);

        // act + assert
        assertThrows(AppException.class, () -> productSer.fetchAndMapFromDb(1, 10));
    }

    // ========================================================
    // saveToCache test
    // ========================================================

    @Test
    @DisplayName("saveToCache: writes cache with correct key, serialized content, and 10-minute TTL")
    void saveToCache_setsCorrectKeyValueAndTTL() {
        // arrange
        ProductSummaryDTO dto = new ProductSummaryDTO(
                1L, "Apple", "Fresh apple", new BigDecimal("9.99"), "Fruit", "http://img/1.png"
        );

        // act
        productSer.saveToCache("product:1:10", List.of(dto));

        // assert
        verify(valueOperations, times(1))
                .set(eq("product:1:10"), anyString(), eq(10L), eq(TimeUnit.MINUTES));
    }

    // ========================================================
    // displaySer orchestration tests
    // No longer concerned with internal details (JSON parsing, Map-to-DTO mapping),
    // only verifies: whether the DB was queried, whether the cache was written, and the return value
    // ========================================================

    @Test
    @DisplayName("displaySer: returns from cache directly on cache hit, without touching DB or re-caching")
    void displaySer_whenCacheHit_returnsFromCacheWithoutTouchingDb() {
        // arrange
        String json = "[{\"id\":1,\"productName\":\"Apple\",\"description\":\"Fresh apple\","
                + "\"price\":9.99,\"category\":\"Fruit\",\"imageUrl\":\"http://img/1.png\"}]";
        when(valueOperations.get("product:1:10")).thenReturn(json);

        // act
        List<ProductSummaryDTO> result = productSer.displaySer(1, 10);

        // assert
        assertEquals(1, result.size());
        verify(productRepo, never()).displayRepo(anyInt(), anyInt());
        verify(valueOperations, never()).set(anyString(), anyString(), anyLong(), any(TimeUnit.class));
    }

    @Test
    @DisplayName("displaySer: queries DB and caches result on cache miss")
    void displaySer_whenCacheMiss_fetchesFromDbAndCaches() {
        // arrange
        when(valueOperations.get("product:1:10")).thenReturn(null);

        Map<String, Object> row = new HashMap<>();
        row.put("id", 1);
        row.put("product_name", "Apple");
        row.put("description", "Fresh apple");
        row.put("price", new BigDecimal("9.99"));
        row.put("category", "Fruit");
        row.put("image_url", "http://img/1.png");
        when(productRepo.displayRepo(1, 10)).thenReturn(List.of(row));

        // act
        List<ProductSummaryDTO> result = productSer.displaySer(1, 10);

        // assert
        assertEquals(1, result.size());
        verify(productRepo, times(1)).displayRepo(1, 10);
        verify(valueOperations, times(1))
                .set(eq("product:1:10"), anyString(), eq(10L), eq(TimeUnit.MINUTES));
    }

    @Test
    @DisplayName("displaySer: throws 404 and does not cache when cache miss and DB has no data")
    void displaySer_whenCacheMissAndDbEmpty_throwsAppExceptionWithoutCaching() {
        // arrange
        when(valueOperations.get("product:1:10")).thenReturn(null);
        when(productRepo.displayRepo(1, 10)).thenReturn(Collections.emptyList());

        // act + assert
        assertThrows(AppException.class, () -> productSer.displaySer(1, 10));
        verify(valueOperations, never()).set(anyString(), anyString(), anyLong(), any(TimeUnit.class));
    }
}