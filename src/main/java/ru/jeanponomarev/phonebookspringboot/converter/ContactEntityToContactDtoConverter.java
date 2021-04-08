package ru.jeanponomarev.phonebookspringboot.converter;

import org.springframework.stereotype.Service;
import ru.jeanponomarev.phonebookspringboot.dto.ContactDto;
import ru.jeanponomarev.phonebookspringboot.entity.Contact;

@Service
public class ContactEntityToContactDtoConverter extends AbstractConverter<Contact, ContactDto> {
    @Override
    public ContactDto convert(Contact source) {

        ContactDto contactDto = new ContactDto();

        contactDto.setId(source.getId());
        contactDto.setFirstName(source.getFirstName());
        contactDto.setLastName(source.getLastName());
        contactDto.setPhoneNumber(source.getPhoneNumber());

        return contactDto;
    }
}
