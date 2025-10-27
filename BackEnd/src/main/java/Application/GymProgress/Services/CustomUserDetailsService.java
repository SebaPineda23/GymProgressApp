package Application.GymProgress.Services;

import Application.GymProgress.Entities.User;
import Application.GymProgress.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + userName));

        // Debug
        System.out.println("🔐 UserDetailsService - User: " + user.getUsername());
        System.out.println("🔐 UserDetailsService - Roles: " + user.getRoleSet());

        return user;
    }
}