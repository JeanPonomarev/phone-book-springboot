package ru.jeanponomarev.phonebookspringboot.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.jeanponomarev.phonebookspringboot.converter.ContactDtoToContactEntityConverter;
import ru.jeanponomarev.phonebookspringboot.converter.ContactEntityToContactDtoConverter;
import ru.jeanponomarev.phonebookspringboot.dto.ContactDto;
import ru.jeanponomarev.phonebookspringboot.entity.Contact;
import ru.jeanponomarev.phonebookspringboot.service.ContactService;
import ru.jeanponomarev.phonebookspringboot.validator.ContactValidationResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@WebMvcTest(value = ContactsController.class)
class ContactsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContactService contactService;
    @MockBean
    private ContactDtoToContactEntityConverter contactDtoToContactEntityConverter;
    @MockBean
    private ContactEntityToContactDtoConverter contactEntityToContactDtoConverter;

    @Test
    void getAllContacts() throws Exception {
        String uri = "/api/getAllContacts";

        List<Contact> mockedContactEntityList = new ArrayList<>();
        mockedContactEntityList.add(new Contact(1L, "John", "Doe", "1111111"));
        mockedContactEntityList.add(new Contact(2L, "Ivan", "Ivanov", "2222222"));
        mockedContactEntityList.add(new Contact(3L, "Petr", "Petrov", "3333333"));

        Mockito.when(contactService.getAll()).thenReturn(mockedContactEntityList);

        List<ContactDto> mockedContactDtoList = new ArrayList<>();
        mockedContactDtoList.add(new ContactDto(1L, "John", "Doe", "1111111"));
        mockedContactDtoList.add(new ContactDto(2L, "Ivan", "Ivanov", "2222222"));
        mockedContactDtoList.add(new ContactDto(3L, "Petr", "Petrov", "3333333"));

        Mockito.when(contactEntityToContactDtoConverter.convert(Mockito.anyList())).thenReturn(mockedContactDtoList);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(uri);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();

        String responseJson = response.getContentAsString();

        assertThat(responseJson).isEqualTo(convertContactDtoListToJson(mockedContactDtoList));

        verify(contactService).getAll();
        verify(contactEntityToContactDtoConverter).convert(mockedContactEntityList);
    }

    @Test
    void getContactByProperty() throws Exception {
        String property = "Ivan";
        String uri = "/api/getContact";

        List<Contact> mockedContactEntityList = new ArrayList<>();
        mockedContactEntityList.add(new Contact(2L, "Ivan", "Ivanov", "2222222"));

        Mockito.when(contactService.getByProperty(Mockito.anyString())).thenReturn(mockedContactEntityList);

        List<ContactDto> mockedContactDtoList = new ArrayList<>();
        mockedContactDtoList.add(new ContactDto(2L, "Ivan", "Ivanov", "2222222"));

        Mockito.when(contactEntityToContactDtoConverter.convert(Mockito.anyList())).thenReturn(mockedContactDtoList);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(uri)
                .param("property", property);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();

        String responseJson = response.getContentAsString();

        assertThat(responseJson).isEqualTo(convertContactDtoListToJson(mockedContactDtoList));

        verify(contactService).getByProperty(property);
        verify(contactEntityToContactDtoConverter).convert(mockedContactEntityList);
    }

    @Test
    void shouldCreateValidContact() throws Exception {
        String uri = "/api/createContact";

        ContactDto contactDto = new ContactDto("John", "Doe", "1234567");
        Contact contact = new Contact("John", "Doe", "1234567");

        Mockito.when(contactDtoToContactEntityConverter.convert(Mockito.any(ContactDto.class))).thenReturn(contact);

        ContactValidationResult mockedValidationResult = new ContactValidationResult();
        mockedValidationResult.setValid(true);

        String expectedResponseMessage = "Contact was successfully created";
        mockedValidationResult.setMessage(expectedResponseMessage);

        Mockito.when(contactService.create(Mockito.any(Contact.class))).thenReturn(mockedValidationResult);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(uri)
                .accept(MediaType.APPLICATION_JSON).content(contactDto.convertToJson())
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();

        String responseJson = response.getContentAsString();
        String responseMessage = retrieveMessage(responseJson);
        assertThat(responseMessage).isEqualTo(expectedResponseMessage);

        int statusCode = response.getStatus();
        assertThat(statusCode).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void shouldNotCreateInvalidContact() throws Exception {
        String uri = "/api/createContact";

        ContactDto contactDto = new ContactDto("", "Doe", "1234567");
        Contact contact = new Contact("", "Doe", "1234567");

        Mockito.when(contactDtoToContactEntityConverter.convert(Mockito.any(ContactDto.class))).thenReturn(contact);

        ContactValidationResult mockedValidationResult = new ContactValidationResult();
        mockedValidationResult.setValid(false);

        String expectedResponseMessage = "First name shouldn't be an empty string";
        mockedValidationResult.setMessage(expectedResponseMessage);

        Mockito.when(contactService.create(Mockito.any(Contact.class))).thenReturn(mockedValidationResult);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(uri)
                .accept(MediaType.APPLICATION_JSON).content(contactDto.convertToJson())
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();

        String responseJson = response.getContentAsString();
        String responseMessage = retrieveMessage(responseJson);
        assertThat(responseMessage).isEqualTo(expectedResponseMessage);

        int statusCode = response.getStatus();
        assertThat(statusCode).isEqualTo(HttpStatus.BAD_REQUEST.value());

    }

    @Test
    void shouldUpdateContact() throws Exception {
        String uri = "/api/updateContact";

        ContactDto contactDtoUpdate = new ContactDto(1L, "newName", "newLastName", "newPhone");

        Mockito.when(contactDtoToContactEntityConverter.convert(Mockito.any(ContactDto.class))).thenReturn(new Contact());

        String expectedResponseMessage = "Contact was successfully updated";
        ContactValidationResult contactValidationResult = new ContactValidationResult(true, expectedResponseMessage);

        Mockito.when(contactService.update(Mockito.any(Contact.class))).thenReturn(contactValidationResult);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(uri)
                .accept(MediaType.APPLICATION_JSON).content(contactDtoUpdate.convertToJson())
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();

        String responseJson = response.getContentAsString();
        String responseMessage = retrieveMessage(responseJson);
        assertThat(responseMessage).isEqualTo(expectedResponseMessage);

        int statusCode = response.getStatus();
        assertThat(statusCode).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void shouldNotProceedInvalidUpdate() throws Exception {
        String uri = "/api/updateContact";

        ContactDto contactDtoUpdate = new ContactDto(1L, "", "newLastName", "newPhoneNumber");

        Mockito.when(contactDtoToContactEntityConverter.convert(Mockito.any(ContactDto.class))).thenReturn(new Contact());

        String expectedResponseMessage = "First name shouldn't be null value";
        ContactValidationResult contactValidationResult = new ContactValidationResult(false, expectedResponseMessage);

        Mockito.when(contactService.update(Mockito.any(Contact.class))).thenReturn(contactValidationResult);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(uri)
                .accept(MediaType.APPLICATION_JSON).content(contactDtoUpdate.convertToJson())
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();

        String responseJson = response.getContentAsString();
        String responseMessage = retrieveMessage(responseJson);
        assertThat(responseMessage).isEqualTo(expectedResponseMessage);

        int statusCode = response.getStatus();
        assertThat(statusCode).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void shouldDeleteContact() throws Exception {
        String uri = "/api/deleteContact";
        String id = "1";

        String expectedResponseMessage = "Contact was successfully deleted";
        ContactValidationResult contactValidationResult = new ContactValidationResult(true, expectedResponseMessage);

        Mockito.when(contactService.deleteById(Mockito.anyLong())).thenReturn(contactValidationResult);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(uri)
                .param("id", id);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();

        String responseJson = response.getContentAsString();
        String responseMessage = retrieveMessage(responseJson);
        assertThat(responseMessage).isEqualTo(expectedResponseMessage);

        int statusCode = response.getStatus();
        assertThat(statusCode).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void shouldHandleAbsentContactDelete() throws Exception {
        String uri = "/api/deleteContact";
        String id = "1";

        String expectedResponseMessage = String.format("Contact with id = %s doesn't exist", id);
        ContactValidationResult contactValidationResult = new ContactValidationResult(false, expectedResponseMessage);

        Mockito.when(contactService.deleteById(Mockito.anyLong())).thenReturn(contactValidationResult);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(uri)
                .param("id", id);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();

        String responseJson = response.getContentAsString();
        String responseMessage = retrieveMessage(responseJson);
        assertThat(responseMessage).isEqualTo(expectedResponseMessage);

        int statusCode = response.getStatus();
        assertThat(statusCode).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void shouldDeleteContactList() throws Exception {
        String uri = "/api/deleteContactList";
        List<Long> ids = Arrays.asList(1L, 2L, 3L, 4L, 5L);

        String expectedResponseMessage = "All target contacts have been successfully deleted";
        ContactValidationResult contactValidationResult = new ContactValidationResult(true, expectedResponseMessage);

        Mockito.when(contactService.deleteContactList(ids)).thenReturn(contactValidationResult);

        String idsJson = generateJsonArray(ids);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(uri)
                .accept(MediaType.APPLICATION_JSON)
                .content(idsJson)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();

        String responseJson = response.getContentAsString();
        String responseMessage = retrieveMessage(responseJson);
        assertThat(responseMessage).isEqualTo(expectedResponseMessage);

        int statusCode = response.getStatus();
        assertThat(statusCode).isEqualTo(HttpStatus.OK.value());
    }

    private String generateJsonArray(List<Long> ids) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jsonNode = mapper.createObjectNode();

        ArrayNode arrayNode = jsonNode.putArray("ids");
        ids.forEach(arrayNode::add);

        return arrayNode.toString();
    }

    private String retrieveMessage(String json) throws JsonProcessingException {
        ObjectNode objectNode = new ObjectMapper().readValue(json, ObjectNode.class);
        return objectNode.findValue("message").asText();
    }

    private String convertContactDtoListToJson(List<ContactDto> contactDtoList) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(contactDtoList);
    }
}