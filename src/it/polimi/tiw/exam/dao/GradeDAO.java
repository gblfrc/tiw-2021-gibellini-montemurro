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
	
	public List<Grade> getGradesByAppealId(int appealId) throws SQLException{
		List<Grade> grades=new ArrayList<Grade>();
		String query="SELECT * FROM student AS s1 join exam AS e1 on s1.id_student=e1.id_student WHERE e1.id_app=? ";			 
		
		ResultSet result=null;
		PreparedStatement pstatement=null;
		
		try {
			pstatement=connection.prepareStatement(query);
			pstatement.setInt(1, appealId);
			
			result=pstatement.executeQuery();
			
			while(result.next()) {
				Grade grade=new Grade();
				grade.setAppealId(result.getInt("id_app"));
				grade.setStudentId(result.getInt("id_student"));
				grade.setStudentSurname(result.getString("surname"));
				grade.setStudentName(result.getString("name"));
				grade.setEmail(result.getString("email"));
				grade.setDegreeCourse(result.getString("degree_course"));
				if(result.getBoolean("failed")==true) {
					grade.setGrade("failed");
				}
				else if(result.getBoolean("recalled")==true) {
					grade.setGrade("recalled");
				}
				else if(result.getBoolean("absent")==true) {
					grade.setGrade("absent");
				}
				else {
					if(result.getBoolean("merit")==true) {
						grade.setGrade(Integer.toString(result.getInt("grade"))+" e lode");
					}
					else {
						grade.setGrade(Integer.toString(result.getInt("grade")));
					}
				}
				grade.setState(result.getString("state"));
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
	
	public List<Grade> getGradesByStudentIdAsc(int appealId) throws SQLException{
		List<Grade> grades=new ArrayList<Grade>();
		String query="SELECT * FROM student AS s1 join exam AS e1 on s1.id_student=e1.id_student WHERE e1.id_app=? ORDER BY s1.id_student ASC";
																	 
				
		ResultSet result=null;
		PreparedStatement pstatement=null;
		
		try {
			pstatement=connection.prepareStatement(query);
			pstatement.setInt(1, appealId);
			
			result=pstatement.executeQuery();
			
			while(result.next()) {
				Grade grade=new Grade();
				grade.setAppealId(result.getInt("id_app"));
				grade.setStudentId(result.getInt("id_student"));
				grade.setStudentSurname(result.getString("surname"));
				grade.setStudentName(result.getString("name"));
				grade.setEmail(result.getString("email"));
				grade.setDegreeCourse(result.getString("degree_course"));
				if(result.getBoolean("failed")==true) {
					grade.setGrade("failed");
				}
				else if(result.getBoolean("recalled")==true) {
					grade.setGrade("recalled");
				}
				else if(result.getBoolean("absent")==true) {
					grade.setGrade("absent");
				}
				else {
					if(result.getBoolean("merit")==true) {
						grade.setGrade(Integer.toString(result.getInt("grade"))+" e lode");
					}
					else {
						grade.setGrade(Integer.toString(result.getInt("grade")));
					}
				}
				grade.setState(result.getString("state"));
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
	
	public List<Grade> getGradesByStudentIdDesc(int appealId) throws SQLException{
		List<Grade> grades=new ArrayList<Grade>();
		String query="SELECT * FROM student AS s1 join exam AS e1 on s1.id_student=e1.id_student WHERE e1.id_app=? ORDER BY s1.id_student DESC";
				
		ResultSet result=null;
		PreparedStatement pstatement=null;
		
		try {
			pstatement=connection.prepareStatement(query);
			pstatement.setInt(1, appealId);
			
			result=pstatement.executeQuery();
			
			while(result.next()) {
				Grade grade=new Grade();
				grade.setAppealId(result.getInt("id_app"));
				grade.setStudentId(result.getInt("id_student"));
				grade.setStudentSurname(result.getString("surname"));
				grade.setStudentName(result.getString("name"));
				grade.setEmail(result.getString("email"));
				grade.setDegreeCourse(result.getString("degree_course"));
				if(result.getBoolean("failed")==true) {
					grade.setGrade("failed");
				}
				else if(result.getBoolean("recalled")==true) {
					grade.setGrade("recalled");
				}
				else if(result.getBoolean("absent")==true) {
					grade.setGrade("absent");
				}
				else {
					if(result.getBoolean("merit")==true) {
						grade.setGrade(Integer.toString(result.getInt("grade"))+" e lode");
					}
					else {
						grade.setGrade(Integer.toString(result.getInt("grade")));
					}
				}
				grade.setState(result.getString("state"));
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
	
	public List<Grade> getGradesBySurnameAsc(int appealId) throws SQLException{
		List<Grade> grades=new ArrayList<Grade>();
		String query="SELECT * FROM student AS s1 join exam AS e1 on s1.id_student=e1.id_student WHERE e1.id_app=? ORDER BY s1.surname ASC";
																	
				
		ResultSet result=null;
		PreparedStatement pstatement=null;
		
		try {
			pstatement=connection.prepareStatement(query);
			pstatement.setInt(1, appealId);
			
			result=pstatement.executeQuery();
			
			while(result.next()) {
				Grade grade=new Grade();
				grade.setAppealId(result.getInt("id_app"));
				grade.setStudentId(result.getInt("id_student"));
				grade.setStudentSurname(result.getString("surname"));
				grade.setStudentName(result.getString("name"));
				grade.setEmail(result.getString("email"));
				grade.setDegreeCourse(result.getString("degree_course"));
				if(result.getBoolean("failed")==true) {
					grade.setGrade("failed");
				}
				else if(result.getBoolean("recalled")==true) {
					grade.setGrade("recalled");
				}
				else if(result.getBoolean("absent")==true) {
					grade.setGrade("absent");
				}
				else {
					if(result.getBoolean("merit")==true) {
						grade.setGrade(Integer.toString(result.getInt("grade"))+" e lode");
					}
					else {
						grade.setGrade(Integer.toString(result.getInt("grade")));
					}
				}
				grade.setState(result.getString("state"));
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
	
	public List<Grade> getGradesBySurnameDesc(int appealId) throws SQLException{
		List<Grade> grades=new ArrayList<Grade>();
		String query="SELECT * FROM student AS s1 join exam AS e1 on s1.id_student=e1.id_student WHERE e1.id_app=? ORDER BY s1.surname DESC";
																	
				
		ResultSet result=null;
		PreparedStatement pstatement=null;
		
		try {
			pstatement=connection.prepareStatement(query);
			pstatement.setInt(1, appealId);
			
			result=pstatement.executeQuery();
			
			while(result.next()) {
				Grade grade=new Grade();
				grade.setAppealId(result.getInt("id_app"));
				grade.setStudentId(result.getInt("id_student"));
				grade.setStudentSurname(result.getString("surname"));
				grade.setStudentName(result.getString("name"));
				grade.setEmail(result.getString("email"));
				grade.setDegreeCourse(result.getString("degree_course"));
				if(result.getBoolean("failed")==true) {
					grade.setGrade("failed");
				}
				else if(result.getBoolean("recalled")==true) {
					grade.setGrade("recalled");
				}
				else if(result.getBoolean("absent")==true) {
					grade.setGrade("absent");
				}
				else {
					if(result.getBoolean("merit")==true) {
						grade.setGrade(Integer.toString(result.getInt("grade"))+" e lode");
					}
					else {
						grade.setGrade(Integer.toString(result.getInt("grade")));
					}
				}
				grade.setState(result.getString("state"));
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
	
	public List<Grade> getGradesByNameAsc(int appealId) throws SQLException{
		List<Grade> grades=new ArrayList<Grade>();
		String query="SELECT * FROM student AS s1 join exam AS e1 on s1.id_student=e1.id_student WHERE e1.id_app=? ORDER BY s1.name ASC";
																	
				
		ResultSet result=null;
		PreparedStatement pstatement=null;
		
		try {
			pstatement=connection.prepareStatement(query);
			pstatement.setInt(1, appealId);
			
			result=pstatement.executeQuery();
			
			while(result.next()) {
				Grade grade=new Grade();
				grade.setAppealId(result.getInt("id_app"));
				grade.setStudentId(result.getInt("id_student"));
				grade.setStudentSurname(result.getString("surname"));
				grade.setStudentName(result.getString("name"));
				grade.setEmail(result.getString("email"));
				grade.setDegreeCourse(result.getString("degree_course"));
				if(result.getBoolean("failed")==true) {
					grade.setGrade("failed");
				}
				else if(result.getBoolean("recalled")==true) {
					grade.setGrade("recalled");
				}
				else if(result.getBoolean("absent")==true) {
					grade.setGrade("absent");
				}
				else {
					if(result.getBoolean("merit")==true) {
						grade.setGrade(Integer.toString(result.getInt("grade"))+" e lode");
					}
					else {
						grade.setGrade(Integer.toString(result.getInt("grade")));
					}
				}
				grade.setState(result.getString("state"));
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
	
	public List<Grade> getGradesByNameDesc(int appealId) throws SQLException{
		List<Grade> grades=new ArrayList<Grade>();
		String query="SELECT * FROM student AS s1 join exam AS e1 on s1.id_student=e1.id_student WHERE e1.id_app=? ORDER BY s1.name DESC";
																
				
		ResultSet result=null;
		PreparedStatement pstatement=null;
		
		try {
			pstatement=connection.prepareStatement(query);
			pstatement.setInt(1, appealId);
			
			result=pstatement.executeQuery();
			
			while(result.next()) {
				Grade grade=new Grade();
				grade.setAppealId(result.getInt("id_app"));
				grade.setStudentId(result.getInt("id_student"));
				grade.setStudentSurname(result.getString("surname"));
				grade.setStudentName(result.getString("name"));
				grade.setEmail(result.getString("email"));
				grade.setDegreeCourse(result.getString("degree_course"));
				if(result.getBoolean("failed")==true) {
					grade.setGrade("failed");
				}
				else if(result.getBoolean("recalled")==true) {
					grade.setGrade("recalled");
				}
				else if(result.getBoolean("absent")==true) {
					grade.setGrade("absent");
				}
				else {
					if(result.getBoolean("merit")==true) {
						grade.setGrade(Integer.toString(result.getInt("grade"))+" e lode");
					}
					else {
						grade.setGrade(Integer.toString(result.getInt("grade")));
					}
				}
				grade.setState(result.getString("state"));
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
	
	public List<Grade> getGradesByEmailAsc(int appealId) throws SQLException{
		List<Grade> grades=new ArrayList<Grade>();
		String query="SELECT * FROM student AS s1 join exam AS e1 on s1.id_student=e1.id_student WHERE e1.id_app=? ORDER BY s1.email ASC";
				
		ResultSet result=null;
		PreparedStatement pstatement=null;
		
		try {
			pstatement=connection.prepareStatement(query);
			pstatement.setInt(1, appealId);
			
			result=pstatement.executeQuery();
			
			while(result.next()) {
				Grade grade=new Grade();
				grade.setAppealId(result.getInt("id_app"));
				grade.setStudentId(result.getInt("id_student"));
				grade.setStudentSurname(result.getString("surname"));
				grade.setStudentName(result.getString("name"));
				grade.setEmail(result.getString("email"));
				grade.setDegreeCourse(result.getString("degree_course"));
				if(result.getBoolean("failed")==true) {
					grade.setGrade("failed");
				}
				else if(result.getBoolean("recalled")==true) {
					grade.setGrade("recalled");
				}
				else if(result.getBoolean("absent")==true) {
					grade.setGrade("absent");
				}
				else {
					if(result.getBoolean("merit")==true) {
						grade.setGrade(Integer.toString(result.getInt("grade"))+" e lode");
					}
					else {
						grade.setGrade(Integer.toString(result.getInt("grade")));
					}
				}
				grade.setState(result.getString("state"));
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
	
	public List<Grade> getGradesByEmailDesc(int appealId) throws SQLException{
		List<Grade> grades=new ArrayList<Grade>();
		String query="SELECT * FROM student AS s1 join exam AS e1 on s1.id_student=e1.id_student WHERE e1.id_app=? ORDER BY s1.email DESC";	
																	 
				
		ResultSet result=null;
		PreparedStatement pstatement=null;
		
		try {
			pstatement=connection.prepareStatement(query);
			pstatement.setInt(1, appealId);
			
			result=pstatement.executeQuery();
			
			while(result.next()) {
				Grade grade=new Grade();
				grade.setAppealId(result.getInt("id_app"));
				grade.setStudentId(result.getInt("id_student"));
				grade.setStudentSurname(result.getString("surname"));
				grade.setStudentName(result.getString("name"));
				grade.setEmail(result.getString("email"));
				grade.setDegreeCourse(result.getString("degree_course"));
				if(result.getBoolean("failed")==true) {
					grade.setGrade("failed");
				}
				else if(result.getBoolean("recalled")==true) {
					grade.setGrade("recalled");
				}
				else if(result.getBoolean("absent")==true) {
					grade.setGrade("absent");
				}
				else {
					if(result.getBoolean("merit")==true) {
						grade.setGrade(Integer.toString(result.getInt("grade"))+" e lode");
					}
					else {
						grade.setGrade(Integer.toString(result.getInt("grade")));
					}
				}
				grade.setState(result.getString("state"));
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
	
	public List<Grade> getGradesByDegreeCourseAsc(int appealId) throws SQLException{
		List<Grade> grades=new ArrayList<Grade>();
		String query="SELECT * FROM student AS s1 join exam AS e1 on s1.id_student=e1.id_student WHERE e1.id_app=? ORDER BY s1.degree_course ASC";
				
		ResultSet result=null;
		PreparedStatement pstatement=null;
		
		try {
			pstatement=connection.prepareStatement(query);
			pstatement.setInt(1, appealId);
			
			result=pstatement.executeQuery();
			
			while(result.next()) {
				Grade grade=new Grade();
				grade.setAppealId(result.getInt("id_app"));
				grade.setStudentId(result.getInt("id_student"));
				grade.setStudentSurname(result.getString("surname"));
				grade.setStudentName(result.getString("name"));
				grade.setEmail(result.getString("email"));
				grade.setDegreeCourse(result.getString("degree_course"));
				if(result.getBoolean("failed")==true) {
					grade.setGrade("failed");
				}
				else if(result.getBoolean("recalled")==true) {
					grade.setGrade("recalled");
				}
				else if(result.getBoolean("absent")==true) {
					grade.setGrade("absent");
				}
				else {
					if(result.getBoolean("merit")==true) {
						grade.setGrade(Integer.toString(result.getInt("grade"))+" e lode");
					}
					else {
						grade.setGrade(Integer.toString(result.getInt("grade")));
					}
				}
				grade.setState(result.getString("state"));
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
	
	public List<Grade> getGradesByDegreeCourseDesc(int appealId) throws SQLException{
		List<Grade> grades=new ArrayList<Grade>();
		String query="SELECT * FROM student AS s1 join exam AS e1 on s1.id_student=e1.id_student WHERE e1.id_app=? ORDER BY s1.degree_course DESC";
				
		ResultSet result=null;
		PreparedStatement pstatement=null;
		
		try {
			pstatement=connection.prepareStatement(query);
			pstatement.setInt(1, appealId);
			
			result=pstatement.executeQuery();
			
			while(result.next()) {
				Grade grade=new Grade();
				grade.setAppealId(result.getInt("id_app"));
				grade.setStudentId(result.getInt("id_student"));
				grade.setStudentSurname(result.getString("surname"));
				grade.setStudentName(result.getString("name"));
				grade.setEmail(result.getString("email"));
				grade.setDegreeCourse(result.getString("degree_course"));
				if(result.getBoolean("failed")==true) {
					grade.setGrade("failed");
				}
				else if(result.getBoolean("recalled")==true) {
					grade.setGrade("recalled");
				}
				else if(result.getBoolean("absent")==true) {
					grade.setGrade("absent");
				}
				else {
					if(result.getBoolean("merit")==true) {
						grade.setGrade(Integer.toString(result.getInt("grade"))+" e lode");
					}
					else {
						grade.setGrade(Integer.toString(result.getInt("grade")));
					}
				}
				grade.setState(result.getString("state"));
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
	
	public List<Grade> getGradesByGradeAsc(int appealId) throws SQLException{
		List<Grade> grades=new ArrayList<Grade>();
		String query="SELECT * FROM student AS s1 join exam AS e1 on s1.id_student=e1.id_student WHERE e1.id_app=? and e1.state='not entered' UNION"
		+ "SELECT * FROM student AS s2 join exam AS e2 on s2.id_student=e2.id_student WHERE e2.id_app=? ORDER BY e2.absent DESC, e2.failed DESC, "
		+ "e2.recalled DESC, e2.grade ASC, e2.merit ASC";
				
		ResultSet result=null;
		PreparedStatement pstatement=null;
		
		try {
			pstatement=connection.prepareStatement(query);
			pstatement.setInt(1, appealId);
			
			result=pstatement.executeQuery();
			
			while(result.next()) {
				Grade grade=new Grade();
				grade.setAppealId(result.getInt("id_app"));
				grade.setStudentId(result.getInt("id_student"));
				grade.setStudentSurname(result.getString("surname"));
				grade.setStudentName(result.getString("name"));
				grade.setEmail(result.getString("email"));
				grade.setDegreeCourse(result.getString("degree_course"));
				if(result.getBoolean("failed")==true) {
					grade.setGrade("failed");
				}
				else if(result.getBoolean("recalled")==true) {
					grade.setGrade("recalled");
				}
				else if(result.getBoolean("absent")==true) {
					grade.setGrade("absent");
				}
				else {
					if(result.getBoolean("merit")==true) {
						grade.setGrade(Integer.toString(result.getInt("grade"))+" e lode");
					}
					else {
						grade.setGrade(Integer.toString(result.getInt("grade")));
					}
				}
				grade.setState(result.getString("state"));
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
	
	public List<Grade> getGradesByGradeDesc(int appealId) throws SQLException{
		List<Grade> grades=new ArrayList<Grade>();
		String query="SELECT * FROM student AS s2 join exam AS e2 on s2.id_student=e2.id_student WHERE e2.id_app=? ORDER BY e2.merit DESC, e2.grade DESC,e2.recalled DESC,"
				+ " e2.absent DESC, e2.failed DESC UNION SELECT * FROM student AS s1 join exam AS e1 on s1.id_student=e1.id_student WHERE e1.id_app=? and e1.state='not entered'";
		ResultSet result=null;
		PreparedStatement pstatement=null;
		
		try {
			pstatement=connection.prepareStatement(query);
			pstatement.setInt(1, appealId);
			
			result=pstatement.executeQuery();
			
			while(result.next()) {
				Grade grade=new Grade();
				grade.setAppealId(result.getInt("id_app"));
				grade.setStudentId(result.getInt("id_student"));
				grade.setStudentSurname(result.getString("surname"));
				grade.setStudentName(result.getString("name"));
				grade.setEmail(result.getString("email"));
				grade.setDegreeCourse(result.getString("degree_course"));
				if(result.getBoolean("failed")==true) {
					grade.setGrade("failed");
				}
				else if(result.getBoolean("recalled")==true) {
					grade.setGrade("recalled");
				}
				else if(result.getBoolean("absent")==true) {
					grade.setGrade("absent");
				}
				else {
					if(result.getBoolean("merit")==true) {
						grade.setGrade(Integer.toString(result.getInt("grade"))+" e lode");
					}
					else {
						grade.setGrade(Integer.toString(result.getInt("grade")));
					}
				}
				grade.setState(result.getString("state"));
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
	
	public List<Grade> getGradesByStateAsc(int appealId) throws SQLException{
		List<Grade> grades=new ArrayList<Grade>();
		String query="SELECT * FROM student AS s1 join exam AS e1 on s1.id_student=e1.id_student WHERE e1.id_app=? and e1.state='not entered' UNION"
				+ "SELECT * FROM student AS s2 join exam AS e2 on s2.id_student=e2.id_student WHERE e2.id_app=? and e2.state='entered' UNION"
				+ "SELECT * FROM student AS s3 join exam AS e3 on s3.id_student=e3.id_student WHERE e3.id_app=? and e3.state='published' UNION"
				+ "SELECT * FROM student AS s4 join exam AS e4 on s4.id_student=e4.id_student WHERE e4.id_app=? and e4.state='refused' UNION"
				+ "SELECT * FROM student AS s1 join exam AS e1 on s1.id_student=e1.id_student WHERE e1.id_app=? and e1.state='recorded'";			 
				
		ResultSet result=null;
		PreparedStatement pstatement=null;
		
		try {
			pstatement=connection.prepareStatement(query);
			pstatement.setInt(1, appealId);
			
			result=pstatement.executeQuery();
			
			while(result.next()) {
				Grade grade=new Grade();
				grade.setAppealId(result.getInt("id_app"));
				grade.setStudentId(result.getInt("id_student"));
				grade.setStudentSurname(result.getString("surname"));
				grade.setStudentName(result.getString("name"));
				grade.setEmail(result.getString("email"));
				grade.setDegreeCourse(result.getString("degree_course"));
				if(result.getBoolean("failed")==true) {
					grade.setGrade("failed");
				}
				else if(result.getBoolean("recalled")==true) {
					grade.setGrade("recalled");
				}
				else if(result.getBoolean("absent")==true) {
					grade.setGrade("absent");
				}
				else {
					if(result.getBoolean("merit")==true) {
						grade.setGrade(Integer.toString(result.getInt("grade"))+" e lode");
					}
					else {
						grade.setGrade(Integer.toString(result.getInt("grade")));
					}
				}
				grade.setState(result.getString("state"));
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
	
	public List<Grade> getGradesByStateDesc(int appealId) throws SQLException{
		List<Grade> grades=new ArrayList<Grade>();
		String query="SELECT * FROM student AS s1 join exam AS e1 on s1.id_student=e1.id_student WHERE e1.id_app=? and e1.state='recorded' UNION"
				+ "SELECT * FROM student AS s2 join exam AS e2 on s2.id_student=e2.id_student WHERE e2.id_app=? and e2.state='refused' UNION"
				+ "SELECT * FROM student AS s3 join exam AS e3 on s3.id_student=e3.id_student WHERE e3.id_app=? and e3.state='published' UNION"
				+ "SELECT * FROM student AS s4 join exam AS e4 on s4.id_student=e4.id_student WHERE e4.id_app=? and e4.state='entered' UNION"
				+ "SELECT * FROM student AS s1 join exam AS e1 on s1.id_student=e1.id_student WHERE e1.id_app=? and e1.state='not entered'";			 //update to counter not-entered grades
																	 //update to include join with student (see specifics)
				
		ResultSet result=null;
		PreparedStatement pstatement=null;
		
		try {
			pstatement=connection.prepareStatement(query);
			pstatement.setInt(1, appealId);
			
			result=pstatement.executeQuery();
			
			while(result.next()) {
				Grade grade=new Grade();
				grade.setAppealId(result.getInt("id_app"));
				grade.setStudentId(result.getInt("id_student"));
				grade.setStudentSurname(result.getString("surname"));
				grade.setStudentName(result.getString("name"));
				grade.setEmail(result.getString("email"));
				grade.setDegreeCourse(result.getString("degree_course"));
				if(result.getBoolean("failed")==true) {
					grade.setGrade("failed");
				}
				else if(result.getBoolean("recalled")==true) {
					grade.setGrade("recalled");
				}
				else if(result.getBoolean("absent")==true) {
					grade.setGrade("absent");
				}
				else {
					if(result.getBoolean("merit")==true) {
						grade.setGrade(Integer.toString(result.getInt("grade"))+" e lode");
					}
					else {
						grade.setGrade(Integer.toString(result.getInt("grade")));
					}
				}
				grade.setState(result.getString("state"));
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
	
	public int insertGrade(int appealId, int studentId) throws SQLException { //change params to student id and appeal id
		int code = 0;
		String query = "INSERT into exam (id_app, id_student, state, failed, recalled, absent, grade, merit)   VALUES(?, ?, not entered , null, null, null, null, null)";

		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);

			pstatement.setInt(1, appealId);
			pstatement.setInt(2, studentId);

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
	
	public int editGrade(Grade grade) throws SQLException {
		String query = "UPDATE exam SET state=?, failed=?, recalled=?, absent=?, grade=?, merit=? WHERE id_app=? and id_stud=?" ;
		PreparedStatement pstatement = null;
		int code = 0;
		try {
			pstatement = connection.prepareStatement(query);

			pstatement.setString(1, grade.getState());
			if(grade.getGrade().equals("failed")) {
				pstatement.setBoolean(2, true);
				pstatement.setBoolean(3, false);
				pstatement.setBoolean(4, false);
				pstatement.setNull(5, java.sql.Types.INTEGER);
				pstatement.setBoolean(6, false);
			}
			else if(grade.getGrade().equals("recalled")) {
				pstatement.setBoolean(2, false);
				pstatement.setBoolean(3, true);
				pstatement.setBoolean(4, false);
				pstatement.setNull(5, java.sql.Types.INTEGER);
				pstatement.setBoolean(6, false);
			}
			else if(grade.getGrade().equals("absent")) {
				pstatement.setBoolean(2, false);
				pstatement.setBoolean(3, false);
				pstatement.setBoolean(4, true);
				pstatement.setNull(5, java.sql.Types.INTEGER);
				pstatement.setBoolean(6, false);
			}
			else {
				if(grade.getGrade().equals("30 e lode")) {
					pstatement.setBoolean(2, false);
					pstatement.setBoolean(3, false);
					pstatement.setBoolean(4, false);
					pstatement.setInt(5, 30);
					pstatement.setBoolean(6, false);
				}
				else {
					pstatement.setBoolean(2, false);
					pstatement.setBoolean(3, false);
					pstatement.setBoolean(4, false);
					pstatement.setInt(5, Integer.parseInt(grade.getGrade()));
					pstatement.setBoolean(6, false);
				}
			}
			
				pstatement.setInt(7, grade.getAppealId());
				pstatement.setInt(8, grade.getStudentId());
			
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
	
	public Grade getResultByAppealAndStudent(int appealId, int studentId) throws SQLException {
		Grade grade=null;
		String query="SELECT * FROM exam WHERE id_app=? and id_stud=?";
		
		ResultSet result=null;
		PreparedStatement pstatement=null;
		
		try {
			pstatement=connection.prepareStatement(query);
			pstatement.setInt(1, appealId);
			pstatement.setInt(2, studentId);
			result=pstatement.executeQuery();
			
			while (result.next()) {
				grade = new Grade();
				grade.setAppealId(result.getInt("id_app"));
				grade.setStudentId(result.getInt("id_student"));
				grade.setStudentSurname(result.getString("surname"));
				grade.setStudentName(result.getString("name"));
				grade.setEmail(result.getString("email"));
				grade.setDegreeCourse(result.getString("degree_course"));
				if(result.getBoolean("failed")==true) {
					grade.setGrade("failed");
				}
				else if(result.getBoolean("recalled")==true) {
					grade.setGrade("recalled");
				}
				else if(result.getBoolean("absent")==true) {
					grade.setGrade("absent");
				}
				else {
					if(result.getBoolean("merit")==true) {
						grade.setGrade(Integer.toString(result.getInt("grade"))+" e lode");
					}
					else {
						grade.setGrade(Integer.toString(result.getInt("grade")));
					}
				}
				grade.setState(result.getString("state"));
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
