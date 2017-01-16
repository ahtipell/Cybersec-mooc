package sec.project.controller;

import org.apache.catalina.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sec.project.domain.Signup;
import sec.project.repository.SignupRepository;
import java.security.Principal;
import java.util.Arrays;

@Controller
public class SignupsController {

    @Autowired
    private SignupRepository signupRepository;

    @Autowired
    private UserDetailsService userDetailsService;

    public SignupsController() {
    }

    // OMG! I have no idea what I'm doing. I need a parameter to check if the user is admin, right?
    @RequestMapping("/signups")
    public String home(Model model, Principal principal, @RequestParam(required = false) Boolean admin) {

        // Örhm, no since I have the parameter, I can put there information about the users status, right?
        // So, the user MIGHT actually be admin!
        String username = principal.getName(); //get logged in username
        if(userDetailsService.loadUserByUsername(username).getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
            admin = true;
        }

        // Since I have no idea about this stuff, I'm not going to do the following
        // in the line where I check if the user is indeed admin. Show something anyway...
        if (admin != null) {
            if(admin) {
                if(signupRepository.findAll().isEmpty()) {
                    model.addAttribute("list", new Signup("Kosh!", "No signups yet!"));
                } else {
                    // OMG! Peoples names and addresses? Someone might find this info useful.
                    // Even might rob their houses while they're away at the gig!
                    model.addAttribute("list", signupRepository.findAll());
                }
            }
        }

        // If the user is an imposture, show error! But, for the error I'm just going to leave a hint
        // about what went wrong, so a clever attacker could figure it out.
        if(admin == null || !admin) {
            model.addAttribute("list", new Signup("ERROR", "No or False parameter 'admin' provided!"));
        }

        return "signups";
    }

    // Following code is mainly a copy-pasta from "S2.07 EuroShopper" task, which minimizes
    // JSESSIONID into a predictable value. Additionally it's made extra vulnerable by disabling
    // httpOnly-flag for the cookie, which allows the client browser to access the session cookie
    // simply from document.cookie (for eg. within XSS-attack ending in session hijack).
    @Bean
    public TomcatEmbeddedServletContainerFactory tomcatContainerFactory() {
        TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory();
        factory.setTomcatContextCustomizers(Arrays.asList(new TomcatContextCustomizer[] { tomcatContextCustomizer() }));
        return factory;
    }

    @Bean
    public TomcatContextCustomizer tomcatContextCustomizer() {
        return new TomcatContextCustomizer() {
            @Override
            public void customize(Context context) {
                context.getManager().getSessionIdGenerator().setSessionIdLength(1);
                context.setUseHttpOnly(false);
            }
        };
    }

}
