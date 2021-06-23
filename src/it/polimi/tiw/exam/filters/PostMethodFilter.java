package it.polimi.tiw.exam.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.exam.objects.ErrorMsg;


public class PostMethodFilter implements Filter{
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		System.out.print("Checking method ...\n");
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		// check if the method is allowed
		ErrorMsg error = (ErrorMsg) request.getAttribute("error");
		if(req.getMethod().equalsIgnoreCase("GET")){
			RequestDispatcher rd = req.getRequestDispatcher("GetCourses");
			error = new ErrorMsg(HttpServletResponse.SC_NOT_FOUND,  "Invalid method");
			request.setAttribute("error", error);
			rd.forward(req, res);
			return;
		}
		// pass the request along the filter chain
		chain.doFilter(request, response);
	}
}
