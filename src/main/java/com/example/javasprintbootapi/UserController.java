package com.example.javasprintbootapi;

import com.example.javasprintbootapi.DatabaseModel.User;
import com.example.javasprintbootapi.DatabaseModel.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    private Map<String,String> userInfo(Authentication authentication){
        Object user = authentication.getPrincipal();
        if (user instanceof User){
            Map<String,String> ret = new HashMap<>();
            ret.put("login",((User) user).getLogin());
            ret.put("password",((User)user).getPassword());
            ret.put("role",((User)user).getRole().name());
            return ret;
        }
        else{
            return null;
        }
    }

    @GetMapping("/all")
    public List<User> getAllUsers(Authentication authentication){
        if (authentication == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"No authentication!");
        }
        Map<String,String> user = userInfo(authentication);
        if (user.get("role").equals(PublicVariables.UserRole.ADMIN.name())){
            return userService.getAllUsers();
        }
        else{
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"This action requires admin access");
        }


    }



    @GetMapping("/me")
    public User getMyProfile(Authentication authentication){
        if (authentication == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"No authentication!");
        }
        Object user = authentication.getPrincipal();
        String login = "";
        if (user instanceof User ){
            login = ((User)user).getLogin();
        }
        return userService.getUserByLogin(login);
    }


    //For testing purposes only
    @GetMapping("/test")
    public ResponseEntity<String> getData(Authentication authentication){
        return ResponseEntity.ok(authentication.getName());
    }

    @PostMapping("/createUser")
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

    @PostMapping("/deleteUserByID")
    public ResponseEntity<?> deleteUser(@RequestBody Map<String,Long> body, Authentication authentication){
        if (authentication == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"No authentication!");
        }
        Long ID = body.get("id");

        Map<String,String> user = userInfo(authentication);
        if (user.get("role").equals(PublicVariables.UserRole.ADMIN.name())){
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


}
