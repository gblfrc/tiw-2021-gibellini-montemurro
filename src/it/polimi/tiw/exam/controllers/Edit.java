package it.polimi.tiw.exam.controllers;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.exam.dao.AppealDAO;
import it.polimi.tiw.exam.dao.GradeDAO;
import it.polimi.tiw.exam.objects.Appeal;
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

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		int appealId;
		int studentId;
		String gradeValue;
		ErrorMsg error = (ErrorMsg) request.getAttribute("error");
		RequestDispatcher rd = request.getRequestDispatcher("GetCourses");
		
		if (error != null) {
			rd.forward(request, response);
			return;
		}
		
		try {
			appealId=Integer.parseInt(request.getParameter("appealId"));
			studentId=Integer.parseInt(request.getParameter("studentId"));
			gradeValue=request.getParameter("gradeValue");
		}catch(NumberFormatException e) {
			error = new ErrorMsg(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
			request.setAttribute("error", error);
			rd.forward(request, response);
			return;
		}
		
		AppealDAO appealDAO = new AppealDAO(connection);
		Appeal appeal;
		try {
			appeal = appealDAO.getAppealById(appealId);
			if (appeal == null)
				throw new Exception();
		} catch (Exception e) {
			error = new ErrorMsg(HttpServletResponse.SC_NOT_FOUND, "Appeal not found");
			request.setAttribute("error", error);
			rd.forward(request, response);
			return;
		}
		
		try {
			if(!appealDAO.hasAppeal(appealId, user.getPersonId(), "Professor")) {
				throw new InvalidParameterException();
			}
		}catch(Exception e) {
			error = new ErrorMsg(HttpServletResponse.SC_BAD_REQUEST,  "Denied access to edit grades");
			request.setAttribute("error", error);
			rd.forward(request, response);
			return;
		}
		
		GradeDAO gradeDAO= new GradeDAO(connection);
		Grade grade;
		try {
			grade=gradeDAO.getResultByAppealAndStudent(appealId,studentId);
			if(grade==null) throw new Exception();
		}catch(Exception e) {
			error = new ErrorMsg(HttpServletResponse.SC_BAD_REQUEST,"Nonexistent student");
			request.setAttribute("error", error);
			rd.forward(request, response);
			return;
		}
		
		try {
			if(!grade.getState().equalsIgnoreCase("entered")&&!grade.getState().equalsIgnoreCase("not entered")) throw new Exception();
		}catch(Exception e) {
			error = new ErrorMsg(HttpServletResponse.SC_BAD_REQUEST,"Uneditable grade");
			request.setAttribute("error", error);
			rd.forward(request, response);
			return;
		}
		
		try {
			gradeDAO.enterGrade(appealId,studentId,gradeValue);
		} catch (SQLException e) {
			error = new ErrorMsg(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"An accidental error occurred while entering grades");
			request.setAttribute("error", error);
			rd.forward(request, response);
			return;
		}
		response.sendRedirect("GetSubscribers?appeal=" + appealId);
	}
	
}
