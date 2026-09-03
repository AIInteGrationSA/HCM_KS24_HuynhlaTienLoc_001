package vn.rikkei.exam.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.rikkei.exam.booking.model.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, String> {}
