package it.polimi.tiw.exam.riaControllers;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import it.polimi.tiw.exam.dao.AppealDAO;
import it.polimi.tiw.exam.dao.GradeDAO;
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

		AppealDAO appealDAO = new AppealDAO(connection);
		int appId;
		try {
			appId = Integer.parseInt(request.getParameter("appeal"));
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Illegal appeal request");
			return;
		}

		Appeal appeal;
		try {
			appeal = appealDAO.getAppealById(appId);  //Can throw SQLException
			if(appeal==null)throw new Exception();
		}catch(Exception e) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.getWriter().println("Appeal not found");
			return;
		}
		
		try {
			if(!appealDAO.hasAppeal(appId, user.getPersonId(), "Professor")) {
				throw new InvalidParameterException();
			}
		}catch(Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Denied access to selected course");
			return;
		}
		
		int studentId;
		try {
			studentId = Integer.parseInt(request.getParameter("studentId"));
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Illegal student request");
			return;
		}
		
		try {
			if (!appealDAO.hasAppeal(appId, studentId, "Student")) {
				throw new Exception();
			}
		}catch(Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Unavailable student");
			return;
		}
		
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
		
		//may want to update usage of appeal object (may be an unnecessary object)
		String json = new Gson().toJson(grade);
		String json1 = new Gson().toJson(appeal);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
		response.getWriter().write(json1);

	}
}
