package com.alfonsovidrio.springboot.app.springboot_crud.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alfonsovidrio.springboot.app.springboot_crud.entities.Role;
import com.alfonsovidrio.springboot.app.springboot_crud.entities.User;
import com.alfonsovidrio.springboot.app.springboot_crud.repositories.RoleRepository;
import com.alfonsovidrio.springboot.app.springboot_crud.repositories.UserRepository;



@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return (List<User>) userRepository.findAll();
    }

    @Override
    @Transactional
    public User save(User user) {
        Optional<Role> optionalRoleUser = roleRepository.findByName("ROLE_USER");
        List<Role> roles = new ArrayList<>();
        
        if (optionalRoleUser.isPresent()) {
            roles.add(optionalRoleUser.get());
        } else {
            throw new RuntimeException("Role ROLE_USER not found in database");
        }

        if (user.isAdmin()) {
            Optional<Role> optionalRoleAdmin = roleRepository.findByName("ROLE_ADMIN");
            if (optionalRoleAdmin.isPresent()) {
                roles.add(optionalRoleAdmin.get());
            } else {
                throw new RuntimeException("Role ROLE_ADMIN not found in database");
            }
        }

        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
}
