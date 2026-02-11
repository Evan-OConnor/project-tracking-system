package ie.universityofgalway.projecttrackingsystem.controller;

import ie.universityofgalway.projecttrackingsystem.domain.core.Contact;
import ie.universityofgalway.projecttrackingsystem.dto.ContactForm;
import ie.universityofgalway.projecttrackingsystem.repository.core.ContactRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/contacts")
public class ContactController {

    private final ContactRepository contactRepository;

    public ContactController(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    // LIST
    @GetMapping
    public String list(Model model) {
        model.addAttribute("contacts", contactRepository.findAll());
        return "contacts/list";
    }

    // NEW FORM
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("contactForm", new ContactForm());
        model.addAttribute("mode", "create");
        return "contacts/form";
    }

    // CREATE
    @PostMapping
    public String create(@ModelAttribute ContactForm form) {
        Contact c = new Contact(form.getName());

        c.setAddress(form.getAddress());
        c.setPhone(form.getPhone());
        c.setFax(form.getFax());
        c.setComments(form.getComments());

        Contact saved = contactRepository.save(c);
        return "redirect:/contacts/" + saved.getId();
    }

    // VIEW
    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        Contact c = contactRepository.findById(id).orElseThrow();
        model.addAttribute("contact", c);
        return "contacts/view";
    }

    // EDIT FORM
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Contact c = contactRepository.findById(id).orElseThrow();

        ContactForm form = new ContactForm();
        form.setId(c.getId());
        form.setName(c.getName());
        form.setAddress(c.getAddress());
        form.setPhone(c.getPhone());
        form.setFax(c.getFax());
        form.setComments(c.getComments());

        model.addAttribute("contactForm", form);
        model.addAttribute("contactId", id);
        model.addAttribute("mode", "edit");
        return "contacts/form";
    }

    // UPDATE
    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute ContactForm form) {
        Contact c = contactRepository.findById(id).orElseThrow();

        c.setName(form.getName());
        c.setAddress(form.getAddress());
        c.setPhone(form.getPhone());
        c.setFax(form.getFax());
        c.setComments(form.getComments());

        contactRepository.save(c);
        return "redirect:/contacts/" + id;
    }

    // DELETE
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        contactRepository.deleteById(id);
        return "redirect:/contacts";
    }
}
