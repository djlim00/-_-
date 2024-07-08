package kuit.remetic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import kuit.remetic.model.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
}
