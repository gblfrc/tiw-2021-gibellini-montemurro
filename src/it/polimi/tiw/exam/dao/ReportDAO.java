package it.polimi.tiw.exam.dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import it.polimi.tiw.exam.objects.*;

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
				result.setReportId(rs.getInt("id_report"));
				AppealDAO auxAppDAO = new AppealDAO(connection);
				result.setAppeal(auxAppDAO.getAppealById(rs.getInt("id_appeal")));
				result.setCreationDate(rs.getDate("date").toString());
				result.setCreationTime(rs.getTime("time").toString());
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
			};
		};
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
				result.setReportId(rs.getInt("id_report"));
				result.setAppeal(appeal);
				result.setCreationDate(rs.getDate("date").toString());
				result.setCreationTime(rs.getTime("time").toString());
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

	
	//public Report createReport(int appealId) {}
	//unlock this method after finalization of GradeDAO
	//need GradeDAO method to report grades?
	
}
