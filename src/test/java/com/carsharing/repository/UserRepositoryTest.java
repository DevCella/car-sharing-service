package com.carsharing.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.carsharing.model.User;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("existsByEmail should return true if email is in database")
    @Sql(scripts = {
            "classpath:database/users/insert-test-user.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void existsByEmail_ExistingEmail_ReturnsTrue() {
        boolean exists = userRepository.existsByEmail("test@gmail.com");
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("findByEmail should return empty optional if user not found")
    void findByEmail_NonExistingEmail_ReturnsEmpty() {
        Optional<User> result = userRepository.findByEmail("non-existent@email.com");
        assertThat(result).isEmpty();
    }
}
