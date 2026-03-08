package com.bootcamp.entity.user;


import com.bootcamp.auditing.Auditing;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "update address SET is_deleted=true where address_id =?")
@SQLRestriction(value = "is_deleted=false")
public class Address {
    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
    @Column(name = "address_id")
    private UUID id;

    @Column(name = "city",nullable = false)
    private String city;

    @Column(name = "state",nullable = false)
    private String state;

    @Column(name = "country",nullable = false)
    private String country;

    @Column(name = "address_line",nullable = false)
    private String addressLine;

    @Column(name = "zip_code",nullable = false)
    private String zipCode;

    @Column(name = "default_address")
    private Boolean isDefaultAddress;

    @JsonIgnore
    @Column(name = "is_deleted")
    private boolean IsDeleted =false;

    @Embedded
    private Auditing auditing;
    /* label is specially for customer where he marks different types of address
        like home1,home2 etc.
    */
    @Column(name = "label",nullable = false)
    private String label;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    public Address(String city, String state, String country, String addressLine, String zipCode,
                   String label, User user,boolean isDefaultAddress) {
        this.city = city;
        this.state = state;
        this.country = country;
        this.addressLine = addressLine;
        this.zipCode = zipCode;
        this.label = label;
        this.user = user;
        this.isDefaultAddress=isDefaultAddress;
    }

    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", country='" + country + '\'' +
                ", addressLine='" + addressLine + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", label='" + label + '\'' +
                ", default='" + isDefaultAddress + '\'' +
                ", user_id=" + user.getId() +
                '}';
    }
}
