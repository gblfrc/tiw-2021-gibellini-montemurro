package it.polimi.tiw.exam.dao;

import java.sql.*;
import it.polimi.tiw.exam.objects.*;

public class StudentDAO {
	
	private Connection connection;
	
	
	//constuctor with attributes
	public StudentDAO(Connection con) {
		this.connection=con;
	}
	
	
	
	// method to get information about students given their id
	public Student getStudentById(int id) throws SQLException {
		
		String query = "SELECT * FROM Student WHERE id_student = ?";
		PreparedStatement statement = null;
		ResultSet rs = null;
		Student result = new Student();
		
		
		try {
			statement = connection.prepareStatement(query);
			statement.setInt(1, id);
			rs = statement.executeQuery();
			if(rs.next()==true) {
				result.setStudentId(rs.getInt("id_student"));
				result.setName(rs.getString("name"));
				result.setSurname(rs.getString("surname"));
				result.setEmail(rs.getString("email"));
				result.setDegreeCourse(rs.getString("degree_course"));
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
		
		return result;
	}
	
	
	/* isn'it more convenient to get all info about students for a grade
	 * by looking into a joined table on the DB?
	 */
	/*
	public List<Student> findStudentsByAppeal (Appeal appeal){
		
	}
	*/
	

}
