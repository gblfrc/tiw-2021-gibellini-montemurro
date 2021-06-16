package it.polimi.tiw.exam.riaControllers;

import java.io.IOException;
import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import it.polimi.tiw.exam.dao.AppealDAO;
import it.polimi.tiw.exam.dao.GradeDAO;
import it.polimi.tiw.exam.objects.Appeal;
import it.polimi.tiw.exam.objects.Grade;
import it.polimi.tiw.exam.objects.User;
import it.polimi.tiw.exam.utils.ConnectionHandler;

@WebServlet("/MultipleEditRIA")
@MultipartConfig
public class MultipleEditRIA extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		int appId = 0;
		List<JsonObject> jlist = null;
		int i;

		// save request body into a string object, then create a JsonArray with the info
		// from the forms
		try {
			String requestBody = request.getReader().readLine();
			JsonArray temp = new Gson().fromJson(requestBody, JsonArray.class);
			jlist = new LinkedList<>();
			// build list of grades
			for (i = 0; i < temp.size(); i++) {
				JsonObject tempObj = temp.get(i).getAsJsonObject();
				if (i == 0)
					appId = tempObj.get("appealId").getAsInt(); // may want to check behavior with null (should throw exception)
				//discard grades which are yet not entered
				if (!tempObj.get("gradeValue").getAsString().equals("")) {
					jlist.add(temp.get(i).getAsJsonObject());
				}
			}
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Illegal request: couldn't recognize appeal");
			return;
		}

		// check all grades share the same appealId
		try {
			for (i = 0; i < jlist.size(); i++) {
				if (jlist.get(i).get("appealId").getAsInt() != appId)
					throw new Exception();
			}
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Illegal request: found grades for different appeals");
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

		// control on professor's rights to access the appeal
		try {
			if (!adao.hasAppeal(appId, user.getPersonId(), "Professor")) {
				throw new Exception();
			}
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Denied access to selected appeal");
			return;
		}
		
		// check all students have 'not entered' grade for that appeal
		GradeDAO gradeDAO = new GradeDAO(connection);
		try {
			for (i = 0; i < jlist.size(); i++) {
				if (jlist.get(i).get("studentId") == null)
					throw new Exception("Illegal student request");
				Grade grade = gradeDAO.getResultByAppealAndStudent(appId, jlist.get(i).get("studentId").getAsInt());
				if (grade == null)
					throw new Exception("Undefined grade for a student");
				if (!grade.getState().equalsIgnoreCase("not entered"))
					throw new Exception("Illegal request for multiple editing");
			}
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println(e.getMessage());
			return;
		}

		// check grades have legal values
		try {
			for (i = 0; i < jlist.size(); i++) {
				if (jlist.get(i).get("gradeValue") == null)
					throw new Exception();
				String tempGrade = jlist.get(i).get("gradeValue").getAsString();
				if (!tempGrade.equalsIgnoreCase("absent") && !tempGrade.equalsIgnoreCase("failed") &&!tempGrade.equalsIgnoreCase("recalled") &&
						!tempGrade.equalsIgnoreCase("18") &&!tempGrade.equalsIgnoreCase("19") &&!tempGrade.equalsIgnoreCase("20") && 
						!tempGrade.equalsIgnoreCase("21") &&!tempGrade.equalsIgnoreCase("22") &&!tempGrade.equalsIgnoreCase("23") && 
						!tempGrade.equalsIgnoreCase("24") &&!tempGrade.equalsIgnoreCase("25") &&!tempGrade.equalsIgnoreCase("26") && 
						!tempGrade.equalsIgnoreCase("27") &&!tempGrade.equalsIgnoreCase("28") &&!tempGrade.equalsIgnoreCase("29") && 
						!tempGrade.equalsIgnoreCase("30") &&!tempGrade.equalsIgnoreCase("30 with merit"))
					throw new Exception();
			}
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Illegal grade request");			
			return;
		}

		
		for (i = 0; i < jlist.size(); i++) {
			try {
				gradeDAO.enterGrade(appId, jlist.get(i).get("studentId").getAsInt(), jlist.get(i).get("gradeValue").getAsString());
			} catch (Exception e) {
				//having checked the legality of received JSON objects, this part should actually be unreachable
				response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
				response.getWriter().println("An error occurred while entering grades; some have been entered, some haven't");
				return;
			}
		}

		String json = new Gson().toJson(appeal);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}

}
