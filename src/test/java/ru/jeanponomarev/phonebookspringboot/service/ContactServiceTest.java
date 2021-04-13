package ru.jeanponomarev.phonebookspringboot.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.jeanponomarev.phonebookspringboot.dao.ContactDaoImpl;
import ru.jeanponomarev.phonebookspringboot.entity.Contact;
import ru.jeanponomarev.phonebookspringboot.validator.ContactValidationResult;
import ru.jeanponomarev.phonebookspringboot.validator.ContactValidator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest {

    @Mock
    private ContactDaoImpl contactDao;
    @Mock
    private ContactValidator contactValidator;

    private ContactService contactService;

    @BeforeEach
    void setUp() {
        contactService = new ContactService(contactDao, contactValidator);
    }

    @Test
    void shouldGetAllContacts() {
        contactService.getAll();

        verify(contactDao).getAllContacts();
    }

    @Test
    void shouldCreateContact() {
        Contact contact = new Contact("John", "Doe", "1234567");

        ContactValidationResult mockedValidationResult = new ContactValidationResult();
        mockedValidationResult.setValid(true);

        Mockito.when(contactValidator.validateCreateContact(contact)).thenReturn(mockedValidationResult);

        contactService.create(contact);

        ArgumentCaptor<Contact> contactArgumentCaptor = ArgumentCaptor.forClass(Contact.class);

        verify(contactDao).create(contactArgumentCaptor.capture());

        Contact capturedContact = contactArgumentCaptor.getValue();
        assertThat(capturedContact).isEqualTo(contact);
    }

    @Test
    void shouldNotCreateContactWithExistentPhoneNumber() {
        Contact contact = new Contact("John", "Doe", "1234567");

        ContactValidationResult mockedValidationResult = new ContactValidationResult();
        mockedValidationResult.setValid(false);

        Mockito.when(contactValidator.validateCreateContact(contact)).thenReturn(mockedValidationResult);

        contactService.create(contact);

        verifyNoInteractions(contactDao);
    }

    @Test
    void shouldUpdateContact() {
        Long id = 1L;

        Contact contact = new Contact(id, "newFirstName", "newLastName", "newPhoneNumber");

        ContactValidationResult mockedValidationResult = new ContactValidationResult();
        mockedValidationResult.setValid(true);

        Mockito.when(contactValidator.validateUpdateContact(contact)).thenReturn(mockedValidationResult);

        Mockito.when(contactDao.getById(id)).thenReturn(new Contact(id, "oldFirstName", "oldLastName", "oldPhoneNumber"));

        contactService.update(contact);

        ArgumentCaptor<Contact> contactArgumentCaptor = ArgumentCaptor.forClass(Contact.class);

        verify(contactDao).update(contactArgumentCaptor.capture());

        Contact capturedContact = contactArgumentCaptor.getValue();
        assertThat(capturedContact).isEqualTo(contact);
    }

    @Test
    void shouldNotProceedInvalidUpdate() {
        Long id = 1L;

        Contact contact = new Contact(id, "newFirstName", "newLastName", "newPhoneNumber");

        ContactValidationResult mockedValidationResult = new ContactValidationResult();
        mockedValidationResult.setValid(false);

        Mockito.when(contactValidator.validateUpdateContact(contact)).thenReturn(mockedValidationResult);

        contactService.update(contact);

        verifyNoInteractions(contactDao);
    }

    @Test
    void shouldDeleteById() {
        Long id = 1L;

        Mockito.when(contactDao.deleteById(id)).thenReturn(new Contact(id, "firstName", "lastName", "1234567"));

        ContactValidationResult contactValidationResult = contactService.deleteById(id);

        String expectedValidationMessage = "Contact was successfully deleted";

        assertThat(contactValidationResult.isValid()).isEqualTo(true);
        assertThat(contactValidationResult.getMessage()).isEqualTo(expectedValidationMessage);

        ArgumentCaptor<Long> contactArgumentCaptor = ArgumentCaptor.forClass(Long.class);

        verify(contactDao).deleteById(contactArgumentCaptor.capture());

        Long capturedId = contactArgumentCaptor.getValue();
        assertThat(capturedId).isEqualTo(id);
    }

    @Test
    void shouldHandleAbsentContactDelete() {
        Long id = 1L;

        Mockito.when(contactDao.deleteById(id)).thenReturn(null);

        ContactValidationResult contactValidationResult = contactService.deleteById(id);

        String expectedValidationMessage = String.format("Contact with id = %d doesn't exist", id);

        assertThat(contactValidationResult.isValid()).isEqualTo(false);
        assertThat(contactValidationResult.getMessage()).isEqualTo(expectedValidationMessage);

        ArgumentCaptor<Long> contactArgumentCaptor = ArgumentCaptor.forClass(Long.class);

        verify(contactDao).deleteById(contactArgumentCaptor.capture());

        Long capturedId = contactArgumentCaptor.getValue();
        assertThat(capturedId).isEqualTo(id);
    }
}