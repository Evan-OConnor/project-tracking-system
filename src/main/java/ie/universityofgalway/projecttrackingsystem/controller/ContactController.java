package ie.universityofgalway.projecttrackingsystem.controller;

import ie.universityofgalway.projecttrackingsystem.domain.core.Contact;
import ie.universityofgalway.projecttrackingsystem.dto.ContactForm;
import ie.universityofgalway.projecttrackingsystem.service.ContactService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/contacts")
public class ContactController extends BaseController<Contact, ContactForm> {

    private final ContactService contactService;

  // Constructor
    public ContactController(ContactService contactService) {
        super(contactService);
        this.contactService = contactService;
    }

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

    // Create Form
    @GetMapping("/new")
    public String newForm(@RequestParam(required = false) String returnUrl,
                          Model model) {

        model.addAttribute("contactForm", new ContactForm());
        model.addAttribute("mode", "create");
        model.addAttribute("returnUrl", returnUrl);

        return "contacts/form";
    }

    //  Submit
    @PostMapping
    public String create(@Valid @ModelAttribute("contactForm") ContactForm form,
                         BindingResult bindingResult,
                         @RequestParam(required = false) String returnUrl, RedirectAttributes ra,
                         Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("mode", "create");
            model.addAttribute("returnUrl", returnUrl);
            return "contacts/form";
        }
        Contact saved = contactService.create(form);

        ra.addFlashAttribute("clientId", saved.getId());
        ra.addFlashAttribute("clientName", saved.getName());

        if (returnUrl != null && !returnUrl.isBlank()) {
            return "redirect:" + returnUrl + "?clientId=" + saved.getId();
        }

        return "redirect:/contacts";
    }

    // Edit Form
    @GetMapping("/{id:\\d+}/edit")
    public String editForm(@PathVariable Long id,
                           @RequestParam(required = false) String returnUrl,
                           Model model) {

        ContactForm form = contactService.getFormById(id);

        model.addAttribute("contactForm", form);
        model.addAttribute("mode", "edit");
        model.addAttribute("returnUrl", returnUrl);

        return "contacts/form";
    }

    // Update Contact
    @PostMapping("/{id:\\d+}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("contactForm") ContactForm form,
                         BindingResult bindingResult,
                         @RequestParam(required = false) String returnUrl,
                         Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("mode", "edit");
            model.addAttribute("returnUrl", returnUrl);
            return "contacts/form";
        }

        contactService.update(id, form);

        if (returnUrl != null && !returnUrl.isBlank()) {
            return "redirect:" + returnUrl;
        }

        return "redirect:/contacts/" + id;
    }
}