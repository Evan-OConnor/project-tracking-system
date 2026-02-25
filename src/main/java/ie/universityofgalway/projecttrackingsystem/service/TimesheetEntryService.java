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

    @Override
    public List<TimesheetEntryView> list() {
        return repository.findAll().stream()
                .map(this::toView)
                .collect(Collectors.toList());
    }

    @Override
    public TimesheetEntryView getById(Long id) {
        TimesheetEntry entry = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Timesheet entry not found: " + id));
        return toView(entry);
    }

    @Override
    public TimesheetEntryForm getFormById(Long id) {
        TimesheetEntry entry = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Timesheet entry not found: " + id));
        return toForm(entry);
    }

    @Override
    public TimesheetEntryView create(TimesheetEntryForm form) {
        Project project = projectRepo.findById(form.getProjectId())
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + form.getProjectId()));
        Employee employee = employeeRepo.findById(form.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + form.getEmployeeId()));
        WorkDescription workDesc = workDescRepo.findById(form.getWorkDescriptionId())
                .orElseThrow(() -> new IllegalArgumentException("WorkDescription not found: " + form.getWorkDescriptionId()));

        TimesheetEntry entry = new TimesheetEntry(
                project, employee, workDesc, form.getEntryDate(), form.getHours()
        );
        repository.save(entry);
        return toView(entry);
    }

    @Override
    public TimesheetEntryView update(Long id, TimesheetEntryForm form) {
        TimesheetEntry entry = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Timesheet entry not found: " + id));

        Project project = projectRepo.findById(form.getProjectId())
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + form.getProjectId()));
        Employee employee = employeeRepo.findById(form.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + form.getEmployeeId()));
        WorkDescription workDesc = workDescRepo.findById(form.getWorkDescriptionId())
                .orElseThrow(() -> new IllegalArgumentException("WorkDescription not found: " + form.getWorkDescriptionId()));

        entry.setProject(project);
        entry.setEmployee(employee);
        entry.setWorkDescription(workDesc);
        entry.setEntryDate(form.getEntryDate());
        entry.setHours(form.getHours());

        repository.save(entry);
        return toView(entry);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    private TimesheetEntryView toView(TimesheetEntry entry) {
        return new TimesheetEntryView(
                entry.getId(),
                entry.getProject().getTitle(),
                entry.getEmployee().getName(),
                entry.getWorkDescription().getName(), // <-- fixed here
                entry.getEntryDate(),
                entry.getHours()
        );
    }

    private TimesheetEntryForm toForm(TimesheetEntry entry) {
        TimesheetEntryForm form = new TimesheetEntryForm();
        form.setProjectId(entry.getProject().getId());
        form.setEmployeeId(entry.getEmployee().getId());
        form.setWorkDescriptionId(entry.getWorkDescription().getId());
        form.setEntryDate(entry.getEntryDate());
        form.setHours(entry.getHours());
        return form;
    }

    public Map<String, Object> getFormLookups() {
        return Map.of(
                "projects", projectRepo.findAll(),
                "employees", employeeRepo.findAll(),
                "workDescriptions", workDescRepo.findAll()
        );
    }
}
