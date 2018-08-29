package si.tjenester.GfxServices.util.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class FileIoHandler {

	public FileIoHandler(){
		
	}
	
	public void savefile(byte[] savedata, String savepath)throws Exception{
		OutputStream out = null;
		try {
			out = new FileOutputStream(savepath);
			out.write(savedata);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}finally{
			out.close();
		}		
	}
	public String getfileAsString(String path)throws Exception{
		String retval = "";
		File tfile = new File(path);
		StringBuilder innhold = new StringBuilder();
		try {
			BufferedReader input = new BufferedReader(new FileReader(tfile));
			try {
				String strline = null;
				while((strline = input.readLine())!=null){
					innhold.append(strline);
					innhold.append(System.getProperty("line.separator"));
				}
				retval = innhold.toString();
			}finally{
				input.close();
			}
		} catch (Exception e) {
			throw new Exception("Feilet i lesing av fil: " + path + " Med melding: " +e.getMessage());
		}
		return retval;
	}
	
	public byte[] getfileAsbytea(String path)throws Exception{
		byte[] retval = null;
		InputStream is = null;
		try {
			File infil = new File(path);
			is = new FileInputStream(infil);
			long length = infil.length();
			if(length>Integer.MAX_VALUE){
				throw new Exception("Filen " + path+ " er for stor!" );
			}
			retval = new byte[(int)length];
			int offset=0;
			int numRead=0;
			while(offset < retval.length && (numRead=is.read(retval,offset,retval.length-offset))>=0){
				offset+=numRead;
			}
		} catch (Exception e) {
			throw new Exception("Feilet i fil til byteArray: " + e.getMessage());
		}finally{
			is.close();
		}
		
		return retval;
	}
	
	public String getfileAsUTF8String(String path)throws Exception{
		String retval ="";
		StringBuffer strBuf = new StringBuffer();
		try {
			BufferedReader buf = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path)),"UTF8"));
			try{
				String strLine=null;
				while((strLine = buf.readLine())!=null){
					strBuf.append(strLine);
					strBuf.append(System.getProperty("line.separator"));
				}
				retval = strBuf.toString();
			}finally{
				buf.close();
			}
		} catch (Exception e) {
			throw new Exception("Feilet i lesing av fil: " + path + " Med melding: " +e.getMessage());
		}
		return retval;
	}
}
