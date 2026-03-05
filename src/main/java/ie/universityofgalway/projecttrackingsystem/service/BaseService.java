package ie.universityofgalway.projecttrackingsystem.service;

import java.util.List;

public interface BaseService<T, F> {

    void updateEntity(T entity, F form);

    F mapToForm(T entity);

    List<T> list();

    T getById(Long id);

    F getFormById(Long id);

    T create(F form);

    T update(Long id, F form);

    void delete(Long id);
}