package com.patrykmarchewka.concordiapi.DatabaseModel;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class UserTaskRepositoryTest implements UserTaskTestHelper, UserTestHelper, TaskTestHelper, TeamTestHelper{

    private final UserTaskRepository userTaskRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final TeamRepository teamRepository;


    public UserTaskRepositoryTest(UserTaskRepository userTaskRepository, UserRepository userRepository, TaskRepository taskRepository, TeamRepository teamRepository) {
        this.userTaskRepository = userTaskRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.teamRepository = teamRepository;
    }

    @AfterEach
    void cleanUp(){
        userTaskRepository.deleteAll();
        userTaskRepository.flush();
        taskRepository.deleteAll();
        taskRepository.flush();
        teamRepository.deleteAll();
        teamRepository.flush();
        userRepository.deleteAll();
        userRepository.flush();
    }

    @Test
    void shouldSaveAndRetrieveUserTaskCorrectly(){
        User user = createUser("TEST",userRepository);
        Team team = createTeam(teamRepository);
        Task task = createTask(team,taskRepository);
        long id = createUserTask(user,task,userTaskRepository).getID();

        UserTask found = userTaskRepository.findByAssignedUserAndAssignedTask(user,task).orElse(null);

        assertNotNull(found);
        assertEquals(id, found.getID());
        assertEquals(user, found.getAssignedUser());
        assertEquals(task, found.getAssignedTask());
    }
}
