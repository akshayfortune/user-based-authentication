package com.example.demo.model;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponce {
	
	private Long userId;
	private String userName;
	private Set<Role> role;
	private String token;

}
