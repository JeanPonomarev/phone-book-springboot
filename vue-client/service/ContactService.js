import axios from "axios";

export default class ContactService {

    url = "http://localhost:8080/api";

    getAllContacts() {
        return axios.get(this.url + "/getAllContacts");
    }

    createContact(contact) {
        return axios.post(this.url + "/createContact", contact);
    }

    updateContact(contact) {
        return axios.put(this.url + "/updateContact", contact);
    }

    deleteContact(id) {
        return axios.delete(this.url + "/deleteContact?id=" + id);
    }

}