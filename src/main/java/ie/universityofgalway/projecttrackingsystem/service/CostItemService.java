package ie.universityofgalway.projecttrackingsystem.service;

import ie.universityofgalway.projecttrackingsystem.domain.core.*;
import ie.universityofgalway.projecttrackingsystem.dto.CostItemForm;
import ie.universityofgalway.projecttrackingsystem.dto.CostItemView;
import ie.universityofgalway.projecttrackingsystem.repository.core.*;
import ie.universityofgalway.projecttrackingsystem.service.security.CurrentUserService;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CostItemService implements BaseService<CostItem, CostItemForm> {

    private final CostItemRepository costItemRepository;
    private final ProjectRepository projectRepository;
    private final ContactRepository contactRepository;
    private final CurrentUserService currentUserService;

    public CostItemService(CostItemRepository costItemRepository,
                           ProjectRepository projectRepository,
                           ContactRepository contactRepository,
                           CurrentUserService currentUserService) {

        this.costItemRepository = costItemRepository;
        this.projectRepository = projectRepository;
        this.contactRepository = contactRepository;
        this.currentUserService = currentUserService;
    }

    // List
    @Override
    @Transactional(readOnly = true)
    public List<CostItem> list() {
        return costItemRepository.findAll();
    }

    // Get by id
    @Override
    @Transactional(readOnly = true)
    public CostItem getById(Long id) {
        return costItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cost item not found"));
    }

    // Create
    @Override
    public CostItem create(CostItemForm form) {
        return saveInternal(form, null);
    }


    // Update
    @Override
    public CostItem update(Long id, CostItemForm form) {

        CostItem item = getById(id);

        if (!item.isUnbilled()) {
            throw new IllegalStateException("Billed cost items cannot be edited.");
        }

        return saveInternal(form, id);
    }

    // Delete
    @Override
    public void delete(Long id) {

        CostItem item = costItemRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Cost item not found"));

        if (!item.isUnbilled()) {
            throw new IllegalStateException("Billed cost items cannot be deleted.");
        }

        costItemRepository.delete(item);
    }

    // Get form by id
    @Override
    @Transactional(readOnly = true)
    public CostItemForm getFormById(Long id) {
        CostItem costItem = getById(id);
        return mapToForm(costItem);
    }

    // Update Entity
    @Override
    public void updateEntity(CostItem entity, CostItemForm form) {

        Project project = projectRepository
                .findById(form.getProjectId())
                .orElseThrow(() -> new IllegalStateException("Project not found"));

        Employee employee = currentUserService.getCurrentEmployee();

        CostItem.Type type = form.getType();

        Contact supplierContact = null;

        if (type == CostItem.Type.OUTLAY) {

            if (form.getSupplierContactId() == null) {
                throw new IllegalStateException("Outlays must be linked to a supplier.");
            }

            supplierContact = contactRepository
                    .findById(form.getSupplierContactId())
                    .orElseThrow();
        }

        entity.setProject(project);
        entity.setEmployee(employee);
        entity.setSupplierContact(supplierContact);
        entity.setCostDate(form.getCostDate());
        entity.setDescription(form.getDescription());
        entity.setCostAmount(form.getCostAmount());
        entity.setType(type);
    }

    // Map to form
    @Override
    public CostItemForm mapToForm(CostItem c) {

        CostItemForm form = new CostItemForm();

        form.setId(c.getId());
        form.setProjectId(c.getProject().getId());
        form.setProjectName(c.getProject().getTitle());
        form.setEmployeeId(c.getEmployee().getId());

        if (c.getSupplierContact() != null) {
            form.setSupplierContactId(c.getSupplierContact().getId());
        }

        form.setCostDate(c.getCostDate());
        form.setDescription(c.getDescription());
        form.setCostAmount(c.getCostAmount());
        form.setType(c.getType());

        return form;
    }

    // Read Only List
    @Transactional(readOnly = true)
    public List<CostItemView> listViews() {
        return costItemRepository.findAllWithDetails()
                .stream()
                .map(this::toView)
                .toList();
    }

    // Read only view
    @Transactional(readOnly = true)
    public CostItemView getViewById(Long id) {
        CostItem item = costItemRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Cost item not found"));

        return toView(item);
    }

    // Internal Save
    private CostItem saveInternal(CostItemForm form, Long id) {

        CostItem costItem;

        if (id != null) {
            // Update existing
            costItem = getById(id);
        } else {
            // Create new (empty entity)
            costItem = new CostItem();
        }

        //  One method handles all mapping and business logic
        updateEntity(costItem, form);

        return costItemRepository.save(costItem);
    }

    private CostItemView toView(CostItem item) {
        return new CostItemView(
                item.getId(),
                item.getProject().getTitle(),
                item.getEmployee().getName(),
                item.getSupplierContact() != null ? item.getSupplierContact().getName() : null,
                item.getCostDate(),
                item.getDescription(),
                item.getCostAmount(),
                item.getType(),
                item.getInvoice() != null
        );
    }

    // Form Dropdowns
    public Map<String, Object> getDropdowns() {

        Map<String, Object> data = new HashMap<>();

        data.put("projects", projectRepository.findAll());
        data.put("contacts", contactRepository.findAll());
        data.put("types", CostItem.Type.values());

        return data;
    }
}