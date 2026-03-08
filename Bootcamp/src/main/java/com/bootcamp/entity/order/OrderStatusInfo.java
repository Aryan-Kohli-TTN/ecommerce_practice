package com.bootcamp.entity.order;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class OrderStatusInfo {
    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
    @Column(name = "order_status_id")
    private UUID id;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "from_status", nullable = false)
    private OrderFromStatus fromStatus;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "to_status", nullable = false)
    private OrderToStatus toStatus;

    @Column(name = "transition_notes_comments", nullable = false)
    private String transitionNotesComments;

    @Column(name = "transition_date", nullable = false)
    private LocalDateTime transitionDate;

    @ManyToOne
    @JoinColumn(name = "order_product_id")
    private OrderProduct orderProduct;

    public OrderStatusInfo(OrderFromStatus fromStatus, OrderToStatus toStatus, String transitionNotesComments, LocalDateTime transitionDate, OrderProduct orderProduct) {
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.transitionNotesComments = transitionNotesComments;
        this.transitionDate = transitionDate;
        this.orderProduct = orderProduct;
    }

    @Override
    public String toString() {
        return "OrderStatus{" +
                "id=" + id +
                ", fromStatus='" + fromStatus + '\'' +
                ", toStatus='" + toStatus + '\'' +
                ", transitionNotesComments='" + transitionNotesComments + '\'' +
                ", transitionDate='" + transitionDate + '\'' +
                ", orderProductId=" + orderProduct.getId() +
                '}';
    }
}
