package ru.jeanponomarev.phonebookspringboot.dao;

import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

public interface GenericDao<T, PK extends Serializable> {

    @Transactional
    List<T> getAll();

    @Transactional
    void create(T object);

    @Transactional
    void update(T object);

    @Transactional
    void delete(T object);

    @Transactional
    T getById(PK id);

    @Transactional
    T deleteById(PK id);
}
