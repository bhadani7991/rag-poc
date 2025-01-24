package springai.rag.poc.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import springai.rag.poc.modal.Employees;

import java.util.List;

/**
 * Employee Repository for {@link Employees}.
 */
public interface IEmployeeRepo extends JpaRepository<Employees, Integer> {

    @Query(value = "SELECT e.* FROM employees.employees e " +
            "ORDER BY e.emp_no desc LIMIT 1000",
            nativeQuery = true)
    List<Employees> findTop1000EmployeesOrderedByEmpNo();
}
