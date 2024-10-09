package sheerio.moodbloom.moodbloom.dao.Interface;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sheerio.moodbloom.moodbloom.dao.model.User;



public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);
    User findByEmail(String email);
}
