package it.polimi.tiw.exam.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SecurityDAO {
	private Connection connection;
	
	public SecurityDAO(Connection connection) {
		this.connection=connection;
	}
	
	public Integer getLastCourse(int personId) throws SQLException {
		Integer out=null;
		String query= "SELECT last_course FROM security WHERE id_person=?";
    	
    	ResultSet result = null;
		PreparedStatement pstatement = null;
		
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1,personId);

			result = pstatement.executeQuery();
			
			while(result.next()) {
				out=result.getInt("last_course");
			}		
		} catch (SQLException e) {
			throw new SQLException(e);

		} finally {
			try {
				if (result != null)
					result.close();
			} catch (Exception e1) {
				throw new SQLException(e1);
			}
			try {
				if (pstatement != null)
					pstatement.close();
			} catch (Exception e2) {
				throw new SQLException(e2);
			}
		}
			
		return out;
    }
    
	public Integer getLastAppeal(int personId) throws SQLException {
		Integer out=null;
		String query= "SELECT last_appeal FROM security WHERE id_person=?";
    	
    	ResultSet result = null;
		PreparedStatement pstatement = null;
		
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1,personId);

			result = pstatement.executeQuery();
			
			while(result.next()) {
				out=result.getInt("last_appeal");
			}		
		} catch (SQLException e) {
			throw new SQLException(e);

		} finally {
			try {
				if (result != null)
					result.close();
			} catch (Exception e1) {
				throw new SQLException(e1);
			}
			try {
				if (pstatement != null)
					pstatement.close();
			} catch (Exception e2) {
				throw new SQLException(e2);
			}
		}
			
		return out;
    }
	
	public Integer getLastStudent(int personId) throws SQLException {
		Integer out=null;
		String query= "SELECT last_student FROM security WHERE id_person=?";
    	
    	ResultSet result = null;
		PreparedStatement pstatement = null;
		
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1,personId);

			result = pstatement.executeQuery();
			
			while(result.next()) {
				out=result.getInt("last_student");
			}		
		} catch (SQLException e) {
			throw new SQLException(e);

		} finally {
			try {
				if (result != null)
					result.close();
			} catch (Exception e1) {
				throw new SQLException(e1);
			}
			try {
				if (pstatement != null)
					pstatement.close();
			} catch (Exception e2) {
				throw new SQLException(e2);
			}
		}
			
		return out;
    }
	
	public void setLastCourse(int personId, int courseId) throws SQLException {
		String query= "UPDATE security SET last_course=?, last_appeal=null, last_student=null WHERE id_person=?";
    	
    	ResultSet result = null;
		PreparedStatement pstatement = null;
		
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1,courseId);
			pstatement.setInt(2,personId);
			
			pstatement.executeUpdate();	
		} catch (SQLException e) {
			throw new SQLException(e);

		} finally {
			try {
				if (result != null)
					result.close();
			} catch (Exception e1) {
				throw new SQLException(e1);
			}
			try {
				if (pstatement != null)
					pstatement.close();
			} catch (Exception e2) {
				throw new SQLException(e2);
			}
		}
    }
    
	public void setLastAppeal(int personId, int appealId) throws SQLException {
		String query= "UPDATE security SET last_appeal=?, last_student=null WHERE id_person=?";
    	
    	ResultSet result = null;
		PreparedStatement pstatement = null;
		
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1,appealId);
			pstatement.setInt(2,personId);
			
			pstatement.executeUpdate();	
		} catch (SQLException e) {
			throw new SQLException(e);

		} finally {
			try {
				if (result != null)
					result.close();
			} catch (Exception e1) {
				throw new SQLException(e1);
			}
			try {
				if (pstatement != null)
					pstatement.close();
			} catch (Exception e2) {
				throw new SQLException(e2);
			}
		}
    }
	
	public void setLastStudent(int personId, int studentId) throws SQLException {
		String query= "UPDATE security SET last_student=? WHERE id_person=?";
    	
    	ResultSet result = null;
		PreparedStatement pstatement = null;
		
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1,studentId);
			pstatement.setInt(2,personId);
			
			pstatement.executeUpdate();	
		} catch (SQLException e) {
			throw new SQLException(e);

		} finally {
			try {
				if (result != null)
					result.close();
			} catch (Exception e1) {
				throw new SQLException(e1);
			}
			try {
				if (pstatement != null)
					pstatement.close();
			} catch (Exception e2) {
				throw new SQLException(e2);
			}
		}
    }
	
	public void clearRow(int personId) throws SQLException {
		String query= "UPDATE security SET last_course=null, last_appeal=null, last_student=null WHERE id_person=?";
    	
    	ResultSet result = null;
		PreparedStatement pstatement = null;
		
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1,personId);
			
			pstatement.executeUpdate();	
		} catch (SQLException e) {
			throw new SQLException(e);

		} finally {
			try {
				if (result != null)
					result.close();
			} catch (Exception e1) {
				throw new SQLException(e1);
			}
			try {
				if (pstatement != null)
					pstatement.close();
			} catch (Exception e2) {
				throw new SQLException(e2);
			}
		}
    }
	
	public void insertRow(int personId) throws SQLException {
		String query= "INSERT INTO security values (?,null,null,null)";
    	
    	ResultSet result = null;
		PreparedStatement pstatement = null;
		
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1,personId);
			
			pstatement.executeUpdate();	
		} catch (SQLException e) {
			throw new SQLException(e);

		} finally {
			try {
				if (result != null)
					result.close();
			} catch (Exception e1) {
				throw new SQLException(e1);
			}
			try {
				if (pstatement != null)
					pstatement.close();
			} catch (Exception e2) {
				throw new SQLException(e2);
			}
		}
    }
	
	public void removeRow(int personId) throws SQLException {
		String query= "DELETE FROM security WHERE id_person=?";
    	
    	ResultSet result = null;
		PreparedStatement pstatement = null;
		
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1,personId);
			
			pstatement.executeUpdate();	
		} catch (SQLException e) {
			throw new SQLException(e);

		} finally {
			try {
				if (result != null)
					result.close();
			} catch (Exception e1) {
				throw new SQLException(e1);
			}
			try {
				if (pstatement != null)
					pstatement.close();
			} catch (Exception e2) {
				throw new SQLException(e2);
			}
		}
    }
}

