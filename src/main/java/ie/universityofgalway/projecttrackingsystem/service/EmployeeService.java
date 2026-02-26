package ie.universityofgalway.projecttrackingsystem.service;

import ie.universityofgalway.projecttrackingsystem.domain.core.Employee;
import ie.universityofgalway.projecttrackingsystem.dto.EmployeeView;
import ie.universityofgalway.projecttrackingsystem.dto.EmployeeForm;
import ie.universityofgalway.projecttrackingsystem.repository.core.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<EmployeeView> searchSummaries(String query) {
        return employeeRepository
                .findByNameContainingIgnoreCase(query)
                .stream()
                .map(e -> new EmployeeView(
                        e.getId(),
                        e.getName()
                ))
                .toList();
    }

    public List<EmployeeView> listSummaries() {
        return employeeRepository.findAll()
                .stream()
                .map(e -> new EmployeeView(
                        e.getId(),
                        e.getName()
                ))
                .toList();
    }

    public Employee getById(Long id) {
        return employeeRepository.findById(id).orElseThrow();
    }

    public Employee create(EmployeeForm form) {
        Employee e = new Employee(form.getName(), form.getHourlyRate());
        e.setAddress(form.getAddress());
        return employeeRepository.save(e);
    }

    public Employee update(Long id, EmployeeForm form) {
        Employee e = getById(id);
        e.setName(form.getName());
        e.setAddress(form.getAddress());
        e.setHourlyRate(form.getHourlyRate());
        return employeeRepository.save(e);
    }

    public void delete(Long id) {
        employeeRepository.deleteById(id);
    }

    public EmployeeForm getFormById(Long id) {
        Employee e = getById(id);
        EmployeeForm form = new EmployeeForm();
        form.setName(e.getName());
        form.setAddress(e.getAddress());
        form.setHourlyRate(e.getHourlyRate());
        form.setId(e.getId());
        return form;
    }
}
