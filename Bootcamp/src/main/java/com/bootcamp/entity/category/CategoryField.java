package com.bootcamp.entity.category;

import com.bootcamp.auditing.Auditing;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
public class CategoryField {
    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
    @Column(name = "category_field_id")
    private UUID id;

    @Column(name = "category_field_name",nullable = false,unique = true)
    private String categoryFieldName;

    @Embedded
    Auditing auditing;

    @OneToMany(mappedBy = "categoryField",cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonBackReference
    private Set<CategoryFieldValues> categoryFieldValuesList = new HashSet<>();


    public void addCategoryFieldValues(CategoryFieldValues categoryFieldValues){
        categoryFieldValuesList.add(categoryFieldValues);
    }
    @Override
    public String toString() {
        return "CategoryField{" +
                "id=" + id +
                ", categoryFieldName='" + categoryFieldName + '\'' +
                ", categoryFieldValuesList Size=" + categoryFieldValuesList.size() +
                '}';
    }
}
