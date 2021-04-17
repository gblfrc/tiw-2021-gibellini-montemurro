package it.polimi.tiw.exam.dao;

import java.sql.*;

import it.polimi.tiw.exam.objects.User;

public class UserDAO {
	
	private Connection connection;
	
	public UserDAO (Connection con) {
		this.connection=con;
	}
	
	
	public User getUser (int id, String pwd) throws SQLException{
		
		String query = "SELECT * FROM ? WHERE id_professor = ? and password = ?";
		PreparedStatement statement = null;
		ResultSet rs = null;
		User result = null;
		
		//search for user given credentials into professor table
		try {
			statement = connection.prepareStatement(query);
			statement.setString(1, "Professor");
			statement.setInt(2, id);
			statement.setString(3, pwd);
			
			rs = statement.executeQuery();
			
			if(rs.next()==true) {
				result = new User();		
				result.setPersonId(rs.getInt("id_professor"));
				result.setAccessRights("Professor");
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
		
		
		//search for user given credentials into student table
		if (result==null) {
			try {
				statement = connection.prepareStatement(query);
				statement.setString(1, "Student");
				statement.setInt(2, id);
				statement.setString(3, pwd);
				
				rs = statement.executeQuery();
				
				if(rs.next()==true) {
					result = new User();		
					result.setPersonId(rs.getInt("id_student"));
					result.setAccessRights("Student");
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
		}
		
		return result;
		
	}
	

}
