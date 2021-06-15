package it.polimi.tiw.exam.controllers;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.sql.Connection;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import it.polimi.tiw.exam.dao.AppealDAO;
import it.polimi.tiw.exam.dao.GradeDAO;
import it.polimi.tiw.exam.objects.Appeal;
import it.polimi.tiw.exam.objects.ErrorMsg;
import it.polimi.tiw.exam.objects.Grade;
import it.polimi.tiw.exam.objects.User;
import it.polimi.tiw.exam.utils.ConnectionHandler;
import it.polimi.tiw.exam.utils.TemplateEngineHandler;

/**
 * Servlet implementation class GetModify
 */
@WebServlet("/GetModify")
public class GetModify extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
		ServletContext servletContext = getServletContext();
		templateEngine = TemplateEngineHandler.getEngine(servletContext);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		GradeDAO gradeDAO = new GradeDAO(connection);
		Grade grade = null;
		ErrorMsg error = (ErrorMsg) request.getAttribute("error");
		
		// forward to GetCourses if an error has already occurred
		RequestDispatcher rd = request.getRequestDispatcher("GetCourses");
		if (error != null) {
			System.out.println("ERRORE");
			rd.forward(request, response);
			return;
		}
		
		AppealDAO appealDAO = new AppealDAO(connection);
		int appealId;
		try {
			appealId = Integer.parseInt(request.getParameter("appealId"));
		} catch (Exception e) {
			error = new ErrorMsg(HttpServletResponse.SC_BAD_REQUEST, "Illegal appeal request");
			request.setAttribute("error", error);
			rd.forward(request, response);
			return;
		}
		
		Appeal appeal;
		try {
			appeal = appealDAO.getAppealById(appealId);  //Can throw SQLException
			if(appeal==null)throw new Exception();
		}catch(Exception e) {
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
			error = new ErrorMsg(HttpServletResponse.SC_BAD_REQUEST, "Denied access to selected course");
			request.setAttribute("error", error);
			rd.forward(request, response);
			return;
		}
	
		int studentId;
		
		try {
			studentId = Integer.parseInt(request.getParameter("studentId"));
		} catch (Exception e) {
			error = new ErrorMsg(HttpServletResponse.SC_BAD_REQUEST, "Illegal student request");
			request.setAttribute("error", error);
			rd.forward(request, response);
			return;
		}
		
		try {
			if (!appealDAO.hasAppeal(appealId, studentId, "Student")) {
				throw new Exception();
			}
		}catch(Exception e) {
			error = new ErrorMsg(HttpServletResponse.SC_BAD_REQUEST, "Unavailable student");
			request.setAttribute("error", error);
			rd.forward(request, response);
			return;
		}

		try {
			grade = gradeDAO.getResultByAppealAndStudent(appealId, studentId);
			if (!grade.getState().equalsIgnoreCase("entered")&&!grade.getState().equalsIgnoreCase("not entered")) {
				throw new Exception();
			}
		} catch (Exception e) {
			error = new ErrorMsg(HttpServletResponse.SC_BAD_REQUEST, "Grade has already been published");
			request.setAttribute("error", error);
			rd.forward(request, response);
			return;
		}

		String path = "/WEB-INF/Modify.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("grade", grade);
		ctx.setVariable("appeal", appeal);
		templateEngine.process(path, ctx, response.getWriter());
	}
}
