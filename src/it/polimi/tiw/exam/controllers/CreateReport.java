package it.polimi.tiw.exam.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import it.polimi.tiw.exam.dao.GradeDAO;
import it.polimi.tiw.exam.dao.ReportDAO;
import it.polimi.tiw.exam.objects.ErrorMsg;
import it.polimi.tiw.exam.utils.ConnectionHandler;

@WebServlet("/CreateReport")
public class CreateReport extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ErrorMsg error = (ErrorMsg) request.getAttribute("error");
		RequestDispatcher rd = request.getRequestDispatcher("GetSubscribers");
		
		//control on "appeal" request parameter legitimacy
		int appId;
		try {
			appId = Integer.parseInt(request.getParameter("appeal"));
		} catch (Exception e) {
			error = new ErrorMsg(HttpServletResponse.SC_BAD_REQUEST, "Illegal appeal request");
			request.setAttribute("error", error);
			rd.forward(request, response);
			return;
		}
				
		//control on number of reportable grades: if none is reportable, won't create an empty report
		GradeDAO gdao = new GradeDAO(connection);
		try {
			int reportableGrades = gdao.countReportableGrades(appId);
			if (reportableGrades == 0) throw new Exception();
		} catch (Exception e) {
			error = new ErrorMsg(HttpServletResponse.SC_BAD_REQUEST,"No grades reportable for specified appeal");
			request.setAttribute("error", error);
			rd.forward(request, response);
			return;
		}
		
		//report grades
		ReportDAO reportDao = new ReportDAO(connection);
		try {
			reportDao.createReport(appId);
		} catch (SQLException e) {
			error = new ErrorMsg(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An accidental error occurred, couldn't create report");
			request.setAttribute("error", error);
			rd.forward(request, response);
			return;
		}

		response.sendRedirect("GetReports?appeal=" + appId + "&type=last");
	}
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
