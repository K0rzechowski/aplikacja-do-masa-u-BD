package emino.backend.config;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter{

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String jwtToken = null;

        try {
            // First, try to get token from cookie
            if (request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if ("jwtToken".equals(cookie.getName()) ||"jwt-token".equals(cookie.getName())) {
                        jwtToken = cookie.getValue();
                        break;
                    }
                }
            }

            // Fallback: check Authorization header (for API calls that still use Bearer token)
            if (jwtToken == null) {
                final String authHeader = request.getHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    jwtToken = authHeader.substring(7);
                }
            }

            // If no token found, continue without authentication
            if (jwtToken == null || jwtToken.isEmpty()) {
                filterChain.doFilter(request, response);
                return;
            }
        
        
            final String userEmail = jwtService.extractUserEmail(jwtToken);
            
            // if user is not authenticated
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // get user from database
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                if (jwtService.isTokenValid(jwtToken, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Token is invalid, continue without authentication
            e.printStackTrace();
            SecurityContextHolder.clearContext();
        }
        
        filterChain.doFilter(request, response);
    }

    // @Override
    // protected void doFilterInternal(@NonNull HttpServletRequest request, 
    //                               @NonNull HttpServletResponse response, 
    //                               @NonNull FilterChain filterChain) throws ServletException, IOException {
        
    //     String jwtToken = null;
    //     String requestURI = request.getRequestURI();
        
    //     System.out.println("=== JWT Filter for: " + requestURI + " ===");
        
    //     try {
    //         // Get token from cookie first
    //         if (request.getCookies() != null) {
    //             System.out.println("Checking cookies... Found " + request.getCookies().length + " cookies");
    //             for (Cookie cookie : request.getCookies()) {
    //                 System.out.println("Cookie: " + cookie.getName() + " = " + cookie.getValue().substring(0, Math.min(20, cookie.getValue().length())) + "...");
                    
    //                 if ("jwtToken".equals(cookie.getName()) || "jwt-token".equals(cookie.getName())) {
    //                     jwtToken = cookie.getValue();
    //                     System.out.println("Found JWT token in cookie: " + cookie.getName());
    //                     break;
    //                 }
    //             }
    //         } else {
    //             System.out.println("No cookies found in request");
    //         }
            
    //         // Fallback to Authorization header
    //         if (jwtToken == null) {
    //             final String authHeader = request.getHeader("Authorization");
    //             if (authHeader != null && authHeader.startsWith("Bearer ")) {
    //                 jwtToken = authHeader.substring(7);
    //                 System.out.println("Found JWT token in Authorization header!");
    //             }
    //         }
            
    //         if (jwtToken == null || jwtToken.isEmpty()) {
    //             System.out.println("No JWT token found, continuing as anonymous");
    //             filterChain.doFilter(request, response);
    //             return;
    //         }
            
    //         System.out.println("Processing JWT token...");
    //         final String userEmail = jwtService.extractUserEmail(jwtToken);
    //         System.out.println("Extracted email: " + userEmail);
            
    //         if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
    //             System.out.println("Loading user details for: " + userEmail);
    //             UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
    //             System.out.println("Loaded user: " + userDetails.getUsername() + ", authorities: " + userDetails.getAuthorities());
                
    //             if (jwtService.isTokenValid(jwtToken, userDetails)) {
    //                 System.out.println("Token is valid, setting authentication...");
    //                 UsernamePasswordAuthenticationToken authToken = 
    //                     new UsernamePasswordAuthenticationToken(
    //                         userDetails, null, userDetails.getAuthorities());
    //                 authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    //                 SecurityContextHolder.getContext().setAuthentication(authToken);
    //                 System.out.println("✓ Authentication set successfully for: " + userEmail);
    //             } else {
    //                 System.out.println("✗ Token validation failed");
    //             }
    //         } else if (userEmail == null) {
    //             System.out.println("User email is null from token");
    //         } else {
    //             System.out.println("Authentication already exists in context");
    //         }
    //     } catch (Exception e) {
    //         System.err.println("Filter exception: " + e.getClass().getName() + " - " + e.getMessage());
    //         e.printStackTrace();
    //         SecurityContextHolder.clearContext();
    //     }
        
    //     System.out.println("=== End JWT Filter ===");
    //     filterChain.doFilter(request, response);
    // }
}