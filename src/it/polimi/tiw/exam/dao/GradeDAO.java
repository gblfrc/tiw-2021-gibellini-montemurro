package it.polimi.tiw.exam.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.exam.objects.*;

public class GradeDAO {
	private Connection connection;
	
	public GradeDAO(Connection connection) {
		this.connection=connection;
	}
	
	public List<Grade> findAllGrades(int id_Appeal) throws SQLException{
		List<Grade> grades=new ArrayList<Grade>();
		String query="SELECT * FROM exam WHERE id_app=? ORDER BY absent DESC, failed DESC, "
				+ "recalled DESC, grade ASC, merit ASC";
				
		ResultSet result=null;
		PreparedStatement pstatement=null;
		
		try {
			pstatement=connection.prepareStatement(query);
			pstatement.setInt(1, id_Appeal);
			
			result=pstatement.executeQuery();
			
			while(result.next()) {
				Grade grade=new Grade();
				grade.setId_app(result.getInt("id_app"));
				grade.setId_stud(result.getInt("id_stud"));
				grade.setState(result.getString("state"));
				grade.setFailed(result.getBoolean("failed"));
				grade.setRecalled(result.getBoolean("recalled"));
				grade.setAbsent(result.getBoolean("absent"));
				grade.setGrade(result.getInt("grade"));
				grade.setMerit(result.getBoolean("merit"));
				
				grades.add(grade);	
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
			
		return grades;
    }
	
	public int insertGrade(Grade grade) throws SQLException {
		int code = 0;
		String query = "INSERT into exam (id_app, id_stud, state, failed, recalled, absent, grade, merit)   VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);

			pstatement.setInt(1, grade.getId_app());
			pstatement.setInt(2, grade.getId_stud());
			pstatement.setString(3, grade.getState());
			pstatement.setBoolean(4, grade.isFailed());
			pstatement.setBoolean(5, grade.isRecalled());
			pstatement.setBoolean(6, grade.isAbsent());
			pstatement.setInt(5, grade.getGrade());
			pstatement.setBoolean(6, grade.isMerit());

			code = pstatement.executeUpdate();

		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			try {
				pstatement.close();
			} catch (Exception e1) {

			}
		}
		return code;
	}
	
	public int editSingleGrade(Grade grade) throws SQLException {

		String query = "UPDATE exam SET state=?, failed=?, recalled=?, absent=?, grade=?, merit=? WHERE id_app=? and id_stud=?" ;
		PreparedStatement pstatement = null;
		int code = 0;
		try {
			pstatement = connection.prepareStatement(query);

			pstatement.setString(1, grade.getState());
			pstatement.setBoolean(2, grade.isFailed());
			pstatement.setBoolean(3, grade.isRecalled());
			pstatement.setBoolean(4, grade.isAbsent());
			pstatement.setInt(5, grade.getGrade());
			pstatement.setBoolean(6, grade.isMerit());
			pstatement.setInt(7, grade.getId_app());
			pstatement.setInt(8, grade.getId_stud());
			
			code = pstatement.executeUpdate();

		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			try {
				if (pstatement != null)
					pstatement.close();
			} catch (Exception e1) {

			}
		}
		return code;
	}
	
	public Grade getResultByAppealAndStudent(int Id_Appeal, int Id_Student) throws SQLException {
		Grade grade=null;
		String query="SELECT * FROM exam WHERE id_app=? and id_stud=?";
		
		ResultSet result=null;
		PreparedStatement pstatement=null;
		
		try {
			pstatement=connection.prepareStatement(query);
			pstatement.setInt(1, Id_Appeal);
			pstatement.setInt(2, Id_Student);
			result=pstatement.executeQuery();
			
			while (result.next()) {
				grade = new Grade();
				grade.setId_app(result.getInt("id_app"));
				grade.setId_stud(result.getInt("id_stud"));
				grade.setState(result.getString("state"));
				grade.setFailed(result.getBoolean("failed"));
				grade.setRecalled(result.getBoolean("recalled"));
				grade.setAbsent(result.getBoolean("absent"));
				grade.setGrade(result.getInt("grade"));
				grade.setMerit(result.getBoolean("merit"));
			}
		} catch (SQLException e) {
			throw new SQLException(e);

		} finally {
			try {
				if (result != null)
					result.close();
			} catch (Exception e1) {
				throw new SQLException("Cannot close result");
			}
			try {
				if (pstatement != null)
					pstatement.close();
			} catch (Exception e1) {
				throw new SQLException("Cannot close statement");
			}
		}
		return grade;		
	}
}
