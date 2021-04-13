package ru.jeanponomarev.phonebookspringboot.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.jeanponomarev.phonebookspringboot.dao.ContactDaoImpl;
import ru.jeanponomarev.phonebookspringboot.entity.Contact;

import java.util.Collections;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ContactValidatorTest {

    @Mock
    private ContactDaoImpl contactDao;
    private ContactValidator contactValidator;

    @BeforeEach
    void setUp() {
        contactValidator = new ContactValidator(contactDao);
    }

    @Test
    void shouldValidateCreateContact() {
        String phoneNumber = "1234567";
        Contact contact = new Contact("John", "Doe", phoneNumber);

        Mockito.when(contactDao.getByPhoneNumber(phoneNumber)).thenReturn(Collections.emptyList());

        ContactValidationResult contactValidationResult = contactValidator.validateCreateContact(contact);
        assertThat(contactValidationResult.isValid()).isTrue();
    }

    @Test
    void shouldValidateCreateContactWithExistentPhoneNumber() {
        String phoneNumber = "1234567";
        Contact contact = new Contact("John", "Doe", phoneNumber);

        Mockito.when(contactDao.getByPhoneNumber(phoneNumber)).thenReturn(Collections.singletonList(new Contact()));

        ContactValidationResult contactValidationResult = contactValidator.validateCreateContact(contact);

        assertThat(contactValidationResult.isValid()).isFalse();
        assertThat(contactValidationResult.getMessage()).isEqualTo(String.format("Contact with phone number \"%s\" already exist", phoneNumber));
    }

    @Test
    void shouldValidateUpdateContact() {
        Contact contact = new Contact("John", "Doe", "1234567");

        ContactValidationResult contactValidationResult = contactValidator.validateUpdateContact(contact);
        assertThat(contactValidationResult.isValid()).isTrue();
    }

    private static Stream<Arguments> provideInvalidContacts() {
        return Stream.of(
            Arguments.of(new Contact(null, "Doe", "1234567"), "First name shouldn't be null value"),
            Arguments.of(new Contact("", "Doe", "1234567"), "First name shouldn't be an empty string"),
            Arguments.of(new Contact("John", null, "1234567"), "Last name shouldn't be null value"),
            Arguments.of(new Contact("John", "", "1234567"), "Last name shouldn't be an empty string"),
            Arguments.of(new Contact("John", "Doe", null), "Phone number shouldn't be null value"),
            Arguments.of(new Contact("John", "Doe", ""), "Phone number shouldn't be an empty string")
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidContacts")
    void shouldValidateCreateContactWithInvalidField(Contact invalidContact, String expectedValidationMessage) {
        Mockito.when(contactDao.getByPhoneNumber(invalidContact.getPhoneNumber())).thenReturn(Collections.emptyList());

        ContactValidationResult contactValidationResult = contactValidator.validateCreateContact(invalidContact);

        assertThat(contactValidationResult.isValid()).isFalse();
        assertThat(contactValidationResult.getMessage()).isEqualTo(expectedValidationMessage);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidContacts")
    void shouldValidateUpdateContactWithInvalidField(Contact invalidContact, String expectedValidationMessage) {
        ContactValidationResult contactValidationResult = contactValidator.validateUpdateContact(invalidContact);

        assertThat(contactValidationResult.isValid()).isFalse();
        assertThat(contactValidationResult.getMessage()).isEqualTo(expectedValidationMessage);
    }
}