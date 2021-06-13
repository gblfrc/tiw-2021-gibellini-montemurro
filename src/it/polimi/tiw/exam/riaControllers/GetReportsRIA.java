package it.polimi.tiw.exam.riaControllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import it.polimi.tiw.exam.dao.AppealDAO;
import it.polimi.tiw.exam.dao.ReportDAO;
import it.polimi.tiw.exam.objects.Appeal;
import it.polimi.tiw.exam.objects.Report;
import it.polimi.tiw.exam.objects.User;
import it.polimi.tiw.exam.utils.ConnectionHandler;

@WebServlet("/GetReportsRIA")
@MultipartConfig
public class GetReportsRIA extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// control on "appeal" request parameter legitimacy
		int appId;
		try {
			appId = Integer.parseInt(request.getParameter("appeal"));
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Illegal appeal request");
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
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.getWriter().println("Appeal not found");
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
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Denied access to selected course");
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
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Illegal report request");
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
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("An accidental error occurred while retrieving reports");
			return;
		}

		//build response object and send it
		String json = new Gson().toJson(reports);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}

}
