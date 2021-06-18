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
import it.polimi.tiw.exam.objects.ErrorMsg;
import it.polimi.tiw.exam.objects.Grade;
import it.polimi.tiw.exam.objects.User;
import it.polimi.tiw.exam.utils.ConnectionHandler;

@WebServlet("/RefuseGrade")
public class RefuseGrade extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Connection connection;

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		GradeDAO gradeDao = new GradeDAO(connection);

		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		ErrorMsg error = null;
		RequestDispatcher rd = request.getRequestDispatcher("GetAppeal");

		// control on "appeal" request parameter legitimacy
		int appId;
		try {
			appId = Integer.parseInt(request.getParameter("appeal"));
		} catch (Exception e) {
			error = new ErrorMsg(HttpServletResponse.SC_BAD_REQUEST, "Illegal appeal request");
			request.setAttribute("error", error);
			rd.forward(request, response);
			return;
		}

		// check grade is refusable (grade is neither refused nor recorded)
		try {
			Grade grade = gradeDao.getResultByAppealAndStudent(appId, user.getPersonId());
			if (grade.getState().equalsIgnoreCase("Refused") || grade.getState().equalsIgnoreCase("Recorded"))
				throw new Exception("Grade has already been " + grade.getState().toLowerCase());
		} catch (Exception e) {
			error = new ErrorMsg(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
			request.setAttribute("error", error);
			rd.forward(request, response);
			return;
		}

		// refuse grade
		try {
			gradeDao.refuseGrade(appId, user.getPersonId());
		} catch (SQLException e) {
			error = new ErrorMsg(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"An accidental error occurred while refusing result");
			request.setAttribute("error", error);
			rd.forward(request, response);
			return;
		}

		response.sendRedirect("GetResult?appeal=" + appId);
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
		}
	}

}
