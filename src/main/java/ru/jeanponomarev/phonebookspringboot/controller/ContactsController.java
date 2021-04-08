package ru.jeanponomarev.phonebookspringboot.controller;

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
import ru.jeanponomarev.phonebookspringboot.validator.ContactValidator;

import java.util.List;

@Controller
@RequestMapping("/api")
public class ContactsController {

    private ContactService contactService;
    private ContactValidator contactValidator;
    private ContactDtoToContactEntityConverter contactDtoToContactEntityConverter;
    private ContactEntityToContactDtoConverter contactEntityToContactDtoConverter;

    @Autowired
    public ContactsController(ContactService contactService,
                              ContactValidator contactValidator,
                              ContactDtoToContactEntityConverter contactDtoToContactEntityConverter,
                              ContactEntityToContactDtoConverter contactEntityToContactDtoConverter) {
        this.contactService = contactService;
        this.contactValidator = contactValidator;
        this.contactDtoToContactEntityConverter = contactDtoToContactEntityConverter;
        this.contactEntityToContactDtoConverter = contactEntityToContactDtoConverter;
    }

    @GetMapping("/getAllContacts")
    @ResponseBody
    public List<Contact> getAllContacts() {
        return contactService.getAll();
    }

    @PostMapping("/createContact")
    @ResponseBody
    public ResponseEntity<ContactValidationResult> createContact(@RequestBody ContactDto contactDto) {
        Contact contactEntity = contactDtoToContactEntityConverter.convert(contactDto);
        
        contactEntity.setId(null);

        ContactValidationResult contactValidationResult = contactService.create(contactEntity);

        if (!contactValidationResult.isValid()) {
            return new ResponseEntity<>(contactValidationResult, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(contactValidationResult, HttpStatus.OK);
    }
}
