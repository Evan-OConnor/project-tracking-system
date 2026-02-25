package ie.universityofgalway.projecttrackingsystem.controller;

import ie.universityofgalway.projecttrackingsystem.domain.core.Contact;
import ie.universityofgalway.projecttrackingsystem.dto.ContactForm;
import ie.universityofgalway.projecttrackingsystem.service.ContactService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/contacts")
public class ContactController extends BaseController<Contact, ContactForm> {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        super(contactService);
        this.contactService = contactService;
    }


    // BaseController

    @Override
    protected String getListView() {
        return "contacts/list";
    }

    @Override
    protected String getDetailsView() {
        return "contacts/view";
    }

    @Override
    protected String getBaseUrl() {
        return "/contacts";
    }

    @Override
    protected String getListAttributeName() {
        return "contacts";
    }

    @Override
    protected String getEntityAttributeName() {
        return "contact";
    }


    // Create / Edit with Validation

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("contactForm", new ContactForm());
        model.addAttribute("mode", "create");
        return "contacts/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("contactForm") ContactForm form,
                         BindingResult bindingResult,
                         Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("mode", "create");
            return "contacts/form";
        }

        Contact saved = contactService.create(form);
        return "redirect:/contacts/" + saved.getId();
    }

    @GetMapping("/{id:\\d+}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        ContactForm form = contactService.getFormById(id);
        model.addAttribute("contactForm", form);
        model.addAttribute("mode", "edit");
        return "contacts/form";
    }

    @PostMapping("/{id:\\d+}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("contactForm") ContactForm form,
                         BindingResult bindingResult,
                         Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("mode", "edit");
            return "contacts/form";
        }

        contactService.update(id, form);
        return "redirect:/contacts/" + id;
    }
}