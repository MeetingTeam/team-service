package meetingteam.teamservice.repositories;

import meetingteam.teamservice.models.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, String> {
    @Query("select tm.team.id from TeamMember tm where tm.userId=?1 and tm.role!=meetingteam.teamservice.models.enums.TeamRole.LEAVE")
    List<String> getTeamIdsByUserId(String userId);

    @Query("select team from Team team left join fetch team.channels where team in :teamIds")
    Page<Team> getTeamsWithChannels(@Param("teamIds") List<String> teamIds, Pageable pageable);

    @Query("select team from Team team "+
                "left join fetch team.channels "+
                "where team.id in ( "+
                        "select tm.team.id from TeamMember tm "+
                        "where tm.userId = :userId "+
                        "and tm.role <> meetingteam.teamservice.models.enums.TeamRole.LEAVE)")
    Page<Team> getTeamsWithChannelsByUserId(@Param("userId") String userId, Pageable pageable);


    @Query("select team from Team team left join fetch team.channels where team=?1")
    Team getTeamWithChannels(Team team);
    
    @Query("select team from Team team "+
                "left join fetch team.channels "+
                "where LOWER(team.teamName) like %:searchName% "+
                "and team.id in ( "+
                        "select tm.team.id from TeamMember tm "+
                        "where tm.userId = :userId "+
                        "and tm.role <> meetingteam.teamservice.models.enums.TeamRole.LEAVE)")
    List<Team> getTeamsByUserIdAndSearchName(String userId, String searchName, Pageable pageable);
}
