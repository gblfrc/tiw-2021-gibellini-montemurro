package it.polimi.tiw.exam.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.exam.dao.GradeDAO;
import it.polimi.tiw.exam.dao.SecurityDAO;
import it.polimi.tiw.exam.objects.ErrorMsg;
import it.polimi.tiw.exam.objects.Grade;
import it.polimi.tiw.exam.objects.User;
import it.polimi.tiw.exam.utils.ConnectionHandler;

/**
 * Servlet implementation class Edit
 */
@WebServlet("/Edit")
public class Edit extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		int appId;
		int studentId;
		String gradeValue;
		ErrorMsg error = (ErrorMsg) request.getAttribute("error");
		// forward to GetSubscribers if an error has already occurred
		RequestDispatcher rd = request.getRequestDispatcher("GetSubscribers");

		// controls on request parameters
		try {
			appId = Integer.parseInt(request.getParameter("appeal"));
			studentId = Integer.parseInt(request.getParameter("studentId"));
			gradeValue = request.getParameter("gradeValue");
			// control on gradeValue validity
			if (!gradeValue.equalsIgnoreCase("absent") && !gradeValue.equalsIgnoreCase("failed")
					&& !gradeValue.equalsIgnoreCase("recalled") && !gradeValue.equalsIgnoreCase("18")
					&& !gradeValue.equalsIgnoreCase("19") && !gradeValue.equalsIgnoreCase("20")
					&& !gradeValue.equalsIgnoreCase("21") && !gradeValue.equalsIgnoreCase("22")
					&& !gradeValue.equalsIgnoreCase("23") && !gradeValue.equalsIgnoreCase("24")
					&& !gradeValue.equalsIgnoreCase("25") && !gradeValue.equalsIgnoreCase("26")
					&& !gradeValue.equalsIgnoreCase("27") && !gradeValue.equalsIgnoreCase("28")
					&& !gradeValue.equalsIgnoreCase("29") && !gradeValue.equalsIgnoreCase("30")
					&& !gradeValue.equalsIgnoreCase("30 with merit"))
				throw new Exception();
		} catch (Exception e) {
			error = new ErrorMsg(HttpServletResponse.SC_BAD_REQUEST, "Illegal request");
			request.setAttribute("error", error);
			rd.forward(request, response);
			return;
		}

		GradeDAO gradeDAO = new GradeDAO(connection);
		Grade grade;

		// retrieve a grade
		try {
			grade = gradeDAO.getResultByAppealAndStudent(appId, studentId);
			if (grade == null)
				throw new Exception();
		} catch (Exception e) {
			error = new ErrorMsg(HttpServletResponse.SC_BAD_REQUEST, "Nonexistent student");
			request.setAttribute("error", error);
			rd.forward(request, response);
			return;
		}

		// control on state: if it's neither 'not entered' nor 'entered', won't edit
		// grade
		try {
			if (!grade.getState().equalsIgnoreCase("entered") && !grade.getState().equalsIgnoreCase("not entered"))
				throw new Exception();
		} catch (Exception e) {
			error = new ErrorMsg(HttpServletResponse.SC_BAD_REQUEST, "Uneditable grade");
			request.setAttribute("error", error);
			rd.forward(request, response);
			return;
		}

		// security: get last-visited student
		SecurityDAO secDAO = new SecurityDAO(connection);
		try {
			if (secDAO.getLastStudent(user.getPersonId()) != studentId)
				throw new Exception();
		} catch (Exception e) {
			error = new ErrorMsg(HttpServletResponse.SC_BAD_REQUEST, "Access denied for security reasons");
			request.setAttribute("error", error);
			rd.forward(request, response);
			return;
		}

		// enter grade
		try {
			gradeDAO.enterGrade(appId, studentId, gradeValue);
		} catch (SQLException e) {
			error = new ErrorMsg(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"An accidental error occurred while entering grades");
			request.setAttribute("error", error);
			rd.forward(request, response);
			return;
		}
		response.sendRedirect("GetSubscribers?appeal=" + appId);
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
