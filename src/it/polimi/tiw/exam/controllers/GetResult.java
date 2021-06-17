package it.polimi.tiw.exam.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

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
		ErrorMsg error = (ErrorMsg) request.getAttribute("error");
		RequestDispatcher rd = request.getRequestDispatcher("GetCourses");
		
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

		// check existence of selected appeal
		Appeal appeal = null;
		AppealDAO adao = new AppealDAO(connection);
		try {
			appeal = adao.getAppealById(appId);
			if (appeal == null)
				throw new Exception();
		} catch (Exception e) {
			error = new ErrorMsg(HttpServletResponse.SC_NOT_FOUND, "Appeal not found");
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
		
		//control on student's rights to access the appeal
		try {
			if(!adao.hasAppeal(appId, user.getPersonId(), "Student")) {
				throw new Exception();
			}
		} catch (Exception e) {
			error = new ErrorMsg(HttpServletResponse.SC_BAD_REQUEST, "No grade found for selected appeal");
			request.setAttribute("error", error);
			rd.forward(request, response);
			return;
		}
		
		//main section: obtaining the student's grade (and information about the appeal)
		GradeDAO gradeDao = new GradeDAO(connection);
		Grade grade = null;
		try {
			grade = gradeDao.getResultByAppealAndStudent(appId, user.getPersonId());
		} catch (SQLException e) {
			error = new ErrorMsg(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"An accidental error occurred while retrieving result");
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
		
		//give actual access to result page
		String path = "/WEB-INF/Result.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("grade", grade);
		ctx.setVariable("appeal", appeal);
		templateEngine.process(path, ctx, response.getWriter());
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {}
	}

}
