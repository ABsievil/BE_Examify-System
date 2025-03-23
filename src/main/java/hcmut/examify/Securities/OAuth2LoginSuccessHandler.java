package hcmut.examify.Securities;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import hcmut.examify.Models.Role;
import hcmut.examify.Models.User;
import hcmut.examify.Repositories.UserRepository;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtUtilities jwtUtilities;

    public OAuth2LoginSuccessHandler(JwtUtilities jwtUtilities) {
        this.jwtUtilities = jwtUtilities;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        OAuth2User principal = (OAuth2User) authentication.getPrincipal();
        // String email = (String) principal.getAttribute("email");       // error: github not public email
        String nameUser = (String) principal.getName();
        User user = userRepository.findByUsername(nameUser);
        Optional<User> optionalUser = Optional.ofNullable(user);

        // Nếu trong db đã có user này thì tạo token với userid
        optionalUser.ifPresentOrElse(u -> {
            String token = jwtUtilities.generateToken(u.getUsername(), u.getRole().toString(), 
                u.getUserid() != null ? u.getUserid().toString() : "");
            Cookie cookie = new Cookie("jwt", token);
            cookie.setHttpOnly(true); 
            cookie.setPath("/");
            cookie.setMaxAge(3600); 
            response.addCookie(cookie);
        }, () -> {
            // Ngược lại thì tạo user mới và token mới
            // save user into db to checking with jwt token on later request
            User userEntity = new User();
            userEntity.setUsername(nameUser);
            // userEntity.setEmail(nameUser);
            userEntity.setPassword(nameUser);
            userEntity.setRole(Role.STUDENT);
            // Không thiết lập userid - sẽ được liên kết với ID từ bảng Users sau
            userRepository.save(userEntity);

            // Tạo token mới không có userid (hoặc với userid là null)
            User savedUser = userRepository.findByUsername(nameUser);
            String savedUserId = savedUser != null ? savedUser.getUserid().toString() : "";

            String token = jwtUtilities.generateToken(nameUser, Role.STUDENT.toString(), savedUserId);
            Cookie cookie = new Cookie("jwt", token);
            cookie.setHttpOnly(true); 
            cookie.setPath("/");
            cookie.setMaxAge(3600); 
            response.addCookie(cookie);
        });

        // super.onAuthenticationSuccess(request, response, authentication);
        getRedirectStrategy().sendRedirect(request, response, "/home");    }
}