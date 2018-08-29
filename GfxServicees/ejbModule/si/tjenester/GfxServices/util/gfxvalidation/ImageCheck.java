package si.tjenester.GfxServices.util.gfxvalidation;

import si.tjenester.GfxServices.util.db.ResourceHandler;
import si.tjenester.GfxServices.util.io.FileIoHandler;
import si.tjenester.GfxServices.util.log.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.pdfbox.exceptions.CryptographyException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.xmlgraphics.image.loader.ImageException;

import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.ICC_Profile;

public class ImageCheck {
	private Image img=null;
	private ResourceHandler res = null;
	
	public ImageCheck() {
		res = new ResourceHandler();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ImageCheck test = new ImageCheck();
		try {
			FileIoHandler fio = new FileIoHandler();
			//byte [] imageb = fio.getfileAsbytea("/home/janmj/test/Gfxtest/jpg/0.jpg");
			byte [] imageb = fio.getfileAsbytea("/home/janmj/Nedlastinger/C01902400f.JPG");
			//byte [] imageb = fio.getfileAsbytea("/home/janmj/test/Gfxtest/tiff/out_1.tif");
			
			Image testimg = Image.getInstance(imageb);
			test.checkImage(testimg);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void checkPdf(byte[] pdf)throws ImageCheckException{
		PDDocument doc = null;
		try {
			doc = PDDocument.load(new ByteArrayInputStream(pdf));
			if(doc.isEncrypted()){
				doc.decrypt("");
				//logger.info("Fjernet krypptering");
			}
			
			doc.setAllSecurityToBeRemoved(true);
			//logger.info("Fjernet beskyttelse");
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			doc.save(bos);
		} catch (Exception e) {
			throw new ImageCheckException("0000214");
		}finally{
			try {
				doc.close();
			} catch (IOException e) {}
		}
	}
	
	public void checkImage(Image img)throws ImageCheckException{
		this.img = img;
		try {
			try {
				checkResolution();
			} catch (Exception e) {
				//For lav oppløsning
				throw new ImageCheckException("0000211");
			}
			try {
				checkDpi();
			} catch (Exception e) {
				//For lav DPI!
				throw new ImageCheckException("0000212");
			}
			try {
				checkColours();
			} catch (Exception e) {
				//Bilde for lyst/mørkt!
				throw new ImageCheckException("0000213");
			}
		} catch (Exception e) {
			throw new ImageCheckException(e.getMessage(), e.getCause());
		}
	}
	
	private void checkResolution()throws Exception{
		float x = img.getWidth();
		float y = img.getHeight();
		
		float chkValue = x*y;
		// 921 600 = 1280*720
		// 2 304 000 = 1600*1440
		// 2 073 600 = 1920*1080
		int refvalue = 0;
		try{
			refvalue = new Integer(res.getResourceString("IMAGE_RESOLUTION")).intValue();
		}catch (Exception e) {
			Logger.logError("Feilet i henting avoppløsnings verdi", e.getMessage());
			System.out.println("Feilet i henting avoppløsnings verdi: " + e.getMessage());
		}
		//System.out.println("Refvalue er: " + refvalue);
		//if(chkValue<921600){
		if(chkValue<refvalue){
			throw new Exception("For lav oppløsning!");
		}
		System.out.println("Bredde: " +x + " Høyde: " + y);	
	}
	
	private void checkDpi()throws Exception{
		int x = img.getDpiX();
		int y = img.getDpiY();
		//System.out.println("DPI-Bredde: " +x + " DPI-Høyde: " + y);

		int refDpi = 72;
		try{
			refDpi = new Integer(res.getResourceString("IMAGE_DPI")).intValue();
		}catch (Exception e) {
			Logger.logError("Feilet i henting DPI verdi", e.getMessage());
			System.out.println("Feilet i henting DPI verdi: " + e.getMessage());
		}
		if((x==y)&&(x!=0)){
			if(x<refDpi){
				throw new Exception("For lav DPI!");
			}
		}
	}
	
	private void checkColours()throws Exception{
		ICC_Profile prf = null;
		prf = img.getICCProfile();
		
	}
}
