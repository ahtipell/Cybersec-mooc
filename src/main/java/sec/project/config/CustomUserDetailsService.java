package sec.project.config;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private Map<String, String> accountDetails;

    @PostConstruct
    public void init() {
        // this data would typically be retrieved from a database, for now, this is easier...
        this.accountDetails = new TreeMap<>();

        // Current password for ted is set to "president" (Intended to be found in top 10k password list)
        // Who is this guy Ted anyway, and why does he have "user"-access to the admin console?
        this.accountDetails.put("ted", "$2a$10$s4h16SPT0oIC//Lqzf9Zxew00l51UtXPqbPr/b9iWHR8Cv0mbgrE6");
        // Password admin you'll have to guess ;) (pun intended)
        this.accountDetails.put("admin", "$2a$10$tubIrFmZyo2MciTi6tvNrOGVZ5lwhi/VIgKyojdzZr3PBDSRxs82C");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!this.accountDetails.containsKey(username)) {
            throw new UsernameNotFoundException("No such user: " + username);
        }
        // Trololo, make ADMIN!
        if(username.equalsIgnoreCase("admin")) {
            return new org.springframework.security.core.userdetails.User(
                    username,
                    this.accountDetails.get(username),
                    true,
                    true,
                    true,
                    true,
                    Arrays.asList(new SimpleGrantedAuthority("ADMIN")));
        }

        // Everyone else shall be "users"
        return new org.springframework.security.core.userdetails.User(
                username,
                this.accountDetails.get(username),
                true,
                true,
                true,
                true,
                Arrays.asList(new SimpleGrantedAuthority("USER")));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
