package it.polimi.tiw.exam.dao;

import java.sql.*;
import it.polimi.tiw.exam.objects.*;
import java.sql.Date;
import java.util.Calendar;

public class ReportDAO {

	private Connection connection;

	// constructor with attributes
	public ReportDAO(Connection con) {
		this.connection = con;
	}

	public Report getReportById(int id) throws SQLException {
		String query = "SELECT * FROM report WHERE id_report = ?";
		PreparedStatement statement = null;
		ResultSet rs = null;
		Report result = null;
		try {
			statement = connection.prepareStatement(query);
			statement.setInt(1, id);
			rs = statement.executeQuery();
			if (rs.next() == true) {
				result = new Report();
				result.setReportId(rs.getInt("id_report"));
				AppealDAO auxAppDAO = new AppealDAO(connection);
				result.setAppeal(auxAppDAO.getAppealById(rs.getInt("id_appeal")));
				result.setCreationDate(rs.getDate("date"));
				result.setCreationTime(rs.getTime("time"));
				// result.setGrades complete this statement after finalizing GradeDAO
			}
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				throw new SQLException("Couldn't close ResultSet");
			}
			;
			try {
				if (statement != null)
					statement.close();
			} catch (Exception e) {
				throw new SQLException("Couldn't close Statement");
			}
			;
		}
		;
		return result;
	}

	public Report getReportByAppeal(Appeal appeal) throws SQLException {
		String query = "SELECT * FROM report WHERE id_appeal = ?";
		PreparedStatement statement = null;
		ResultSet rs = null;
		Report result = null;
		try {
			statement = connection.prepareStatement(query);
			statement.setInt(1, appeal.getAppealId());
			rs = statement.executeQuery();
			if (rs.next() == true) {
				result = new Report();
				result.setReportId(rs.getInt("id_report"));
				result.setAppeal(appeal);
				result.setCreationDate(rs.getDate("date"));
				result.setCreationTime(rs.getTime("time"));
				// result.setGrades complete this statement after finalizing GradeDAO
			}
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				throw new SQLException("Couldn't close ResultSet");
			}
			;
			try {
				if (statement != null)
					statement.close();
			} catch (Exception e) {
				throw new SQLException("Couldn't close Statement");
			}
			;
		}
		;
		return result;
	}

	public void createReport(int appealId) throws SQLException {
		connection.setAutoCommit(false);
		GradeDAO gradeDao = new GradeDAO(connection); //may want to check SQLExceptions
		int code = gradeDao.reportGrade(appealId);
		if (code == 1) {
			String query = "INSERT INTO report(id_appeal, date, hour) VALUES (?, ?, ?)";
			PreparedStatement pstatement = null;
			ResultSet rs = null;
			try {
				pstatement = connection.prepareStatement(query);
				pstatement.setInt(1, appealId);
				pstatement.setDate(2, new Date(Calendar.getInstance().getTime().getTime())); //introduces the current date
				pstatement.setTime(2, new Time(Calendar.getInstance().getTime().getTime())); //introduces the current time
				pstatement.executeUpdate();
				connection.commit();
			} catch (SQLException e) {
				connection.rollback();
			} finally {
				connection.setAutoCommit(true);
				try {
					if (rs != null)
						rs.close();
				} catch (Exception e) {
					throw new SQLException("Couldn't close ResultSet");
				}
				;
				try {
					if (pstatement != null)
						pstatement.close();
				} catch (Exception e) {
					throw new SQLException("Couldn't close Statement");
				}
				;
			}
			;
		}
	}

}