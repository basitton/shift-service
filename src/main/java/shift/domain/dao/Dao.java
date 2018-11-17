package shift.domain.dao;

import java.util.List;
import java.util.Optional;

/**
 * Dao api for performing CRUD operations on "fake" DB
 * @param <T>
 */
public interface Dao<T> {
    List<T> getAll();
    T get(int id) throws IllegalArgumentException;
    void save(T params);
    void update(int id, T params) throws IllegalArgumentException;
    void delete(int id) throws IllegalArgumentException;
}
