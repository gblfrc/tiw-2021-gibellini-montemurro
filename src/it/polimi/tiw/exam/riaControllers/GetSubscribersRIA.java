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

@WebServlet("/GetSubscribersRIA") @MultipartConfig
public class GetSubscribersRIA extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession();
		Boolean changeOrder=false;
		String field=null;
		
		//control on professor's rights to access the appeal
		Integer appId = null;
		User user = null; //(User) session.getAttribute("user");
		user = new User (3, "Professor");
		try {
			AppealDAO appealDAO= new AppealDAO(connection);
			appId = Integer.parseInt(request.getParameter("appealId"));
			if(!appealDAO.hasAppeal(appId, user.getPersonId(), "Professor")) {
				throw new InvalidParameterException();
			}
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unavailable appeal");
			return;
		}
		
		//controls on request parameters
		try {			
			List<String> params= Collections.list(request.getParameterNames());
			//checks "changeOrder" has legal values
			if(params.contains("changeOrder")&&
			   !request.getParameter("changeOrder").equalsIgnoreCase("true")&&
			   !request.getParameter("changeOrder").equalsIgnoreCase("false")) {
					throw new InvalidParameterException("Unacceptable request");
				}
			params.remove("changeOrder");
			//checks "field" has legal values 
			if(params.contains("field")) {
				field=request.getParameter("field");  //saves "field" request parameter in a variable
				if(!field.equalsIgnoreCase("studentId")&&!field.equalsIgnoreCase("surname")&&!field.equalsIgnoreCase("name")&&
			      !field.equalsIgnoreCase("email")&&!field.equalsIgnoreCase("degree_course")&&!field.equalsIgnoreCase("grade")&&
			      !field.equalsIgnoreCase("state")) {
					throw new InvalidParameterException("Unacceptable request");
				}
			}
			params.remove("field");
			
			params.remove("appealId");
			//checks there aren't too many parameters in the request
			//if(params.size()>0) throw new InvalidParameterException("Couldn't handle request"); 
			
			changeOrder=Boolean.parseBoolean(request.getParameter("changeOrder")); //if there is no "changeOrder" parameter, parseBoolean returns false
			//checks existence of the attribute related to "field" request parameter (and handles it)
			if((session.getAttribute(field + "Order")==null||session.getAttribute(field+ "Order")=="DESC") && changeOrder) {
				session.setAttribute(field + "Order", "ASC");
			}
			else if(session.getAttribute(field + "Order")=="ASC" && changeOrder) {
				session.setAttribute(field + "Order", "DESC");
			}
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
			return;
		}
		
		//get list of grades (in specific order)
		GradeDAO gradeDAO= new GradeDAO(connection);
		List<Grade> grades= new ArrayList<Grade>();
		
		try {
			if(user.getAccessRights().equals("Professor")) {
				if(field==null || session.getAttribute(field+ "Order")==null) grades=gradeDAO.getGradesByAppealId(appId);
				else if (session.getAttribute(field + "Order")=="ASC") grades=gradeDAO.getGradesByFieldAsc(appId,field);
				else if (session.getAttribute(field + "Order")=="DESC") grades=gradeDAO.getGradesByFieldDesc(appId,field);
			}
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to find grades");
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
