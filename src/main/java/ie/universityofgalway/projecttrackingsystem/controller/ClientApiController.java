package ie.universityofgalway.projecttrackingsystem.controller;
import ie.universityofgalway.projecttrackingsystem.dto.ClientDto;
import ie.universityofgalway.projecttrackingsystem.repository.core.ContactRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
public class ClientApiController {

    private final ContactRepository contactRepository;

    public ClientApiController(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @GetMapping("/search")
    public List<ClientDto> search(@RequestParam String query) {
        return contactRepository.findByNameContainingIgnoreCase(query)
                .stream()
                .map(c -> new ClientDto(c.getId(), c.getName()))
                .toList();
    }
}