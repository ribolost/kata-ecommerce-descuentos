package com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.application;

import com.ribolost.pruebastecnicas.kataecommerce.catalog.application.ProductService;
import com.ribolost.pruebastecnicas.kataecommerce.catalog.domain.Product;
import com.ribolost.pruebastecnicas.kataecommerce.catalog.domain.Product.ProductCategory;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.application.dto.in.DiscountCalculationRequest;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.application.dto.out.DiscountCalculationResponse;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.application.dto.out.DiscountItemResponse;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.Coupon;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.DiscountRuleDefinition;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.DiscountRuleDefinition.DiscountType;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.infrastructure.repository.DiscountPolicyDocument;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.infrastructure.repository.DiscountPolicyRepository;
import com.ribolost.pruebastecnicas.kataecommerce.shared.error.InvalidCartItemException;
import com.ribolost.pruebastecnicas.kataecommerce.shared.validation.ResponseValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * DiscountService orquesta la carga de la DiscountPolicy, la construcción de
 * la cadena de reglas (DiscountChainFactory) y el reparto del descuento total
 * entre las líneas del carrito. Aquí se prueba esa orquestación end-to-end
 * con una política real (mockeando solo los colaboradores externos:
 * DiscountPolicyRepository, ProductService y ResponseValidator), de modo que
 * las pruebas demuestren el resultado numérico real de aplicar la cadena
 * completa -- no solo que se invocaron métodos.
 */
@ExtendWith(MockitoExtension.class)
class DiscountServiceTest {

    @Mock
    private DiscountPolicyRepository discountPolicyRepository;
    @Mock
    private ProductService productService;
    @Mock
    private ResponseValidator responseValidator;

    private DiscountService discountService;

    private static final BigDecimal CATEGORY_VALUE = new BigDecimal("0.10");
    private static final BigDecimal VOLUME_VALUE = new BigDecimal("0.05");
    private static final BigDecimal COUPON_VALUE = new BigDecimal("0.15");
    private static final BigDecimal CAP_VALUE = new BigDecimal("0.35");
    private static final String COUPON_CODE = "WELCOME2026";

    @BeforeEach
    void setUp() {
        discountService = new DiscountService(discountPolicyRepository, productService, responseValidator);
        // ResponseValidator no es responsabilidad de este servicio: se deja pasar
        // la respuesta tal cual, igual que haría la validación real cuando la
        // respuesta cumple su contrato (que es el caso en todos estos escenarios).
        // lenient(): algunos tests (p. ej. producto inexistente) lanzan su
        // excepción antes de llegar a construir la respuesta, por lo que este
        // stub compartido nunca se usa en esos casos; sin "lenient" Mockito
        // (strict stubs, MockitoExtension) marcaría el stub como innecesario
        // en esos tests puntuales, aunque sí es necesario en el resto.
        lenient().when(responseValidator.validate(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    private void givenPolicy(DiscountRuleDefinition... rules) {
        DiscountPolicyDocument document = new DiscountPolicyDocument("policy-1", List.of(rules));
        when(discountPolicyRepository.findAll()).thenReturn(List.of(document));
    }

    private DiscountRuleDefinition categoryRule() {
        return new DiscountRuleDefinition(1, DiscountType.CATEGORY, CATEGORY_VALUE, null);
    }

    private DiscountRuleDefinition volumeRule() {
        return new DiscountRuleDefinition(2, DiscountType.VOLUME, VOLUME_VALUE, null);
    }

    private DiscountRuleDefinition couponRule(Coupon coupon) {
        return new DiscountRuleDefinition(3, DiscountType.COUPON, COUPON_VALUE, coupon);
    }

    private DiscountRuleDefinition capRule() {
        return new DiscountRuleDefinition(4, DiscountType.TOTAL, CAP_VALUE, null);
    }

    private Product product(String id, BigDecimal price, ProductCategory category) {
        return new Product(id, "Producto " + id, "desc", price, category, 1000);
    }

    private DiscountCalculationRequest requestFor(String couponCode, DiscountCalculationRequest.CartItem... items) {
        return new DiscountCalculationRequest(List.of(items), couponCode);
    }

    @Test
    void appliesOnlyCategoryDiscountWhenSubtotalDoesNotReachVolumeThreshold() {
        givenPolicy(categoryRule(), volumeRule(), couponRule(new Coupon(COUPON_CODE, true, false)), capRule());
        Product tech = product("p1", new BigDecimal("50.00"), ProductCategory.TECNOLOGIA);
        when(productService.getProductsByIds(any())).thenReturn(List.of(tech));

        DiscountCalculationResponse response = discountService.calculateDiscounts(
                requestFor(null, new DiscountCalculationRequest.CartItem("p1", 1)));

        assertThat(response.subtotal()).isEqualByComparingTo("50.00");
        assertThat(response.totalDiscountAmount()).isEqualByComparingTo("5.00");
        assertThat(response.total()).isEqualByComparingTo("45.00");
        assertThat(response.appliedDiscounts()).containsExactly(DiscountType.CATEGORY);
        assertThat(response.discountBreakdown().categoryDiscountAmount()).isEqualByComparingTo("5.00");
        assertThat(response.discountBreakdown().volumeDiscountAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void appliesVolumeDiscountWhenSubtotalExceedsOneHundredEvenWithoutTechnologyItems() {
        givenPolicy(categoryRule(), volumeRule(), couponRule(new Coupon(COUPON_CODE, true, false)), capRule());
        Product other = product("p1", new BigDecimal("150.00"), ProductCategory.OTRO);
        when(productService.getProductsByIds(any())).thenReturn(List.of(other));

        DiscountCalculationResponse response = discountService.calculateDiscounts(
                requestFor(null, new DiscountCalculationRequest.CartItem("p1", 1)));

        assertThat(response.appliedDiscounts()).containsExactly(DiscountType.VOLUME);
        assertThat(response.totalDiscountAmount()).isEqualByComparingTo("7.50");
        assertThat(response.total()).isEqualByComparingTo("142.50");
    }

    @Test
    void appliesCategoryVolumeAndCouponInPrecedenceOrderWhenAllConditionsAreMet() {
        Coupon coupon = new Coupon(COUPON_CODE, true, false);
        givenPolicy(categoryRule(), volumeRule(), couponRule(coupon), capRule());
        Product tech = product("p1", new BigDecimal("200.00"), ProductCategory.TECNOLOGIA);
        when(productService.getProductsByIds(any())).thenReturn(List.of(tech));

        DiscountCalculationResponse response = discountService.calculateDiscounts(
                requestFor(COUPON_CODE, new DiscountCalculationRequest.CartItem("p1", 1)));

        // category: 10% de 200 = 20 -> currentAmount 180
        // volume: 5% de 180 = 9 -> currentAmount 171
        // coupon: 15% de 171 = 25.65 -> currentAmount 145.35
        assertThat(response.appliedDiscounts())
                .containsExactly(DiscountType.CATEGORY, DiscountType.VOLUME, DiscountType.COUPON);
        assertThat(response.totalDiscountAmount()).isEqualByComparingTo("54.65");
        assertThat(response.total()).isEqualByComparingTo("145.35");
    }

    @Test
    void sumOfItemDiscountsAlwaysEqualsTotalDiscountAmountAcrossMultipleLines() {
        givenPolicy(categoryRule(), volumeRule(), couponRule(new Coupon(COUPON_CODE, true, false)), capRule());
        Product a = product("p1", new BigDecimal("33.33"), ProductCategory.OTRO);
        Product b = product("p2", new BigDecimal("66.67"), ProductCategory.OTRO);
        Product c = product("p3", new BigDecimal("10.00"), ProductCategory.OTRO);
        when(productService.getProductsByIds(any())).thenReturn(List.of(a, b, c));

        DiscountCalculationResponse response = discountService.calculateDiscounts(requestFor(null,
                new DiscountCalculationRequest.CartItem("p1", 1),
                new DiscountCalculationRequest.CartItem("p2", 1),
                new DiscountCalculationRequest.CartItem("p3", 1)));

        BigDecimal sumOfItemDiscounts = response.items().stream()
                .map(DiscountItemResponse::discount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        assertThat(sumOfItemDiscounts).isEqualByComparingTo(response.totalDiscountAmount());
    }

    @Test
    void mergesDuplicateCartLinesForTheSameProductBeforeCalculating() {
        givenPolicy(categoryRule(), volumeRule(), couponRule(new Coupon(COUPON_CODE, true, false)), capRule());
        Product tech = product("p1", new BigDecimal("10.00"), ProductCategory.TECNOLOGIA);
        when(productService.getProductsByIds(any())).thenReturn(List.of(tech));

        DiscountCalculationResponse response = discountService.calculateDiscounts(requestFor(null,
                new DiscountCalculationRequest.CartItem("p1", 2),
                new DiscountCalculationRequest.CartItem("p1", 3)));

        // Cantidades fusionadas: 2 + 3 = 5 unidades de $10.00 -> subtotal 50.00
        assertThat(response.subtotal()).isEqualByComparingTo("50.00");
        assertThat(response.items()).hasSize(1);
        assertThat(response.items().get(0).quantity()).isEqualTo(5);
    }

    @Test
    void throwsInvalidCartItemExceptionWhenCartReferencesAnUnknownProduct() {
        // No se stubea discountPolicyRepository: DiscountService.resolveItems()
        // lanza la excepción antes de llegar a cargar la política (getChain()),
        // así que stubear el repositorio aquí sería un stubbing innecesario
        // (Mockito strict stubs lo rechaza con UnnecessaryStubbingException).
        when(productService.getProductsByIds(any())).thenReturn(List.of());

        assertThatThrownBy(() -> discountService.calculateDiscounts(
                requestFor(null, new DiscountCalculationRequest.CartItem("unknown-id", 1))))
                .isInstanceOf(InvalidCartItemException.class)
                .hasMessageContaining("unknown-id");
    }

    @Test
    void doesNotApplyCouponDiscountAndCompletesCalculationWhenCouponCodeIsNotRegistered() {
        givenPolicy(categoryRule(), volumeRule(), couponRule(new Coupon(COUPON_CODE, true, false)), capRule());
        Product tech = product("p1", new BigDecimal("50.00"), ProductCategory.TECNOLOGIA);
        when(productService.getProductsByIds(any())).thenReturn(List.of(tech));

        DiscountCalculationResponse response = discountService.calculateDiscounts(
                requestFor("EXPIRED2024", new DiscountCalculationRequest.CartItem("p1", 1)));

        assertThat(response.appliedDiscounts()).containsExactly(DiscountType.CATEGORY);
        assertThat(response.discountBreakdown().couponDiscountAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(response.totalDiscountAmount()).isEqualByComparingTo("5.00");
    }

    @Test
    void doesNotApplyCouponDiscountWhenCouponWasAlreadyUsed() {
        givenPolicy(categoryRule(), volumeRule(), couponRule(new Coupon(COUPON_CODE, true, true)), capRule());
        Product tech = product("p1", new BigDecimal("50.00"), ProductCategory.TECNOLOGIA);
        when(productService.getProductsByIds(any())).thenReturn(List.of(tech));

        DiscountCalculationResponse response = discountService.calculateDiscounts(
                requestFor(COUPON_CODE, new DiscountCalculationRequest.CartItem("p1", 1)));

        assertThat(response.appliedDiscounts()).doesNotContain(DiscountType.COUPON);
    }

    @Test
    void truncatesToThirtyFivePercentWhenAccumulatedDiscountExceedsTheCap() {
        // Política deliberadamente distinta de la sembrada por defecto (0.10/0.05/0.15)
        // para forzar, de forma determinista y sin inventar la fórmula del tope
        // (que sigue siendo la de MaxDiscountCapRule), un escenario superior al 35%.
        DiscountRuleDefinition aggressiveCategoryRule = new DiscountRuleDefinition(1, DiscountType.CATEGORY, new BigDecimal("0.40"), null);
        givenPolicy(aggressiveCategoryRule, capRule());
        Product tech = product("p1", new BigDecimal("100.00"), ProductCategory.TECNOLOGIA);
        when(productService.getProductsByIds(any())).thenReturn(List.of(tech));

        DiscountCalculationResponse response = discountService.calculateDiscounts(
                requestFor(null, new DiscountCalculationRequest.CartItem("p1", 1)));

        assertThat(response.appliedDiscounts()).containsExactly(DiscountType.CATEGORY, DiscountType.TOTAL);
        assertThat(response.totalDiscountAmount()).isEqualByComparingTo("35.00");
        assertThat(response.total()).isEqualByComparingTo("65.00");
        assertThat(response.discountBreakdown().effectiveDiscountPercentage()).isEqualByComparingTo("0.3500");
    }

    @Test
    void doesNotTruncateWhenAccumulatedDiscountIsExactlyThirtyFivePercent() {
        DiscountRuleDefinition exactCategoryRule = new DiscountRuleDefinition(1, DiscountType.CATEGORY, new BigDecimal("0.35"), null);
        givenPolicy(exactCategoryRule, capRule());
        Product tech = product("p1", new BigDecimal("100.00"), ProductCategory.TECNOLOGIA);
        when(productService.getProductsByIds(any())).thenReturn(List.of(tech));

        DiscountCalculationResponse response = discountService.calculateDiscounts(
                requestFor(null, new DiscountCalculationRequest.CartItem("p1", 1)));

        assertThat(response.appliedDiscounts()).containsExactly(DiscountType.CATEGORY);
        assertThat(response.appliedDiscounts()).doesNotContain(DiscountType.TOTAL);
        assertThat(response.totalDiscountAmount()).isEqualByComparingTo("35.00");
    }

    @Test
    void marksCouponAsUsedAndPersistsPolicyWhenConsumeCouponIsTrueAndCouponWasApplied() {
        Coupon coupon = new Coupon(COUPON_CODE, true, false);
        givenPolicy(couponRule(coupon), capRule());
        Product other = product("p1", new BigDecimal("50.00"), ProductCategory.OTRO);
        when(productService.getProductsByIds(any())).thenReturn(List.of(other));

        discountService.calculateDiscounts(
                requestFor(COUPON_CODE, new DiscountCalculationRequest.CartItem("p1", 1)), true);

        ArgumentCaptor<DiscountPolicyDocument> captor = ArgumentCaptor.forClass(DiscountPolicyDocument.class);
        verify(discountPolicyRepository).save(captor.capture());
        Coupon savedCoupon = captor.getValue().getRules().stream()
                .map(DiscountRuleDefinition::getCoupon)
                .filter(c -> c != null)
                .findFirst()
                .orElseThrow();
        assertThat(savedCoupon.isUsed()).isTrue();
    }

    @Test
    void doesNotPersistPolicyWhenConsumeCouponIsFalseEvenIfCouponWasApplied() {
        Coupon coupon = new Coupon(COUPON_CODE, true, false);
        givenPolicy(couponRule(coupon), capRule());
        Product other = product("p1", new BigDecimal("50.00"), ProductCategory.OTRO);
        when(productService.getProductsByIds(any())).thenReturn(List.of(other));

        discountService.calculateDiscounts(
                requestFor(COUPON_CODE, new DiscountCalculationRequest.CartItem("p1", 1)), false);

        verify(discountPolicyRepository, never()).save(any());
    }

    @Test
    void doesNotPersistPolicyWhenConsumeCouponIsTrueButCouponWasNotApplied() {
        Coupon coupon = new Coupon(COUPON_CODE, true, false);
        givenPolicy(couponRule(coupon), capRule());
        Product other = product("p1", new BigDecimal("50.00"), ProductCategory.OTRO);
        when(productService.getProductsByIds(any())).thenReturn(List.of(other));

        discountService.calculateDiscounts(
                requestFor("NOTREGISTERED", new DiscountCalculationRequest.CartItem("p1", 1)), true);

        verify(discountPolicyRepository, never()).save(any());
    }
}
