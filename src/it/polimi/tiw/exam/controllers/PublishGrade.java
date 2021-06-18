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

import it.polimi.tiw.exam.dao.GradeDAO;
import it.polimi.tiw.exam.objects.ErrorMsg;
import it.polimi.tiw.exam.utils.ConnectionHandler;

@WebServlet("/PublishGrade")
public class PublishGrade extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		ErrorMsg error = null;
		RequestDispatcher rd = request.getRequestDispatcher("GetSubscribers");
		Integer appId = null;

		// control on "appeal" request parameter legitimacy
		try {
			appId = Integer.parseInt(request.getParameter("appeal"));
		} catch (Exception e) {
			error = new ErrorMsg(HttpServletResponse.SC_BAD_REQUEST, "Illegal appeal request");
			request.setAttribute("error", error);
			rd.forward(request, response);
			return;
		}

		// try to publish grades
		GradeDAO gradeDAO = new GradeDAO(connection);
		try {
			gradeDAO.publishGrade(appId);
		} catch (SQLException e) {
			error = new ErrorMsg(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"An accidental error occurred while retrieving reports");
			request.setAttribute("error", error);
			rd = request.getRequestDispatcher("GetSubscribers");
			rd.forward(request, response);
			return;
		}

		// everything was alright; go back to subscribers page
		response.sendRedirect("GetSubscribers?appeal=" + appId);
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
		}
		;
	}
}
