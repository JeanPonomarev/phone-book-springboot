package ru.jeanponomarev.phonebookspringboot.dao;

import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.List;

@Transactional
public class GenericDaoImpl<T, PK extends Serializable> implements GenericDao<T, PK> {

    @PersistenceContext
    protected EntityManager entityManager;

    protected Class<T> clazz;

    public GenericDaoImpl(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Transactional
    @Override
    public List<T> getAll() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(clazz);

        Root<T> entityRoot = criteriaQuery.from(clazz);

        criteriaQuery.select(entityRoot);
        TypedQuery<T> typedQuery = entityManager.createQuery(criteriaQuery);

        return typedQuery.getResultList();
    }

    @Transactional
    @Override
    public T getById(PK id) {
        return entityManager.find(clazz, id);
    }

    @Transactional
    @Override
    public void create(T object) {
        entityManager.persist(object);
    }

    @Transactional
    @Override
    public void update(T object) {
        entityManager.merge(object);
    }

    @Transactional
    @Override
    public void delete(T object) {
        entityManager.remove(object);
    }

    @Transactional
    @Override
    public T deleteById(PK id) {
        T targetEntity = getById(id);
        delete(targetEntity);

        return targetEntity;
    }
}
