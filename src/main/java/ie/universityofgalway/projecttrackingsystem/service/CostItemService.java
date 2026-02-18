package ie.universityofgalway.projecttrackingsystem.service;

import ie.universityofgalway.projecttrackingsystem.domain.core.CostItem;
import ie.universityofgalway.projecttrackingsystem.domain.core.Contact;
import ie.universityofgalway.projecttrackingsystem.domain.core.Employee;
import ie.universityofgalway.projecttrackingsystem.domain.core.Project;
import ie.universityofgalway.projecttrackingsystem.dto.CostItemForm;
import ie.universityofgalway.projecttrackingsystem.repository.core.ContactRepository;
import ie.universityofgalway.projecttrackingsystem.repository.core.CostItemRepository;
import ie.universityofgalway.projecttrackingsystem.repository.core.EmployeeRepository;
import ie.universityofgalway.projecttrackingsystem.repository.core.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CostItemService {

    private final CostItemRepository costItemRepository;
    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;
    private final ContactRepository contactRepository;

    public CostItemService(CostItemRepository costItemRepository,
                           ProjectRepository projectRepository,
                           EmployeeRepository employeeRepository,
                           ContactRepository contactRepository) {
        this.costItemRepository = costItemRepository;
        this.projectRepository = projectRepository;
        this.employeeRepository = employeeRepository;
        this.contactRepository = contactRepository;
    }

    // LIST
    public List<CostItem> list() {
        return costItemRepository.findAll();
    }

    // GET ONE
    public CostItem get(Long id) {
        return costItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid cost item ID"));
    }

    // DELETE
    public void delete(Long id) {
        costItemRepository.deleteById(id);
    }

    // Dropdown data
    public Map<String, Object> getDropdowns() {
        Map<String, Object> data = new HashMap<>();
        data.put("projects", projectRepository.findAll());
        data.put("employees", employeeRepository.findAll());
        data.put("contacts", contactRepository.findAll());
        data.put("types", CostItem.Type.values());
        return data;
    }

    // CREATE OR UPDATE FROM DTO
    public CostItem saveFromForm(CostItemForm form) {
        // Fetch mandatory entities
        Project project = projectRepository.findById(form.getProjectId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid project ID"));
        Employee employee = employeeRepository.findById(form.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid employee ID"));

        // Fetch optional supplier contact
        Contact contact = null;
        if (form.getSupplierContactId() != null) {
            contact = contactRepository.findById(form.getSupplierContactId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid contact ID"));
        }

        // Convert type string to enum
        CostItem.Type type;
        try {
            type = CostItem.Type.valueOf(form.getType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid cost item type");
        }

        CostItem costItem;

        if (form.getId() != null) {
            // UPDATE: fetch existing entity
            costItem = costItemRepository.findById(form.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid cost item ID"));
            costItem.setProject(project);
            costItem.setEmployee(employee);
            costItem.setSupplierContact(contact);
            costItem.setCostDate(form.getCostDate());
            costItem.setDescription(form.getDescription());
            costItem.setCostAmount(form.getCostAmount());
            costItem.setType(type);
        } else {
            // CREATE: use full constructor
            costItem = new CostItem(
                    project,
                    employee,
                    form.getCostDate(),
                    form.getDescription(),
                    form.getCostAmount(),
                    type
            );
            costItem.setSupplierContact(contact);
        }

        // Save and return
        return costItemRepository.save(costItem);
    }
}
