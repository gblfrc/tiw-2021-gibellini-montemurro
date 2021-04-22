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

import org.thymeleaf.TemplateEngine;

import it.polimi.tiw.exam.dao.GradeDAO;
import it.polimi.tiw.exam.utils.ConnectionHandler;
import it.polimi.tiw.exam.utils.TemplateEngineHandler;

@WebServlet("/RefuseGrade")
public class RefuseGrade extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine templateEngine;

	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		connection = ConnectionHandler.getConnection(servletContext);
		templateEngine = TemplateEngineHandler.getEngine(servletContext);
	}

	/*
	 * protected void doGet(HttpServletRequest request, HttpServletResponse
	 * response) throws ServletException, IOException { // TODO Auto-generated
	 * method stub
	 * response.getWriter().append("Served at: ").append(request.getContextPath());
	 * }
	 */

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		GradeDAO gradeDao = new GradeDAO(connection);
		int appId = 0;
		int studId = 0;

		try {
			appId = Integer.parseInt(request.getParameter("appeal"));
			studId = Integer.parseInt(request.getParameter("student"));
		} catch (NumberFormatException e) {
			// manda alla servlet di ottenimento degli appelli per lo studente loggato
			// aggiungi messaggio di errore (ex: Impossibile rifiutare voto)
		}

		// controllare che appId e studId non siano 0 e che studId sia quello dello
		// studente loggato
		try {
			gradeDao.refuseGrade(appId, studId);
		} catch (SQLException e) {
			// come prima per NFE
		}
		
		response.sendRedirect("GetResult?appeal=" + appId + "&student=" + studId);
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {}
	}


}
