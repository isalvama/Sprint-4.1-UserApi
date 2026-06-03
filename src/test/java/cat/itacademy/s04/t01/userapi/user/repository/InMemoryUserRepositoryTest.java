package cat.itacademy.s04.t01.userapi.user.repository;

import cat.itacademy.s04.t01.userapi.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryUserRepositoryTest {

    private InMemoryUserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new InMemoryUserRepository();
    }

    @Test
    @DisplayName("save() should add user to the list and return it")
    void save_addsUserToList() {
        User user = new User("John", "john@mail.com", UUID.randomUUID());

        User savedUser = userRepository.save(user);

        assertEquals(user, savedUser);
        assertEquals(1, userRepository.findAll().size());
        assertTrue(userRepository.findAll().contains(user));
    }

    @Test
    @DisplayName("findAll() should return an immutable copy of the users list")
    void findAll_returnsCopyOfList() {
        userRepository.save(new User("John", "john@mail.com", UUID.randomUUID()));
        userRepository.save(new User("Jane", "jane@mail.com", UUID.randomUUID()));

        List<User> allUsers = userRepository.findAll();

        assertEquals(2, allUsers.size());
    }

    @Nested
    @DisplayName("findById() tests")
    class FindByIdTests {
        @Test
        @DisplayName("should return user when ID exists")
        void findById_returnsUser_whenIdExists() {
            UUID id = UUID.randomUUID();
            User user = new User( "John", "john@mail.com", id);
            userRepository.save(user);

            Optional<User> result = userRepository.findById(id);

            assertTrue(result.isPresent());
            assertEquals("John", result.get().getName());
        }

        @Test
        @DisplayName("should return the only user with the ID when it exists")
        void findById_returnsUserWithId_whenIdExists() {
            UUID id = UUID.randomUUID();
            User user = new User( "John", "john@mail.com", id);
            userRepository.save(new User( "Martha", "m@mail.com", UUID.randomUUID()));
            userRepository.save(user);
            userRepository.save(new User( "Alice", "a@mail.com", UUID.randomUUID()));

            Optional<User> result = userRepository.findById(id);

            assertTrue(result.isPresent());
            assertEquals("John", result.get().getName());
        }

        @Test
        @DisplayName("should return empty optional when ID does not exist")
        void findById_returnsEmpty_whenIdDoesNotExist() {
            Optional<User> result = userRepository.findById(UUID.randomUUID());
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("searchByName() tests (Case Insensitive & Partial)")
    class SearchByNameTests {
        @BeforeEach
        void init() {
            userRepository.save(new User("Alice Cooper", "alice@mail.com", UUID.randomUUID()));
            userRepository.save(new User("Bob Dylan", "bob@mail.com", UUID.randomUUID()));
            userRepository.save(new User("Alice In Chains", "chains@mail.com", UUID.randomUUID()));
        }

        @Test
        @DisplayName("should find multiple users with partial match ignoring case")
        void searchByName_partialMatch_ignoreCase() {
            List<User> results = userRepository.searchByName("ali");

            assertEquals(2, results.size());
            assertTrue(results.stream().allMatch(u -> u.getName().contains("Alice")));
        }

        @Test
        @DisplayName("should return empty list when no name matches")
        void searchByName_noMatch_returnsEmpty() {
            List<User> results = userRepository.searchByName("Zack");
            assertTrue(results.isEmpty());
        }
    }

    @Nested
    @DisplayName("existsByEmail() tests")
    class ExistsByEmailTests {
        @Test
        @DisplayName("should return true when email exists (ignoring case)")
        void existsByEmail_returnsTrue_whenEmailExists() {
            userRepository.save(new User("John", "John@Mail.com", UUID.randomUUID()));

            boolean exists = userRepository.existsByEmail("john@mail.com");

            assertTrue(exists);
        }

        @Test
        @DisplayName("should return false when email does not exist")
        void existsByEmail_returnsFalse_whenEmailDoesNotExist() {
            boolean exists = userRepository.existsByEmail("unknown@mail.com");
            assertFalse(exists);
        }
    }
}