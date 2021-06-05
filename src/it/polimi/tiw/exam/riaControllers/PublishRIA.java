package it.polimi.tiw.exam.riaControllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
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
import it.polimi.tiw.exam.objects.Appeal;
import it.polimi.tiw.exam.objects.Grade;
import it.polimi.tiw.exam.objects.User;
import it.polimi.tiw.exam.utils.ConnectionHandler;
import it.polimi.tiw.exam.utils.TemplateEngineHandler;

@WebServlet("/PublishRIA")
@MultipartConfig
public class PublishRIA extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		Integer appId = null;
		User user = null;// (User) session.getAttribute("user");
		user = new User(3, "Professor");
		Appeal appeal = null;

		try {
			AppealDAO appealDAO = new AppealDAO(connection);
			appId = Integer.parseInt(request.getParameter("appeal"));
			if (!appealDAO.hasAppeal(appId, user.getPersonId(), "Professor")) {
				throw new Exception();
			}
			appeal = appealDAO.getAppealById(appId);
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Couldn't find appeal");
			return;
		}
		GradeDAO gradeDAO = new GradeDAO(connection);

		try {
			gradeDAO.publishGrade(appId);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to find grades");
			return;
		}
		String json = new Gson().toJson(appeal);
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
