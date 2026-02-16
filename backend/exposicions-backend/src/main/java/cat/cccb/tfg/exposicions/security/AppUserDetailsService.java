package cat.cccb.tfg.exposicions.security;

import cat.cccb.tfg.exposicions.user.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public AppUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        var u = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return User.builder()
                .username(u.getEmail())
                .password(u.getPasswordHash())
                .authorities(List.of(
                        new SimpleGrantedAuthority("ROLE_" + u.getRole())
                ))
                .build();
    }
}
