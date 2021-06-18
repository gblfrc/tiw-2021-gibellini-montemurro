package it.polimi.tiw.exam.filters;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.exam.dao.AppealDAO;
import it.polimi.tiw.exam.dao.SecurityDAO;
import it.polimi.tiw.exam.objects.Appeal;
import it.polimi.tiw.exam.objects.ErrorMsg;
import it.polimi.tiw.exam.objects.User;
import it.polimi.tiw.exam.utils.ConnectionHandler;


public class AppealChecker implements Filter{	
	private Connection connection;
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		System.out.print("Checking appeal's existence ...\n");
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		ErrorMsg error = (ErrorMsg) request.getAttribute("error");
		// check if the logged user is a professor
		RequestDispatcher rd = req.getRequestDispatcher("GetAppeal");
		
		int appId;
		try {
			appId = Integer.parseInt(req.getParameter("appeal"));
		} catch (Exception e) {
			error = new ErrorMsg(HttpServletResponse.SC_BAD_REQUEST,  "Illegal appeal request");
			request.setAttribute("error", error);
			rd.forward(req, res);
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
			error = new ErrorMsg(HttpServletResponse.SC_NOT_FOUND,  "Appeal not found");
			request.setAttribute("error", error);
			rd.forward(req, res);
			return;
		}
		
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
			error = new ErrorMsg(HttpServletResponse.SC_BAD_REQUEST,  "Access denied to selected appeal");
			request.setAttribute("error", error);
			rd.forward(request, response);
			return;
		}
		
		SecurityDAO secDAO=new SecurityDAO(connection);
		try {
			if(secDAO.getLastAppeal(user.getPersonId())!=appId) throw new Exception();
		}catch(Exception e) {
			error = new ErrorMsg(HttpServletResponse.SC_BAD_REQUEST, "Access denied for security reasons");
			request.setAttribute("error", error);
			rd.forward(request, response);
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
