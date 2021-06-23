package it.polimi.tiw.exam.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PostMethodFilterRIA implements Filter{
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		System.out.print("Checking method ...\n");
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		// check if the method is allowed
		if(req.getMethod().equalsIgnoreCase("GET")){
			res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			res.getWriter().println("Invalid request");
			return;
		}
		// pass the request along the filter chain
		chain.doFilter(request, response);
	}
}
