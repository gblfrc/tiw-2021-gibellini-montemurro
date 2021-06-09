package it.polimi.tiw.exam.riaControllers;

import java.io.IOException;
import java.security.InvalidParameterException;
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

@WebServlet("/GetReportsRIA") @MultipartConfig
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
			appId = Integer.parseInt(request.getParameter("appealId"));
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Couldn't handle the request");
			return;
		}

		// control on professor's rights to access the appeal
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		try {
			AppealDAO appealDAO = new AppealDAO(connection);
			appId = Integer.parseInt(request.getParameter("appealId"));
			if (!appealDAO.hasAppeal(appId, user.getPersonId(), "Professor")) {
				throw new InvalidParameterException();
			}
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unavailable appeal");
			return;
		}

		// main section: report grades and fetch latest report
		ReportDAO reportDao = new ReportDAO(connection);
		List<Report> reports = new LinkedList<>();
		Appeal appeal = null;
		try {
			String type = request.getParameter("type");
			if (type.equalsIgnoreCase("all")) {
				reports = reportDao.getAllReports(appId);
			}
			if (type.equalsIgnoreCase("last")) {
				reports.add(reportDao.getReportById(reportDao.getLastReport(appId)));
			}
			/*
			 * AppealDAO adao = new AppealDAO(connection); appeal =
			 * adao.getAppealById(appId);
			 */
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"An accidental error occurred, couldn't retrieve report");
			return;
		}

		String json = new Gson().toJson(reports);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}

}
