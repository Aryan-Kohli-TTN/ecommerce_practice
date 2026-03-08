package com.bootcamp.entity.user;

import com.bootcamp.entity.cart.Cart;
import com.bootcamp.entity.order.Orders;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
//@DiscriminatorValue("customer")
public class Customer extends User {

    @Column(name = "customer_contact_number")
    private String customerContactNumber;

    @OneToMany(mappedBy = "customer",cascade = CascadeType.ALL,orphanRemoval = true,fetch = FetchType.LAZY)
    List<Orders> ordersList=new ArrayList<>();

    @OneToMany(mappedBy = "customer",cascade = CascadeType.ALL,orphanRemoval = true)
    Set<Cart> cartItems=new HashSet<>();


    public void addOrders(Orders orders){
        ordersList.add(orders);
    }
    public void addCartItem(Cart cart){
        cartItems.add(cart);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerContactNumber='" + customerContactNumber + '\'' +
                ", orders count=" + ordersList.size() +
                ", cartItems count=" + cartItems.size() +
                super.toString()+
                '}';
    }
}
