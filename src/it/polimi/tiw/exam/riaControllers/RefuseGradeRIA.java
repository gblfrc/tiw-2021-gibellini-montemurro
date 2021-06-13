package it.polimi.tiw.exam.riaControllers;

import java.io.IOException;
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

import it.polimi.tiw.exam.dao.AppealDAO;
import it.polimi.tiw.exam.dao.GradeDAO;
import it.polimi.tiw.exam.objects.Appeal;
import it.polimi.tiw.exam.objects.Grade;
import it.polimi.tiw.exam.objects.User;
import it.polimi.tiw.exam.utils.ConnectionHandler;

@WebServlet("/RefuseGradeRIA")
@MultipartConfig
public class RefuseGradeRIA extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Connection connection;

	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		connection = ConnectionHandler.getConnection(servletContext);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		GradeDAO gradeDao = new GradeDAO(connection);
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		int appId;

		// control on "appeal" request parameter legitimacy
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

		// control on student's rights to access the appeal
		try {
			if (!adao.hasAppeal(appId, user.getPersonId(), "Student")) {
				throw new Exception();
			}
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("No grade found for selected appeal");
			return;
		}

		// check grade is refusable (grade is neither refused nor recorded)
		try {
			Grade grade = gradeDao.getResultByAppealAndStudent(appId, user.getPersonId());
			if (grade.getState().equalsIgnoreCase("Refused") || grade.getState().equalsIgnoreCase("Recorded"))
				throw new Exception("Grade has already been " + grade.getState().toLowerCase());
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println(e.getMessage());
			return;
		}

		// refuse grade
		try {
			gradeDao.refuseGrade(appId, user.getPersonId());
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("An accidental error occurred while refusing result");
			return;
		}

		// set response status to OK
		response.setStatus(HttpServletResponse.SC_OK);
		return;
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
		}
	}

}
