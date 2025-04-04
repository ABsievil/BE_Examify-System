package hcmut.examify.Repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import hcmut.examify.Models.User;

public interface UserRepository extends JpaRepository<User, String> {
    User findByUsername(String username);
    User findByDbUserEmail(String email);

    @Query("SELECT u.password FROM User u WHERE u.username = :username")
    String findPasswordByUsername(@Param("username") String username);
} 