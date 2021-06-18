package it.polimi.tiw.exam.riaControllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
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

/**
 * Servlet implementation class GetModify
 */
@WebServlet("/GetModifyRIA")
public class GetModifyRIA extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		GradeDAO gradeDAO = new GradeDAO(connection);
		Grade grade = null;

		//get appealId parameter
		int appId;
		try {
			appId = Integer.parseInt(request.getParameter("appeal"));
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Illegal appeal request");
			return;
		}
		
		//fetch appeal object
		AppealDAO appealDAO = new AppealDAO(connection);
		Appeal appeal;
		try {
			appeal = appealDAO.getAppealById(appId);  //Can throw SQLException
			if(appeal==null)throw new Exception();
		}catch(Exception e) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.getWriter().println("Appeal not found");
			return;
		}

		//control on "studentId" request parameter legitimacy
		int studentId;
		try {
			studentId = Integer.parseInt(request.getParameter("studentId"));
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Illegal student request");
			return;
		}

		//control on availability of selected appeal for selected student
		try {
			if (!appealDAO.hasAppeal(appId, studentId, "Student")) {
				throw new Exception();
			}
		}catch(Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Unavailable student");
			return;
		}
		
		//retrieve a grade
		try {
			grade = gradeDAO.getResultByAppealAndStudent(appId, studentId);
			if (!grade.getState().equalsIgnoreCase("entered")&&!grade.getState().equalsIgnoreCase("not entered")) {
				throw new Exception();
			}
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Grade has already been published");
			return;
		}
		
		//set last-visited student
		SecurityDAO secDAO=new SecurityDAO(connection);
		try {
			secDAO.setLastStudent(user.getPersonId(), studentId);
		}catch(SQLException e){
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("An accidental error occurred while updating security settings");
			return;
		}
		
		// build response object and send it
		Gson gson = new GsonBuilder().setDateFormat("yyyy/MM/dd").create();
		String json = gson.toJson(grade);
		String json1 = gson.toJson(appeal);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
		response.getWriter().write(json1);

	}
}
