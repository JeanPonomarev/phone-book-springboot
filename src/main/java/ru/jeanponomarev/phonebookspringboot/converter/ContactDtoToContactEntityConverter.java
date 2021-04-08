package ru.jeanponomarev.phonebookspringboot.converter;

import org.springframework.stereotype.Service;
import ru.jeanponomarev.phonebookspringboot.dto.ContactDto;
import ru.jeanponomarev.phonebookspringboot.entity.Contact;

@Service
public class ContactDtoToContactEntityConverter extends AbstractConverter<ContactDto, Contact> {

    @Override
    public Contact convert(ContactDto source) {
        Contact contact = new Contact();

        contact.setId(source.getId());
        contact.setFirstName(source.getFirstName());
        contact.setLastName(source.getLastName());
        contact.setPhoneNumber(source.getPhoneNumber());

        return contact;
    }
}
