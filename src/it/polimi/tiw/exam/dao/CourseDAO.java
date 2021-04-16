package it.polimi.tiw.exam.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import it.polimi.tiw.exam.objects.*;

public class CourseDAO {

	private Connection connection;
    public CourseDAO(Connection connection) {
        this.connection=connection;
    }
    
    public Course getCourseById(int courseId) throws SQLException {
    	Course course=null;
    	String query= "SELECT * FROM course WHERE id_course=?";
    	
    	ResultSet result = null;
		PreparedStatement pstatement = null;
		
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1,courseId);

			result = pstatement.executeQuery();
			
			while(result.next()) {
				course=new Course();
				course.setCourseId(result.getInt("id_course"));
				course.setProfessorId(result.getInt("id_professor"));
				course.setTitle(result.getString("title"));
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
			
		return course;
    }
    
    public List<Course> getCoursesByProfessorId(int professorId) throws SQLException {
    	List<Course> courses= new ArrayList<Course>();
    	String query= "SELECT * FROM course WHERE id_professor=? ORDER BY title DESC";
    	
    	ResultSet result = null;
		PreparedStatement pstatement = null;
		
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1,professorId);

			result = pstatement.executeQuery();
			
			while(result.next()) {
				Course course=new Course();
				course.setCourseId(result.getInt("id_course"));
				course.setProfessorId(result.getInt("id_professor"));
				course.setTitle(result.getString("title"));
				
				courses.add(course);
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
			
		return courses;
    }

    public List<Course> getCoursesByStudentId(int studentId) throws SQLException {
    	List<Course> courses= new ArrayList<Course>();
    	String query= "SELECT * FROM course AS c1 join followings AS f1 on c1.id_course=f1.id_course WHERE f1.id_student=? "
    			+ "ORDER BY title DESC";
    	
    	ResultSet result = null;
		PreparedStatement pstatement = null;
		
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1,studentId);

			result = pstatement.executeQuery();
			
			while(result.next()) {
				Course course=new Course();
				course.setCourseId(result.getInt("id_course"));
				course.setProfessorId(result.getInt("id_professor"));
				course.setTitle(result.getString("title"));
				
				courses.add(course);
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
			
		return courses;
    }
    
    public int createCourse(Course course) throws SQLException { //may not be necessary
		int code = 0;
		String query = "INSERT into courses (id_course,id_professor,title)   VALUES(?, ?, ?)";

		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);

			pstatement.setInt(1, course.getCourseId());
			pstatement.setInt(2, course.getProfessorId());
			pstatement.setString(3, course.getTitle());

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
}
