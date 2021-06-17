package it.polimi.tiw.exam.filters;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.exam.dao.AppealDAO;
import it.polimi.tiw.exam.dao.SecurityDAO;
import it.polimi.tiw.exam.objects.Appeal;
import it.polimi.tiw.exam.objects.User;
import it.polimi.tiw.exam.utils.ConnectionHandler;

public class AppealCheckerRIA implements Filter{	
	private Connection connection;
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		System.out.print("Checking appeal's existence ...\n");
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		// control on "appeal" request parameter legitimacy
		int appId;
		try {
			appId = Integer.parseInt(req.getParameter("appeal"));
		} catch (Exception e) {
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			res.getWriter().println("Illegal appeal request");
			return;
		}
		
		// check existence of selected appeal
		Appeal appeal = null;
		connection = ConnectionHandler.getConnection(req.getSession().getServletContext());
		AppealDAO adao = new AppealDAO(connection);
		try {
			appeal = adao.getAppealById(appId);
			if (appeal == null)
				throw new Exception();
		} catch (Exception e) {
			res.setStatus(HttpServletResponse.SC_NOT_FOUND);
			res.getWriter().println("Appeal not found");
			return;
		}
		
		// control on user's rights to access the appeal
		User user=(User)req.getSession().getAttribute("user");
		try {
			if(user.getAccessRights().equalsIgnoreCase("Professor")) {
				if(!adao.hasAppeal(appId, user.getPersonId(), "Professor")) {
					throw new Exception();
				}
			}
			if(user.getAccessRights().equalsIgnoreCase("Student")) {
				if(!adao.hasAppeal(appId, user.getPersonId(), "Student")) {
					throw new Exception();
				}
			}
		}catch(Exception e) {
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			res.getWriter().println("Access denied to selected appeal");
			return;
		}
		
		//security: get last-visited appeal
		SecurityDAO secDAO=new SecurityDAO(connection);
		try {
			if(secDAO.getLastAppeal(user.getPersonId())!=appId) throw new Exception();
		}catch(Exception e) {
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			res.getWriter().println("Access denied for security reasons");
			return;
		}
		
		// pass the request along the filter chain
		chain.doFilter(request, response);
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
		}
		;
	}
}
