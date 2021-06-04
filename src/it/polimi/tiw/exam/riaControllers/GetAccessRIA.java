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

/*
import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;
*/
import it.polimi.tiw.exam.objects.User;
import it.polimi.tiw.exam.utils.ConnectionHandler;
import it.polimi.tiw.exam.dao.UserDAO;
import it.polimi.tiw.exam.forms.UserForm;

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

		try {
			personId = Integer.parseInt(request.getParameter("personId"));
			password = request.getParameter("password");
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Invalid credentials");
			return;
		}

		UserForm uf = new UserForm(personId, password);
		UserDAO userDAO = new UserDAO(connection);
		User user = null;
		
		if (!uf.isValid()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			String errorString="";
			if(uf.getIdError()!=null)errorString+= uf.getIdError()+" ";
			if(uf.getPwdError()!=null)errorString+= uf.getPwdError();
			response.getWriter().println(errorString);
			return;
		} 
		
		try {
			user = userDAO.getUser(personId, password);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.getWriter().println("User not found");
			return;
		}

		if (user == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.getWriter().println("User not found");
			return;
		} else {
			request.getSession().setAttribute("user", user);
			response.setStatus(HttpServletResponse.SC_OK);
			String json = new Gson().toJson(user);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().println(json);
			return;
		}

	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
		}
		;
	}
}
