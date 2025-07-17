package com.cbs.AuthService.Service;

import com.cbs.AuthService.Entity.AuthEntity;
import com.cbs.AuthService.Repository.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomUserDetails implements UserDetailsService {
    @Autowired
    AuthRepository authRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
         AuthEntity user =  authRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("User Not Found"));

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole()));
        System.out.println(user.getUsername());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}
