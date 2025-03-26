package com.example.javasprintbootapi;

import com.example.javasprintbootapi.DatabaseModel.Team;
import com.example.javasprintbootapi.DatabaseModel.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class TeamController {
    @Autowired
    private TeamService teamService;

    @PostMapping("/teams")
    public ResponseEntity<?> createTeam(@RequestBody Map<String,String> body, Authentication authentication){
        Team team = teamService.createTeam(body.get("name"),(User) authentication.getPrincipal());
        return ResponseEntity.status(HttpStatus.CREATED).body("Team created with ID of " + teamService.getID(team));
    }

    @GetMapping("/teams/{ID}")
    public ResponseEntity<?> getTeam(@PathVariable long ID){
        Team team = teamService.getTeamByID(ID);
        return ResponseEntity.ok(team);
    }





}
