package it.polimi.tiw.exam.riaControllers;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
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
import it.polimi.tiw.exam.dao.GradeDAO;
import it.polimi.tiw.exam.objects.Grade;
import it.polimi.tiw.exam.objects.User;
import it.polimi.tiw.exam.utils.ConnectionHandler;

@WebServlet("/GetNotEnteredRIA") @MultipartConfig
public class GetNotEnteredRIA extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();
		Boolean changeOrder = false;
		String field = null;

		// control on professor's rights to access the appeal
		Integer appId = null;
		User user = (User) session.getAttribute("user");
		try {
			AppealDAO appealDAO = new AppealDAO(connection);
			appId = Integer.parseInt(request.getParameter("appealId"));
			if (!appealDAO.hasAppeal(appId, user.getPersonId(), "Professor")) {
				throw new InvalidParameterException();
			}
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Unavailable appeal");
			return;
		}

		// get list of grades
		GradeDAO gradeDAO = new GradeDAO(connection);
		List<Grade> grades = new ArrayList<Grade>();

		try {
			grades = gradeDAO.getNotEnteredGradesByAppealId(appId);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("An accidental error occurred while trying to retrieve grades");
			return;
		}
		
		if (grades.size()==0) {
			response.setStatus(HttpServletResponse.SC_NO_CONTENT);
			response.getWriter().println("It seems this appeal doesn't have any \"NOT ENTERED\" grade");
			return;
		}

		String json = new Gson().toJson(grades);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
		}
		;
	}
}
