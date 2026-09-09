package com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application;

import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.application.dto.out.DiscountCalculationResponse;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.application.dto.out.DiscountItemResponse;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.DiscountBreakdown;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.DiscountRuleDefinition.DiscountType;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application.dto.in.CartItemRequest;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application.dto.in.CartRequest;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application.exception.OrderNotFoundException;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application.exception.OrderStockException;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application.port.out.DiscountCalculationPort;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application.port.out.OrderRepository;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application.port.out.ProductStockPort;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.order.domain.Order;
import com.ribolost.pruebastecnicas.kataecommerce.shared.error.InsufficientStockException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * PlaceOrderService orquesta la confirmación de la compra (Command PlaceOrder,
 * arquitectura.md 1.9). Se aíslan sus tres colaboradores de salida
 * (ProductStockPort, DiscountCalculationPort, OrderRepository) porque lo que
 * se protege aquí es la orquestación de PlaceOrderService, no la lógica de
 * descuentos (que ya tiene su propia suite en checkout.discount) ni la de
 * stock (catalog.application.ProductServiceTest).
 *
 * Nota de discrepancia arquitectura vs. código (se documenta, no se resuelve
 * silenciosamente, arquitectura.md RN-11): el documento describe el orden
 * "decrementar stock -> persistir la orden", pero PlaceOrderService.placeOrder
 * persiste la orden ANTES de decrementar el stock (ver comentario en el propio
 * código: "Persistir antes de decrementar stock (RN-11)..."). Las pruebas de
 * este archivo verifican el orden REAL implementado, no el documentado.
 */
@ExtendWith(MockitoExtension.class)
class PlaceOrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductStockPort productStockPort;
    @Mock
    private DiscountCalculationPort discountCalculationPort;

    private PlaceOrderService placeOrderService;

    @BeforeEach
    void setUp() {
        placeOrderService = new PlaceOrderService(orderRepository, productStockPort, discountCalculationPort);
    }

    private CartRequest cartRequest() {
        return new CartRequest(List.of(new CartItemRequest("p1", 2)), null);
    }

    private DiscountCalculationResponse discountResponseFor(CartRequest cartRequest) {
        DiscountItemResponse item = new DiscountItemResponse("p1", 2, new BigDecimal("100.00"),
                new BigDecimal("10.00"), new BigDecimal("90.00"));
        DiscountBreakdown breakdown = new DiscountBreakdown(new BigDecimal("10.00"), BigDecimal.ZERO,
                BigDecimal.ZERO, new BigDecimal("10.00"), new BigDecimal("0.1000"));
        return new DiscountCalculationResponse(List.of(item), new BigDecimal("100.00"), new BigDecimal("10.00"),
                new BigDecimal("90.00"), List.of(DiscountType.CATEGORY), breakdown);
    }

    @Test
    void placesOrderWhenStockIsSufficient() {
        CartRequest cartRequest = cartRequest();
        DiscountCalculationResponse discountResponse = discountResponseFor(cartRequest);
        when(discountCalculationPort.calculate(cartRequest, true)).thenReturn(discountResponse);
        when(productStockPort.getProductNames(any())).thenReturn(Map.of("p1", "Laptop"));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order result = placeOrderService.placeOrder(cartRequest);

        verify(productStockPort).ensureAvailability(cartRequest);
        verify(discountCalculationPort).calculate(cartRequest, true);
        verify(productStockPort).decrementStock(cartRequest);

        assertThat(result.getSubtotal()).isEqualByComparingTo("100.00");
        assertThat(result.getTotalDiscountAmount()).isEqualByComparingTo("10.00");
        assertThat(result.getTotal()).isEqualByComparingTo("90.00");
        assertThat(result.getLines()).hasSize(1);
        assertThat(result.getLines().get(0).getProductName()).isEqualTo("Laptop");
        assertThat(result.getLines().get(0).getUnitPrice()).isEqualByComparingTo("50.00");
    }

    @Test
    void rejectsOrderWithoutCalculatingDiscountsOrPersistingWhenStockIsInsufficient() {
        CartRequest cartRequest = cartRequest();
        org.mockito.Mockito.doThrow(new InsufficientStockException("Stock insuficiente para el producto p1: disponible 1, solicitado 2"))
                .when(productStockPort).ensureAvailability(cartRequest);

        assertThatThrownBy(() -> placeOrderService.placeOrder(cartRequest))
                .isInstanceOf(OrderStockException.class)
                .hasMessageContaining("Stock insuficiente");

        verify(discountCalculationPort, never()).calculate(any(), anyBoolean());
        verify(orderRepository, never()).save(any());
        verify(productStockPort, never()).decrementStock(any());
    }

    @Test
    void wrapsInsufficientStockDuringDecrementAsOrderStockExceptionAfterOrderWasAlreadyPersisted() {
        // Documenta el comportamiento real (no el de arquitectura.md RN-11): al
        // fallar el decremento de stock, la orden ya fue guardada por
        // orderRepository.save() en el paso anterior del método.
        CartRequest cartRequest = cartRequest();
        DiscountCalculationResponse discountResponse = discountResponseFor(cartRequest);
        when(discountCalculationPort.calculate(cartRequest, true)).thenReturn(discountResponse);
        when(productStockPort.getProductNames(any())).thenReturn(Map.of("p1", "Laptop"));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        org.mockito.Mockito.doThrow(new InsufficientStockException("Stock insuficiente para el producto p1"))
                .when(productStockPort).decrementStock(cartRequest);

        assertThatThrownBy(() -> placeOrderService.placeOrder(cartRequest))
                .isInstanceOf(OrderStockException.class)
                .hasMessageContaining("No fue posible descontar el stock");

        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void returnsExistingOrderWhenFound() {
        Order order = new Order();
        order.setId("order-1");
        when(orderRepository.findById("order-1")).thenReturn(Optional.of(order));

        Order result = placeOrderService.getOrder("order-1");

        assertThat(result).isSameAs(order);
    }

    @Test
    void throwsOrderNotFoundExceptionWhenOrderDoesNotExist() {
        when(orderRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> placeOrderService.getOrder("missing"))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessageContaining("missing");
    }
}
