package com.bootcamp.entity.email;

import com.bootcamp.auditing.Auditing;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@ToString
@Entity
@Builder
public class EmailDetails {
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "email_details_id")
    @Id
    UUID id;
    @Column(name = "recipient")
    String recipient;
    @Column(name = "product_id")
    UUID productId;
    @Column(name = "sender")
    String sender;
    @Column(name = "subject")
    String subject;
    @Column(name="email_type")
    @Enumerated(EnumType.STRING)
    EmailType emailType;
    @Column(name = "is_success")
    boolean isSuccess;
    @Embedded
    Auditing auditing;
}
