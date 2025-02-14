package meetingteam.teamservice.repositories;

import meetingteam.teamservice.models.Channel;
import meetingteam.teamservice.models.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import jakarta.transaction.Transactional;

public interface ChannelRepository extends JpaRepository<Channel, String>{
	@Query("select channel.team.id from Channel channel where channel.id=?1")
	String findTeamIdById(String channelId);
	
	@Query("select channel.team from Channel channel where channel.id=?1")
	Team findTeamById(String channelId);
	
	@Query("select channel.team.teamName from Channel channel where channel.id=?1")
	String findTeamNameById(String channelId);
}
