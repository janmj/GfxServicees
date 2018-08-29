package si.tjenester.GfxServices.util;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.soap.SOAPFaultException;

import com.itextpdf.text.Image;

import si.tjenester.GfxServices.bean.ImageBean;
import si.tjenester.GfxServices.util.gfxvalidation.ImageCheck;
import si.tjenester.GfxServices.util.gfxvalidation.ImageCheckException;
import si.tjenester.GfxServices.util.io.FileIoHandler;

public class GfxVerifyFactory {

	public GfxVerifyFactory() {
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GfxVerifyFactory test = new GfxVerifyFactory();
		try {
			FileIoHandler fio = new FileIoHandler();
			byte [] imageb = fio.getfileAsbytea("/home/janmj/test/Gfxtest/png/0.png");
			ImageBean imgbean = new ImageBean("test.tif",
											   "image/png",
											   imageb);
			System.out.println(test.validateImage(imgbean));
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public Boolean validateImage(ImageBean image) throws SOAPFaultException{
		Boolean retval = new Boolean(false); 
		SOAPFault fault = null;
		try{
			try{
				if(image.getMimetype().equalsIgnoreCase("application/pdf")){
					ImageCheck imgchk = new ImageCheck();
					imgchk.checkPdf(image.getImagedata());
				}else{
					byte[] imgB = image.getImagedata();
					//System.out.println("Størrelse på mottatt bilde: " + imgB.length);
					Image img = Image.getInstance(imgB);
					ImageCheck imgchk = new ImageCheck();
					imgchk.checkImage(img);
				}
				retval = new Boolean(true);
			}catch (ImageCheckException e) {
				retval = new Boolean(false);
				SOAPFactory sfact = SOAPFactory.newInstance();
				fault = sfact.createFault(e.getMessage(), new QName("http://schemas.xmlsoap.org/soap/envelope/", "gfxmodule","gfxmodule"));
				fault.setFaultCode(e.getMessage());
				throw new SOAPFaultException(fault);
			}
		}catch (Exception e) {
			retval = new Boolean(false);
			try {
				SOAPFactory sfact = SOAPFactory.newInstance();
				fault = sfact.createFault(e.getMessage(), new QName("http://schemas.xmlsoap.org/soap/envelope/", "gfxmodule","gfxmodule"));
				fault.setFaultCode(e.getMessage());
			} catch (SOAPException e1) {
				System.out.println("Feilet i siste forsøk på SOAPException.. : " + e1.getMessage());
			}
			throw new SOAPFaultException(fault);
		}	
		
		return retval;
	}

}
