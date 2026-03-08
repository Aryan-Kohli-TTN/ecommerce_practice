package com.bootcamp.entity.category;

import com.bootcamp.auditing.Auditing;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
public class CategoryFieldValues {
    @EmbeddedId
    private CategoryFieldValuesId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("categoryId")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("categoryFieldId")
    private CategoryField categoryField;

    @Embedded
    Auditing auditing;

    @Column(name = "field_values",nullable = false)
    private String fieldValues;

    public CategoryFieldValues(Category category, CategoryField categoryField, String fieldValues){
        this.category=category;
        this.categoryField = categoryField;
        this.fieldValues=fieldValues;
        this.id=new CategoryFieldValuesId(category.getId(), getCategoryField().getId());
        auditing=new Auditing();
    }

    @Override
    public String toString() {
        return "CategoryFieldValues{" +
                "id=" + id +
                ", fieldValues='" + fieldValues + '\'' +
                '}';
    }
}
