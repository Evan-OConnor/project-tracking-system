package ie.universityofgalway.projecttrackingsystem.service;

import ie.universityofgalway.projecttrackingsystem.domain.core.Contact;
import ie.universityofgalway.projecttrackingsystem.dto.ContactForm;
import ie.universityofgalway.projecttrackingsystem.repository.core.ContactRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactService implements BaseService<Contact, ContactForm> {

    private final ContactRepository contactRepository;

    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    // CRUD methods

    @Override
    public List<Contact> list() {
        return contactRepository.findAll();
    }

    @Override
    public Contact getById(Long id) {
        return contactRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Contact not found with id: " + id));
    }

    @Override
    public void delete(Long id) {
        Contact contact = getById(id);  // ensure it exists
        contactRepository.delete(contact);
    }


    // Create & Update

    @Override
    public Contact create(ContactForm form) {
        Contact contact = new Contact(form.getName());
        applyFormData(contact, form);
        return contactRepository.save(contact);
    }

    @Override
    public Contact update(Long id, ContactForm form) {
        Contact contact = getById(id);
        applyFormData(contact, form);
        return contactRepository.save(contact);
    }

    // Load form for editing

    @Override
    public ContactForm getFormById(Long id) {
        Contact contact = getById(id);

        ContactForm form = new ContactForm();
        form.setId(contact.getId());
        form.setName(contact.getName());
        form.setAddress(contact.getAddress());
        form.setPhone(contact.getPhone());
        form.setFax(contact.getFax());
        form.setComments(contact.getComments());

        return form;
    }

    // Helper method

    private void applyFormData(Contact contact, ContactForm form) {
        contact.setName(form.getName());
        contact.setAddress(form.getAddress());
        contact.setPhone(form.getPhone());
        contact.setFax(form.getFax());
        contact.setComments(form.getComments());
    }
}