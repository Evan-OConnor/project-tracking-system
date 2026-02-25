package ie.universityofgalway.projecttrackingsystem.service;

import ie.universityofgalway.projecttrackingsystem.domain.core.*;
import ie.universityofgalway.projecttrackingsystem.dto.CostItemForm;
import ie.universityofgalway.projecttrackingsystem.repository.core.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CostItemService implements BaseService<CostItem, CostItemForm> {

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

    @Override
    public List<CostItem> list() {
        return costItemRepository.findAll();
    }

    @Override
    public CostItem getById(Long id) {
        return costItemRepository.findById(id).orElseThrow();
    }

    @Override
    public CostItem create(CostItemForm form) {
        return saveInternal(form, null);
    }

    @Override
    public CostItem update(Long id, CostItemForm form) {
        return saveInternal(form, id);
    }

    @Override
    public void delete(Long id) {
        costItemRepository.deleteById(id);
    }

    @Override
    public CostItemForm getFormById(Long id) {

        CostItem c = getById(id);
        CostItemForm form = new CostItemForm();

        form.setId(c.getId());
        form.setProjectId(c.getProject().getId());
        form.setEmployeeId(c.getEmployee().getId());

        if (c.getSupplierContact() != null) {
            form.setSupplierContactId(c.getSupplierContact().getId());
        }

        form.setCostDate(c.getCostDate());
        form.setDescription(c.getDescription());
        form.setCostAmount(c.getCostAmount());
        form.setType(c.getType().name());

        return form;
    }

    private CostItem saveInternal(CostItemForm form, Long id) {

        Project project = projectRepository.findById(form.getProjectId())
                .orElseThrow();

        Employee employee = employeeRepository.findById(form.getEmployeeId())
                .orElseThrow();

        CostItem.Type type = CostItem.Type.valueOf(form.getType());

        Contact supplierContact = null;

        if (type == CostItem.Type.OUTLAY) {

            if (form.getSupplierContactId() == null) {
                throw new IllegalStateException(
                        "Outlays must be linked to a supplier."
                );
            }

            supplierContact = contactRepository
                    .findById(form.getSupplierContactId())
                    .orElseThrow();

        }

        if (type == CostItem.Type.EXPENSE) {

            // Supplier must NOT exist
            supplierContact = null;
        }

        CostItem costItem;

        if (id != null) {
            costItem = getById(id);
        } else {
            costItem = new CostItem(
                    project,
                    employee,
                    supplierContact,
                    form.getCostDate(),
                    form.getDescription(),
                    form.getCostAmount(),
                    type
            );
        }

        // Always update fields (safe for both create & update)
        costItem.setProject(project);
        costItem.setEmployee(employee);
        costItem.setSupplierContact(supplierContact);
        costItem.setCostDate(form.getCostDate());
        costItem.setDescription(form.getDescription());
        costItem.setCostAmount(form.getCostAmount());
        costItem.setType(type);

        return costItemRepository.save(costItem);
    }

    public Map<String, Object> getDropdowns() {

        Map<String, Object> data = new HashMap<>();

        data.put("projects", projectRepository.findAll());
        data.put("employees", employeeRepository.findAll());
        data.put("contacts", contactRepository.findAll());
        data.put("types", CostItem.Type.values());

        return data;
    }
}