package ru.jeanponomarev.phonebookspringboot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.jeanponomarev.phonebookspringboot.controller.ContactsController;
import ru.jeanponomarev.phonebookspringboot.dao.ContactDao;
import ru.jeanponomarev.phonebookspringboot.entity.Contact;
import ru.jeanponomarev.phonebookspringboot.validator.ContactValidationResult;
import ru.jeanponomarev.phonebookspringboot.validator.ContactValidator;

import java.util.List;

import static ru.jeanponomarev.phonebookspringboot.utils.LoggerUtils.*;

@Service
public class ContactService {
    private static final Logger logger = LoggerFactory.getLogger(ContactsController.class);

    private final ContactDao contactDao;
    private final ContactValidator contactValidator;

    @Autowired
    public ContactService(ContactDao contactDao, ContactValidator contactValidator) {
        this.contactDao = contactDao;
        this.contactValidator = contactValidator;
    }

    public List<Contact> getAll() {
        List<Contact> contacts = contactDao.getAllContacts();

        logGetAllContacts(logger, contacts);

        return contacts;
    }

    public List<Contact> getByProperty(String property) {
        List<Contact> contactsWithTargetProperty = contactDao.getByProperty(property);

        logGetContactByProperty(logger, property, contactsWithTargetProperty);

        return contactsWithTargetProperty;
    }

    public ContactValidationResult create(Contact contact) {
        ContactValidationResult contactValidationResult = contactValidator.validateCreateContact(contact);

        if (contactValidationResult.isValid()) {
            contactDao.create(contact);

            logCreateContact(logger, contact);

            contactValidationResult.setMessage("Contact was successfully created");
        }

        return contactValidationResult;
    }

    public ContactValidationResult update(Contact contact) {
        ContactValidationResult contactValidationResult = contactValidator.validateUpdateContact(contact);

        if (contactValidationResult.isValid()) {
            String oldContactAsString = contactDao.getById(contact.getId()).toString();

            contactDao.update(contact);

            logUpdateContact(logger, oldContactAsString, contact);

            contactValidationResult.setMessage("Contact was successfully updated");
        }

        return contactValidationResult;
    }

    public ContactValidationResult deleteById(Long id) {
        Contact removedContact = contactDao.deleteById(id);

        logDeleteContactById(logger, removedContact);

        ContactValidationResult contactValidationResult = new ContactValidationResult();

        if (removedContact == null) {
            contactValidationResult.setValid(false);
            contactValidationResult.setMessage(String.format("Contact with id = %d doesn't exist", id));

            return contactValidationResult;
        }

        contactValidationResult.setValid(true);
        contactValidationResult.setMessage("Contact was successfully deleted");

        return contactValidationResult;
    }

    public ContactValidationResult deleteContactList(List<Long> ids) {
        int expectedAmountOfDeletedContacts = ids.size();
        int actualAmountOfDeletedContacts = contactDao.deleteContactList(ids);

        logDeleteContactList(logger, actualAmountOfDeletedContacts, expectedAmountOfDeletedContacts);

        ContactValidationResult contactValidationResult = new ContactValidationResult();
        contactValidationResult.setValid(true);

        if (actualAmountOfDeletedContacts != expectedAmountOfDeletedContacts) {
            contactValidationResult.setMessage(String.format("%d contacts not presented in the database",
                    expectedAmountOfDeletedContacts - actualAmountOfDeletedContacts));
        } else {
            contactValidationResult.setMessage("All target contacts have been successfully deleted");
        }

        return contactValidationResult;
    }
}
