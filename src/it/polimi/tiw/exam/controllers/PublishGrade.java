package it.polimi.tiw.exam.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import it.polimi.tiw.exam.dao.GradeDAO;
import it.polimi.tiw.exam.objects.Grade;
import it.polimi.tiw.exam.objects.User;
import it.polimi.tiw.exam.utils.ConnectionHandler;
import it.polimi.tiw.exam.utils.TemplateEngineHandler;

/**
 * Servlet implementation class PublishGrade
 */
@WebServlet("/PublishGrade")
public class PublishGrade extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;
   
    /**
     * @see HttpServlet#HttpServlet()
     */
	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
		ServletContext servletContext = getServletContext();
		templateEngine = TemplateEngineHandler.getEngine(servletContext);
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String loginpath = getServletContext().getContextPath() + "/Login.html";
		HttpSession session = request.getSession();
		
		if (session.isNew() || session.getAttribute("user") == null) {
			response.sendRedirect(loginpath);
			return;
		}
		
		Integer appId = null;
		try {
			appId = Integer.parseInt(request.getParameter("appeal"));
		} catch (NumberFormatException| NullPointerException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
			return;
		}
		User user = (User) session.getAttribute("user");
		GradeDAO gradeDAO= new GradeDAO(connection);
		
		try {
			if(user.getAccessRights().equals("Professor"))gradeDAO.publishGrade(appId);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to find grades");
			return;
		}
		
		response.sendRedirect("GetSubscribers?appealId=" + appId );
		
	}
	
	public void destroy() {
		try {
		ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {};
	}
}