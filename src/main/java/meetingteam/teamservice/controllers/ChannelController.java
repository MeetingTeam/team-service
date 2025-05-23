package meetingteam.teamservice.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import meetingteam.teamservice.dtos.Channel.CreateChannelDto;
import meetingteam.teamservice.dtos.Channel.ResChannelDto;
import meetingteam.teamservice.dtos.Channel.UpdateChannelDto;
import meetingteam.teamservice.services.ChannelService;
import org.hibernate.validator.constraints.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/channel")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class ChannelController {
    private final ChannelService channelService;

    @PostMapping
    public ResponseEntity<ResChannelDto> createChannel(
            @RequestBody @Valid CreateChannelDto channelDto
    ){
        channelService.createChannel(channelDto);
        return ResponseEntity.ok().build();
    }

    @PatchMapping
    public ResponseEntity<Void> updateChannel(
            @RequestBody @Valid UpdateChannelDto channelDto
    ){
        channelService.updateChannel(channelDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{channelId}")
    public ResponseEntity<Void> deleteChannel(
            @PathVariable @UUID String channelId){
        channelService.deleteChannel(channelId);
        return ResponseEntity.ok().build();
    }
}
