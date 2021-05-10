package it.polimi.tiw.exam.controllers;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import it.polimi.tiw.exam.dao.CourseDAO;
import it.polimi.tiw.exam.dao.GradeDAO;
import it.polimi.tiw.exam.objects.Course;
import it.polimi.tiw.exam.objects.Grade;
import it.polimi.tiw.exam.objects.User;
import it.polimi.tiw.exam.utils.ConnectionHandler;
import it.polimi.tiw.exam.utils.TemplateEngineHandler;

@WebServlet("/GetSubscribers")
public class GetSubscribers extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		connection = ConnectionHandler.getConnection(servletContext);
		templateEngine = TemplateEngineHandler.getEngine(servletContext);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		Boolean changeOrder=false;
		String field=null;
		try {
			List<String> params= Collections.list(request.getParameterNames());
			if(params.contains("changeOrder")&&
			   !request.getParameter("changeOrder").equalsIgnoreCase("true")&&
			   !request.getParameter("changeOrder").equalsIgnoreCase("false")) {
					throw new InvalidParameterException("Unacceptable request");
				}
			params.remove("changeOrder");
			
			if(params.contains("field")) {
				field=request.getParameter("field");
				if(!field.equalsIgnoreCase("studentId")&&!field.equalsIgnoreCase("surname")&&!field.equalsIgnoreCase("name")&&
			      !field.equalsIgnoreCase("email")&&!field.equalsIgnoreCase("degree_course")&&!field.equalsIgnoreCase("grade")&&
			      !field.equalsIgnoreCase("state")) {
					throw new InvalidParameterException("Unacceptable request");
				}
			}
			params.remove("field");
			
			params.remove("appeal");
			
			if(params.size()>0) throw new InvalidParameterException("Couldn't handle request");
			
			changeOrder=Boolean.parseBoolean(request.getParameter("changeOrder"));
			
			if((session.getAttribute(field+ "Order")==null||session.getAttribute(field+ "Order")=="DESC") && changeOrder) {
				session.setAttribute(field+ "Order", "ASC");
			}
			else if(session.getAttribute(field+ "Order")=="ASC" && changeOrder) {
				session.setAttribute(field+ "Order", "DESC");
			}
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
			return;
		}
		
		
		Integer appId = null;
		User user = (User) session.getAttribute("user");
		try {
			AppealDAO appealDAO= new AppealDAO(connection);
			appId = Integer.parseInt(request.getParameter("appeal"));
			if(!appealDAO.hasAppeal(appId, user.getPersonId(), /*courseId*/1, "Professor")) {
				throw new InvalidParameterException();
			}
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unavailable appeal");
			return;
		}
	
		
		GradeDAO gradeDAO= new GradeDAO(connection);
		List<Grade> grades= new ArrayList<Grade>();
		
		try {
			if(user.getAccessRights().equals("Professor")) {
				if(field==null || session.getAttribute(field+ "Order")==null) grades=gradeDAO.getGradesByAppealId(appId);
				else if (session.getAttribute(field+ "Order")=="ASC") grades=gradeDAO.getGradesByFieldAsc(appId,field);
				else if (session.getAttribute(field+ "Order")=="DESC") grades=gradeDAO.getGradesByFieldDesc(appId,field);
			}
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to find grades");
			return;
		}
		String path = "/WEB-INF/Subscribers.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("appId", appId);
		ctx.setVariable("grades", grades);
		templateEngine.process(path, ctx, response.getWriter());
		
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
		}
		;
	}
}
