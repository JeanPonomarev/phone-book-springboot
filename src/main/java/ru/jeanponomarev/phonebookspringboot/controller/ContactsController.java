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

import java.util.List;

@Controller
@RequestMapping("/api")
public class ContactsController {

    private ContactService contactService;
    private ContactDtoToContactEntityConverter contactDtoToContactEntityConverter;
    private ContactEntityToContactDtoConverter contactEntityToContactDtoConverter;

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
    public List<ContactDto> getAllContacts() {
        return contactEntityToContactDtoConverter.convert(contactService.getAll());
    }

    @GetMapping("/getContact")
    @ResponseBody
    public List<ContactDto> getContactByProperty(@RequestParam String property) {
        return contactEntityToContactDtoConverter.convert(contactService.getByProperty(property));
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

    @PutMapping("/updateContact")
    @ResponseBody
    public ResponseEntity<ContactValidationResult> updateContact(@RequestBody ContactDto contactDto) {

        Contact contactEntity = contactDtoToContactEntityConverter.convert(contactDto);

        ContactValidationResult contactValidationResult = contactService.update(contactEntity);

        if (!contactValidationResult.isValid()) {
            return new ResponseEntity<>(contactValidationResult, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(contactValidationResult, HttpStatus.OK);
    }

    @DeleteMapping("/deleteContact")
    @ResponseBody
    public ResponseEntity<ContactValidationResult> deleteContact(@RequestParam Long id) {

        Contact removedContact = contactService.deleteById(id);

        ContactValidationResult contactValidationResult = new ContactValidationResult();

        if (removedContact == null) {
            contactValidationResult.setValid(false);
            contactValidationResult.setMessage("Target contact doesn't exist");

            return new ResponseEntity<>(contactValidationResult, HttpStatus.NO_CONTENT);
        }

        contactValidationResult.setValid(true);
        contactValidationResult.setMessage("Target contact was successfully deleted");

        return new ResponseEntity<>(contactValidationResult, HttpStatus.OK);
    }
}
