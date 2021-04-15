package ru.jeanponomarev.phonebookspringboot.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.jeanponomarev.phonebookspringboot.dao.ContactDao;
import ru.jeanponomarev.phonebookspringboot.entity.Contact;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ContactValidator {

    private final ContactDao contactDao;

    @Autowired
    public ContactValidator(ContactDao contactDao) {
        this.contactDao = contactDao;
    }

    public ContactValidationResult validateCreateContact(Contact contact) {
        ContactValidationResult contactValidationResult = validateContactFields(contact);

        String phoneNumber = contact.getPhoneNumber();

        List<Contact> contactList = contactDao.getByPhoneNumber(phoneNumber);

        if (!contactList.isEmpty()) {
            contactValidationResult.setValid(false);
            contactValidationResult.setMessage(String.format("Contact with phone number \"%s\" already exists", phoneNumber));

            return contactValidationResult;
        }

        return contactValidationResult;
    }

    public ContactValidationResult validateUpdateContact(Contact contact) {

        ContactValidationResult contactValidationResult = validateContactFields(contact);

        String phoneNumber = contact.getPhoneNumber();

        List<Contact> contactList = contactDao.getByPhoneNumber(phoneNumber);

        if (contactList.size() > 1) {
            contactValidationResult.setValid(false);
            contactValidationResult.setMessage(String.format("Contact with phone number \"%s\" already exists", phoneNumber));

            return contactValidationResult;
        }

        return contactValidationResult;
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

        Pattern firstNamePattern = Pattern.compile("(?i)(^[a-z])((?![ .,'-]$)[a-z .,'-]){1,20}$");
        Matcher firstNameMatcher = firstNamePattern.matcher(contact.getFirstName());

        if (!firstNameMatcher.matches()) {
            contactValidationResult.setValid(false);
            contactValidationResult.setMessage("First name is incorrect");

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

        Pattern lastNamePattern = Pattern.compile("(?i)(^[a-z])((?![ .,'-]$)[a-z .,'-]){1,20}$");
        Matcher lastNameMatcher = lastNamePattern.matcher(contact.getLastName());

        if (!lastNameMatcher.matches()) {
            contactValidationResult.setValid(false);
            contactValidationResult.setMessage("Last name is incorrect");

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

        Pattern phonePattern = Pattern.compile("([-()_.+ ]*\\d[-()_.+ ]*){4,}");
        Matcher phoneMatcher = phonePattern.matcher(contact.getPhoneNumber());

        if (!phoneMatcher.matches()) {
            contactValidationResult.setValid(false);
            contactValidationResult.setMessage("Phone number is incorrect");

            return contactValidationResult;
        }

        contactValidationResult.setValid(true);

        return contactValidationResult;
    }
}
