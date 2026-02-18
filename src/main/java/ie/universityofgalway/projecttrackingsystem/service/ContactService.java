package Service;

import ie.universityofgalway.projecttrackingsystem.domain.core.Contact;
import ie.universityofgalway.projecttrackingsystem.dto.ContactForm;
import ie.universityofgalway.projecttrackingsystem.repository.core.ContactRepository;
import org.springframework.stereotype.Service;

@Service
public class ContactService extends BaseService <Contact, Long> {

    private final ContactRepository contactRepository;

    public ContactService(ContactRepository contactRepository) {
        super(contactRepository); // Pass to BaseService
        this.contactRepository = contactRepository;
    }

    public Contact create(ContactForm form) {
        Contact c = new Contact(form.getName());
        c.setAddress(form.getAddress());
        c.setPhone(form.getPhone());
        c.setFax(form.getFax());
        c.setComments(form.getComments());
        return contactRepository.save(c);
    }

    public ContactForm getFormById(Long id) {
        Contact c = get(id); // use BaseService.get
        ContactForm form = new ContactForm();
        form.setId(c.getId());
        form.setName(c.getName());
        form.setAddress(c.getAddress());
        form.setPhone(c.getPhone());
        form.setFax(c.getFax());
        form.setComments(c.getComments());
        return form;
    }

    public void update(Long id, ContactForm form) {
        Contact c = get(id); // use BaseService.get
        c.setName(form.getName());
        c.setAddress(form.getAddress());
        c.setPhone(form.getPhone());
        c.setFax(form.getFax());
        c.setComments(form.getComments());
        contactRepository.save(c);
    }
}
