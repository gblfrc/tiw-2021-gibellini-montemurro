package it.polimi.tiw.riaControllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import it.polimi.tiw.exam.dao.CourseDAO;
import it.polimi.tiw.exam.objects.Course;
import it.polimi.tiw.exam.objects.User;
import it.polimi.tiw.exam.utils.ConnectionHandler;

@WebServlet("/GetCoursesRIA")
public class GetCoursesRIA extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Connection connection=null;

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession();
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

		String json = new Gson().toJson(courses);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}

	public void destroy() {
		try {
		ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {};
	}

}
