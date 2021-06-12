package it.polimi.tiw.exam.riaControllers;

import java.io.IOException;
import java.security.InvalidParameterException;
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


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.exam.dao.AppealDAO;
import it.polimi.tiw.exam.dao.GradeDAO;
import it.polimi.tiw.exam.objects.Appeal;
import it.polimi.tiw.exam.objects.Grade;
import it.polimi.tiw.exam.objects.User;
import it.polimi.tiw.exam.utils.ConnectionHandler;



@WebServlet("/GetResultRIA")
@MultipartConfig
public class GetResultRIA extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private Connection connection;

    public void init() throws ServletException{
    	ServletContext servletContext = getServletContext();
    	connection = ConnectionHandler.getConnection(servletContext);
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		User user = (User)session.getAttribute("user");
		
		//control on student's rights to access the appeal
		Integer appId = null;
		try {
			AppealDAO appealDAO= new AppealDAO(connection);
			appId = Integer.parseInt(request.getParameter("appeal"));
			if(!appealDAO.hasAppeal(appId, user.getPersonId(), "Student")) {
				throw new InvalidParameterException();
			}
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Unavailable appeal");
			return;
		}
		
		//main section: obtaining the student's grade (and information about the appeal)
		GradeDAO gradeDao = new GradeDAO(connection);
		Grade grade = null;
		try {
			grade = gradeDao.getResultByAppealAndStudent(appId, user.getPersonId());
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("It seems this student never subscribed to the requested exam");
			return;
		}
		AppealDAO appealDao = new AppealDAO(connection);
		Appeal appeal = new Appeal();
		try {
			appeal = appealDao.getAppealById(appId);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Impossible to find the requested appeal");
			return;
		}
		response.setStatus(HttpServletResponse.SC_OK);
		String json = new Gson().toJson(grade);
		Gson gson = new GsonBuilder().setDateFormat("yyyy/MM/dd").create();
		String json1 = gson.toJson(appeal);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
		response.getWriter().write(json1);

	}

	/*
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	*/
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {}
	}

}
