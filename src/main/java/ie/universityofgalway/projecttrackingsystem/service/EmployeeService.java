package ie.universityofgalway.projecttrackingsystem.service;

import Service.BaseService;
import ie.universityofgalway.projecttrackingsystem.domain.core.Employee;
import ie.universityofgalway.projecttrackingsystem.dto.EmployeeForm;
import ie.universityofgalway.projecttrackingsystem.repository.core.EmployeeRepository;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService extends BaseService<Employee, EmployeeForm, Long> {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        super(EmployeeRepository);
        this.employeeRepository = employeeRepository;
    }

    /** Implement abstract method from BaseService */
    @Override
    public Employee saveFromForm(EmployeeForm form) {
        Employee e = new Employee(form.getName(), form.getHourlyRate());
        e.setAddress(form.getAddress());
        return employeeRepository.save(e);
    }

    /** GET FORM */
    public EmployeeForm getFormById(Long id) {
        Employee e = get(id); // BaseService.get()
        EmployeeForm form = new EmployeeForm();
        form.setName(e.getName());
        form.setAddress(e.getAddress());
        form.setHourlyRate(e.getHourlyRate());
        return form;
    }

    /** UPDATE */
    public void update(Long id, EmployeeForm form) {
        Employee e = get(id); // BaseService.get()
        e.setName(form.getName());
        e.setAddress(form.getAddress());
        e.setHourlyRate(form.getHourlyRate());
        employeeRepository.save(e);
    }
}
