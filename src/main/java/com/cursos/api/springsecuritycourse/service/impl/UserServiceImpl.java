package com.cursos.api.springsecuritycourse.service.impl;

import com.cursos.api.springsecuritycourse.dto.SaveUser;
import com.cursos.api.springsecuritycourse.exception.InvalidPasswordException;
import com.cursos.api.springsecuritycourse.persistence.entity.User;
import com.cursos.api.springsecuritycourse.persistence.repository.UserRepository;
import com.cursos.api.springsecuritycourse.service.UserService;
import com.cursos.api.springsecuritycourse.util.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public User registerOneCustomer(SaveUser newUser) {

        validatePassword(newUser);


        User user = new User();
        user.setName(newUser.getName());
        user.setUsername(newUser.getUsername());
        user.setRole(Role.ROLE_CUSTOMER);
        user.setPassword(passwordEncoder.encode(newUser.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public Optional<User> finOneByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    private void validatePassword(SaveUser dto) {
        if (!StringUtils.hasText(dto.getPassword()) || !StringUtils.hasText(dto.getRepeatPassword())){
            throw new InvalidPasswordException("Passwords don't match");
        }
        if (!dto.getPassword().equals(dto.getRepeatPassword())){
            throw new InvalidPasswordException("Passwords don't match");
        }
    }

}
