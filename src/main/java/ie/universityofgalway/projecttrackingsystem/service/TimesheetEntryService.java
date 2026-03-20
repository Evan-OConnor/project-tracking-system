package ie.universityofgalway.projecttrackingsystem.service;

import ie.universityofgalway.projecttrackingsystem.domain.core.Employee;
import ie.universityofgalway.projecttrackingsystem.domain.core.Project;
import ie.universityofgalway.projecttrackingsystem.domain.core.TimesheetEntry;
import ie.universityofgalway.projecttrackingsystem.domain.lookup.WorkDescription;
import ie.universityofgalway.projecttrackingsystem.dto.TimesheetEntryForm;
import ie.universityofgalway.projecttrackingsystem.dto.TimesheetEntryView;
import ie.universityofgalway.projecttrackingsystem.repository.core.TimesheetEntryRepository;
import ie.universityofgalway.projecttrackingsystem.repository.core.EmployeeRepository;
import ie.universityofgalway.projecttrackingsystem.repository.core.ProjectRepository;
import ie.universityofgalway.projecttrackingsystem.repository.lookup.WorkDescriptionRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TimesheetEntryService implements BaseService<TimesheetEntryView, TimesheetEntryForm> {

    private final TimesheetEntryRepository repository;
    private final ProjectRepository projectRepo;
    private final EmployeeRepository employeeRepo;
    private final WorkDescriptionRepository workDescRepo;

    public TimesheetEntryService(TimesheetEntryRepository repository,
                                 ProjectRepository projectRepo,
                                 EmployeeRepository employeeRepo,
                                 WorkDescriptionRepository workDescRepo) {

        this.repository = repository;
        this.projectRepo = projectRepo;
        this.employeeRepo = employeeRepo;
        this.workDescRepo = workDescRepo;
    }

    // List
    @Override
    public List<TimesheetEntryView> list() {
        return repository.findAll()
                .stream()
                .map(this::toView)
                .collect(Collectors.toList());
    }

    // Get by id
    @Override
    public TimesheetEntryView getById(Long id) {

        TimesheetEntry entry = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Timesheet entry not found: " + id));

        return toView(entry);
    }

    // Get form
    @Override
    public TimesheetEntryForm getFormById(Long id) {

        TimesheetEntry entry = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Timesheet entry not found: " + id));

        return toForm(entry);
    }

    // Create
    @Override
    public TimesheetEntryView create(TimesheetEntryForm form) {

        Project project = projectRepo.findById(form.getProjectId())
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        Employee employee = employeeRepo.findById(form.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        WorkDescription workDesc = resolveWorkDescription(form);

        TimesheetEntry entry = new TimesheetEntry(
                project,
                employee,
                workDesc,
                form.getEntryDate(),
                form.getHours()
        );

        repository.save(entry);

        return toView(entry);
    }

    // Update
    @Override
    public TimesheetEntryView update(Long id, TimesheetEntryForm form) {

        TimesheetEntry entry = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Timesheet entry not found"));

        Project project = projectRepo.findById(form.getProjectId()).orElseThrow();
        Employee employee = employeeRepo.findById(form.getEmployeeId()).orElseThrow();
        WorkDescription workDesc = resolveWorkDescription(form);

        entry.setProject(project);
        entry.setEmployee(employee);
        entry.setWorkDescription(workDesc);
        entry.setEntryDate(form.getEntryDate());
        entry.setHours(form.getHours());

        repository.save(entry);

        return toView(entry);
    }

    // Delete
    @Override
    public void delete(Long id) {

        TimesheetEntry entry = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Timesheet entry not found"));

        if (entry.getInvoice() != null) {
            throw new IllegalStateException("Cannot delete a billed timesheet entry.");
        }

        repository.delete(entry);
    }

    // Resolve "Other" Work Description
    private WorkDescription resolveWorkDescription(TimesheetEntryForm form) {

        WorkDescription selected = workDescRepo.findById(form.getWorkDescriptionId())
                .orElseThrow(() -> new IllegalArgumentException("Work description not found"));

        if ("Other".equalsIgnoreCase(selected.getName())
                && form.getOtherDescription() != null
                && !form.getOtherDescription().isBlank()) {

            return workDescRepo.findByName(form.getOtherDescription())
                    .orElseGet(() -> workDescRepo.save(new WorkDescription(form.getOtherDescription())));
        }

        return selected;
    }

    @Override
    public void updateEntity(TimesheetEntryView entity, TimesheetEntryForm form) {

    }

    @Override
    public TimesheetEntryForm mapToForm(TimesheetEntryView entity) {
        return new TimesheetEntryForm();
    }

    // Entity to view
    private TimesheetEntryView toView(TimesheetEntry entry) {

        BigDecimal charge = entry.getNetAmount()
                .setScale(2, RoundingMode.HALF_UP);

        return new TimesheetEntryView(
                entry.getId(),
                entry.getProject().getTitle(),
                entry.getEmployee().getName(),
                entry.getWorkDescription().getName(),
                entry.getEntryDate(),
                entry.getHours(),
                charge,
                entry.getInvoice() != null
        );
    }

    // Entity to form
    private TimesheetEntryForm toForm(TimesheetEntry entry) {

        TimesheetEntryForm form = new TimesheetEntryForm();

        form.setId(entry.getId());
        form.setProjectId(entry.getProject().getId());
        form.setEmployeeId(entry.getEmployee().getId());
        form.setWorkDescriptionId(entry.getWorkDescription().getId());
        form.setEntryDate(entry.getEntryDate());
        form.setHours(entry.getHours());

        return form;
    }

    // Project filter
    public List<TimesheetEntryView> findByProjectId(Long projectId) {

        return repository.findByProject_Id(projectId)
                .stream()
                .map(this::toView)
                .toList();
    }

    // Form lookups
    public Map<String, Object> getFormLookups() {

        return Map.of(
                "projects", projectRepo.findAll(),
                "employees", employeeRepo.findAll(),
                "workDescriptions", workDescRepo.findAll()
        );
    }
}