package com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.rules;

import com.ribolost.pruebastecnicas.kataecommerce.catalog.domain.Product;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.DiscountContext;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.DiscountRuleDefinition.DiscountType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;


public record CategoryDiscountRule(BigDecimal value, DiscountRule next) implements DiscountRule {

    @Override
    public DiscountContext apply(DiscountContext context) {
        BigDecimal technologySubtotal = BigDecimal.ZERO;
        for (Map.Entry<Product, Integer> entry : context.getItems().entrySet()) {
            if (entry.getKey().getCategory() == Product.ProductCategory.TECNOLOGIA) {
                technologySubtotal = technologySubtotal.add(
                        entry.getKey().getUnitPrice().multiply(BigDecimal.valueOf(entry.getValue())));
            }
        }

        if (technologySubtotal.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discount = technologySubtotal.multiply(value).setScale(2, RoundingMode.HALF_UP);
            context.setCategoryDiscountAmount(discount);
            context.setCurrentAmount(context.getCurrentAmount().subtract(discount));
            context.setTotalDiscountAmount(context.getTotalDiscountAmount().add(discount));
            context.getAppliedDiscounts().add(DiscountType.CATEGORY);
        }

        return next != null ? next.apply(context) : context;
    }
}
