package ru.jeanponomarev.phonebookspringboot.dao;

import ru.jeanponomarev.phonebookspringboot.entity.Contact;

import java.util.List;

public interface ContactDao extends GenericDao<Contact, Long> {

    List<Contact> getAllContacts();

    List<Contact> getByPhoneNumber(String phoneNumber);

    List<Contact> getByProperty(String property);
}
