package it.polimi.tiw.exam.dao;

import java.sql.*;
import java.util.*;
import it.polimi.tiw.exam.objects.*;

public class AppealDAO {
	
	private Connection connection;
	
	//constructor with attributes
	public AppealDAO(Connection con) {
		this.connection=con;
	}
	
	
	public List<Appeal> getAppealsByCourse(int courseId, int personId, String accessRights) throws SQLException { //needs Course Object
		String query=null;
		if(accessRights.equals("Professor")) query= "SELECT * FROM Appeal WHERE id_course = ? ORDER BY date DESC";
		else if(accessRights.equals("Student")) query= "SELECT * FROM Appeal JOIN Exam ON Appeal.id_appeal = Exam.id_appeal WHERE Appeal.id_course = ? and Exam.id_student=? ORDER BY date DESC";
		PreparedStatement statement = null;
		ResultSet rs = null;
		List<Appeal> resultList = new LinkedList<>();
		
		try {
			statement = connection.prepareStatement(query);
			statement.setInt(1, courseId); 
			if(accessRights.equals("Student")) {
				statement.setInt(2, personId); 
			}
			
			rs = statement.executeQuery();
			while(rs.next()==true) {
				Appeal temp = new Appeal();		
				
				temp.setAppealId(rs.getInt("Appeal.id_appeal"));
				temp.setCourseId(rs.getInt("Appeal.id_course"));
				//temp.setCourseTitle(rs.getString("Course.title"));
				temp.setDate(rs.getDate("Appeal.date")); 	
				
				resultList.add(temp);
			}
		} catch (SQLException e) {
			throw new SQLException("Error while accessing DB");
		} finally {
			try {
				if (rs!=null) rs.close();
			} catch (Exception e) {
				throw new SQLException ("Couldn't close ResultSet");
			};
			try {
				if (statement!=null) statement.close();
			} catch (Exception e) {
				throw new SQLException ("Couldn't close Statement");
			};
		};
		return resultList;
	}
	
	public Appeal getAppealById(int id) throws SQLException{
		String query = "SELECT * FROM appeal JOIN course ON appeal.id_course = course.id_course WHERE appeal.id_appeal = ?";
		PreparedStatement statement = null;
		ResultSet rs = null;
		Appeal result = null;
		try {
			statement = connection.prepareStatement(query);
			statement.setInt(1, id);
			rs = statement.executeQuery();
			if (rs.next()==true) {
				result = new Appeal(rs.getInt("appeal.id_appeal"),
									rs.getInt("appeal.id_course"),
									rs.getString("course.title"),
									rs.getDate("appeal.date"));
			}
		} finally {
			try {
				if (rs!=null) rs.close();
			} catch (Exception e) {
				throw new SQLException ("Couldn't close ResultSet");
			};
			try {
				if (statement!=null) statement.close();
			} catch (Exception e) {
				throw new SQLException ("Couldn't close Statement");
			};
		};
		return result;
	}
	
	
	
}
