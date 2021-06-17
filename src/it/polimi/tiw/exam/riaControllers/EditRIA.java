package it.polimi.tiw.exam.riaControllers;

import java.io.IOException;
import java.sql.Connection;

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
import it.polimi.tiw.exam.dao.SecurityDAO;
import it.polimi.tiw.exam.objects.Appeal;
import it.polimi.tiw.exam.objects.Grade;
import it.polimi.tiw.exam.objects.User;
import it.polimi.tiw.exam.utils.ConnectionHandler;

@WebServlet("/EditRIA") @MultipartConfig
public class EditRIA extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	
	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		int appId;
		int studentId;
		String gradeValue;
		
		try {
			appId=Integer.parseInt(request.getParameter("appeal"));
			studentId=Integer.parseInt(request.getParameter("studentId"));
			gradeValue=request.getParameter("gradeValue");
		}catch(NumberFormatException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Incorrect param values");
			return;
		}
		
		//security: get last-visited student
		SecurityDAO secDAO=new SecurityDAO(connection);
		try {
			if(secDAO.getLastStudent(user.getPersonId())!=studentId) throw new Exception();
		}catch(Exception e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Access denied for security reasons");
			return;
		}
		
		AppealDAO appealDAO = new AppealDAO(connection);
		Appeal appeal;
		try {
			appeal = appealDAO.getAppealById(appId);
			if (appeal == null)
				throw new Exception();
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.getWriter().println("Appeal not found");
			return;
		}
		
		GradeDAO gradeDAO= new GradeDAO(connection);
		Grade grade;
		try {
			grade=gradeDAO.getResultByAppealAndStudent(appId,studentId);
			if(grade==null) throw new Exception();
		}catch(Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Nonexistent student");
			return;
		}
		
		try {
			if(!grade.getState().equalsIgnoreCase("entered")&&!grade.getState().equalsIgnoreCase("not entered")) throw new Exception();
		}catch(Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Uneditable grade");
			return;
		}
		
		try {
			gradeDAO.enterGrade(appId,studentId,gradeValue);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("An accidental error occurred while entering grades");
			return;
		}
		
		String json = new Gson().toJson(appeal);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}
	
}
