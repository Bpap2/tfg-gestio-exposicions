package cat.cccb.tfg.exposicions.config;

import cat.cccb.tfg.exposicions.user.Role;
import cat.cccb.tfg.exposicions.user.UserEntity;
import cat.cccb.tfg.exposicions.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initUsers(UserRepository userRepo, PasswordEncoder encoder) {
        return args -> {
            if (userRepo.findByEmail("admin@demo.local").isEmpty()) {
                UserEntity u = new UserEntity();
                u.setEmail("admin@demo.local");
                u.setPasswordHash(encoder.encode("admin123"));
                u.setRole(Role.ADMIN);
                userRepo.save(u);
                System.out.println("✅ Admin user created");
            }
        };
    }
}