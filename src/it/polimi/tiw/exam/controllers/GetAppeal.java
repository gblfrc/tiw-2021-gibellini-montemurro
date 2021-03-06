package it.polimi.tiw.exam.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import it.polimi.tiw.exam.dao.AppealDAO;
import it.polimi.tiw.exam.dao.CourseDAO;
import it.polimi.tiw.exam.dao.SecurityDAO;
import it.polimi.tiw.exam.objects.Course;
import it.polimi.tiw.exam.objects.ErrorMsg;
import it.polimi.tiw.exam.objects.User;
import it.polimi.tiw.exam.objects.Appeal;
import it.polimi.tiw.exam.utils.ConnectionHandler;
import it.polimi.tiw.exam.utils.TemplateEngineHandler;

@WebServlet("/GetAppeal")
public class GetAppeal extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
		ServletContext servletContext = getServletContext();
		templateEngine = TemplateEngineHandler.getEngine(servletContext);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		ErrorMsg error = (ErrorMsg) request.getAttribute("error");
		RequestDispatcher rd = request.getRequestDispatcher("GetCourses");
		Integer cId = null;
		SecurityDAO secDAO=new SecurityDAO(connection);
		
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		if (error != null) {
			try {
				cId=secDAO.getLastCourse(user.getPersonId());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		else {
			// check course parameter validity
			try {
				cId = Integer.parseInt(request.getParameter("courseId"));
			} catch (NumberFormatException | NullPointerException e) {
				e.printStackTrace();
				error = new ErrorMsg(HttpServletResponse.SC_BAD_REQUEST, "Illegal course request");
				request.setAttribute("error", error);
				rd.forward(request, response);
				return;
			}
		}
		// check requested course existence
		Course course = null;
		CourseDAO coursesDAO = new CourseDAO(connection);
		try {
			course = coursesDAO.getCourseById(cId);
			if (course == null)
				throw new Exception();
		} catch (Exception e) {
			error = new ErrorMsg(HttpServletResponse.SC_NOT_FOUND, "Course not found");
			request.setAttribute("error", error);
			rd.forward(request, response);
			return;
		}

		// check user rights to access selected course	
		try {
			if (coursesDAO.hasCourse(cId, user.getPersonId(), user.getAccessRights()) == false)
				throw new Exception();
		} catch (Exception e) {
			error = new ErrorMsg(HttpServletResponse.SC_BAD_REQUEST, "Denied access to selected course");
			request.setAttribute("error", error);
			rd.forward(request, response);
			return;
		}

		// fetch appeals
		List<Appeal> appeals = new LinkedList<>();
		try {
			AppealDAO appealDAO = new AppealDAO(connection);
			appeals = appealDAO.getAppealsByCourse(course.getCourseId(), user.getPersonId(), user.getAccessRights());
		} catch (SQLException e) {
			error = new ErrorMsg(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"An accidental error occurred while retrieving appeals");
			request.setAttribute("error", error);
			rd.forward(request, response);
			return;
		}	

		//security: setting last visited course
		try {
			secDAO.setLastCourse(user.getPersonId(), cId);
		}catch(SQLException e){
			error = new ErrorMsg(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"An accidental error occurred while updating security settings");
			request.setAttribute("error", error);
			rd.forward(request, response);
			return;
		}
		
		// give access to actual appeals page
		String path = "/WEB-INF/Appeal.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("appeals", appeals);
		ctx.setVariable("error", error); 
		templateEngine.process(path, ctx, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ErrorMsg error=(ErrorMsg)request.getAttribute("error");
		if(error==null) {
			error = new ErrorMsg(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Illegal request");
			request.setAttribute("error", error);
		}
		doGet(request, response);
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}