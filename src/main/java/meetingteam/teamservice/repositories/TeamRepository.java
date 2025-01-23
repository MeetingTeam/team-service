package meetingteam.teamservice.repositories;

import meetingteam.teamservice.models.Team;
import meetingteam.teamservice.models.enums.TeamRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, String> {
    @Query("select tm.team.id from TeamMember tm where tm.userId=?1 and tm.role!=meetingteam.teamservice.models.enums.TeamRole.LEAVE")
    List<String> getTeamIdsByUserId(String userId);

    @Query("select team from Team team "
            + "left join fetch team.members "
            + "where team.id in :teamIds")
    List<Team> getTeamsWithMembers(@Param("teamIds") List<String> teamIds);

    @Query("select team from Team team "
            + "left join fetch team.channels "
            + "where team in :teamIds")
    List<Team> getTeamsWithChannels(@Param("teamIds") List<String> teamIds);

    @Query("select team from Team team left join fetch team.channels where team.id=?1")
    Team getTeamWithChannels(String teamId);

    @Query("select team from Team team left join fetch team.members where team.id=?1")
    Team getTeamWithMembers(String teamId);
}
