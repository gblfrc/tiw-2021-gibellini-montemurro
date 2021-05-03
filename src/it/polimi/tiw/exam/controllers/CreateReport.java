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
import it.polimi.tiw.exam.dao.ReportDAO;
import it.polimi.tiw.exam.objects.Appeal;
import it.polimi.tiw.exam.objects.Report;
import it.polimi.tiw.exam.utils.ConnectionHandler;
import it.polimi.tiw.exam.utils.TemplateEngineHandler;

@WebServlet("/CreateReport")
public class CreateReport extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine templateEngine;

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
		templateEngine = TemplateEngineHandler.getEngine(getServletContext());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int appId;
		try {
			appId = Integer.parseInt(request.getParameter("appeal"));
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Couldn't evade the request");
			return;
		}
		
		ReportDAO reportDao = new ReportDAO(connection);
		Report report = null;
		
		try {
			reportDao.createReport(appId);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An accidental error occurred, couldn't create report");
			return;
		}
		
		try {
			AppealDAO appealDao = new AppealDAO(connection); //may want to improve behavior by modifying the getReport method to get only appId parameter
			Appeal appeal = appealDao.getAppealById(appId);
			report = reportDao.getReportByAppeal(appeal);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An accidental error occurred, couldn't retrieve report");
			return;
		}
		
		String path = "/WEB-INF/Report.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("report", report);
		templateEngine.process(path, ctx, response.getWriter());
	}

}
