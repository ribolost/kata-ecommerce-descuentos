package com.ribolost.pruebastecnicas.kataecommerce.shared.seed;

import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.Coupon;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.DiscountRuleDefinition;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.DiscountRuleDefinition.DiscountType;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.infrastructure.repository.DiscountPolicyDocument;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.infrastructure.repository.DiscountPolicyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Seed de arranque para el Motor de Descuentos: crea en MongoDB el registro
 * de DiscountPolicyDocument con la lista de reglas vigentes (RN-01 a RN-04
 * de arquitectura.md) si todavía no existe ninguna política.
 *
 * Se ejecuta explícitamente desde KataecommerceApplication.main una vez que
 * el contexto de Spring está inicializado (no se registra como
 * CommandLineRunner/ApplicationRunner para evitar que se dispare dos veces).
 *
 * Aunque DiscountService vuelve a consultar esta misma DiscountPolicy para
 * construir la cadena de responsabilidad, eso es intencional (MVP de un
 * único usuario, arquitectura.md 1.1): el seed garantiza que el dato exista
 * en la base antes de que llegue la primera petición, y DiscountService la
 * carga y cachea una única vez (ver DiscountService.getChain()).
 */
@Component
public class DiscountPolicySeeder {

    private static final Logger log = LoggerFactory.getLogger(DiscountPolicySeeder.class);

    private static final String DEFAULT_COUPON_CODE = "WELCOME2026";
    private static final BigDecimal CATEGORY_DISCOUNT_VALUE = new BigDecimal("0.10");
    private static final BigDecimal VOLUME_DISCOUNT_VALUE = new BigDecimal("0.05");
    private static final BigDecimal COUPON_DISCOUNT_VALUE = new BigDecimal("0.15");
    private static final BigDecimal MAX_DISCOUNT_CAP_VALUE = new BigDecimal("0.35");

    private final DiscountPolicyRepository discountPolicyRepository;

    public DiscountPolicySeeder(DiscountPolicyRepository discountPolicyRepository) {
        this.discountPolicyRepository = discountPolicyRepository;
    }

    public void seed() {
        if (discountPolicyRepository.count() > 0) {
            log.info("Ya existe una DiscountPolicy; se omite el seed.");
            return;
        }

        Coupon coupon = new Coupon(DEFAULT_COUPON_CODE, true, false, null);

        DiscountRuleDefinition categoryRule = new DiscountRuleDefinition(1, DiscountType.CATEGORY, CATEGORY_DISCOUNT_VALUE, null);
        DiscountRuleDefinition volumeRule = new DiscountRuleDefinition(2, DiscountType.VOLUME, VOLUME_DISCOUNT_VALUE, null);
        DiscountRuleDefinition couponRule = new DiscountRuleDefinition(3, DiscountType.COUPON, COUPON_DISCOUNT_VALUE, coupon);
        DiscountRuleDefinition capRule = new DiscountRuleDefinition(4, DiscountType.TOTAL, MAX_DISCOUNT_CAP_VALUE, null);

        coupon.setDiscountRule(couponRule);

        DiscountPolicyDocument document = new DiscountPolicyDocument(
                UUID.randomUUID().toString(),
                List.of(categoryRule, volumeRule, couponRule, capRule)
        );

        discountPolicyRepository.save(document);
        log.info("DiscountPolicy sembrada con cupón '{}'.", DEFAULT_COUPON_CODE);
    }
}
