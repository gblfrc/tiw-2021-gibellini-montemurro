package it.polimi.tiw.exam.riaControllers;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;

import it.polimi.tiw.exam.dao.AppealDAO;
import it.polimi.tiw.exam.dao.GradeDAO;
import it.polimi.tiw.exam.objects.Grade;
import it.polimi.tiw.exam.objects.User;
import it.polimi.tiw.exam.utils.ConnectionHandler;
import it.polimi.tiw.exam.utils.TemplateEngineHandler;

@WebServlet("/RefuseGradeRIA")
@MultipartConfig
public class RefuseGradeRIA extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Connection connection;

	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		connection = ConnectionHandler.getConnection(servletContext);
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		GradeDAO gradeDao = new GradeDAO(connection);
		//control on student's rights to access the appeal
		HttpSession session = request.getSession();
		User user = (User)session.getAttribute("user");
		Integer appId = null;
		
		try {
			AppealDAO appealDAO= new AppealDAO(connection);
			appId = Integer.parseInt(request.getParameter("appeal"));
			if(!appealDAO.hasAppeal(appId, user.getPersonId(), "Student")) {
				throw new InvalidParameterException();
			}
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Unavailable appeal");
			return;
		}

		//check grade is refusable
		try {
			Grade grade = gradeDao.getResultByAppealAndStudent(appId, user.getPersonId());
			if (grade.getState().equalsIgnoreCase("Refused")) throw new InvalidParameterException("Grade has already been refused");
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println(e.getMessage());
			return;
		}

		try {
			gradeDao.refuseGrade(appId, user.getPersonId());
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Error while refusing grade");
			return;
		}
		response.setStatus(HttpServletResponse.SC_OK);
		return;
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {}
	}


}
