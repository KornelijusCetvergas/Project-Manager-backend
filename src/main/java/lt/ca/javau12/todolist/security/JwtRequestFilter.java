package lt.ca.javau12.todolist.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lt.ca.javau12.todolist.services.MyUserDetailsService;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

	private JwtUtils jwtUtils;
	private MyUserDetailsService userDetailsService;
	
	public JwtRequestFilter(JwtUtils jwtUtils, MyUserDetailsService userDetailsService) {
		this.jwtUtils = jwtUtils;
		this.userDetailsService = userDetailsService;
	}
	
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		final String authHeader = request.getHeader("Authorization");
        
		
		String jwt = null, username = null;
        
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            username = jwtUtils.extractUsername(jwt);
        } 
		
        if (username != null 
        		&& SecurityContextHolder
        			.getContext().getAuthentication() == null) {
        	
        	UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
        	
        	if (jwtUtils.validateToken(jwt, username)) {
        		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
        	}
        	
        }
        
        filterChain.doFilter(request, response);
	}
	
}
