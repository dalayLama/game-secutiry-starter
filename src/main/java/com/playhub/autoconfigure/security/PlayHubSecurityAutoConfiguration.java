package com.playhub.autoconfigure.security;

import com.playhub.autoconfigure.security.consts.KeycloakJwtClaimNames;
import com.playhub.autoconfigure.security.jwt.DefaultJwdDecoder;
import com.playhub.autoconfigure.security.jwt.PlayHubJwtAuthenticationConverter;
import com.playhub.autoconfigure.security.jwt.PlayHubJwtGrantedAuthoritiesConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Slf4j
public class PlayHubSecurityAutoConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("Configuring Security Filter Chain");
        http.oauth2ResourceServer(customizer ->
                customizer.jwt(jwtCustomizer ->
                        jwtCustomizer.jwtAuthenticationConverter(jwtAuthenticationConverter())
                ));
        http.authorizeHttpRequests(registry -> registry.anyRequest().authenticated())
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        log.info("Creating Jwt Decoder");
        return new DefaultJwdDecoder();
    }

    private Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
        PlayHubJwtGrantedAuthoritiesConverter authoritiesConverter = new PlayHubJwtGrantedAuthoritiesConverter(
                KeycloakJwtClaimNames.ROLES
        );
        return new PlayHubJwtAuthenticationConverter(KeycloakJwtClaimNames.PREFERRED_USERNAME, authoritiesConverter);
    }

}
