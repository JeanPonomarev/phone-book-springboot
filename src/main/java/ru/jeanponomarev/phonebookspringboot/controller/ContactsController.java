package ru.jeanponomarev.phonebookspringboot.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.jeanponomarev.phonebookspringboot.converter.ContactDtoToContactEntityConverter;
import ru.jeanponomarev.phonebookspringboot.converter.ContactEntityToContactDtoConverter;
import ru.jeanponomarev.phonebookspringboot.dto.ContactDto;
import ru.jeanponomarev.phonebookspringboot.entity.Contact;
import ru.jeanponomarev.phonebookspringboot.service.ContactService;
import ru.jeanponomarev.phonebookspringboot.validator.ContactValidationResult;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static ru.jeanponomarev.phonebookspringboot.utils.LoggerUtils.logBasicUriInfo;

@Controller
@RequestMapping("/api")
public class ContactsController {
    private static final Logger logger = LoggerFactory.getLogger(ContactsController.class);

    private final ContactService contactService;
    private final ContactDtoToContactEntityConverter contactDtoToContactEntityConverter;
    private final ContactEntityToContactDtoConverter contactEntityToContactDtoConverter;

    @Autowired
    public ContactsController(ContactService contactService,
                              ContactDtoToContactEntityConverter contactDtoToContactEntityConverter,
                              ContactEntityToContactDtoConverter contactEntityToContactDtoConverter) {
        this.contactService = contactService;
        this.contactDtoToContactEntityConverter = contactDtoToContactEntityConverter;
        this.contactEntityToContactDtoConverter = contactEntityToContactDtoConverter;
    }

    @GetMapping("/getAllContacts")
    @ResponseBody
    public List<ContactDto> getAllContacts(HttpServletRequest request) {
        logBasicUriInfo(logger, request);

        return contactEntityToContactDtoConverter.convert(contactService.getAll());
    }

    @GetMapping("/getContact")
    @ResponseBody
    public List<ContactDto> getContactByProperty(@RequestParam String property, HttpServletRequest request) {
        logBasicUriInfo(logger, request);

        List<Contact> targetContacts = contactService.getByProperty(property);

        return contactEntityToContactDtoConverter.convert(targetContacts);
    }

    @PostMapping("/createContact")
    @ResponseBody
    public ResponseEntity<ContactValidationResult> createContact(@RequestBody ContactDto contactDto, HttpServletRequest request) {
        logBasicUriInfo(logger, request);

        Contact contactEntity = contactDtoToContactEntityConverter.convert(contactDto);
        
        contactEntity.setId(null);

        ContactValidationResult contactValidationResult = contactService.create(contactEntity);

        if (!contactValidationResult.isValid()) {
            return new ResponseEntity<>(contactValidationResult, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(contactValidationResult, HttpStatus.OK);
    }

    @PutMapping("/updateContact")
    @ResponseBody
    public ResponseEntity<ContactValidationResult> updateContact(@RequestBody ContactDto contactDto, HttpServletRequest request) {
        logBasicUriInfo(logger, request);

        Contact contactEntity = contactDtoToContactEntityConverter.convert(contactDto);

        ContactValidationResult contactValidationResult = contactService.update(contactEntity);

        if (!contactValidationResult.isValid()) {
            return new ResponseEntity<>(contactValidationResult, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(contactValidationResult, HttpStatus.OK);
    }

    @DeleteMapping("/deleteContact")
    @ResponseBody
    public ResponseEntity<ContactValidationResult> deleteContact(@RequestParam Long id, HttpServletRequest request) {
        logBasicUriInfo(logger, request);

        ContactValidationResult contactValidationResult = contactService.deleteById(id);

        if (!contactValidationResult.isValid()) {
            return new ResponseEntity<>(contactValidationResult, HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(contactValidationResult, HttpStatus.OK);
    }
}
