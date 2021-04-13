package ru.jeanponomarev.phonebookspringboot.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.jeanponomarev.phonebookspringboot.entity.Contact;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
class ContactDaoImplTest {

    @Autowired
    private ContactDaoImpl contactDao;

    @AfterEach
    void tearDown() {
        contactDao.deleteAll();
    }

    @Test
    void shouldCreateContact() {
        Contact contact = new Contact("John", "Doe", "1234567");

        contactDao.create(contact);

        List<Contact> actualContactList = contactDao.getAllContacts();

        assertThat(actualContactList.size()).isEqualTo(1);
        assertThat(actualContactList).contains(contact);
    }

    @Test
    void shouldGetAllContacts() {
        List<Contact> expectedContactList = getPopulatedContactList();

        expectedContactList.forEach(contact -> contactDao.create(contact));

        List<Contact> actualContactList = contactDao.getAllContacts();

        assertThat(actualContactList.size()).isEqualTo(expectedContactList.size());

        for (int i = 0; i < expectedContactList.size(); i++) {
            Contact nextActualContact = actualContactList.get(i);

            assertThat(nextActualContact).isEqualTo(expectedContactList.get(i));
        }
    }

    @Test
    void shouldGetContactByPhoneNumber() {
        List<Contact> contactList = getPopulatedContactList();

        contactList.forEach(contact -> contactDao.create(contact));

        String expectedPhoneNumber = contactList.get(2).getPhoneNumber();

        List<Contact> actualContactList = contactDao.getByPhoneNumber(expectedPhoneNumber);
        assertThat(actualContactList.size()).isEqualTo(1);

        Contact actualTargetContact = actualContactList.get(0);
        assertThat(actualTargetContact.getPhoneNumber()).isEqualTo(expectedPhoneNumber);
    }

    @Test
    void shouldGetContactByFirstName() {
        List<Contact> contactList = getPopulatedContactList();

        contactList.forEach(contact -> contactDao.create(contact));

        String expectedFirstName = contactList.get(1).getFirstName();

        List<Contact> actualContactList = contactDao.getByProperty(expectedFirstName);
        assertThat(actualContactList.size()).isEqualTo(1);

        Contact actualTargetContact = actualContactList.get(0);
        assertThat(actualTargetContact.getFirstName()).isEqualTo(expectedFirstName);
    }

    @Test
    void shouldGetContactByLastName() {
        List<Contact> contactList = getPopulatedContactList();

        contactList.forEach(contact -> contactDao.create(contact));

        String expectedLastName = contactList.get(1).getLastName();

        List<Contact> actualContactList = contactDao.getByProperty(expectedLastName);
        assertThat(actualContactList.size()).isEqualTo(1);

        Contact actualTargetContact = actualContactList.get(0);
        assertThat(actualTargetContact.getLastName()).isEqualTo(expectedLastName);
    }

    @Test
    void shouldUpdateContact() {
        List<Contact> initialContactList = getPopulatedContactList();

        initialContactList.forEach(contact -> contactDao.create(contact));

        Contact expectedUpdatedContact = initialContactList.get(3);

        expectedUpdatedContact.setFirstName("newFirstName");
        expectedUpdatedContact.setLastName("newLastName");
        expectedUpdatedContact.setPhoneNumber("7654321");
        Long expectedUpdatedContactId = expectedUpdatedContact.getId();

        contactDao.update(expectedUpdatedContact);

        Contact actualUpdatedContact = contactDao.getById(expectedUpdatedContactId);

        assertThat(actualUpdatedContact).isEqualTo(expectedUpdatedContact);
    }

    @Test
    void shouldDeleteContact() {
        List<Contact> initialContactList = getPopulatedContactList();

        initialContactList.forEach(contact -> contactDao.create(contact));

        Contact expectedDeletedContact = initialContactList.get(3);
        Long expectedDeletedContactId = expectedDeletedContact.getId();

        Contact actualDeletedContact = contactDao.deleteById(expectedDeletedContactId);

        assertThat(actualDeletedContact).isEqualTo(expectedDeletedContact);
        assertThat(actualDeletedContact.getId()).isEqualTo(expectedDeletedContact.getId());

        List<Contact> contactListAfterDelete = contactDao.getAllContacts();

        assertThat(contactListAfterDelete).doesNotContain(expectedDeletedContact);
        assertThat(contactListAfterDelete.size()).isEqualTo(initialContactList.size() - 1);
    }

    private List<Contact> getPopulatedContactList() {
        List<Contact> contacts = new ArrayList<>();

        contacts.add(new Contact("John", "Doe", "1111111"));
        contacts.add(new Contact("Jean", "Ponomarev", "2222222"));
        contacts.add(new Contact("Petr", "Petrov", "3333333"));
        contacts.add(new Contact("Ivan", "Ivanov", "4444444"));
        contacts.add(new Contact("Roman", "Romanov", "5555555"));

        return contacts;
    }
}