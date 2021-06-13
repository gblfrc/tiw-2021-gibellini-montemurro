package it.polimi.tiw.exam.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
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
import it.polimi.tiw.exam.dao.ReportDAO;
import it.polimi.tiw.exam.objects.Appeal;
import it.polimi.tiw.exam.objects.ErrorMsg;
import it.polimi.tiw.exam.objects.Report;
import it.polimi.tiw.exam.objects.User;
import it.polimi.tiw.exam.utils.ConnectionHandler;
import it.polimi.tiw.exam.utils.TemplateEngineHandler;

@WebServlet("/GetReports")
public class GetReports extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine templateEngine;

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
		templateEngine = TemplateEngineHandler.getEngine(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		ErrorMsg error = null;
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

		// control on professor's rights to access the appeal
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		try {
			if (!adao.hasAppeal(appId, user.getPersonId(), "Professor")) {
				throw new Exception();
			}
		} catch (Exception e) {
			error = new ErrorMsg(HttpServletResponse.SC_BAD_REQUEST, "Denied access to selected course");
			request.setAttribute("error", error);
			rd.forward(request, response);
			return;
		}

		// control on "type" request parameter legitimacy
		String type;
		try {
			type = request.getParameter("type");
			if (type == null)
				type = "all";
			if (!type.equals("all") && !type.equals("last"))
				throw new Exception();
		} catch (Exception e) {
			error = new ErrorMsg(HttpServletResponse.SC_BAD_REQUEST, "Illegal report request");
			rd = request.getRequestDispatcher("GetSubscribers");
			request.setAttribute("error", error);
			rd.forward(request, response);
			return;
		}

		// main section: report grades and fetch latest report
		ReportDAO reportDao = new ReportDAO(connection);
		List<Report> reports = null;
		reports = new LinkedList<>();
		try {
			if (type.equals("all"))
				reports = reportDao.getAllReports(appId);
			else if (type.equals("last"))
				reports.add(reportDao.getReportById(reportDao.getLastReport(appId)));
		} catch (SQLException e) {
			error = new ErrorMsg(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"An accidental error occurred while retrieving reports");
			request.setAttribute("error", error);
			rd = request.getRequestDispatcher("GetSubscribers");
			rd.forward(request, response);
			return;
		}

		// actually give access to report page
		String path = "/WEB-INF/Reports.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("type", type);
		ctx.setVariable("reports", reports);
		ctx.setVariable("appeal", appeal);
		templateEngine.process(path, ctx, response.getWriter());
	}

}
