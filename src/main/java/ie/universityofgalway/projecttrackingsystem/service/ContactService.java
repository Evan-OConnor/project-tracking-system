package ie.universityofgalway.projecttrackingsystem.service;

import ie.universityofgalway.projecttrackingsystem.domain.core.Contact;
import ie.universityofgalway.projecttrackingsystem.dto.ContactForm;
import ie.universityofgalway.projecttrackingsystem.repository.core.ContactRepository;
import ie.universityofgalway.projecttrackingsystem.repository.core.ProjectRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactService implements BaseService<Contact, ContactForm> {

    private final ContactRepository contactRepository;
    private final ProjectRepository projectRepository;

    public ContactService(ContactRepository contactRepository,
                          ProjectRepository projectRepository) {
        this.contactRepository = contactRepository;
        this.projectRepository = projectRepository;
    }

    // LIST

    @Override
    public List<Contact> list() {
        return contactRepository.findAll();
    }

    // GET BY ID

    @Override
    public Contact getById(Long id) {
        return contactRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Contact not found with id: " + id));
    }

    // DELETE

    @Override
    public void delete(Long id) {

        Contact contact = getById(id);

        if (projectRepository.existsByClientContactId(id) ||
                projectRepository.existsBySolicitorContactId(id) ||
                projectRepository.existsByInsuranceCompanyContactId(id)) {

            throw new IllegalStateException(
                    "Cannot delete contact because it is used in a project."
            );
        }

        contactRepository.delete(contact);
    }

    // CREATE

    @Override
    public Contact create(ContactForm form) {

        Contact contact = new Contact(form.getName());

        updateEntity(contact, form);

        return contactRepository.save(contact);
    }

    // UPDATE

    @Override
    public Contact update(Long id, ContactForm form) {

        Contact contact = getById(id);

        updateEntity(contact, form);

        return contactRepository.save(contact);
    }

    // GET FORM BY ID

    @Override
    public ContactForm getFormById(Long id) {

        Contact contact = getById(id);

        return mapToForm(contact);
    }

    // REQUIRED BY BASESERVICE

    @Override
    public void updateEntity(Contact contact, ContactForm form) {
        contact.setName(form.getName());
        contact.setAddress(form.getAddress());
        contact.setPhone(form.getPhone());
        contact.setFax(form.getFax());
        contact.setComments(form.getComments());
    }

    @Override
    public ContactForm mapToForm(Contact contact) {

        ContactForm form = new ContactForm();

        form.setId(contact.getId());
        form.setName(contact.getName());
        form.setAddress(contact.getAddress());
        form.setPhone(contact.getPhone());
        form.setFax(contact.getFax());
        form.setComments(contact.getComments());

        return form;
    }
}