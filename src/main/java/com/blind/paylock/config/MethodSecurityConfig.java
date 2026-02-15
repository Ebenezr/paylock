// Java
package com.blind.paylock.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;

@Configuration
@EnableReactiveMethodSecurity // enable @PreAuthorize on reactive controllers/services
public class MethodSecurityConfig { }
