package it.polimi.tiw.exam.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.exam.dao.GradeDAO;
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

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int appealId=Integer.parseInt(request.getParameter("appealId"));
		int studentId=Integer.parseInt(request.getParameter("studentId"));
		String gradeValue=request.getParameter("gradeValue");
		try {
			GradeDAO gradeDAO= new GradeDAO(connection);
			gradeDAO.enterGrade(appealId,studentId,gradeValue);
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
			return;
		}
		response.sendRedirect("GetSubscribers?appealId=" + appealId );
	}
	
}
