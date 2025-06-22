package org.crm.reachai.service;

import org.crm.reachai.enums.RoleName;
import org.crm.reachai.model.User;
import org.crm.reachai.repository.UserRepository;
import org.crm.reachai.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository usersRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest);
        String email = user.getAttribute("email");

        User existingUser = usersRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(email)
                            .userName(email.split("@")[0])
                            .role(RoleName.USER)
                            .build();
                    return usersRepository.save(newUser);
                });

        return new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority("ROLE_" + existingUser.getRole())),
                user.getAttributes(),
                "email"
        );
    }
}

