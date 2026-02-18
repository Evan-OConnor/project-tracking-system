package Service;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public abstract class BaseService<T, ID> {

    protected final JpaRepository<T, ID> repository;

    public BaseService(JpaRepository<T, ID> repository) {
        this.repository = repository;
    }

    /** List all entities */
    public List<T> list() {
        return repository.findAll();
    }

    /** Get single entity by ID */
    public T get(ID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ID: " + id));
    }

    /** Delete entity by ID */
    public void delete(ID id) {
        repository.deleteById(id);
    }

    /** Save entity - implemented in module-specific service */
    public abstract T saveFromForm(Object form);
}
