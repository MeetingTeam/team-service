package meetingteam.teamservice;

import meetingteam.teamservice.dtos.Team.CreateTeamDto;
import meetingteam.teamservice.services.TeamService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

@SpringBootConfiguration
class TeamServiceApplicationTests {
  private final Validator validator;

    public TeamServiceApplicationTests() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    void contextLoads() {
        var createUserDto= new CreateTeamDto();
        createUserDto.setTeamName("MyTeam");

        Set<ConstraintViolation<CreateTeamDto>> violations = validator.validate(createUserDto);
        assertTrue(violations.isEmpty(), "DTO should be valid");
    }
}
