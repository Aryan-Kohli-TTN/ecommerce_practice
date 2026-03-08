package com.bootcamp.entity.order;

import com.bootcamp.entity.user.Customer;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Orders {
    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
    @Column(name = "order_id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    Customer customer;

    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    Set<OrderProduct> orderProductList = new HashSet<>();


    @Column(name = "amount_paid",nullable = false)
    private BigDecimal amountPaid;

    @Column(name = "date_created",nullable = false)
    private LocalDateTime dateCreated;


    @Column(name = "payment_method",nullable = false)
    private String  paymentMethod;

    @Column(name = "customer_address_city",nullable = false)
    private String  customerAddressCity;

    @Column(name = "customer_address_country", nullable = false)
    private String customerAddressCountry;

    @Column(name = "customer_address_state", nullable = false)
    private String customerAddressState;

    @Column(name = "customer_address_line", nullable = false)
    private String customerAddressLine;

    @Column(name = "customer_address_zip_code", nullable = false)
    private String customerAddressZipCode;

    @Column(name = "customer_address_label", nullable = false)
    private String customerAddressLabel;

    public Orders(Customer customer, Set<OrderProduct> orderProductList, BigDecimal amountPaid, LocalDateTime dateCreated, String paymentMethod, String customerAddressCity, String customerAddressCountry, String customerAddressState, String customerAddressLine, String customerAddressZipCode, String customerAddressLabel) {
        this.customer = customer;
        this.orderProductList = orderProductList;
        this.amountPaid = amountPaid;
        this.dateCreated = dateCreated;
        this.paymentMethod = paymentMethod;
        this.customerAddressCity = customerAddressCity;
        this.customerAddressCountry = customerAddressCountry;
        this.customerAddressState = customerAddressState;
        this.customerAddressLine = customerAddressLine;
        this.customerAddressZipCode = customerAddressZipCode;
        this.customerAddressLabel = customerAddressLabel;
    }
    public void addOrderProduct(OrderProduct orderProduct){
        orderProductList.add(orderProduct);
    }

    @Override
    public String toString() {
        return "Orders{" +
                "id=" + id +
                ", customerId=" + customer.getId() +
                ", orderProduct count=" + orderProductList.size() +
                ", amountPaid=" + amountPaid +
                ", dateCreated=" + dateCreated +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", customerAddressCity='" + customerAddressCity + '\'' +
                ", customerAddressCountry='" + customerAddressCountry + '\'' +
                ", customerAddressState='" + customerAddressState + '\'' +
                ", customerAddressLine='" + customerAddressLine + '\'' +
                ", customerAddressZipCode='" + customerAddressZipCode + '\'' +
                ", customerAddressLabel='" + customerAddressLabel + '\'' +
                '}';
    }
}
