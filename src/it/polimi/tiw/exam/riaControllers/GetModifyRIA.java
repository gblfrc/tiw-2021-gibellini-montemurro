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
		User user = new User(3, "Professor"); 
				//(User) session.getAttribute("user");
		GradeDAO gradeDAO = new GradeDAO(connection);
		Grade grade = null;

		AppealDAO appealDAO = new AppealDAO(connection);
		int appealId;
		try {
			appealId = Integer.parseInt(request.getParameter("appealId"));
			if (!appealDAO.hasAppeal(appealId, user.getPersonId(), "Professor")) {
				throw new Exception();
			}
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unavailable appeal");
			return;
		}

		int studentId;
		try {
			studentId = Integer.parseInt(request.getParameter("studentId"));
			
			if (!appealDAO.hasAppeal(appealId, studentId, "Student")) {
				throw new Exception();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unavailable student ");
			return;
		}

		try {
			grade = gradeDAO.getResultByAppealAndStudent(appealId, studentId);
			if (!grade.getState().equalsIgnoreCase("entered")&&!grade.getState().equalsIgnoreCase("not entered")) {
				throw new Exception();
			}
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Grade has already been published");
			return;
		}

		Appeal appeal = null;
		try {
			appeal = appealDAO.getAppealById(appealId);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to find courses");
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
