package com.example.demo.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.model.UserDto;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;

@Service
public class UserServiceImp implements UserService {
	
	@Autowired
	UserRepository userRepository;
	@Autowired
	RoleRepository roleRepository;
	
	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		 User user = userRepository.findByUserName(username);
	     return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(), mapRolesToAuthorities(user.getRoles()));
	}
	
	public Collection<? extends GrantedAuthority> mapRolesToAuthorities(Set<Role> roles){
		return roles.stream().map(role -> new SimpleGrantedAuthority(role.getRole())).collect(Collectors.toList());
	}
    //roles.stream().map(role -> new SimpleGrantedAuthority(role.getRole())).collect(Collectors.toList());
	
	@Override
	public User save(UserDto dto) {

		Role role = new Role();

		if (dto.getRole().equals("USER"))
			role = roleRepository.findByRole("ROLE_USER");
		else if (dto.getRole().equals("ADMIN"))
			role = roleRepository.findByRole("ROLE_ADMIN");

		User user = new User();
		user.setEmail(dto.getEmail());
		user.setUserName(dto.getUserName());
		user.setPassword(passwordEncoder.encode(dto.getPassword()));
		Set<Role> roles = new HashSet<>();
		roles.add(role);
		user.setRoles(roles);

		return userRepository.save(user);
	}

}
