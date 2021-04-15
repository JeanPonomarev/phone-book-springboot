package ru.jeanponomarev.phonebookspringboot.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.jeanponomarev.phonebookspringboot.dao.ContactDaoImpl;
import ru.jeanponomarev.phonebookspringboot.entity.Contact;

import java.util.Arrays;
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

    @ParameterizedTest
    @ValueSource(strings = {"Alexander", "Jo", "jason", "Aaaaaaaaaaaaaaaaaaaa"})
    void shouldValidateCreateContactWithVariousFirstNameTypes(String firstName) {
        Contact contact = new Contact(firstName, "Doe", "1234567");

        Mockito.when(contactDao.getByPhoneNumber(Mockito.anyString())).thenReturn(Collections.emptyList());

        ContactValidationResult contactValidationResult = contactValidator.validateCreateContact(contact);
        assertThat(contactValidationResult.isValid()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"Ponomarev", "Jo", "smith", "Aaaaaaaaaaaaaaaaaaaa", "Sausage-Hausen", "d'Arras"})
    void shouldValidateCreateContactWithVariousLastNameTypes(String lastName) {
        Contact contact = new Contact("John", lastName, "1234567");

        Mockito.when(contactDao.getByPhoneNumber(Mockito.anyString())).thenReturn(Collections.emptyList());

        ContactValidationResult contactValidationResult = contactValidator.validateCreateContact(contact);
        assertThat(contactValidationResult.isValid()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"3469755", "346-97-55", "89134564322", "8-913-471-4748", "+79137436399", "+7-913-743-6399", "8-(383)-234-4534", "346 97 55", "1234"})
    void shouldValidateCreateContactWithVariousPhoneNumberTypes(String phoneNumber) {
        Contact contact = new Contact("John", "Doe", phoneNumber);

        Mockito.when(contactDao.getByPhoneNumber(phoneNumber)).thenReturn(Collections.emptyList());

        ContactValidationResult contactValidationResult = contactValidator.validateCreateContact(contact);
        assertThat(contactValidationResult.isValid()).isTrue();
    }

    @ParameterizedTest
    @MethodSource("provideInvalidContacts")
    void shouldValidateCreateContactWithInvalidField(Contact invalidContact, String expectedValidationMessage) {
        Mockito.when(contactDao.getByPhoneNumber(invalidContact.getPhoneNumber())).thenReturn(Collections.emptyList());

        ContactValidationResult contactValidationResult = contactValidator.validateCreateContact(invalidContact);

        assertThat(contactValidationResult.isValid()).isFalse();
        assertThat(contactValidationResult.getMessage()).isEqualTo(expectedValidationMessage);
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
        String phoneNumber = "1234567";
        Contact contact = new Contact("John", "Doe", "1234567");

        Mockito.when(contactDao.getByPhoneNumber(phoneNumber)).thenReturn(Collections.singletonList(new Contact()));

        ContactValidationResult contactValidationResult = contactValidator.validateUpdateContact(contact);
        assertThat(contactValidationResult.isValid()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"Alexander", "Jo", "jason", "Aaaaaaaaaaaaaaaaaaaa"})
    void shouldValidateUpdateContactWithVariousFirstNameTypes(String firstName) {
        Contact contact = new Contact(firstName, "Doe", "1234567");

        Mockito.when(contactDao.getByPhoneNumber(Mockito.anyString())).thenReturn(Collections.singletonList(new Contact()));

        ContactValidationResult contactValidationResult = contactValidator.validateUpdateContact(contact);
        assertThat(contactValidationResult.isValid()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"Ponomarev", "Jo", "smith", "Aaaaaaaaaaaaaaaaaaaa", "Sausage-Hausen", "d'Arras"})
    void shouldValidateUpdateContactWithVariousLastNameTypes(String lastName) {
        Contact contact = new Contact("John", lastName, "1234567");

        Mockito.when(contactDao.getByPhoneNumber(Mockito.anyString())).thenReturn(Collections.singletonList(new Contact()));

        ContactValidationResult contactValidationResult = contactValidator.validateUpdateContact(contact);
        assertThat(contactValidationResult.isValid()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"3469755", "346-97-55", "89134564322", "8-913-471-4748", "+79137436399", "+7-913-743-6399", "8-(383)-234-4534", "346 97 55", "1234"})
    void shouldValidateUpdateContactWithVariousPhoneNumberTypes(String phoneNumber) {
        Contact contact = new Contact("John", "Doe", phoneNumber);

        Mockito.when(contactDao.getByPhoneNumber(phoneNumber)).thenReturn(Collections.emptyList());

        ContactValidationResult contactValidationResult = contactValidator.validateUpdateContact(contact);
        assertThat(contactValidationResult.isValid()).isTrue();
    }

    @ParameterizedTest
    @MethodSource("provideInvalidContacts")
    void shouldValidateUpdateContactWithInvalidField(Contact invalidContact, String expectedValidationMessage) {
        ContactValidationResult contactValidationResult = contactValidator.validateUpdateContact(invalidContact);

        assertThat(contactValidationResult.isValid()).isFalse();
        assertThat(contactValidationResult.getMessage()).isEqualTo(expectedValidationMessage);
    }

    @Test
    void shouldValidateUpdateContactWithTakenPhoneNumber() {
        String phoneNumber = "1234567";
        Contact contact = new Contact("John", "Doe", phoneNumber);

        Mockito.when(contactDao.getByPhoneNumber(phoneNumber)).thenReturn(Arrays.asList(new Contact(), new Contact()));

        ContactValidationResult contactValidationResult = contactValidator.validateUpdateContact(contact);

        assertThat(contactValidationResult.isValid()).isFalse();
        assertThat(contactValidationResult.getMessage()).isEqualTo(String.format("Contact with phone number \"%s\" already exist", phoneNumber));
    }

    private static Stream<Arguments> provideInvalidContacts() {
        return Stream.of(
                Arguments.of(new Contact(null, "Doe", "1234567"), "First name shouldn't be null value"),
                Arguments.of(new Contact("", "Doe", "1234567"), "First name shouldn't be an empty string"),
                Arguments.of(new Contact("2423423", "Doe", "1234567"), "First name is incorrect"),
                Arguments.of(new Contact("I", "Doe", "1234567"), "First name is incorrect"),
                Arguments.of(new Contact("Aaaaaaaaaaaaaaaaaaaaaa", "Doe", "1234567"), "First name is incorrect"),

                Arguments.of(new Contact("John", null, "1234567"), "Last name shouldn't be null value"),
                Arguments.of(new Contact("John", "", "1234567"), "Last name shouldn't be an empty string"),
                Arguments.of(new Contact("John", "12345", "1234567"), "Last name is incorrect"),
                Arguments.of(new Contact("John", "I", "1234567"), "Last name is incorrect"),
                Arguments.of(new Contact("John", "Aaaaaaaaaaaaaaaaaaaaaa", "1234567"), "Last name is incorrect"),

                Arguments.of(new Contact("John", "Doe", null), "Phone number shouldn't be null value"),
                Arguments.of(new Contact("John", "Doe", ""), "Phone number shouldn't be an empty string"),
                Arguments.of(new Contact("John", "Doe", "abcde"), "Phone number is incorrect"),
                Arguments.of(new Contact("John", "Doe", "123"), "Phone number is incorrect"),
                Arguments.of(new Contact("John", "Doe", "123abc"), "Phone number is incorrect")
        );
    }
}