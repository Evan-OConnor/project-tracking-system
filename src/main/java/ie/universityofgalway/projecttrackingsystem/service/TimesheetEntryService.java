package ie.universityofgalway.projecttrackingsystem.service;

import ie.universityofgalway.projecttrackingsystem.domain.core.Employee;
import ie.universityofgalway.projecttrackingsystem.domain.core.Project;
import ie.universityofgalway.projecttrackingsystem.domain.core.TimesheetEntry;
import ie.universityofgalway.projecttrackingsystem.domain.lookup.WorkDescription;
import ie.universityofgalway.projecttrackingsystem.dto.TimesheetEntryForm;
import ie.universityofgalway.projecttrackingsystem.repository.core.EmployeeRepository;
import ie.universityofgalway.projecttrackingsystem.repository.core.ProjectRepository;
import ie.universityofgalway.projecttrackingsystem.repository.core.TimesheetEntryRepository;
import ie.universityofgalway.projecttrackingsystem.repository.lookup.WorkDescriptionRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TimesheetEntryService {

    private final TimesheetEntryRepository repository;
    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;
    private final WorkDescriptionRepository workDescriptionRepository;

    public TimesheetEntryService(
            TimesheetEntryRepository repository,
            ProjectRepository projectRepository,
            EmployeeRepository employeeRepository,
            WorkDescriptionRepository workDescriptionRepository) {

        this.repository = repository;
        this.projectRepository = projectRepository;
        this.employeeRepository = employeeRepository;
        this.workDescriptionRepository = workDescriptionRepository;
    }

    // LIST all entries
    public List<TimesheetEntry> list() {
        return repository.findAll();
    }

    // CREATE new entry
    public void create(TimesheetEntryForm form) {

        Project project = projectRepository.findById(form.getProjectId()).orElseThrow();
        Employee employee = employeeRepository.findById(form.getEmployeeId()).orElseThrow();
        WorkDescription description = workDescriptionRepository.findById(form.getWorkDescriptionId()).orElseThrow();

        TimesheetEntry entry = new TimesheetEntry(
                project,
                employee,
                description,
                form.getEntryDate(),
                form.getHours()
        );

        repository.save(entry);
    }

    // LOAD lookups for form dropdowns
    public Map<String, Object> getFormLookups() {
        Map<String, Object> lookups = new HashMap<>();
        lookups.put("projects", projectRepository.findAll());
        lookups.put("employees", employeeRepository.findAll());
        lookups.put("descriptions", workDescriptionRepository.findAll());
        return lookups;
    }
}
