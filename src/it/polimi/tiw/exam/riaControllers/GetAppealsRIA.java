package it.polimi.tiw.exam.riaControllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import com.google.gson.Gson;

import it.polimi.tiw.exam.dao.AppealDAO;
import it.polimi.tiw.exam.dao.CourseDAO;
import it.polimi.tiw.exam.objects.Course;
import it.polimi.tiw.exam.objects.User;
import it.polimi.tiw.exam.objects.Appeal;
import it.polimi.tiw.exam.utils.ConnectionHandler;
import it.polimi.tiw.exam.utils.TemplateEngineHandler;


@WebServlet("/GetAppealsRIA") @MultipartConfig
public class GetAppealsRIA extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Integer cId = null;
		try {
			cId = Integer.parseInt(request.getParameter("courseId"));
		} catch (NumberFormatException| NullPointerException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Incorrect param values");
			return;
		}
		HttpSession session=request.getSession();
		
		CourseDAO coursesDAO = new CourseDAO(connection);
		User user=null;
		try {
			user=(User)session.getAttribute("user");
			if(coursesDAO.hasCourse(cId, user.getPersonId(), user.getAccessRights())==false) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("It's not a course of yours");
				return;
			}
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Illegal course request");
			return;
		}
		
		Course course = null;
		List<Appeal> appeals=new LinkedList<>();
		try {
			course = coursesDAO.getCourseById(cId);
			if (course == null) {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				response.getWriter().println("Course not found");
				return;
			}
			
			AppealDAO appealDAO = new AppealDAO(connection);
			appeals = appealDAO.getAppealsByCourse(course.getCourseId(),user.getPersonId(),user.getAccessRights());
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
		
		String json = new Gson().toJson(appeals);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}