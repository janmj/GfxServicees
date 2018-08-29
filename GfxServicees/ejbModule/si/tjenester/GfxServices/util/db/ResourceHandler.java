package si.tjenester.GfxServices.util.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.InitialContext;
import javax.sql.DataSource;

public class ResourceHandler {

	public ResourceHandler() {
		
	}
	
	public byte[] getResourceValue(String identifyer)throws Exception{
		byte[] retval = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "select datavalue from gfxmod.resources where identifyer=?";
		try{
			con = dbConnect();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, identifyer);
			rs = pstmt.executeQuery();
			while(rs.next()){
				retval = rs.getBytes("datavalue");
			}
		}catch(Exception e){
			throw new Exception("Feilet i henting av binærdata fra reources: " + e.getMessage());
		}finally{
			try {
				pstmt.close();
				con.close();
			} catch (Exception e) {;}
		}
		return retval;
	}
	
	public String getResourceString(String identifyer)throws Exception{
		String retval = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "select textvalue from gfxmod.resources where identifyer=?";
		try{
			con = dbConnect();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, identifyer);
			rs = pstmt.executeQuery();
			while(rs.next()){
				retval = rs.getString("textvalue").trim();
			}
			if(retval==null){
				throw new Exception("Oppgitt property finnes ikke! ");
			}
		}catch(Exception e){
			throw new Exception("Feilet i henting av tekstdata fra resources: " + e.getMessage());
		}finally{
			try {
				pstmt.close();
				con.close();
			} catch (Exception e) {;}
		}
		return retval;
	}
	private Connection dbConnect()throws Exception{
		Connection con = null;
		InitialContext ic = new InitialContext();
		try {
			DataSource ds = (DataSource)ic.lookup("java:/GfxmodDS");
			con = ds.getConnection();
		} catch (Exception e) {
			throw new Exception("Feilet i Resourcehandler dbConnect: " + e.getMessage());
		}
		return con;
	}
}
