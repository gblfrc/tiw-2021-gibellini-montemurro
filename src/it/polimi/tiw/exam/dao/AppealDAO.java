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
	
	
	public List<Appeal> getAppealsByCourse(Course course) throws SQLException { //needs Course Object
		
		String query = "SELECT * FROM Appeal WHERE id_course = ?";
		PreparedStatement statement = null;
		ResultSet rs = null;
		List<Appeal> resultList = new LinkedList<>();
		
		try {
			statement = connection.prepareStatement(query);
			statement.setInt(1, course.getCourseId()); 		//update this after seeing which getter for courseId
												 			//is actually implemented in Course Object
			rs = statement.executeQuery();
			if(rs.next()==true) {
				Appeal temp = new Appeal();		
				
				temp.setAppealId(rs.getInt("id_app"));
				temp.setCourseId(rs.getInt("id_course"));
				temp.setDate(rs.getDate("date")); 	// may want to check if this works later on
				
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
}
