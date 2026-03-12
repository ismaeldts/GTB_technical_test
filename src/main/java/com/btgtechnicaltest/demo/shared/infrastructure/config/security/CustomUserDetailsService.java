package com.btgtechnicaltest.demo.shared.infrastructure.config.security;

import com.btgtechnicaltest.demo.cliente.domain.model.Cliente;
import com.btgtechnicaltest.demo.cliente.domain.ports.FindByEmailClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final FindByEmailClienteRepository findByEmailClienteRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Cliente cliente = findByEmailClienteRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Cliente no encontrado con email: " + email));

        return new User(
                cliente.getEmail(),
                cliente.getPassword(),
                List.of(new SimpleGrantedAuthority(cliente.getRole()))
        );
    }
}

