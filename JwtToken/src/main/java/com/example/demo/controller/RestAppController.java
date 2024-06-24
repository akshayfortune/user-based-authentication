package com.example.demo.controller;

import org.springframework.security.core.Authentication;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.config.JwtGeneratorValidator;
import com.example.demo.model.LoginResponce;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.model.UserDto;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;

@RestController
public class RestAppController {
	
	@Autowired
	UserRepository userRepo;

	@Autowired
	AuthenticationManager authManager;

	@Autowired
	JwtGeneratorValidator jwtGenVal;
	
	@Autowired
	BCryptPasswordEncoder bcCryptPasswordEncoder;
	
	@Autowired
	UserService userService;
	
	@PostMapping("/registration")
	public ResponseEntity<String> registerUser(@RequestBody UserDto dto) {
	    User user = userService.save(dto);
	    if (user == null) {
	        return new ResponseEntity<>("Not able to save user", HttpStatus.BAD_REQUEST);
	    } else {
	        return new ResponseEntity<>("User saved successfully", HttpStatus.OK);
	    }
	}
	
	@GetMapping("/login")
	public LoginResponce generateJwtToken(@RequestBody UserDto userDto) throws Exception {

		Authentication authentication = authManager
				.authenticate(new UsernamePasswordAuthenticationToken(userDto.getUserName(), userDto.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);	
		User user= userRepo.findByUserName(userDto.getUserName());
		Long userId = user.getId();
		String userName = user.getUserName();
		Set<Role> role = user.getRoles();
		String token =jwtGenVal.generateToken(authentication);
		LoginResponce bean = new LoginResponce();
		bean.setUserId(userId);
		bean.setUserName(userName);
		bean.setRole(role);
		bean.setToken(token);
		return bean;
	}

	@GetMapping("/welcomeAdmin")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public String welcome() {
		return "WelcomeAdmin";
	}
	
	@GetMapping("/welcomeUser")
	@PreAuthorize("hasRole('ROLE_USER')")
	public String welcomeUser() {
		return "WelcomeUSER";
	}

}
