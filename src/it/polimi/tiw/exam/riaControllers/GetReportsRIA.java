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

import com.google.gson.Gson;

import it.polimi.tiw.exam.dao.ReportDAO;
import it.polimi.tiw.exam.objects.Report;
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
		
		// send a specific response if selected appeal doesn't have any report
		if (reports.size() == 0) {
			response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
			response.getWriter().println("No reports for selected appeal");
			return;
		}

		//build response object and send it
		String json = new Gson().toJson(reports);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}

}
