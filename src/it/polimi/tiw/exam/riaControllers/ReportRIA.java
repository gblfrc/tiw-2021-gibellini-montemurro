package it.polimi.tiw.exam.riaControllers;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import it.polimi.tiw.exam.dao.AppealDAO;
import it.polimi.tiw.exam.dao.GradeDAO;
import it.polimi.tiw.exam.dao.ReportDAO;
import it.polimi.tiw.exam.objects.Report;
import it.polimi.tiw.exam.objects.User;
import it.polimi.tiw.exam.utils.ConnectionHandler;

@WebServlet("/ReportRIA") @MultipartConfig
public class ReportRIA extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//control on "appeal" request parameter legitimacy
		int appId;
		try {
			appId = Integer.parseInt(request.getParameter("appeal"));
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Couldn't handle the request");
			System.out.println("Couldn't handle the request");
			return;
		}
		
		//control on professor's rights to access the appeal
		HttpSession session = request.getSession();
		User user = null;//(User) session.getAttribute("user");
		user = new User(3, "Professor");		
		try {
			AppealDAO appealDAO= new AppealDAO(connection);
			appId = Integer.parseInt(request.getParameter("appeal"));
			if(!appealDAO.hasAppeal(appId, user.getPersonId(), "Professor")) {
				throw new InvalidParameterException();
			}
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unavailable appeal");
			System.out.println("Unavailable appeal");
			return;
		}

		//control on number of reportable grades: if none is reportable, won't create an empty report
		GradeDAO gdao = new GradeDAO(connection);
		try {
			int reportableGrades = gdao.countReportableGrades(appId);
			if (reportableGrades == 0) throw new InvalidParameterException("No grades reportable for specified appeal");
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
			System.out.println(e.getMessage());
			return;
		}
		
		//may want to check that no additional parameters have been given
		
		//main section: report grades and fetch latest report
		ReportDAO reportDao = new ReportDAO(connection);
		Report report = null;
		
		try {
			reportDao.createReport(appId);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An accidental error occurred, couldn't create report");
			System.out.println("An accidental error occurred, couldn't create report");
			return;
		}
		
		try {
			report = reportDao.getReportById(reportDao.getLastReport(appId));
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An accidental error occurred, couldn't retrieve report");
			System.out.println("An accidental error occurred, couldn't retrieve report");
			return;
		}
		
		String json = new Gson().toJson(report.getAppeal());
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}

}
