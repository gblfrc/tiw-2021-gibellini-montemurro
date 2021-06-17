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

import com.google.gson.Gson;

import it.polimi.tiw.exam.objects.User;
import it.polimi.tiw.exam.utils.ConnectionHandler;
import it.polimi.tiw.exam.dao.SecurityDAO;
import it.polimi.tiw.exam.dao.UserDAO;

@WebServlet("/GetAccessRIA")
@MultipartConfig
public class GetAccessRIA extends HttpServlet {

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

		// get credentials and check their validity
		try {
			personId = Integer.parseInt(request.getParameter("personId")); // can throw NumberFormatException
			password = request.getParameter("password");
			if (personId <= 0 || password == null || password.isEmpty())
				throw new Exception(); // throws Exception
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.getWriter().println("Invalid credentials");
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
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.getWriter().println("User not found");
			return;
		}

		//remove previously entered security row (if logout hadn't been done)
		SecurityDAO secDAO=new SecurityDAO(connection);
		try {
			secDAO.removeRow(user.getPersonId());
		}catch(SQLException e){
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("An accidental error occurred while updating security settings");
			return;
		}
		
		//insert new security row
		try {
			secDAO.insertRow(user.getPersonId());
		}catch(SQLException e){
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("An accidental error occurred while updating security settings");
			return;
		}
		
		// found a legal user --> give access to main section
		request.getSession().setAttribute("user", user);
		response.setStatus(HttpServletResponse.SC_OK);
		String json = new Gson().toJson(user);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().println(json);
		return;
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
		}
		;
	}
}
