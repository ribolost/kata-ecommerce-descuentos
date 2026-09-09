package com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.application;

import com.ribolost.pruebastecnicas.kataecommerce.catalog.application.ProductService;
import com.ribolost.pruebastecnicas.kataecommerce.catalog.domain.Product;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.application.dto.in.DiscountCalculationRequest;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.application.dto.out.DiscountCalculationResponse;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.application.dto.out.DiscountItemResponse;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.Coupon;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.DiscountBreakdown;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.DiscountContext;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.DiscountPolicy;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.DiscountRuleDefinition;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.DiscountRuleDefinition.DiscountType;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.rules.DiscountChainFactory;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.rules.DiscountRule;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.infrastructure.repository.DiscountPolicyDocument;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.infrastructure.repository.DiscountPolicyRepository;
import com.ribolost.pruebastecnicas.kataecommerce.shared.error.InvalidCartItemException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Service
public class DiscountService {

    private final DiscountPolicyRepository discountPolicyRepository;
    private final ProductService productService;
    private final DiscountChainFactory discountChainFactory = new DiscountChainFactory();

    private volatile DiscountPolicy cachedPolicy;
    private volatile DiscountRule cachedChain;

    public DiscountService(DiscountPolicyRepository discountPolicyRepository, ProductService productService) {
        this.discountPolicyRepository = discountPolicyRepository;
        this.productService = productService;
    }

    public DiscountCalculationResponse calculateDiscounts(DiscountCalculationRequest request) {
        return calculate(request, false);
    }

    public DiscountCalculationResponse calculateDiscounts(DiscountCalculationRequest request, boolean consumeCoupon) {
        return calculate(request, consumeCoupon);
    }

    private DiscountCalculationResponse calculate(DiscountCalculationRequest request, boolean consumeCoupon) {
        Map<Product, Integer> items = resolveItems(request.items());

        BigDecimal subtotal = items.entrySet().stream()
                .map(entry -> entry.getKey().getUnitPrice().multiply(BigDecimal.valueOf(entry.getValue())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        DiscountContext context = new DiscountContext(subtotal, items, request.couponCode());
        getChain().apply(context);

        if (consumeCoupon && context.getAppliedDiscounts().contains(DiscountType.COUPON)) {
            consumeAppliedCoupon(request.couponCode());
        }

        return buildResponse(items, context);
    }

    private Map<Product, Integer> resolveItems(List<DiscountCalculationRequest.CartItem> cartItems) {
        Map<String, Integer> quantitiesByProductId = new LinkedHashMap<>();
        for (DiscountCalculationRequest.CartItem item : cartItems) {
            quantitiesByProductId.merge(item.productId(), item.quantity(), Integer::sum);
        }

        Map<String, Product> productsById = new LinkedHashMap<>();
        for (Product product : productService.getProductsByIds(quantitiesByProductId.keySet())) {
            productsById.put(product.getId(), product);
        }

        Map<Product, Integer> items = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : quantitiesByProductId.entrySet()) {
            Product product = productsById.get(entry.getKey());
            if (product == null) {
                throw new InvalidCartItemException("El carrito referencia un producto inexistente: " + entry.getKey());
            }
            items.put(product, entry.getValue());
        }
        return items;
    }

    private DiscountCalculationResponse buildResponse(Map<Product, Integer> items, DiscountContext context) {
        List<DiscountItemResponse> itemResponses = allocateItemDiscounts(items, context);

        DiscountBreakdown breakdown = new DiscountBreakdown(
                context.getCategoryDiscountAmount(),
                context.getVolumeDiscountAmount(),
                context.getCouponDiscountAmount(),
                context.getTotalDiscountAmount(),
                effectiveDiscountPercentage(context)
        );

        BigDecimal total = context.getSubtotal().subtract(context.getTotalDiscountAmount());

        return new DiscountCalculationResponse(
                itemResponses,
                context.getSubtotal(),
                context.getTotalDiscountAmount(),
                total,
                List.copyOf(context.getAppliedDiscounts()),
                breakdown
        );
    }

    /**
     * Reparte totalDiscountAmount entre las líneas del carrito en proporción
     * al subtotal de cada una, de modo que la suma de los descuentos por
     * línea sea exactamente igual a totalDiscountAmount (contrato OpenAPI de
     * DiscountItemResponse.discount / OrderItemResponse.discount): la última
     * línea absorbe el remanente de redondeo.
     */
    private List<DiscountItemResponse> allocateItemDiscounts(Map<Product, Integer> items, DiscountContext context) {
        BigDecimal subtotal = context.getSubtotal();
        BigDecimal totalDiscount = context.getTotalDiscountAmount();

        List<Map.Entry<Product, Integer>> entries = new ArrayList<>(items.entrySet());
        List<DiscountItemResponse> result = new ArrayList<>(entries.size());
        BigDecimal allocatedDiscount = BigDecimal.ZERO;

        for (int i = 0; i < entries.size(); i++) {
            Map.Entry<Product, Integer> entry = entries.get(i);
            BigDecimal itemSubtotal = entry.getKey().getUnitPrice()
                    .multiply(BigDecimal.valueOf(entry.getValue()))
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal itemDiscount;
            if (i == entries.size() - 1) {
                itemDiscount = totalDiscount.subtract(allocatedDiscount);
            } else if (subtotal.compareTo(BigDecimal.ZERO) == 0) {
                itemDiscount = BigDecimal.ZERO;
            } else {
                itemDiscount = totalDiscount.multiply(itemSubtotal)
                        .divide(subtotal, 2, RoundingMode.HALF_UP);
            }
            allocatedDiscount = allocatedDiscount.add(itemDiscount);

            BigDecimal itemTotal = itemSubtotal.subtract(itemDiscount);
            result.add(new DiscountItemResponse(entry.getKey().getId(), entry.getValue(), itemSubtotal, itemDiscount, itemTotal));
        }

        return result;
    }

    private BigDecimal effectiveDiscountPercentage(DiscountContext context) {
        if (context.getSubtotal().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return context.getTotalDiscountAmount().divide(context.getSubtotal(), 4, RoundingMode.HALF_UP);
    }

    private synchronized DiscountRule getChain() {
        if (cachedChain == null) {
            cachedPolicy = loadPolicy();
            cachedChain = discountChainFactory.buildChain(cachedPolicy);
        }
        return cachedChain;
    }

    private DiscountPolicy loadPolicy() {
        DiscountPolicyDocument document = discountPolicyRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "No existe una DiscountPolicy configurada. Verifique que el seed (shared/seed) se haya ejecutado."));
        return new DiscountPolicy(document.getId(), document.getRules());
    }

    /**
     * Marca como usado (RN-07) el Coupon de la DiscountPolicy cacheada cuyo
     * código coincide con couponCode, y persiste el cambio. Al mutar el
     * mismo objeto Coupon referenciado por la cadena cacheada, el efecto se
     * ve reflejado de inmediato en cualquier petición posterior sin
     * necesidad de recargar la política ni reconstruir la cadena.
     */
    private synchronized void consumeAppliedCoupon(String couponCode) {
        if (cachedPolicy == null || couponCode == null || couponCode.isBlank()) {
            return;
        }

        cachedPolicy.getRules().stream()
                .map(DiscountRuleDefinition::getCoupon)
                .filter(Objects::nonNull)
                .filter(coupon -> matchesCode(coupon, couponCode))
                .findFirst()
                .ifPresent(coupon -> {
                    coupon.setUsed(true);
                    discountPolicyRepository.save(new DiscountPolicyDocument(cachedPolicy.getId(), cachedPolicy.getRules()));
                });
    }

    private boolean matchesCode(Coupon coupon, String couponCode) {
        return coupon.getCode() != null && coupon.getCode().trim().equalsIgnoreCase(couponCode.trim());
    }
}
