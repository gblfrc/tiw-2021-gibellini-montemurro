package it.polimi.tiw.exam.dao;

import java.security.InvalidParameterException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import it.polimi.tiw.exam.objects.*;

public class GradeDAO {
	private Connection connection;
	
	public GradeDAO(Connection connection) {
		this.connection=connection;
	}
	
	public List<Grade> getGradesByAppealId(int appealId) throws SQLException{
		List<Grade> grades=new ArrayList<Grade>();
		String query="SELECT * FROM student AS s1 join exam AS e1 on s1.id_student=e1.id_student WHERE e1.id_appeal=? ";			 
		ResultSet result=null;
		PreparedStatement pstatement=null;
		
		try {
			pstatement=connection.prepareStatement(query);
			pstatement.setInt(1, appealId);
			
			result=pstatement.executeQuery();
			
			while(result.next()) {
				Grade grade=new Grade();
				grade.setAppealId(result.getInt("id_appeal"));
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
						grade.setGrade(Integer.toString(result.getInt("grade"))+" with merit");
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
	
	public List<Grade> getNotEnteredGradesByAppealId(int appealId) throws SQLException{
		List<Grade> grades=new ArrayList<Grade>();
		String query="SELECT * FROM student AS s join exam AS e on s.id_student=e.id_student WHERE e.id_appeal=? and e.state='not entered' ";			 
		ResultSet result=null;
		PreparedStatement pstatement=null;
		
		try {
			pstatement=connection.prepareStatement(query);
			pstatement.setInt(1, appealId);
			
			result=pstatement.executeQuery();
			
			while(result.next()) {
				Grade grade=new Grade();
				grade.setAppealId(result.getInt("id_appeal"));
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
						grade.setGrade(Integer.toString(result.getInt("grade"))+" with merit");
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
	
	public List<Grade> getGradesByFieldAsc(int appealId, String field) throws SQLException,InvalidParameterException{
		List<Grade> grades=new ArrayList<Grade>();
		String query;
		switch(field) {		
			case "studentId":query="SELECT * FROM student AS s1 join exam AS e1 on s1.id_student=e1.id_student WHERE e1.id_appeal=? ORDER BY s1.id_student ASC";
							break;
			case "surname":query="SELECT * FROM student AS s1 join exam AS e1 on s1.id_student=e1.id_student WHERE e1.id_appeal=? ORDER BY s1.surname ASC";
						   break;
			case "name":query="SELECT * FROM student AS s1 join exam AS e1 on s1.id_student=e1.id_student WHERE e1.id_appeal=? ORDER BY s1.name ASC";
						break;
			case "email":query="SELECT * FROM student AS s1 join exam AS e1 on s1.id_student=e1.id_student WHERE e1.id_appeal=? ORDER BY s1.email ASC";
						break;
			case "degree_course":{query="SELECT * FROM student AS s1 join exam AS e1 on s1.id_student=e1.id_student WHERE e1.id_appeal=? ORDER BY s1.degree_course ASC";
					break;}
			case "grade": {query="(SELECT * FROM student join exam on student.id_student=exam.id_student WHERE exam.id_appeal=? and exam.state='not entered')";
											break;}
			case"state":{query="(SELECT * FROM student AS s1 join exam AS e1 on s1.id_student=e1.id_student WHERE e1.id_appeal=? and e1.state='not entered')"+" UNION"
											+ "(SELECT * FROM student AS s2 join exam AS e2 on s2.id_student=e2.id_student WHERE e2.id_appeal=? and e2.state='entered')"+" UNION"
											+ "(SELECT * FROM student AS s3 join exam AS e3 on s3.id_student=e3.id_student WHERE e3.id_appeal=? and e3.state='published')"+" UNION"
											+ "(SELECT * FROM student AS s4 join exam AS e4 on s4.id_student=e4.id_student WHERE e4.id_appeal=? and e4.state='refused')"+"UNION"
											+ "(SELECT * FROM student AS s1 join exam AS e1 on s1.id_student=e1.id_student WHERE e1.id_appeal=? and e1.state='recorded')";
											break;}
			default:  throw new InvalidParameterException();
		}
		
		ResultSet result=null;
		PreparedStatement pstatement=null;
		int phase = 0;
		
		try {
			while (phase < 1 || (field.equals("grade") && phase < 2)) {
				if (phase==1 && field.equals("grade")) {
					query = "(SELECT * FROM student join exam on student.id_student=exam.id_student WHERE exam.id_appeal=? and state != 'not entered'" +
							"ORDER BY absent DESC, failed DESC, recalled DESC, grade ASC, merit ASC)";
				}
				pstatement = connection.prepareStatement(query);
				pstatement.setInt(1, appealId);
				if ((field.equals("state"))) {
					pstatement.setInt(2, appealId);
				}
				if (field.equals("state")) {
					pstatement.setInt(3, appealId);
					pstatement.setInt(4, appealId);
					pstatement.setInt(5, appealId);
				}
				result = pstatement.executeQuery();

				while (result.next()) {
					Grade grade = new Grade();
					grade.setAppealId(result.getInt("id_appeal"));
					grade.setStudentId(result.getInt("id_student"));
					grade.setStudentSurname(result.getString("surname"));
					grade.setStudentName(result.getString("name"));
					grade.setEmail(result.getString("email"));
					grade.setDegreeCourse(result.getString("degree_course"));
					if (result.getBoolean("failed") == true) {
						grade.setGrade("failed");
					} else if (result.getBoolean("recalled") == true) {
						grade.setGrade("recalled");
					} else if (result.getBoolean("absent") == true) {
						grade.setGrade("absent");
					} else {
						if (result.getBoolean("merit") == true) {
							grade.setGrade(Integer.toString(result.getInt("grade")) + " with merit");
						} else {
							grade.setGrade(Integer.toString(result.getInt("grade")));
						}
					}
					grade.setState(result.getString("state"));
					grades.add(grade);
					phase++;
				}
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
	
	public List<Grade> getGradesByFieldDesc(int appealId, String field) throws SQLException,InvalidParameterException{
		List<Grade> grades=new ArrayList<Grade>();
		String query;
		switch(field) {
			case "studentId":query="SELECT * FROM student AS s1 join exam AS e1 on s1.id_student=e1.id_student WHERE e1.id_appeal=? ORDER BY s1.id_student DESC";
				break;
			case "surname": query="SELECT * FROM student AS s1 join exam AS e1 on s1.id_student=e1.id_student WHERE e1.id_appeal=? ORDER BY s1.surname DESC";
				break;
			case "name": query="SELECT * FROM student AS s1 join exam AS e1 on s1.id_student=e1.id_student WHERE e1.id_appeal=? ORDER BY s1.name DESC";
				break;
			case "email":query="SELECT * FROM student AS s1 join exam AS e1 on s1.id_student=e1.id_student WHERE e1.id_appeal=? ORDER BY s1.email DESC";
				break;
			case "degree_course": query="SELECT * FROM student AS s1 join exam AS e1 on s1.id_student=e1.id_student WHERE e1.id_appeal=? ORDER BY s1.degree_course DESC";
				break;
			case "grade": {query="(SELECT * FROM student AS s1 join exam AS e1 on s1.id_student=e1.id_student WHERE e1.id_appeal=?" +" UNION"+" SELECT * FROM student AS s2 "
					+ "join exam AS e2 on s2.id_student=e2.id_student WHERE e2.id_appeal=? and e2.state='not entered')ORDER BY merit DESC, "
					+ "grade DESC,recalled DESC, absent DESC,failed DESC";
					break;}
			case "state": {query="(SELECT * FROM student AS s1 join exam AS e1 on s1.id_student=e1.id_student WHERE e1.id_appeal=? and e1.state='recorded')"+" UNION"
					+ "(SELECT * FROM student AS s2 join exam AS e2 on s2.id_student=e2.id_student WHERE e2.id_appeal=? and e2.state='refused')"+" UNION"
					+ "(SELECT * FROM student AS s3 join exam AS e3 on s3.id_student=e3.id_student WHERE e3.id_appeal=? and e3.state='published')"+" UNION"
					+ "(SELECT * FROM student AS s4 join exam AS e4 on s4.id_student=e4.id_student WHERE e4.id_appeal=? and e4.state='entered')"+" UNION"
					+ "(SELECT * FROM student AS s1 join exam AS e1 on s1.id_student=e1.id_student WHERE e1.id_appeal=? and e1.state='not entered')";
					break;}	
			default: throw new InvalidParameterException();
		}
		ResultSet result=null;
		PreparedStatement pstatement=null;
		
		try {
			pstatement=connection.prepareStatement(query);
			pstatement.setInt(1, appealId);
			
			if(field.equals("grade")||field.equals("state")) {
				pstatement.setInt(2, appealId);
			}
			if(field.equals("state")) {
				pstatement.setInt(3, appealId);
				pstatement.setInt(4, appealId);
				pstatement.setInt(5, appealId);
			}
			result=pstatement.executeQuery();
			
			while(result.next()) {
				Grade grade=new Grade();
				grade.setAppealId(result.getInt("id_appeal"));
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
						grade.setGrade(Integer.toString(result.getInt("grade"))+" with merit");
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
	
	public void enterGrade(int appealId, int studentId, String gradeValue) throws SQLException {
		String query="UPDATE exam SET state='entered', failed=?, recalled=?, absent=?, grade=?, merit=? WHERE id_appeal=? and id_student=?";

		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);

			if(gradeValue.equalsIgnoreCase("failed")) {
				pstatement.setBoolean(1, true);
				pstatement.setBoolean(2, false);
				pstatement.setBoolean(3, false);
				pstatement.setNull(4, java.sql.Types.INTEGER);
				pstatement.setNull(5, java.sql.Types.BOOLEAN);
			}
			else if(gradeValue.equalsIgnoreCase("recalled")) {
				pstatement.setBoolean(1, false);
				pstatement.setBoolean(2, true);
				pstatement.setBoolean(3, false);
				pstatement.setNull(4, java.sql.Types.INTEGER);
				pstatement.setNull(5, java.sql.Types.BOOLEAN);
			}
			else if(gradeValue.equalsIgnoreCase("absent")) {
				pstatement.setBoolean(1, false);
				pstatement.setBoolean(2, false);
				pstatement.setBoolean(3, true);
				pstatement.setNull(4, java.sql.Types.INTEGER);
				pstatement.setNull(5, java.sql.Types.BOOLEAN);
			}
			else if(gradeValue.equalsIgnoreCase("30 with merit")){
				pstatement.setBoolean(1, false);
				pstatement.setBoolean(2, false);
				pstatement.setBoolean(3, false);
				pstatement.setInt(4, 30);
				pstatement.setBoolean(5, true);	
			}
			else {
				pstatement.setBoolean(1, false);
				pstatement.setBoolean(2, false);
				pstatement.setBoolean(3, false);
				pstatement.setInt(4, Integer.parseInt(gradeValue));
				pstatement.setBoolean(5, false);
			}
			pstatement.setInt(6, appealId);
			pstatement.setInt(7, studentId);
			pstatement.executeUpdate();
			} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			try {
				pstatement.close();
			} catch (Exception e1) {}
		}	
	}
	
	
	
	public int editGrade(Grade grade) throws SQLException {
		String query = "UPDATE exam SET state=?, failed=?, recalled=?, absent=?, grade=?, merit=? WHERE id_appeal=? and id_student=?" ;
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
				pstatement.setNull(6, java.sql.Types.BOOLEAN);
			}
			else if(grade.getGrade().equals("recalled")) {
				pstatement.setBoolean(2, false);
				pstatement.setBoolean(3, true);
				pstatement.setBoolean(4, false);
				pstatement.setNull(5, java.sql.Types.INTEGER);
				pstatement.setNull(6, java.sql.Types.BOOLEAN);
			}
			else if(grade.getGrade().equals("absent")) {
				pstatement.setBoolean(2, false);
				pstatement.setBoolean(3, false);
				pstatement.setBoolean(4, true);
				pstatement.setNull(5, java.sql.Types.INTEGER);
				pstatement.setNull(6, java.sql.Types.BOOLEAN);
			}
			else {
				if(grade.getGrade().equals("30 with merit")) {
					pstatement.setBoolean(2, false);
					pstatement.setBoolean(3, false);
					pstatement.setBoolean(4, false);
					pstatement.setInt(5, 30);
					pstatement.setBoolean(6, true);
				}
				else {
					pstatement.setBoolean(2, false);
					pstatement.setBoolean(3, false);
					pstatement.setBoolean(4, false);
					pstatement.setInt(5, Integer.parseInt(grade.getGrade()));
					pstatement.setNull(6, java.sql.Types.BOOLEAN);
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
	
	/**
	 * @ensures \result is either '1', in case update query was correct, or '0' in case something went wrong
	 */
	public int refuseGrade(int appealId, int studentId) throws SQLException{
		connection.setAutoCommit(false);
		String query = "UPDATE exam SET state='refused' WHERE id_appeal = ? and id_student = ?";
		PreparedStatement pstatement = null;
		ResultSet rs = null;
		int exitCode = 1;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, appealId);
			pstatement.setInt(2, studentId);
			pstatement.executeUpdate();
			connection.commit();
		} catch (SQLException e){
			connection.rollback();
			exitCode = 0;
		} finally {
			connection.setAutoCommit(true);
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				throw new SQLException("Couldn't close ResultSet");
			};
			try {
				if (pstatement != null)
					pstatement.close();
			} catch (Exception e) {
				throw new SQLException("Couldn't close Statement");
			};
		};
		return exitCode;
	}
	
	/**
	 * @ensures \result is either '1', in case update query was correct, or '0' in case something went wrong
	 */
	public int failRefused(int appealId) throws SQLException{
		String query="UPDATE exam SET failed=1, recalled=0, absent=0, grade=null, merit=null WHERE id_appeal=? and state = 'refused'";
		PreparedStatement pstatement = null;
		ResultSet rs = null;
		int exitCode = 1;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, appealId);
			pstatement.executeUpdate();
		} catch (SQLException e){
			connection.rollback();
			exitCode = 0;
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				throw new SQLException("Couldn't close ResultSet");
			};
			try {
				if (pstatement != null)
					pstatement.close();
			} catch (Exception e) {
				throw new SQLException("Couldn't close Statement");
			};
		};
		return exitCode;
	}
	
	
	/**
	 * @ensures \result is either '1', in case update query was correct, or '0' in case something went wrong
	 * //CALL THIS METHOD INSIDE ONE WITH COMMIT CONTROL!!
	 */
	public int reportGrade(int appealId, int reportId) throws SQLException{
		String query = "UPDATE exam SET state='recorded', id_report = ? WHERE id_appeal = ? and (state = 'published' or state = 'refused')";
		PreparedStatement pstatement = null;
		ResultSet rs = null;
		int exitCode = 1;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, reportId);
			pstatement.setInt(2, appealId);
			pstatement.executeUpdate();
		} catch (SQLException e){
			e.printStackTrace();
			exitCode = 0;
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				throw new SQLException("Couldn't close ResultSet");
			};
			try {
				if (pstatement != null)
					pstatement.close();
			} catch (Exception e) {
				throw new SQLException("Couldn't close Statement");
			};
		};
		return exitCode;
	}

	
	public int countReportableGrades (int appealId) throws SQLException{
		String query = "SELECT count(*) FROM exam WHERE (state='published' or state='refused') and id_appeal = ?";
		PreparedStatement pstatement = null;
		ResultSet rs = null;
		int result = 0;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, appealId);
			rs = pstatement.executeQuery();
			if(rs.next()) result = rs.getInt(1);
		} catch (SQLException e){
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				throw new SQLException("Couldn't close ResultSet");
			};
			try {
				if (pstatement != null)
					pstatement.close();
			} catch (Exception e) {
				throw new SQLException("Couldn't close Statement");
			};
		};
		return result;
	}
	
	
	public void publishGrade(int appealId) throws SQLException{
		String query = "UPDATE exam SET state='published' WHERE id_appeal = ? and state='entered'";
		PreparedStatement pstatement = null;
		
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, appealId);
			
			pstatement.executeUpdate();
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			try {
				pstatement.close();
			} catch (Exception e1) {
			}
		}
	}
	
	public Grade getResultByAppealAndStudent(int appealId, int studentId) throws SQLException {
		Grade grade=null;
		String query="SELECT * FROM student AS s1 join exam AS e1 on s1.id_student=e1.id_student WHERE e1.id_appeal=? and e1.id_student=?";
		
		ResultSet result=null;
		PreparedStatement pstatement=null;
		
		try {
			pstatement=connection.prepareStatement(query);
			pstatement.setInt(1, appealId);
			pstatement.setInt(2, studentId);
			result=pstatement.executeQuery();
			
			while (result.next()) {
				grade = new Grade();
				grade.setAppealId(result.getInt("id_appeal"));
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
						grade.setGrade(Integer.toString(result.getInt("grade"))+" with merit");
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
	
	public List<Grade> getRecordedGrades (Report report) throws SQLException{
		Grade grade=null;
		String query="SELECT * FROM exam JOIN report ON (exam.id_appeal = report.id_appeal and exam.id_report = report.id_report) "
									  + "JOIN student ON exam.id_student = student.id_student "
									  + "WHERE exam.state='recorded' and exam.id_appeal=? and report.id_report = ?";
		ResultSet result=null;
		PreparedStatement pstatement=null;
		List<Grade> resultList = new LinkedList<>();
		try {
			pstatement=connection.prepareStatement(query);
			pstatement.setInt(1, report.getAppeal().getAppealId());
			pstatement.setInt(2, report.getReportId());
			result=pstatement.executeQuery();
			while (result.next()) {
				grade = new Grade();
				grade.setAppealId(result.getInt("exam.id_appeal"));
				grade.setStudentId(result.getInt("exam.id_student"));
				grade.setStudentSurname(result.getString("student.surname"));
				grade.setStudentName(result.getString("student.name"));
				grade.setEmail(result.getString("student.email"));
				grade.setDegreeCourse(result.getString("student.degree_course"));
				if(result.getBoolean("exam.failed")==true) {
					grade.setGrade("failed");
				}
				else if(result.getBoolean("exam.recalled")==true) {
					grade.setGrade("recalled");
				}
				else if(result.getBoolean("exam.absent")==true) {
					grade.setGrade("absent");
				}
				else {
					if(result.getBoolean("exam.merit")==true) {
						grade.setGrade(Integer.toString(result.getInt("grade"))+" with merit");
					}
					else {
						grade.setGrade(Integer.toString(result.getInt("grade")));
					}
				}
				grade.setState(result.getString("exam.state"));
				resultList.add(grade);
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
		return resultList;		
	}
	
}
