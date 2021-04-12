package ru.jeanponomarev.phonebookspringboot.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.jeanponomarev.phonebookspringboot.email.EmailSender;
import ru.jeanponomarev.phonebookspringboot.entity.Contact;
import ru.jeanponomarev.phonebookspringboot.service.ContactService;
import ru.jeanponomarev.phonebookspringboot.utils.HtmlTableBuilder;

import java.util.List;

@Component
public class EmailScheduler {

    private final ContactService contactService;
    private final EmailSender emailSender;

    @Autowired
    public EmailScheduler(ContactService contactService, EmailSender emailSender) {
        this.contactService = contactService;
        this.emailSender = emailSender;
    }

    @Scheduled(fixedDelayString = "${emailDeliveryJob.delay}")
    public void sendEmailWithContactList() {
        String subject = "Contacts table state from phone-book-springboot application";

        List<Contact> currentContactList = contactService.getAll();
        String contactsHtmlTable = buildHtmlFromContactList(currentContactList);

        emailSender.sendEmail(subject, contactsHtmlTable);
    }

    private String buildHtmlFromContactList(List<Contact> contacts) {
        HtmlTableBuilder htmlBuilder = new HtmlTableBuilder(null, true, contacts.size(), 4);
        htmlBuilder.addTableHeader("Id", "First name", "Last name", "Phone number");

        contacts.forEach(contact -> {
            htmlBuilder.addRowValues(contact.getId().toString(), contact.getFirstName(), contact.getLastName(), contact.getPhoneNumber());
        });

        return htmlBuilder.build();
    }
}
