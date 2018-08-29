package si.tjenester.GfxServices.util;

import java.util.ArrayList;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.pdfbox.exceptions.CryptographyException;

import si.tjenester.GfxServices.bean.ImageBean;
import si.tjenester.GfxServices.util.convert.Gif2Pdf;
import si.tjenester.GfxServices.util.convert.Jpeg2Pdf;
import si.tjenester.GfxServices.util.convert.Pdf2Gfx;
import si.tjenester.GfxServices.util.convert.Png2Pdf;
import si.tjenester.GfxServices.util.convert.Tiff2Pdf;
import si.tjenester.GfxServices.util.gfxConvert.ConvertFacade;
import si.tjenester.GfxServices.util.gfxConvert.TiffUtil;

public class Gfx2PdfFactory {

	public Gfx2PdfFactory(){
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}
	
	public byte[] makePdf(ArrayList<ImageBean> toconvert)throws SOAPFaultException{
		byte[] retval = null;
		SOAPFault fault = null;
		boolean knownmime = false;
		ArrayList<byte[]> pdfs = new ArrayList<byte[]>();
		try {
			for(int x=0;x<toconvert.size();x++){
				ImageBean tmpimage = toconvert.get(x);
				String mimetype = tmpimage.getMimetype();
				byte[] pdf = null;
				
				if(mimetype.equalsIgnoreCase("image/jpeg")||
				   mimetype.equalsIgnoreCase("image/pjpg")||
				   mimetype.equalsIgnoreCase("image/pjpeg")
				  ){
					Jpeg2Pdf conv = new Jpeg2Pdf();
					knownmime = true;
					try {
						pdf = conv.convert(tmpimage.getImagedata());
					} catch (Exception e) {
						SOAPFactory sfact = SOAPFactory.newInstance();
						fault = sfact.createFault(e.getMessage(), new QName("http://schemas.xmlsoap.org/soap/envelope/", "gfxmodule","gfxmodule"));
						fault.setFaultCode(e.getMessage());
						throw new SOAPFaultException(fault);
					}
				}else if(mimetype.equalsIgnoreCase("image/gif")){
					Gif2Pdf conv = new Gif2Pdf();
					knownmime = true;
					try {
						pdf = conv.convert(tmpimage.getImagedata());
					} catch (Exception e) {
						SOAPFactory sfact = SOAPFactory.newInstance();
						fault = sfact.createFault(e.getMessage(), new QName("http://schemas.xmlsoap.org/soap/envelope/", "gfxmodule","gfxmodule"));
						fault.setFaultCode(e.getMessage());
						throw new SOAPFaultException(fault);
					}
				}else if(mimetype.equalsIgnoreCase("image/png")||
						 mimetype.equalsIgnoreCase("image/x-png")
						){
					Png2Pdf conv = new Png2Pdf();
					knownmime = true;
					try {
						
							pdf = conv.convert(tmpimage.getImagedata());
						
					} catch (Exception e) {
						SOAPFactory sfact = SOAPFactory.newInstance();
						fault = sfact.createFault(e.getMessage(), new QName("http://schemas.xmlsoap.org/soap/envelope/", "gfxmodule","gfxmodule"));
						fault.setFaultCode(e.getMessage());
						throw new SOAPFaultException(fault);
					}
				}else if(mimetype.equalsIgnoreCase("image/tiff")){
					Tiff2Pdf conv = new Tiff2Pdf();
					knownmime = true;
					byte[] tiffimg = tmpimage.getImagedata();
					/**
					 * 
					 *Denne funker ikke helt som den skal.
					 *Er dessuten ikke nødvendig da SIFOB gjør sjekk og nødvendig invertering selv. 
					*
					
					TiffUtil tu = new TiffUtil();
					if(tu.checkTiffInvert(tiffimg)){
						tiffimg = tu.invertMultipleTiffs(tiffimg);
					}
					*/
					//Sjekker for invertering, inverterer om nødvendig.
					ConvertFacade cvf = new ConvertFacade();
					tiffimg =  cvf.checkAndInvertTiff(tiffimg).getImagedata();
					try {
						if(tmpimage.getPages().isEmpty()){
							pdf = conv.convert(tiffimg);
						}else{
							pdf = conv.convert(tiffimg,tmpimage.getPages());
							//pdf = conv.convert(tiffimg, new ArrayList(tmpimage.getPages().getList()));
						}	
					} catch (Exception e) {
						SOAPFactory sfact = SOAPFactory.newInstance();
						fault = sfact.createFault(e.getMessage(), new QName("http://schemas.xmlsoap.org/soap/envelope/", "gfxmodule","gfxmodule"));
						fault.setFaultCode(e.getMessage());
						throw new SOAPFaultException(fault);
					}
				}else if(mimetype.equalsIgnoreCase("application/pdf")){
					pdf = tmpimage.getImagedata();
					knownmime = true;
					if(tmpimage.getPages().isEmpty()){
						//pdfs.add(pdf);
					}else{
						Pdf2Gfx conv = new Pdf2Gfx();
						pdf = conv.removePages(pdf, tmpimage.getPages());
						//pdf = conv.removePages(pdf, new ArrayList(tmpimage.getPages().getList()));						
					}
				}
				pdfs.add(pdf);
			}
			
			if(knownmime){
				ConcatenatePdf concat = new ConcatenatePdf();
				try {
					retval = concat.concatenate(pdfs);
				}catch(CryptographyException e){
					SOAPFactory sfact = SOAPFactory.newInstance();
					fault = sfact.createFault(e.getMessage(), new QName("http://schemas.xmlsoap.org/soap/envelope/", "gfxmodule","gfxmodule"));
					fault.setFaultCode("0000214"); //Feilkode for passordbeskyttet PDF
					throw new Exception();
				}catch (Exception e) {
					SOAPFactory sfact = SOAPFactory.newInstance();
					fault = sfact.createFault(e.getMessage(), new QName("http://schemas.xmlsoap.org/soap/envelope/", "gfxmodule","gfxmodule"));
					fault.setFaultCode("0000115"); //Feilkode for pdf conkatenerings probelmer..
					throw new Exception();
				}				
			}else{
				SOAPFactory sfact = SOAPFactory.newInstance();
				fault = sfact.createFault("0000116", new QName("http://schemas.xmlsoap.org/soap/envelope/", "gfxmodule","gfxmodule"));
				fault.setFaultCode("0000116"); //Feilkode for ukjent mimetype..	
				throw new Exception();
			}
		} catch (Exception e) {
			throw new SOAPFaultException(fault);
			//throw new Exception("Feilet i makePdf: " + e.getMessage());
		}
		return retval;
	}
	
}
