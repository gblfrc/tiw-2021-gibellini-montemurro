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
    
    
    public List<Course> findCourses(int id) throws SQLException {
    	List<Course> courses= new ArrayList<Course>();
    	String query= "SELECT * FROM course WHERE id_professor=? ORDER BY title DESC";
    	
    	ResultSet result = null;
		PreparedStatement pstatement = null;
		
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1,id);

			result = pstatement.executeQuery();
			
			while(result.next()) {
				Course course=new Course();
				course.setId_course(result.getInt("id_course"));
				course.setId_professor(result.getInt("id_professor"));
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
    
    public int createCourse(Course course) throws SQLException {
		int code = 0;
		String query = "INSERT into courses (id_course,id_professor,title)   VALUES(?, ?, ?)";

		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);

			pstatement.setInt(1, course.getId_course());
			pstatement.setInt(2, course.getId_professor());
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
