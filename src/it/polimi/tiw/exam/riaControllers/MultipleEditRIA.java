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
		// HttpSession session = request.getSession();
		User user = null;// (User) session.getAttribute("user");
		user = new User(3, "Professor");
		int appealId = 0;
		int studentId;
		String gradeValue;
		Gson gson = new Gson();
		List<JsonObject> jlist = null;
		int i;
		boolean badRequest = false;

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
					appealId = tempObj.get("appealId").getAsInt(); // may want to check behavior with null (should throw exception)
				if (!tempObj.get("gradeValue").getAsString().equals("")) {
					jlist.add(temp.get(i).getAsJsonObject());
				}
			}
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
			return;
		}

		// check all grades share the same appealId
		try {
			for (i = 0; i < jlist.size(); i++) {
				if (jlist.get(i).get("appealId").getAsInt() != appealId)
					throw new Exception();
			}
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Illegal request for given appeal");
			return;
		}

		//check professor has the rights to access appeal information
		AppealDAO appealDAO = new AppealDAO(connection);
		try {
			if (!appealDAO.hasAppeal(appealId, user.getPersonId(), "Professor")) {
				throw new Exception();
			}
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unavailable appeal");
			return;
		}
		
		// check all students have 'not entered' grade for that appeal
		GradeDAO gradeDAO = new GradeDAO(connection);
		try {
			for (i = 0; i < jlist.size(); i++) {
				if (jlist.get(i).get("studentId") == null)
					throw new Exception("Nonexistent student");
				Grade grade = gradeDAO.getResultByAppealAndStudent(appealId, jlist.get(i).get("studentId").getAsInt());
				if (grade == null)
					throw new Exception("Undefined grade for a student");
				if (!grade.getState().equalsIgnoreCase("not entered"))
					throw new Exception("Illegal request for multiple editing");
			}
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
			return;
		}

		// check grades have legal values
		try {
			for (i = 0; i < jlist.size(); i++) {
				if (jlist.get(i).get("gradeValue") == null)
					throw new Exception("Illegal request for grades");
				String tempGrade = jlist.get(i).get("gradeValue").getAsString();
				if (!tempGrade.equalsIgnoreCase("absent") && !tempGrade.equalsIgnoreCase("failed") &&!tempGrade.equalsIgnoreCase("recalled") &&
						!tempGrade.equalsIgnoreCase("18") &&!tempGrade.equalsIgnoreCase("19") &&!tempGrade.equalsIgnoreCase("20") && 
						!tempGrade.equalsIgnoreCase("21") &&!tempGrade.equalsIgnoreCase("22") &&!tempGrade.equalsIgnoreCase("23") && 
						!tempGrade.equalsIgnoreCase("24") &&!tempGrade.equalsIgnoreCase("25") &&!tempGrade.equalsIgnoreCase("26") && 
						!tempGrade.equalsIgnoreCase("27") &&!tempGrade.equalsIgnoreCase("28") &&!tempGrade.equalsIgnoreCase("29") && 
						!tempGrade.equalsIgnoreCase("30") &&!tempGrade.equalsIgnoreCase("30 with merit"))
					throw new Exception("Illegal request for grades");
			}
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
			return;
		}

		
		for (i = 0; i < jlist.size(); i++) {
			try {
				gradeDAO.enterGrade(appealId, jlist.get(i).get("studentId").getAsInt(),	jlist.get(i).get("gradeValue").getAsString());
			} catch (Exception e) {
				//having checked the legality of submitted JSON objects, this part should actually be unreachable
				response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
				return;
			}
		}

		Appeal appeal = null;
		try {
			appeal = appealDAO.getAppealById(appealId);
		} catch (Exception e) {
			//having checked the legality of submitted JSON objects, this part should actually be unreachable
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}

		String json = new Gson().toJson(appeal);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}

}
