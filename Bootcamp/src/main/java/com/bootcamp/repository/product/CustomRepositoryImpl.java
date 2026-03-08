package com.bootcamp.repository.product;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CustomRepositoryImpl implements CustomRepository {
    @PersistenceContext
    EntityManager entityManager;
    @Override
    public List<Object[]> executeQuery(String query) {
        Query nativeQuery = entityManager.createNativeQuery(query);
        return nativeQuery.getResultList();
    }
}
