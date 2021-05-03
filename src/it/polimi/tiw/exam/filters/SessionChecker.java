package it.polimi.tiw.exam.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class SessionChecker {
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		//System.out.print("Admin filter executing ..\n");
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		String loginpath = req.getServletContext().getContextPath() + "/Login.html";
		// check if the client is logged
		HttpSession s = req.getSession();
		if (s.isNew() || s.getAttribute("user") == null) {
			res.sendRedirect(loginpath);
			return;
		}		
		// pass the request along the filter chain
		chain.doFilter(request, response);
	}
	
}
