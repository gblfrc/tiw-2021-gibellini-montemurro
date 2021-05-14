package it.polimi.tiw.exam.controllers;

import java.io.IOException;
import java.security.InvalidParameterException;
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


@WebServlet("/GetResult")
public class GetResult extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine templateEngine;

    public void init() throws ServletException{
    	ServletContext servletContext = getServletContext();
    	connection = ConnectionHandler.getConnection(servletContext);
    	templateEngine = TemplateEngineHandler.getEngine(servletContext);
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		User user = (User)session.getAttribute("user");

		//control on student's rights to access the appeal
		Integer appId = null;
		try {
			AppealDAO appealDAO= new AppealDAO(connection);
			appId = Integer.parseInt(request.getParameter("appeal"));
			if(!appealDAO.hasAppeal(appId, user.getPersonId(), /*courseId*/1, "Student")) {
				throw new InvalidParameterException();
			}
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unavailable appeal");
			return;
		}
		
		//main section: obtaining the student's grade (and information about the appeal)
		GradeDAO gradeDao = new GradeDAO(connection);
		Grade grade = null;
		try {
			grade = gradeDao.getResultByAppealAndStudent(appId, user.getPersonId());
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "It seems this student never subscribed to the requested exam");
			return;
		}
		AppealDAO appealDao = new AppealDAO(connection);
		Appeal appeal = new Appeal();
		try {
			appeal = appealDao.getAppealById(appId);
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Impossible to find the requested appeal");
			return;
		}

		String path = "/WEB-INF/Result.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("grade", grade);
		ctx.setVariable("appeal", appeal);
		templateEngine.process(path, ctx, response.getWriter());
	}

	/*
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	*/
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {}
	}

}
