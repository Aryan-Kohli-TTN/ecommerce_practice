package com.bootcamp.entity.order;

import com.bootcamp.entity.product.ProductVariation;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class OrderProduct {
    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
    @Column(name = "order_product_id")
    private UUID id;

    @Column(name = "order_product_variation_quantity",nullable = false)
    private BigInteger orderProductVariationQuantity;

    @Column(name = "order_product_variation_price",nullable = false)
    private BigDecimal orderProductVariationPrice;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonBackReference
    private Orders order;

    @ManyToOne
    @JoinColumn(name = "product_variation_id")
    @JsonBackReference
    private ProductVariation productVariation;

    @OneToMany(mappedBy = "orderProduct",cascade = CascadeType.ALL,orphanRemoval = true)
    Set<OrderStatusInfo> orderStatusInfos = new HashSet<>();

    public OrderProduct(BigInteger orderProductVariationQuantity, BigDecimal orderProductVariationPrice, Orders order, ProductVariation productVariation, Set<OrderStatusInfo> orderStatusInfos) {
        this.orderProductVariationQuantity = orderProductVariationQuantity;
        this.orderProductVariationPrice = orderProductVariationPrice;
        this.order = order;
        this.productVariation = productVariation;
        this.orderStatusInfos = orderStatusInfos;
    }

    public void addOrderStatusInfo(OrderStatusInfo orderStatusInfo)
    {
        orderStatusInfos.add(orderStatusInfo);
    }
    @Override
    public String toString() {
        return "OrderProduct{" +
                "id=" + id +
                ", orderProductVariationQuantity=" + orderProductVariationQuantity +
                ", orderProductVariationPrice=" + orderProductVariationPrice +
                ", orderId=" + order.getId() +
                ", productVariationId=" + productVariation.getId() +
                ", orderStatuses count=" + orderStatusInfos.toString() +
                '}';
    }
}
