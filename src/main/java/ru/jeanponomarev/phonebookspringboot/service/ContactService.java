package ru.jeanponomarev.phonebookspringboot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.jeanponomarev.phonebookspringboot.dao.ContactDao;
import ru.jeanponomarev.phonebookspringboot.entity.Contact;
import ru.jeanponomarev.phonebookspringboot.validator.ContactValidationResult;
import ru.jeanponomarev.phonebookspringboot.validator.ContactValidator;

import java.util.List;

@Service
public class ContactService {

    private ContactDao contactDao;
    private ContactValidator contactValidator;

    @Autowired
    public ContactService(ContactDao contactDao, ContactValidator contactValidator) {
        this.contactDao = contactDao;
        this.contactValidator = contactValidator;
    }

    public List<Contact> getAll() {
        return contactDao.getAllContacts();
    }

    public List<Contact> getByProperty(String property) {
        return contactDao.getByProperty(property);
    }

    public ContactValidationResult create(Contact contact) {
        ContactValidationResult contactValidationResult = contactValidator.validateContact(contact);

        if (contactValidationResult.isValid()) {
            contactDao.create(contact);
            contactValidationResult.setMessage("Contact was successfully created");
        }

        return contactValidationResult;
    }

    public void update(Contact contact) {
        contactDao.update(contact);
    }

    public Contact removeById(Long id) {
        return contactDao.removeById(id);
    }
}