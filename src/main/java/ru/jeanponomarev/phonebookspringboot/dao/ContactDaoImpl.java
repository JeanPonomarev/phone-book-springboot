package ru.jeanponomarev.phonebookspringboot.dao;

import org.springframework.stereotype.Repository;
import ru.jeanponomarev.phonebookspringboot.entity.Contact;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

@Repository
public class ContactDaoImpl extends GenericDaoImpl<Contact, Long> implements ContactDao {

    public ContactDaoImpl() {
        super(Contact.class);
    }

    @Override
    public List<Contact> getAllContacts() {
        return getAll();
    }

    @Override
    public List<Contact> getByPhoneNumber(String phoneNumber) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Contact> criteriaQuery = criteriaBuilder.createQuery(clazz);

        Root<Contact> contactRoot = criteriaQuery.from(clazz);

        Predicate predicateForPhoneNumber = criteriaBuilder.equal(contactRoot.get("phoneNumber"), phoneNumber);

        criteriaQuery.where(predicateForPhoneNumber);

        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    @Override
    public List<Contact> getByProperty(String property) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Contact> criteriaQuery = criteriaBuilder.createQuery(clazz);

        Root<Contact> contactRoot = criteriaQuery.from(clazz);

        Predicate predicateForFirstName = criteriaBuilder.equal(contactRoot.get("firstName"), property);
        Predicate predicateForLastName = criteriaBuilder.equal(contactRoot.get("lastName"), property);
        Predicate predicateForPhoneNumber = criteriaBuilder.equal(contactRoot.get("phoneNumber"), property);

        Predicate finalPredicate = criteriaBuilder.or(predicateForFirstName, predicateForLastName, predicateForPhoneNumber);

        criteriaQuery.where(finalPredicate);

        return entityManager.createQuery(criteriaQuery).getResultList();
    }
}
