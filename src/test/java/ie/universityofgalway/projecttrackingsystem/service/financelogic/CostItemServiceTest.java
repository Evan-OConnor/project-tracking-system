package ie.universityofgalway.projecttrackingsystem.service.financelogic;

import ie.universityofgalway.projecttrackingsystem.domain.core.CostItem;
import ie.universityofgalway.projecttrackingsystem.domain.core.Employee;
import ie.universityofgalway.projecttrackingsystem.domain.core.Project;
import ie.universityofgalway.projecttrackingsystem.dto.CostItemForm;
import ie.universityofgalway.projecttrackingsystem.repository.core.ContactRepository;
import ie.universityofgalway.projecttrackingsystem.repository.core.CostItemRepository;

import ie.universityofgalway.projecttrackingsystem.repository.core.EmployeeRepository;
import ie.universityofgalway.projecttrackingsystem.repository.core.ProjectRepository;
import ie.universityofgalway.projecttrackingsystem.service.CostItemService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CostItemServiceTest {

    @Mock
    private CostItemRepository repository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private ContactRepository contactRepository;

    @InjectMocks
    private CostItemService service;

    @Test
    void create_validCostItem_savesSuccessfully() {

        CostItemForm form = new CostItemForm();
        form.setDescription("Expense");
        form.setCostAmount(new BigDecimal("50"));
        form.setProjectId(1L);
        form.setEmployeeId(1L);
        form.setType(CostItem.Type.EXPENSE);

        Project project = mock(Project.class);
        Employee employee = mock(Employee.class);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        assertDoesNotThrow(() -> service.create(form));

        verify(repository).save(any());
    }
}