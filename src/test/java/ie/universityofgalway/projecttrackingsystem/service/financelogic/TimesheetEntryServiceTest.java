package ie.universityofgalway.projecttrackingsystem.service.financelogic;

import ie.universityofgalway.projecttrackingsystem.domain.core.Employee;
import ie.universityofgalway.projecttrackingsystem.domain.core.Project;
import ie.universityofgalway.projecttrackingsystem.domain.core.TimesheetEntry;
import ie.universityofgalway.projecttrackingsystem.domain.lookup.WorkDescription;
import ie.universityofgalway.projecttrackingsystem.dto.TimesheetEntryForm;
import ie.universityofgalway.projecttrackingsystem.dto.TimesheetEntryView;
import ie.universityofgalway.projecttrackingsystem.repository.core.EmployeeRepository;
import ie.universityofgalway.projecttrackingsystem.repository.core.ProjectRepository;
import ie.universityofgalway.projecttrackingsystem.repository.core.TimesheetEntryRepository;
import ie.universityofgalway.projecttrackingsystem.repository.lookup.WorkDescriptionRepository;
import ie.universityofgalway.projecttrackingsystem.service.TimesheetEntryService;
import ie.universityofgalway.projecttrackingsystem.service.security.CurrentUserService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimesheetEntryServiceTest {

    @Mock private TimesheetEntryRepository repository;
    @Mock private ProjectRepository projectRepo;
    @Mock private EmployeeRepository employeeRepo;
    @Mock private WorkDescriptionRepository workDescRepo;
    @Mock private CurrentUserService currentUserService;

    @InjectMocks
    private TimesheetEntryService service;

    // Create

    @Test
    void create_validEntry_savesAndReturnsView() {
        TimesheetEntryForm form = new TimesheetEntryForm();
        form.setProjectId(1L);
        form.setWorkDescriptionId(1L);
        form.setEntryDate(LocalDate.now());
        form.setHours(BigDecimal.valueOf(8));

        Project project = mock(Project.class);
        when(project.getTitle()).thenReturn("Project");

        Employee employee = mock(Employee.class);
        when(employee.getName()).thenReturn("User");

        WorkDescription workDesc = mock(WorkDescription.class);
        when(workDesc.getName()).thenReturn("Dev");

        when(projectRepo.findById(1L)).thenReturn(Optional.of(project));
        when(workDescRepo.findById(1L)).thenReturn(Optional.of(workDesc));
        when(currentUserService.getCurrentEmployee()).thenReturn(employee);

        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        TimesheetEntryView result = service.create(form);

        assertNotNull(result);
        verify(repository).save(any());
    }

    @Test
    void create_projectNotFound_throwsException() {
        TimesheetEntryForm form = new TimesheetEntryForm();
        form.setProjectId(1L);

        when(projectRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> service.create(form));
    }

    @Test
    void create_workDescriptionNotFound_throwsException() {
        TimesheetEntryForm form = new TimesheetEntryForm();
        form.setProjectId(1L);
        form.setWorkDescriptionId(1L);

        when(projectRepo.findById(1L)).thenReturn(Optional.of(mock(Project.class)));
        when(workDescRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.create(form));
    }

    @Test
    void create_otherWorkDescription_createsNew() {
        TimesheetEntryForm form = new TimesheetEntryForm();
        form.setProjectId(1L);
        form.setWorkDescriptionId(1L);
        form.setOtherDescription("Custom");

        WorkDescription other = mock(WorkDescription.class);
        when(other.getName()).thenReturn("Other");

        when(projectRepo.findById(1L)).thenReturn(Optional.of(mock(Project.class)));
        when(workDescRepo.findById(1L)).thenReturn(Optional.of(other));
        when(workDescRepo.findByName("Custom")).thenReturn(Optional.empty());
        when(workDescRepo.save(any())).thenAnswer(i -> i.getArgument(0));
        when(currentUserService.getCurrentEmployee()).thenReturn(mock(Employee.class));

        service.create(form);

        verify(workDescRepo).save(any());
    }

    // Update

    @Test
    void update_validEntry_updatesSuccessfully() {
        TimesheetEntry entry = mock(TimesheetEntry.class);

        // FULL mapping required for toView()
        Project project = mock(Project.class);
        when(project.getTitle()).thenReturn("Project");

        Employee employee = mock(Employee.class);
        when(employee.getName()).thenReturn("User");

        WorkDescription work = mock(WorkDescription.class);
        when(work.getName()).thenReturn("Dev");

        when(entry.isUnbilled()).thenReturn(true);
        when(entry.getProject()).thenReturn(project);
        when(entry.getEmployee()).thenReturn(employee);
        when(entry.getWorkDescription()).thenReturn(work);
        when(entry.getNetAmount()).thenReturn(BigDecimal.TEN);
        when(entry.getInvoice()).thenReturn(null);

        when(repository.findById(1L)).thenReturn(Optional.of(entry));
        when(projectRepo.findById(1L)).thenReturn(Optional.of(project));
        when(workDescRepo.findById(1L)).thenReturn(Optional.of(work));
        when(currentUserService.getCurrentEmployee()).thenReturn(employee);

        TimesheetEntryForm form = new TimesheetEntryForm();
        form.setProjectId(1L);
        form.setWorkDescriptionId(1L);
        form.setEntryDate(LocalDate.now());
        form.setHours(BigDecimal.valueOf(8));

        TimesheetEntryView result = service.update(1L, form);

        assertNotNull(result);
        verify(repository).save(entry);
    }

    @Test
    void update_entryNotFound_throwsException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> service.update(1L, new TimesheetEntryForm()));
    }

    @Test
    void update_billedEntry_throwsException() {
        TimesheetEntry entry = mock(TimesheetEntry.class);

        when(repository.findById(1L)).thenReturn(Optional.of(entry));
        when(entry.isUnbilled()).thenReturn(false);

        assertThrows(IllegalStateException.class,
                () -> service.update(1L, new TimesheetEntryForm()));
    }

    @Test
    void update_projectNotFound_throwsException() {
        TimesheetEntry entry = mock(TimesheetEntry.class);

        when(repository.findById(1L)).thenReturn(Optional.of(entry));
        when(entry.isUnbilled()).thenReturn(true);
        when(projectRepo.findById(1L)).thenReturn(Optional.empty());

        TimesheetEntryForm form = new TimesheetEntryForm();
        form.setProjectId(1L);

        assertThrows(IllegalArgumentException.class,
                () -> service.update(1L, form));
    }

    // Delete

    @Test
    void delete_unbilledEntry_deletesSuccessfully() {
        TimesheetEntry entry = mock(TimesheetEntry.class);

        when(repository.findById(1L)).thenReturn(Optional.of(entry));
        when(entry.isUnbilled()).thenReturn(true);

        service.delete(1L);

        verify(repository).delete(entry);
    }

    @Test
    void delete_billedEntry_throwsException() {
        TimesheetEntry entry = mock(TimesheetEntry.class);

        when(repository.findById(1L)).thenReturn(Optional.of(entry));
        when(entry.isUnbilled()).thenReturn(false);

        assertThrows(IllegalStateException.class,
                () -> service.delete(1L));
    }

    //  Get

    @Test
    void getById_valid_returnsView() {
        TimesheetEntry entry = fullyMockedEntry();

        when(repository.findById(1L)).thenReturn(Optional.of(entry));

        TimesheetEntryView result = service.getById(1L);

        assertNotNull(result);
    }

    @Test
    void getById_notFound_throwsException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> service.getById(1L));
    }

    //  List

    @Test
    void list_returnsViews() {
        TimesheetEntry entry = fullyMockedEntry();

        when(repository.findAll()).thenReturn(List.of(entry));

        List<TimesheetEntryView> result = service.list();

        assertEquals(1, result.size());
    }

    //  Helper

    private TimesheetEntry fullyMockedEntry() {
        TimesheetEntry entry = mock(TimesheetEntry.class);

        Project project = mock(Project.class);
        when(project.getTitle()).thenReturn("Project");

        Employee employee = mock(Employee.class);
        when(employee.getName()).thenReturn("User");

        WorkDescription work = mock(WorkDescription.class);
        when(work.getName()).thenReturn("Dev");

        when(entry.getProject()).thenReturn(project);
        when(entry.getEmployee()).thenReturn(employee);
        when(entry.getWorkDescription()).thenReturn(work);
        when(entry.getNetAmount()).thenReturn(BigDecimal.TEN);
        when(entry.getInvoice()).thenReturn(null);

        return entry;
    }
}