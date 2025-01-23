package springai.rag.poc.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import springai.rag.poc.modal.Employees;

/**
 * Employee Repository for {@link Employees}.
 */
public interface IEmployeeRepo extends JpaRepository<Employees, Integer> {
}
