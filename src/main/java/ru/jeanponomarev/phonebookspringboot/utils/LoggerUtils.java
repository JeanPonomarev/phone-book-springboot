package ru.jeanponomarev.phonebookspringboot.utils;

import org.slf4j.Logger;
import ru.jeanponomarev.phonebookspringboot.entity.Contact;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class LoggerUtils {
    public static void logBasicUriInfo(Logger logger, HttpServletRequest request) {
        String uriWithQueryString;

        if (request.getQueryString() != null) {
            uriWithQueryString = request.getRequestURI() + "?" + request.getQueryString();
        } else {
            uriWithQueryString = request.getRequestURI();
        }

        logger.debug(String.format("Called %s %s from remote address: %s",
                request.getMethod(), uriWithQueryString, request.getRemoteAddr()));
    }

    public static void logGetAllContacts(Logger logger, List<Contact> contacts) {
        logger.debug(String.format("%d contacts retrieved from database", contacts.size()));
    }

    public static void logGetContactByProperty(Logger logger, String property, List<Contact> contactsWithTargetProperty) {
        logger.debug(String.format("Retrieved contacts with property \"%s\": %s", property, contactsWithTargetProperty));
    }

    public static void logCreateContact(Logger logger, Contact contact) {
        logger.debug(String.format("Contact was created: %s", contact));
    }

    public static void logUpdateContact(Logger logger, String oldContactAsString, Contact updatedContact) {
        logger.debug("Contact was updated:");
        logger.debug(String.format("From: %s", oldContactAsString));
        logger.debug(String.format("To: %s", updatedContact));
    }

    public static void logDeleteContactById(Logger logger, Contact contact) {
        if (contact == null) {
            logger.debug("Target contact doesn't exist in database");
        } else {
            logger.debug(String.format("Contact was deleted: %s", contact));
        }
    }
}
