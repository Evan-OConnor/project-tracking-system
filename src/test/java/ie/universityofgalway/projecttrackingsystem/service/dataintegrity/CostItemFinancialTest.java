package ie.universityofgalway.projecttrackingsystem.service.dataintegrity;

import ie.universityofgalway.projecttrackingsystem.domain.core.*;
import ie.universityofgalway.projecttrackingsystem.dto.CostItemForm;
import ie.universityofgalway.projecttrackingsystem.repository.core.*;

import ie.universityofgalway.projecttrackingsystem.service.CostItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CostItemFinancialTest {

    @InjectMocks
    private CostItemService service;

    @Mock
    private CostItemRepository repo;
    @Mock
    private ProjectRepository projectRepo;
    @Mock
    private EmployeeRepository employeeRepo;
    @Mock
    private ContactRepository contactRepo;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldNotAllowEditingBilledItem() {

        CostItem item = mock(CostItem.class);

        when(item.isUnbilled()).thenReturn(false);
        when(repo.findById(1L)).thenReturn(Optional.of(item));

        CostItemForm form = new CostItemForm();

        assertThrows(IllegalStateException.class,
                () -> service.update(1L, form));
    }

    @Test
    void shouldRequireSupplierForOutlay() {

        CostItemForm form = new CostItemForm();
        form.setType(CostItem.Type.OUTLAY);
        form.setProjectId(1L);
        form.setEmployeeId(1L);

        when(projectRepo.findById(any())).thenReturn(Optional.of(new Project()));
        when(employeeRepo.findById(any())).thenReturn(Optional.of(mock(Employee.class)));

        assertThrows(IllegalStateException.class,
                () -> service.create(form));
    }
}
