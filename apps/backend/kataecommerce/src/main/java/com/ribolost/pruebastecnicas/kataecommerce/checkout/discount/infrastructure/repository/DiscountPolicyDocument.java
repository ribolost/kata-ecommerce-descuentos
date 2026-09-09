package com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.infrastructure.repository;

import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.Coupon;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.DiscountRuleDefinition;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;


@Document(collection = "discount_policies")
public class DiscountPolicyDocument {

    @Id
    private String id;

    @Field("rules")
    private List<DiscountRuleDefinition> rules = new ArrayList<>();

    @Field("coupons")
    private List<Coupon> coupons = new ArrayList<>();

    public DiscountPolicyDocument() {
    }

    public DiscountPolicyDocument(String id, List<DiscountRuleDefinition> rules, List<Coupon> coupons) {
        this.id = id;
        this.rules = rules;
        this.coupons = coupons;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<DiscountRuleDefinition> getRules() {
        return rules;
    }

    public void setRules(List<DiscountRuleDefinition> rules) {
        this.rules = rules;
    }

    public List<Coupon> getCoupons() {
        return coupons;
    }

    public void setCoupons(List<Coupon> coupons) {
        this.coupons = coupons;
    }
}
