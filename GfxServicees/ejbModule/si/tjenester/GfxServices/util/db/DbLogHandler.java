package si.tjenester.GfxServices.util.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.sql.DataSource;

public class DbLogHandler {
	
	
	public void logError(String exception, String message){
		Connection con = null;
		PreparedStatement pstmt = null;
		String sql = "insert into gfxlog.log (logmessage, logerror) values(?,?)";
		try {
			con = dbConnect();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, message);
			pstmt.setString(2, exception);
			pstmt.executeUpdate();

		}catch (Exception e) {
			System.err.println(e.getMessage());
		}finally{
			try {
				pstmt.close();
				con.close();
			} catch (SQLException e2) {
				System.err.println("Feilet i logError: " + e2.getMessage());
			}
		}
	}
	
	public void logMessage(String message){
		Connection con = null;
		PreparedStatement pstmt = null;
		String sql = "insert into gfxlog.log (logmessage) values(?)";
		try {
			con = dbConnect();
			//con.setAutoCommit(false);
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, message);
			pstmt.executeUpdate();
			//con.commit();
		} catch (Exception e) {
			/*
			try {
				con.rollback();
			} catch (SQLException e1) {
				System.out.println("Feilet i rollback av logging: " + e1.getMessage());
			}
			*/
			System.out.println("Feilet i logging av feilmelding: " + e.getMessage());
			//throw new Exception("Feilet i logging av logmelding: " + e.getMessage());
		}finally{
			try {
				//con.setAutoCommit(true);
				pstmt.close();
				con.close();
			} catch (SQLException e2) {
				System.err.println("Feilet i logmessage: " + e2.getMessage());
			}
		}		
	}
	
	private Connection dbConnect()throws Exception{
		Connection con = null;
		InitialContext ic = new InitialContext();
		try {
			DataSource ds = (DataSource)ic.lookup("java:/GfxmodLogDS");
			con = ds.getConnection();
		} catch (Exception e) {
			throw new Exception("Feilet i Resourcehandler dbConnect: " + e.getMessage());
		}
		return con;
	}
}
