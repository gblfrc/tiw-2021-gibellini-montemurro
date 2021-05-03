package it.polimi.tiw.exam.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

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
	private Connection connection=null;
	private TemplateEngine templateEngine;
	
	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
		ServletContext servletContext = getServletContext();
		templateEngine = TemplateEngineHandler.getEngine(servletContext);
	} 
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		GradeDAO gradeDAO= new GradeDAO(connection);
		Grade grade=null;
		try {
			int appealId=Integer.parseInt(request.getParameter("appealId"));
			int studentId=Integer.parseInt(request.getParameter("studentId"));
			grade = gradeDAO.getResultByAppealAndStudent(appealId,studentId);
			
		} catch (NumberFormatException| NullPointerException |SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
			return;
		} 
		
		AppealDAO appealDAO= new AppealDAO(connection);
		Appeal appeal=null;
		try {
			int appealId=Integer.parseInt(request.getParameter("appealId"));
			appeal=appealDAO.getAppealById(appealId);
		}catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to find courses");
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
