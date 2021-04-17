<template>
    <div>
        <Toast/>

        <DataTable ref="dt" :value="contacts" :selection.sync="selectedContacts" dataKey="id"
                   :paginator="true" :rows="10" :filters="filters"
                   paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink CurrentPageReport RowsPerPageDropdown"
                   :rowsPerPageOptions="[10,25,50]"
                   currentPageReportTemplate="Showing {first} to {last} of {totalRecords} contacts">
            <template #header>
                <div class="table-header p-d-flex p-jc-between">
                    <div>
                        <Button label="New" icon="pi pi-plus" class="p-button-success p-mr-2" @click="openNew"/>
                        <Button label="Delete" icon="pi pi-trash" class="p-button-danger p-mr-2"
                                @click="confirmDeleteSelected"
                                :disabled="!selectedContacts || !selectedContacts.length"/>
                        <Button label="Refresh" icon="pi pi-refresh" class="p-badge-info"
                                @click="getAllContacts"></Button>
                    </div>
                    <span class="p-input-icon-left">
                        <i class="pi pi-search"/>
                        <InputText v-model="filters['global']" placeholder="Search..."/>
                    </span>
                </div>
            </template>

            <Column selectionMode="multiple" headerStyle="width: 3rem"></Column>
            <Column field="id" header="ID" sortable></Column>
            <Column field="firstName" header="First Name" sortable></Column>
            <Column field="lastName" header="Last Name" sortable></Column>
            <Column field="phoneNumber" header="Phone Number" sortable></Column>
            <Column>
                <template #body="slotProps">
                    <Button icon="pi pi-pencil" class="p-button-rounded p-button-success p-mr-2"
                            @click="editContact(slotProps.data)"/>
                    <Button icon="pi pi-trash" class="p-button-rounded p-button-warning"
                            @click="confirmDeleteContact(slotProps.data)"/>
                </template>
            </Column>
        </DataTable>

        <Dialog :visible.sync="contactDialog" :style="{width: '450px'}" header="Contact Details" :modal="true" class="p-fluid">
            <div class="p-field">
                <label for="firstName">First Name</label>
                <InputText id="firstName" v-model.trim="contact.firstName" required="true" autofocus
                           :class="{'p-invalid': submitted && !contact.firstName}"/>
                <small class="p-invalid" v-if="submitted && !contact.firstName">First name is required.</small>
            </div>

            <div class="p-field">
                <label for="lastName">Last Name</label>
                <InputText id="lastName" v-model.trim="contact.lastName" required="true" autofocus :class="{'p-invalid': submitted && !contact.lastName}"/>
                <small class="p-invalid" v-if="submitted && !contact.lastName">Last name is required.</small>
            </div>

            <div class="p-field">
                <label for="phoneNumber">Phone Number</label>
                <InputText id="phoneNumber" v-model.trim="contact.phoneNumber" required="true" autofocus :class="{'p-invalid': submitted && !contact.phoneNumber}"/>
                <small class="p-invalid" v-if="submitted && !contact.phoneNumber">Phone number is required.</small>
            </div>

            <Panel class="p-error" header="Error from server" v-if="errorFromServer">
                <div class="p-error">{{ errorFromServer }}</div>
            </Panel>

            <template #footer>
                <Button label="Save" icon="pi pi-check" class="p-button-text" @click="saveContact"/>
                <Button label="Cancel" icon="pi pi-times" class="p-button-text" @click="hideDialog"/>
            </template>
        </Dialog>

        <Dialog :visible.sync="deleteContactDialog" :style="{width: '450px'}" header="Confirm" :modal="true">
            <div class="confirmation-content">
                <i class="pi pi-exclamation-triangle p-mr-3" style="font-size: 2rem"/>
                <span v-if="contact">Are you sure you want to delete this contact?</span>
            </div>

            <Panel class="p-error p-mt-4" header="Error from server" v-if="errorFromServer">
                <div class="p-error">{{ errorFromServer }}</div>
            </Panel>

            <template #footer>
                <Button label="Yes" icon="pi pi-check" class="p-button-text" @click="deleteContact"/>
                <Button label="No" icon="pi pi-times" class="p-button-text" @click="deleteContactDialog = false"/>
            </template>
        </Dialog>

        <Dialog :visible.sync="deleteContactsDialog" :style="{width: '450px'}" header="Confirm" :modal="true">
            <div class="confirmation-content">
                <i class="pi pi-exclamation-triangle p-mr-3" style="font-size: 2rem"/>
                <span v-if="contact">Are you sure you want to delete the selected contacts?</span>
            </div>

            <Panel class="p-error p-mt-4" header="Error from server" v-if="errorFromServer">
                <div class="p-error">{{ errorFromServer }}</div>
            </Panel>

            <template #footer>
                <Button label="Yes" icon="pi pi-check" class="p-button-text" @click="deleteSelectedContacts"/>
                <Button label="No" icon="pi pi-times" class="p-button-text" @click="deleteContactsDialog = false"/>
            </template>
        </Dialog>
    </div>
</template>

<script>
    import ContactService from "../../service/ContactService";

    export default {
        name: "PhoneBook",

        data() {
            return {
                contacts: null,
                contactDialog: false,
                deleteContactDialog: false,
                deleteContactsDialog: false,
                contact: {
                    firstName: null,
                    lastName: null,
                    phoneNumber: null
                },
                selectedContacts: null,
                filters: {},
                submitted: false,
                errorFromServer: null,
                actionType: null
            }
        },

        contactService: null,

        created() {
            this.contactService = new ContactService();
        },

        mounted() {
            this.getAllContacts();
        },

        methods: {
            getAllContacts() {
                this.contactService.getAllContacts().then(data => this.contacts = data.data);
            },

            openNew() {
                this.contact = {};
                this.submitted = false;
                this.errorFromServer = null;
                this.contactDialog = true;
                this.actionType = "create";
            },

            hideDialog() {
                this.contactDialog = false;
                this.submitted = false;
            },

            createContact() {
                if (this.contact.firstName && this.contact.lastName && this.contact.phoneNumber) {
                    this.contactService.createContact(this.contact)
                        .then(response => {
                            const successServerMessage = response.data.message;
                            this.$toast.add({
                                severity: 'success',
                                summary: 'Successful',
                                detail: successServerMessage,
                                life: 3000
                            });

                            this.contactDialog = false;
                            this.contact = {};

                            this.getAllContacts();
                        })
                        .catch(error => {
                            if (error.response) {
                                this.errorFromServer = error.response.data.message;
                            } else if (error.request) {
                                this.errorFromServer = 'Server is not responding';
                            } else {
                                console.log('Error', error.message);
                            }
                        });
                }
            },

            updateContact() {
                if (this.contact.firstName && this.contact.lastName && this.contact.phoneNumber) {
                    this.contactService.updateContact(this.contact)
                        .then(response => {
                            const successServerMessage = response.data.message;
                            this.$toast.add({
                                severity: 'success',
                                summary: 'Successful',
                                detail: successServerMessage,
                                life: 3000
                            });

                            this.contactDialog = false;
                            this.contact = {};

                            this.getAllContacts();
                        })
                        .catch(error => {
                            if (error.response) {
                                this.errorFromServer = error.response.data.message;
                            } else if (error.request) {
                                this.errorFromServer = 'Server is not responding';
                            } else {
                                console.log('Error', error.message);
                            }
                        });
                }
            },

            saveContact() {
                this.submitted = true;

                if (this.actionType === "create") {
                    this.createContact();
                } else if (this.actionType === "update") {
                    this.updateContact();
                } else {
                    throw new Error("Unexpected actionType");
                }
            },

            editContact(contact) {
                this.contact = {...contact};
                this.errorFromServer = null;
                this.contactDialog = true;
                this.actionType = "update";
            },

            confirmDeleteContact(contact) {
                this.contact = contact;
                this.errorFromServer = null;
                this.deleteContactDialog = true;
            },

            deleteContact() {
                this.contactService.deleteContact(this.contact.id)
                    .then(response => {
                        this.deleteContactDialog = false;
                        this.contact = {};

                        const successServerMessage = response.data.message;

                        this.$toast.add({
                            severity: 'success',
                            summary: 'Successful',
                            detail: successServerMessage,
                            life: 3000
                        });

                        this.getAllContacts();
                    })
                    .catch(error => {
                        if (error.response) {
                            if (error.response.status === 404) {
                                this.deleteContactDialog = false;
                                this.contact = {};

                                const errorServerMessage = error.response.data.message;

                                this.$toast.add({
                                    severity: 'error',
                                    summary: 'Error message',
                                    detail: errorServerMessage,
                                    life: 3000
                                });
                            } else {
                                this.errorFromServer = error.response.data.message;
                            }
                        } else if (error.request) {
                            this.errorFromServer = 'Server is not responding';
                        } else {
                            console.log('Error', error.message);
                        }
                    });
            },

            confirmDeleteSelected() {
                this.errorFromServer = null;
                this.deleteContactsDialog = true;
            },

            deleteSelectedContacts() {
                const ids = this.selectedContacts.map(selectedContact => selectedContact.id);

                this.contactService.deleteContactList(ids)
                    .then(response => {
                        this.deleteContactsDialog = false;
                        this.selectedContacts = null;

                        const successServerMessage = response.data.message;

                        this.$toast.add({
                            severity: 'success',
                            summary: 'Successful',
                            detail: successServerMessage,
                            life: 3000
                        });

                        this.getAllContacts();
                    })
                    .catch(error => {
                        if (error.response) {
                            this.errorFromServer = error.response.data.message;
                        } else if (error.request) {
                            this.errorFromServer = 'Server is not responding';
                        } else {
                            console.log('Error', error.message);
                        }
                    });
            }
        }
    }
</script>