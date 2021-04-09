package ru.jeanponomarev.phonebookspringboot.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.jeanponomarev.phonebookspringboot.dao.ContactDao;
import ru.jeanponomarev.phonebookspringboot.entity.Contact;

import java.util.List;

@Service
public class ContactValidator {

    private final ContactDao contactDao;

    @Autowired
    public ContactValidator(ContactDao contactDao) {
        this.contactDao = contactDao;
    }

    public ContactValidationResult validateCreateContact(Contact contact) {
        ContactValidationResult contactValidationResult = validateContactFields(contact);

        if (isContactWithTargetPhoneNumberExistent(contact.getPhoneNumber())) {
            contactValidationResult.setValid(false);
            contactValidationResult.setMessage("Contact with this phone number already exist");

            return contactValidationResult;
        }

        return contactValidationResult;
    }

    public ContactValidationResult validateUpdateContact(Contact contact) {
        return validateContactFields(contact);
    }

    private ContactValidationResult validateContactFields(Contact contact) {
        ContactValidationResult contactValidationResult = new ContactValidationResult();

        if (contact.getFirstName() == null) {
            contactValidationResult.setValid(false);
            contactValidationResult.setMessage("First name shouldn't be null value");

            return contactValidationResult;
        }

        if (contact.getFirstName().isEmpty()) {
            contactValidationResult.setValid(false);
            contactValidationResult.setMessage("First name shouldn't be an empty string");

            return contactValidationResult;
        }

        if (contact.getLastName() == null) {
            contactValidationResult.setValid(false);
            contactValidationResult.setMessage("Last name shouldn't be null value");

            return contactValidationResult;
        }

        if (contact.getLastName().isEmpty()) {
            contactValidationResult.setValid(false);
            contactValidationResult.setMessage("Last name shouldn't be an empty string");

            return contactValidationResult;
        }

        if (contact.getPhoneNumber() == null) {
            contactValidationResult.setValid(false);
            contactValidationResult.setMessage("Phone number shouldn't be null value");

            return contactValidationResult;
        }

        if (contact.getPhoneNumber().isEmpty()) {
            contactValidationResult.setValid(false);
            contactValidationResult.setMessage("Phone number shouldn't be an empty string");

            return contactValidationResult;
        }

        contactValidationResult.setValid(true);

        return contactValidationResult;
    }

    private boolean isContactWithTargetPhoneNumberExistent(String phoneNumber) {
        List<Contact> contactList = contactDao.getByPhoneNumber(phoneNumber);

        return !contactList.isEmpty();
    }
}
