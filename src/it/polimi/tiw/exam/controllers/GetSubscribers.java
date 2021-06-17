package it.polimi.tiw.exam.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import it.polimi.tiw.exam.dao.SecurityDAO;
import it.polimi.tiw.exam.objects.Appeal;
import it.polimi.tiw.exam.objects.ErrorMsg;
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
		Appeal appeal = null;
		ErrorMsg error = (ErrorMsg) request.getAttribute("error");
		boolean entered=false;
				
		// forward to GetCourses if an error has already occurred
		RequestDispatcher rd = request.getRequestDispatcher("GetCourses");
		if (error != null) {
			rd.forward(request, response);
			return;
		}
		
		//control on professor's rights to access the appeal
		Integer appId = null;
		User user = (User) session.getAttribute("user");
		AppealDAO appealDAO= new AppealDAO(connection);
		try {
			appId = Integer.parseInt(request.getParameter("appeal"));
			//may want to change appeal parameter name to appealId to make this servlet equal to the RIA one
		} catch (Exception e) {
			error = new ErrorMsg(HttpServletResponse.SC_BAD_REQUEST, "Illegal appeal request");
			request.setAttribute("error", error);
			rd.forward(request, response);
			return;
		}
		
		try {
			appeal = appealDAO.getAppealById(appId);  //Can throw SQLException
			if(appeal==null)throw new Exception();
		}catch(Exception e) {
			error = new ErrorMsg(HttpServletResponse.SC_NOT_FOUND, "Appeal not found");
			request.setAttribute("error", error);
			rd.forward(request, response);
			return;
		}
		
		try {
			if(!appealDAO.hasAppeal(appId, user.getPersonId(), "Professor")) {
				throw new Exception();
			}
		}catch(Exception e) {
			error = new ErrorMsg(HttpServletResponse.SC_BAD_REQUEST, "Denied access to selected course");
			request.setAttribute("error", error);
			rd.forward(request, response);
			return;
		}
		
		//security: get last-visited course
		SecurityDAO secDAO=new SecurityDAO(connection);
		try {
			if(secDAO.getLastCourse(user.getPersonId())!=appeal.getCourseId()) throw new Exception();
		}catch(Exception e) {
			error = new ErrorMsg(HttpServletResponse.SC_BAD_REQUEST, "Access denied for security reasons");
			request.setAttribute("error", error);
			rd.forward(request, response);
			return;
		}
		
		//controls on request parameters
		try {			
			List<String> params= Collections.list(request.getParameterNames());
			//checks "changeOrder" has legal values
			if(params.contains("changeOrder")&&
			   !request.getParameter("changeOrder").equalsIgnoreCase("true")&&
			   !request.getParameter("changeOrder").equalsIgnoreCase("false")) {
					entered=true;
				}
			//params.remove("changeOrder");
			//checks "field" has legal values 
			if(params.contains("field")) {
				field=request.getParameter("field");  //saves "field" request parameter in a variable
				if(!field.equalsIgnoreCase("studentId")&&!field.equalsIgnoreCase("surname")&&!field.equalsIgnoreCase("name")&&
			      !field.equalsIgnoreCase("email")&&!field.equalsIgnoreCase("degree_course")&&!field.equalsIgnoreCase("grade")&&
			      !field.equalsIgnoreCase("state")) {
						entered=true;
						field="studentId";
				}
			}
			
			if(entered==true) throw new Exception();
			//params.remove("field");
			
			//params.remove("appeal");
			//checks there aren't too many parameters in the request
			//if(params.size()>0) throw new InvalidParameterException("Couldn't handle request"); 
			
			changeOrder=Boolean.parseBoolean(request.getParameter("changeOrder")); 
			//if there is no "changeOrder" parameter, parseBoolean returns false
			
			//checks existence of the attribute related to "field" request parameter (and handles it)
		} catch (Exception e) {
			changeOrder=false;
			error = new ErrorMsg(HttpServletResponse.SC_BAD_REQUEST, "Illegal sorting request");	
		}
				
		if((session.getAttribute(field + "Order")==null||session.getAttribute(field+ "Order")=="DESC") && changeOrder) {
			session.setAttribute(field + "Order", "ASC");
		}
		else if(session.getAttribute(field + "Order")=="ASC" && changeOrder) {
			session.setAttribute(field + "Order", "DESC");
		}
	
		//get list of grades (in specific order)
		GradeDAO gradeDAO= new GradeDAO(connection);
		List<Grade> grades= new ArrayList<Grade>();

		try {
			//if(user.getAccessRights().equals("Professor")) {
				if(field==null || session.getAttribute(field+ "Order")==null) grades=gradeDAO.getGradesByAppealId(appId);
				else if (session.getAttribute(field + "Order")=="ASC") grades=gradeDAO.getGradesByFieldAsc(appId,field);
				else if (session.getAttribute(field + "Order")=="DESC") grades=gradeDAO.getGradesByFieldDesc(appId,field);
			//}
		} catch (SQLException e) {
			error = new ErrorMsg(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"An accidental error occurred while retrieving subscribers");
			request.setAttribute("error", error);
			rd.forward(request, response);
			return;
		}
		
		//security: set last-visited appeal
		try {
			secDAO.setLastAppeal(user.getPersonId(), appId);
		}catch(SQLException e){
			error = new ErrorMsg(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"An accidental error occurred while updating security settings");
			request.setAttribute("error", error);
			rd.forward(request, response);
			return;
		}
		
		String path = "/WEB-INF/Subscribers.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("appeal", appeal);
		ctx.setVariable("grades", grades);
		//ctx.setVariable("error", error); //DA CONTROLLARE, potrebbe essere inutile!!! 
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
