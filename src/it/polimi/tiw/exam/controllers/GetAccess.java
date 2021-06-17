package it.polimi.tiw.exam.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.exam.objects.ErrorMsg;
import it.polimi.tiw.exam.objects.User;
import it.polimi.tiw.exam.utils.ConnectionHandler;
import it.polimi.tiw.exam.dao.SecurityDAO;
import it.polimi.tiw.exam.dao.UserDAO;

@WebServlet("/GetAccess")
public class GetAccess extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		connection = ConnectionHandler.getConnection(servletContext);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Integer personId = null;
		String password = null;
		ErrorMsg error = (ErrorMsg) request.getAttribute("error");
		RequestDispatcher rd = request.getRequestDispatcher("GetLogin");

		// section to handle illegal method request
		if (error != null) {
			request.setAttribute("error", error);
			rd.forward(request, response);
			return;
		}

		// get credentials and check their validity
		try {
			personId = Integer.parseInt(request.getParameter("personId")); // can throw NumberFormatException
			password = request.getParameter("password");
			if (personId <= 0 || password == null || password.isEmpty())
				throw new Exception(); // throws Exception
		} catch (Exception e) {
			error = new ErrorMsg(HttpServletResponse.SC_NOT_FOUND, "Invalid credentials");
			request.setAttribute("error", error);
			rd.forward(request, response);
			return;
		}

		// get user after checking all parameters are legal
		UserDAO userDAO = new UserDAO(connection);
		User user = null;
		try {
			user = userDAO.getUser(personId, password); // can throw SQLException
			if (user == null)
				throw new Exception(); // throws Exception
		} catch (Exception e) {
			error = new ErrorMsg(HttpServletResponse.SC_NOT_FOUND, "User not found");
			request.setAttribute("error", error);
			rd.forward(request, response);
			return;
		}

		// found a legal user --> give access to main section
		request.getSession().setAttribute("user", user);
		
		SecurityDAO secDAO=new SecurityDAO(connection);
		
		try {
			secDAO.removeRow(user.getPersonId());
		}catch(SQLException e){
			error = new ErrorMsg(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"An accidental error occurred while updating security settings");
			request.setAttribute("error", error);
			rd.forward(request, response);
			return;
		}
		
		try {
			secDAO.insertRow(user.getPersonId());
		}catch(SQLException e){
			error = new ErrorMsg(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"An accidental error occurred while updating security settings");
			request.setAttribute("error", error);
			rd.forward(request, response);
			return;
		}
		
		response.sendRedirect(getServletContext().getContextPath() + "/GetCourses");
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ErrorMsg error = new ErrorMsg(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Illegal request");
		request.setAttribute("error", error);
		doPost(request, response);
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
		}
		;
	}
}
