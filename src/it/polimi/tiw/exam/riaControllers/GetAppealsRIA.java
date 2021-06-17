package it.polimi.tiw.exam.riaControllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.exam.dao.AppealDAO;
import it.polimi.tiw.exam.dao.CourseDAO;
import it.polimi.tiw.exam.dao.SecurityDAO;
import it.polimi.tiw.exam.objects.Course;
import it.polimi.tiw.exam.objects.User;
import it.polimi.tiw.exam.objects.Appeal;
import it.polimi.tiw.exam.utils.ConnectionHandler;

@WebServlet("/GetAppealsRIA")
@MultipartConfig
public class GetAppealsRIA extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// check course parameter validity
		Integer cId = null;
		try {
			cId = Integer.parseInt(request.getParameter("courseId"));
		} catch (NumberFormatException | NullPointerException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Illegal course request");
			return;
		}
		
		// check requested course existence
		Course course = null;
		CourseDAO coursesDAO = new CourseDAO(connection);
		try {
			course = coursesDAO.getCourseById(cId);
			if (course == null)
				throw new Exception();
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.getWriter().println("Course not found");
			return;
		}
		
		// check user rights to access selected course
		HttpSession session = request.getSession();
		User user = null;
		try {
			user = (User) session.getAttribute("user");
			if (coursesDAO.hasCourse(cId, user.getPersonId(), user.getAccessRights()) == false)
				throw new Exception();
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Denied access to selected course");
			return;
		}
		
		// fetch appeals
		List<Appeal> appeals = new LinkedList<>();
		try {
			AppealDAO appealDAO = new AppealDAO(connection);
			appeals = appealDAO.getAppealsByCourse(course.getCourseId(), user.getPersonId(), user.getAccessRights());
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("An accidental error occurred while retrieving appeals");
			return;
		}

		//security: setting last visited course
		SecurityDAO secDAO=new SecurityDAO(connection);
		try {
			secDAO.setLastCourse(user.getPersonId(), cId);
		}catch(SQLException e){
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("An accidental error occurred while updating security settings");
			return;
		}
		
		//build response object and send it
		Gson gson = new GsonBuilder().setDateFormat("yyyy/MM/dd").create();
		String json = gson.toJson(appeals);
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