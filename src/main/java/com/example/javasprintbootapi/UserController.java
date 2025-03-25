package com.example.javasprintbootapi;

import com.example.javasprintbootapi.DatabaseModel.User;
import com.example.javasprintbootapi.DatabaseModel.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(Authentication authentication){
        if(((User)authentication.getPrincipal()).getRole().equals(PublicVariables.UserRole.ADMIN)){
            return ResponseEntity.ok(userService.getAllUsers());
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("This action requires admin access");
        }
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody Map<String,String> body){
        String login = body.get("login");
        String password = body.get("password");
        String name = body.get("name");
        String lastName = body.get("lastname");
        String role = body.get("role");

        User user = userRepository.findByLogin(login);
        if (user == null){
            try {
                userService.createUser(login,password,name,lastName, PublicVariables.UserRole.fromString(role));
                return ResponseEntity.ok("User created");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cant create user, check credentials");
            }
        }
        else{
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User with that login already exists");
        }
    }
    @DeleteMapping("/users")
    public ResponseEntity<?> deleteUser(@RequestBody Map<String,String> body, Authentication authentication){
        long ID = Long.parseLong(body.get("id"));

        User user = (User)authentication.getPrincipal();
        if (user.getRole().equals(PublicVariables.UserRole.ADMIN)){
            try {
                if (userService.getUserByID(ID).getRole().equals(PublicVariables.UserRole.ADMIN) && !(((User)authentication.getPrincipal()).getID() == ID)){
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Can't delete other admins");
                }
                userService.deleteUserByID(ID);
                return ResponseEntity.ok("User deleted");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cant delete user:" + e);
            }
        }
        else{
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"This action requires admin access");
        }
    }

    @GetMapping("/users/me")
    public ResponseEntity<?> getMyProfile(Authentication authentication){
        Object user = authentication.getPrincipal();
        Long id = null;
        if (user instanceof User ){
            id = ((User)user).getID();
        }
        return userRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/users/me/refresh")
    public ResponseEntity<?> refreshToken(Authentication authentication){
        User user = (User) authentication.getPrincipal();
        String response;
        try {
            response = JSONWebToken.GenerateJWToken(user.getLogin(),user.getPassword(),user.getRole().name());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Cant generate JSON Token");
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{ID}")
    public ResponseEntity<?> getUser(@PathVariable long ID, Authentication authentication){
        if (((User)authentication.getPrincipal()).getRole().equals(PublicVariables.UserRole.ADMIN)){
            User user = userService.getUserByID(ID);
            return ResponseEntity.ok(user);
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You require admin for that action");
        }

    }

    @PutMapping("/users/{ID}")
    public ResponseEntity<?> putUser(@PathVariable long ID,@RequestBody Map<String,String> body, Authentication authentication){
        if (((User)authentication.getPrincipal()).getRole().equals(PublicVariables.UserRole.ADMIN) && userRepository.existsById(ID)){
            User user = userService.getUserByID(ID);
            String login = body.get("login");
            user.setLogin(login);
            String password = body.get("password");
            user.setPassword(Passwords.HashPasswordBCrypt(password));
            String name = body.get("name");
            user.setName(name);
            String lastName = body.get("lastname");
            user.setLastName(lastName);
            String role = body.get("role");
            user.setRole(PublicVariables.UserRole.fromString(role));
            userRepository.save(user);
            return ResponseEntity.ok("User fully changed");
        }
        else{
            return ResponseEntity.status(HttpStatus.MULTI_STATUS).body("User not found or insufficient privileges");
        }
    }

    @PatchMapping("/users/{ID}")
    public ResponseEntity<?> patchUser(@PathVariable long ID, @RequestBody Map<String,String> body, Authentication authentication){
        if (((User)authentication.getPrincipal()).getRole().equals(PublicVariables.UserRole.ADMIN) && userRepository.existsById(ID)){
            User user = userService.getUserByID(ID);
            if (body.containsKey("login"))
                user.setLogin(body.get("login"));
            if (body.containsKey("password"))
                user.setPassword(Passwords.HashPasswordBCrypt(body.get("password")));
            if (body.containsKey("name"))
                user.setName(body.get("name"));
            if (body.containsKey("lastname"))
                user.setLastName(body.get("lastname"));
            if (body.containsKey("role"))
                user.setRole(PublicVariables.UserRole.fromString(body.get("role")));
            userRepository.save(user);
            return ResponseEntity.ok("User changed");
        }
        else{
            return ResponseEntity.status(HttpStatus.MULTI_STATUS).body("User not found or insufficent priviledges");
        }
    }

    @DeleteMapping("/users/{ID}")
    public ResponseEntity<?> deleteUser(@PathVariable long ID, Authentication authentication){
        User user = (User)authentication.getPrincipal();
        if (user.getRole().equals(PublicVariables.UserRole.ADMIN)){
            try {
                if (userService.getUserByID(ID).getRole().equals(PublicVariables.UserRole.ADMIN) && !(((User)authentication.getPrincipal()).getID() == ID)){
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Can't delete other admins");
                }
                userService.deleteUserByID(ID);
                return ResponseEntity.ok("User deleted");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cant delete user:" + e);
            }
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("This action requires admin access");
        }
    }

    @GetMapping("/users/ban")
    public ResponseEntity<?> getBannedUsers(Authentication authentication){
        User user = (User)authentication.getPrincipal();
        if (user.getRole().equals(PublicVariables.UserRole.ADMIN)){
            Set<User> temp = new HashSet<>();
            for (User users : userService.getAllUsers()){
                if (users.getRole().equals(PublicVariables.UserRole.BANNED))
                    temp.add(users);
            }
            return ResponseEntity.ok(temp);
        }
        else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("That action requires admin access");
        }
    }

    @PostMapping("/users/ban/{ID}")
    public ResponseEntity<?> postBanUser(@PathVariable long ID, Authentication authentication){
        User user = (User)authentication.getPrincipal();
        if (user.getRole().equals(PublicVariables.UserRole.ADMIN)){
            User ban = userService.getUserByID(ID);
            ban.setRole(PublicVariables.UserRole.BANNED);
            userRepository.save(ban);
            return ResponseEntity.ok("User banned!");
        }
        else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("That action requires admin access");
        }
    }


}
