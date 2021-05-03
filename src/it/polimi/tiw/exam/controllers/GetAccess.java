package it.polimi.tiw.exam.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;

/*import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

/*
import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;
*/
import it.polimi.tiw.exam.objects.User;
import it.polimi.tiw.exam.utils.ConnectionHandler;
import it.polimi.tiw.exam.utils.TemplateEngineHandler;
import it.polimi.tiw.exam.dao.UserDAO;

@WebServlet("/GetAccess")
public class GetAccess extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	public void init() throws ServletException {
    	ServletContext servletContext = getServletContext();
    	connection = ConnectionHandler.getConnection(servletContext);
    	templateEngine = TemplateEngineHandler.getEngine(servletContext);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Integer personId = null;
		String password = null;
		

		try {
			personId = Integer.parseInt(request.getParameter("personId"));
			password = request.getParameter("password");
		} catch (Exception e) {
			response.sendRedirect(getServletContext().getContextPath() + "/Login.html");
			return;
		}

		if (password == null || password.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Not possible to check credentials");
			return;
		}

		UserDAO userDAO = new UserDAO(connection);
		User user = null;

		try {
			user = userDAO.getUser(personId, password);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Not Possible to access DB");
			return;
		}

		String path;
		if (user == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Unavailable user");
			return;
		} else {
			request.getSession().setAttribute("user", user);
			path = getServletContext().getContextPath() + "/GetCourses";
			response.sendRedirect(path);
		}

	}
	
	public void destroy() {
		try {
		ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {};
	}
}
