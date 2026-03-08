package com.bootcamp.EntityTests;

import com.bootcamp.entity.order.*;
import com.bootcamp.entity.user.Customer;
import com.bootcamp.repository.order.OrderProductRepository;
import com.bootcamp.repository.order.OrderRepository;
import com.bootcamp.repository.order.OrderStatusInfoRepository;
import com.bootcamp.repository.product.ProductVariationRepository;
import com.bootcamp.repository.user.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

@SpringBootTest
public class OrdersTest {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderProductRepository orderProductRepository;

    @Autowired
    OrderStatusInfoRepository orderStatusInfoRepository;

    @Autowired
    ProductVariationRepository productVariationRepository;

    @Autowired
    CustomerRepository customerRepository;
    @Test
    @Transactional
    @Rollback(value = false)
    public void test_create_order(){
        Customer customer = (Customer) customerRepository.findByFirstName("first14").get();
        Orders orders = new Orders();
        orders.setCustomer(customer);
        orders.setAmountPaid(BigDecimal.valueOf(0));
        orders.setDateCreated(LocalDateTime.now());
        orders.setPaymentMethod("Phonepe");
        orders.setCustomerAddressCity("jn");
        orders.setCustomerAddressCountry("India");
        orders.setCustomerAddressState("Delhi");
        orders.setCustomerAddressLine("WZ-3636 Raja Park");
        orders.setCustomerAddressZipCode("110034");
        orders.setCustomerAddressLabel("my home");

            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setOrderProductVariationQuantity(BigInteger.valueOf(100));
            orderProduct.setOrderProductVariationPrice(BigDecimal.valueOf(1021.22));
            orderProduct.setOrder(orders);
            orderProduct.setProductVariation(productVariationRepositoryion.findByProductPrimaryImageName("image2 0"));


            OrderStatusInfo orderStatusInfo = new OrderStatusInfo();
            orderStatusInfo.setFromStatus(OrderFromStatus.ORDER_PLACED);
            orderStatusInfo.setToStatus(OrderToStatus.ORDER_CONFIRMED);
            orderStatusInfo.setTransitionNotesComments("Notes");
            orderStatusInfo.setTransitionDate(LocalDateTime.now());
            orderStatusInfo.setOrderProduct(orderProduct);
            orderProduct.getOrderStatusInfos().add(orderStatusInfo);
            orderProductRepository.save(orderProduct);
        customer.addOrders(orders);

    }


    @Test
    public void test_create_order_Product(){
        Orders orders = orderRepository.findById(UUID.fromString("0x0B6BAC36EC744CBBB4C8C0A798467046".replace("0x", ""))).get();

    }

    /*


    * */
}
