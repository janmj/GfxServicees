package no.si.gfxmodule.test.GfxModuleTest.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class IOHandler {
	
	public IOHandler(){
		
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

}
