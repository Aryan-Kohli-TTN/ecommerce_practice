package com.bootcamp.entity.category;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
public class CategoryFieldValuesId implements Serializable {

    @Column(name="category_id")
    private UUID categoryId;

    @Column(name="category_field_id")
    private UUID categoryFieldId;

    public CategoryFieldValuesId(UUID categoryFieldId, UUID categoryId) {
        this.categoryFieldId = categoryFieldId;
        this.categoryId = categoryId;
    }

    @Override
    public String toString() {
        return "CategoryFieldValuesId{" +
                "categoryId=" + categoryId +
                ", categoryFieldId=" + categoryFieldId +
                '}';
    }
}
