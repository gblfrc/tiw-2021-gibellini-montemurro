package it.polimi.tiw.exam.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.exam.utils.ConnectionHandler;
import it.polimi.tiw.exam.utils.TemplateEngineHandler;
import it.polimi.tiw.exam.objects.User;
import it.polimi.tiw.exam.objects.Course;
import it.polimi.tiw.exam.dao.CourseDAO;

/**
 * Servlet implementation class GetCourses
 */
@WebServlet("/getCourses")
public class GetCourses extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection=null;
	private TemplateEngine templateEngine;
	
	public GetCourses() {
        super();
    }
      
	public void init() throws ServletException {
		try {
			ServletContext servletContext = getServletContext();
			connection = ConnectionHandler.getConnection(getServletContext());
			templateEngine = TemplateEngineHandler.getEngine(servletContext);
		} catch (UnavailableException e) {
			throw new ServletException(e.getMessage());
		}
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String loginpath = getServletContext().getContextPath() + "/login.html";
		HttpSession session = request.getSession();
		
		if (session.isNew() || session.getAttribute("user") == null) {
			response.sendRedirect(loginpath);
			return;
		}
		
		User user = (User) session.getAttribute("user");
		CourseDAO courseDAO= new CourseDAO(connection);
		List<Course> courses= new ArrayList<Course>();
		
		try {
			if(user.getAccessRights().equals("Professor")) courses=courseDAO.getCoursesByProfessorId(user.getPersonId());
			else courses=courseDAO.getCoursesByStudentId(user.getPersonId());
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to find courses");
			return;
		}
		String path = "/WEB-INF/Home.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("courses", courses);
		templateEngine.process(path, ctx, response.getWriter());
		
	}
	
	public void destroy() {
		try {
		ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {};
	}

}