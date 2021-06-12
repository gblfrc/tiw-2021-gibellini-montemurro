package it.polimi.tiw.exam.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import it.polimi.tiw.exam.objects.ErrorMsg;
import it.polimi.tiw.exam.utils.ConnectionHandler;
import it.polimi.tiw.exam.utils.TemplateEngineHandler;

@WebServlet("/GetLogin")
public class GetLogin extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		connection = ConnectionHandler.getConnection(servletContext);
		templateEngine = TemplateEngineHandler.getEngine(servletContext);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String type = null;
		String path = "/WEB-INF/Login.html";
		//try to get errors from a forwarded request
		ErrorMsg error = (ErrorMsg) request.getAttribute("error");
		//try to get errors from a forwarded request
		/*try {
			error = (ErrorMsg) request.getAttribute("error");
		} catch (ClassCastException cce) {
			error = null;
		}*/

		if (error==null) {
			type = request.getParameter("type");
			if (type == null)
				type = "pureHtml";
			if (type.equalsIgnoreCase("ria")) path = "/WEB-INF/LoginRIA.html";
			else if (!type.equalsIgnoreCase("pureHtml")) {
				error = new ErrorMsg(HttpServletResponse.SC_NOT_FOUND, "Requested login page isn't available");
			}
		}

		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("error", error);
		templateEngine.process(path, ctx, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
	
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
		}
		;
	}
}
