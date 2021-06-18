package it.polimi.tiw.exam.riaControllers;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
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
import it.polimi.tiw.exam.dao.GradeDAO;
import it.polimi.tiw.exam.dao.SecurityDAO;
import it.polimi.tiw.exam.objects.Appeal;
import it.polimi.tiw.exam.objects.Grade;
import it.polimi.tiw.exam.objects.User;
import it.polimi.tiw.exam.utils.ConnectionHandler;

@WebServlet("/GetSubscribersRIA") @MultipartConfig
public class GetSubscribersRIA extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		
		//control on professor's rights to access the appeal
		Integer appId = null;
		User user = (User) session.getAttribute("user");
		try {
			appId = Integer.parseInt(request.getParameter("appeal"));
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Illegal appeal request");
			return;
		}
		
		AppealDAO appealDAO= new AppealDAO(connection);
		Appeal appeal = null;
		try {
			appeal = appealDAO.getAppealById(appId);  //Can throw SQLException
			if(appeal==null)throw new Exception();
		}catch(Exception e) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.getWriter().println("Appeal not found");
			return;
		}
		
		//security: get last-visited course
		SecurityDAO secDAO=new SecurityDAO(connection);
		try {
			if(secDAO.getLastCourse(user.getPersonId())!=appeal.getCourseId()) throw new Exception();
		}catch(Exception e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Access denied for security reasons");
			return;
		}
		
		try {
			if(!appealDAO.hasAppeal(appId, user.getPersonId(), "Professor")) {
				throw new InvalidParameterException();
			}
		}catch(Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println( "Denied access to selected course");
			return;
		}

		//get list of grades (in specific order)
		GradeDAO gradeDAO= new GradeDAO(connection);
		List<Grade> grades= new ArrayList<Grade>();
		
		try {
			if(user.getAccessRights().equals("Professor")) {
				grades=gradeDAO.getGradesByAppealId(appId);
			}
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("An accidental error occurred while retrieving grades");
			return;
		}
		
		//security: set last-visited appeal
		try {
			secDAO.setLastAppeal(user.getPersonId(), appId);
		}catch(SQLException e){
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("An accidental error occurred while updating security settings");
			return;
		}
		
		//build response object and send it
		Gson gson = new GsonBuilder().setDateFormat("yyyy/MM/dd").create();
		String json = gson.toJson(grades);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
		}
		;
	}
}
