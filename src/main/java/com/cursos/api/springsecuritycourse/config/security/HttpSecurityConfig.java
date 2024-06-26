package com.cursos.api.springsecuritycourse.config.security;


import com.cursos.api.springsecuritycourse.config.security.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class HttpSecurityConfig {

    @Autowired
    private AuthenticationProvider daoAuthProvider;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Deshabilitar la protección contra CSRF (Cross-Site Request Forgery)
        // CSRF no es necesario ya que no estamos utilizando formularios web
        SecurityFilterChain filterChain = http.csrf(csrfConfig -> csrfConfig.disable())
                // Configurar la gestión de sesiones para que sea sin estado
                // Esto significa que no se mantendrán sesiones HTTP entre solicitudes
                .sessionManagement(sessMagConfig -> sessMagConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Configurar el proveedor de autenticación personalizado
                // Utiliza el proveedor de autenticación definido en daoAuthProvider
                .authenticationProvider(daoAuthProvider)

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                // Configurar la autorización para las solicitudes HTTP
                .authorizeHttpRequests(authReqConfig -> {
                    // Permitir acceso sin autenticación a las solicitudes HTTP POST a "/customers"
                    authReqConfig.requestMatchers(HttpMethod.POST,"/customers").permitAll();
                    // Permitir acceso sin autenticación a las solicitudes HTTP POST a "/auth/**"
                    authReqConfig.requestMatchers(HttpMethod.GET,"/auth/validate-token").permitAll();
                    authReqConfig.requestMatchers(HttpMethod.POST,"/auth/authenticate").permitAll();
                    // Requerir autenticación para todas las demás solicitudes HTTP
                    authReqConfig.anyRequest().authenticated();
                })
                // Construir la cadena de filtros de seguridad configurada
                .build();
        return filterChain;
    }
}
