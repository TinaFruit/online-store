package com.example.springproject.controller;

import com.example.springproject.model.OrderDetailDTO;
import com.example.springproject.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 单元测试：测试 GET /order/searchOrder 接口
 * 
 * 测试场景：
 * 1. 用户有订单 - 返回订单列表
 * 2. 用户没有订单 - 返回 409 Conflict
 * 3. 未认证用户 - 返回 401 Unauthorized
 * 4. 多个订单和订单项 - 正确返回所有数据
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("订单查询接口测试")
public class OrderControllerSearchOrderTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    private List<OrderDetailDTO> mockOrders;
    private OrderDetailDTO order1;
    private OrderDetailDTO order2;

    @BeforeEach
    void setUp() {
        // 初始化模拟订单数据
        mockOrders = new ArrayList<>();

        // 订单 1 - 包含两件商品
        order1 = new OrderDetailDTO();
        order1.setOrderId(101);
        order1.setUserId(1);
        order1.setTotalPrice(new BigDecimal("1598.00"));
        order1.setStatus("PENDING");
        order1.setOrderCreatedAt(LocalDateTime.of(2024, 1, 15, 10, 30, 0));
        order1.setProductId(1);
        order1.setProductName("Apple iPhone 15");
        order1.setPrice(new BigDecimal("5999.00"));
        order1.setImageUrl("https://example.com/iphone15.jpg");
        order1.setQuantity(1);
        order1.setDetailCreatedAt(LocalDateTime.of(2024, 1, 15, 10, 30, 0));

        // 订单 1 - 第二件商品
        OrderDetailDTO order1Item2 = new OrderDetailDTO();
        order1Item2.setOrderId(101);
        order1Item2.setUserId(1);
        order1Item2.setTotalPrice(new BigDecimal("1598.00"));
        order1Item2.setStatus("PENDING");
        order1Item2.setOrderCreatedAt(LocalDateTime.of(2024, 1, 15, 10, 30, 0));
        order1Item2.setProductId(2);
        order1Item2.setProductName("Nike Running Shoes");
        order1Item2.setPrice(new BigDecimal("599.00"));
        order1Item2.setImageUrl("https://example.com/nike.jpg");
        order1Item2.setQuantity(2);
        order1Item2.setDetailCreatedAt(LocalDateTime.of(2024, 1, 15, 10, 30, 0));

        // 订单 2 - 单件商品
        order2 = new OrderDetailDTO();
        order2.setOrderId(102);
        order2.setUserId(1);
        order2.setTotalPrice(new BigDecimal("29.90"));
        order2.setStatus("COMPLETED");
        order2.setOrderCreatedAt(LocalDateTime.of(2024, 1, 20, 14, 15, 0));
        order2.setProductId(3);
        order2.setProductName("Organic Apples");
        order2.setPrice(new BigDecimal("29.90"));
        order2.setImageUrl("https://example.com/apple.jpg");
        order2.setQuantity(1);
        order2.setDetailCreatedAt(LocalDateTime.of(2024, 1, 20, 14, 15, 0));

        mockOrders.add(order1);
        mockOrders.add(order1Item2);
        mockOrders.add(order2);
    }

    @Test
    @DisplayName("测试场景1: 用户有订单 - 返回订单列表")
    @WithMockUser(username = "testuser", roles = "USER")
    void testSearchOrderSuccess_WithOrders() throws Exception {
        // 模拟 OrderService 返回订单列表
        when(orderService.searchOderServ("testuser")).thenReturn(mockOrders);

        // 执行 GET 请求
        mockMvc.perform(
                get("/order/searchOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
        )
        // 验证响应状态码为 200
        .andExpect(status().isOk())
        // 验证响应类型为 JSON
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        // 验证返回的是一个数组
        .andExpect(jsonPath("$", isA(Object.class)))
        // 验证数组包含 3 个元素（2 个订单项 + 1 个订单项）
        .andExpect(jsonPath("$.size()").value(3))
        // 验证第一个订单的字段
        .andExpect(jsonPath("$[0].orderId").value(101))
        .andExpect(jsonPath("$[0].userId").value(1))
        .andExpect(jsonPath("$[0].totalPrice").value(1598.00))
        .andExpect(jsonPath("$[0].status").value("PENDING"))
        .andExpect(jsonPath("$[0].productName").value("Apple iPhone 15"))
        .andExpect(jsonPath("$[0].quantity").value(1))
        // 验证第二个订单项的字段
        .andExpect(jsonPath("$[1].productName").value("Nike Running Shoes"))
        .andExpect(jsonPath("$[1].quantity").value(2))
        // 验证第三个订单的字段
        .andExpect(jsonPath("$[2].orderId").value(102))
        .andExpect(jsonPath("$[2].status").value("COMPLETED"))
        .andExpect(jsonPath("$[2].productName").value("Organic Apples"))
        // 打印响应详情（用于调试）
        .andDo(print());

        // 验证 OrderService.searchOderServ 被调用了一次
        verify(orderService, times(1)).searchOderServ("testuser");
    }

    @Test
    @DisplayName("测试场景2: 用户没有订单 - 返回 409 Conflict")
    @WithMockUser(username = "emptyuser", roles = "USER")
    void testSearchOrderFailure_EmptyOrders() throws Exception {
        // 模拟 OrderService 返回空列表
        when(orderService.searchOderServ("emptyuser")).thenReturn(Collections.emptyList());

        // 执行 GET 请求
        mockMvc.perform(
                get("/order/searchOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
        )
        // 验证响应状态码为 409
        .andExpect(status().isConflict())
        // 验证响应体为 "empty"
        .andExpect(content().string("empty"))
        .andDo(print());

        // 验证 OrderService.searchOderServ 被调用了一次
        verify(orderService, times(1)).searchOderServ("emptyuser");
    }

    @Test
    @DisplayName("测试场景3: 未认证用户 - 返回 401 Unauthorized")
    void testSearchOrder_Unauthorized() throws Exception {
        // 不使用 @WithMockUser 注解，模拟未认证用户
        mockMvc.perform(
                get("/order/searchOrder")
                        .contentType(MediaType.APPLICATION_JSON)
        )
        // 验证响应状态码为 401 或 302（重定向到登录页）
        .andExpect(status().isUnauthorized())
        .andDo(print());

        // 验证 OrderService.searchOderServ 没有被调用
        verify(orderService, never()).searchOderServ(anyString());
    }

    @Test
    @DisplayName("测试场景4: 单个订单 - 验证数据完整性")
    @WithMockUser(username = "singleorder", roles = "USER")
    void testSearchOrder_SingleOrder() throws Exception {
        List<OrderDetailDTO> singleOrder = new ArrayList<>();
        singleOrder.add(order2);

        when(orderService.searchOderServ("singleorder")).thenReturn(singleOrder);

        mockMvc.perform(
                get("/order/searchOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(1))
        .andExpect(jsonPath("$[0].orderId").value(102))
        .andExpect(jsonPath("$[0].userId").value(1))
        .andExpect(jsonPath("$[0].totalPrice").value(29.90))
        .andExpect(jsonPath("$[0].status").value("COMPLETED"))
        .andExpect(jsonPath("$[0].productId").value(3))
        .andExpect(jsonPath("$[0].productName").value("Organic Apples"))
        .andExpect(jsonPath("$[0].price").value(29.90))
        .andExpect(jsonPath("$[0].imageUrl").value("https://example.com/apple.jpg"))
        .andExpect(jsonPath("$[0].quantity").value(1))
        .andDo(print());

        verify(orderService, times(1)).searchOderServ("singleorder");
    }

    @Test
    @DisplayName("测试场景5: 验证正确的用户名被传递到服务层")
    @WithMockUser(username = "specificuser", roles = "USER")
    void testSearchOrder_CorrectUsernamePassedToService() throws Exception {
        when(orderService.searchOderServ("specificuser")).thenReturn(mockOrders);

        mockMvc.perform(
                get("/order/searchOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
        )
        .andExpect(status().isOk())
        .andDo(print());

        // 验证服务层收到了正确的用户名
        verify(orderService, times(1)).searchOderServ("specificuser");
        verify(orderService, never()).searchOderServ("wronguser");
    }

    @Test
    @DisplayName("测试场景6: ADMIN 用户也能查询订单")
    @WithMockUser(username = "adminuser", roles = "ADMIN")
    void testSearchOrder_AdminUser() throws Exception {
        when(orderService.searchOderServ("adminuser")).thenReturn(mockOrders);

        mockMvc.perform(
                get("/order/searchOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(3))
        .andDo(print());

        verify(orderService, times(1)).searchOderServ("adminuser");
    }

    @Test
    @DisplayName("测试场景7: 响应包含所有必要字段")
    @WithMockUser(username = "testuser", roles = "USER")
    void testSearchOrder_ResponseContainsAllFields() throws Exception {
        when(orderService.searchOderServ("testuser")).thenReturn(mockOrders);

        mockMvc.perform(
                get("/order/searchOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
        )
        .andExpect(status().isOk())
        // 验证每个订单对象都包含必要字段
        .andExpect(jsonPath("$[*].orderId").isArray())
        .andExpect(jsonPath("$[*].userId").isArray())
        .andExpect(jsonPath("$[*].totalPrice").isArray())
        .andExpect(jsonPath("$[*].status").isArray())
        .andExpect(jsonPath("$[*].productId").isArray())
        .andExpect(jsonPath("$[*].productName").isArray())
        .andExpect(jsonPath("$[*].price").isArray())
        .andExpect(jsonPath("$[*].quantity").isArray())
        .andExpect(jsonPath("$[*].imageUrl").isArray())
        .andDo(print());

        verify(orderService, times(1)).searchOderServ("testuser");
    }
}
