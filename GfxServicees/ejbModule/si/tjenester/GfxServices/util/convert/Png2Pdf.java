package si.tjenester.GfxServices.util.convert;

import java.io.ByteArrayOutputStream;

import si.tjenester.GfxServices.util.gfxvalidation.ImageCheck;
import si.tjenester.GfxServices.util.io.FileIoHandler;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

public class Png2Pdf {
	
	public Png2Pdf(){
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Png2Pdf test = new Png2Pdf();
		try {
			FileIoHandler fio = new FileIoHandler();
			byte [] tiffimage = fio.getfileAsbytea("/home/janmj/test/Gfxtest/png/0.png");	
			//byte [] pdffile = test.convert(tiffimage);
			byte [] pdffile = test.convertTest(tiffimage);
			fio.savefile(pdffile, "/home/janmj/test/Gfxtest/pdf/out_png.pdf");					
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}		
	}
	
	private byte[] convertTest(byte[] gifimage)throws Exception{
		byte[] retval = null;
		String errorcode = "0000113";
		Document doc = new Document(PageSize.A4,0,0,0,0);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try{
			PdfWriter writer = PdfWriter.getInstance(doc, out);
			doc.open();
			writer.open();
			Image img = Image.getInstance(gifimage);
			try {
				ImageCheck chkimg = new ImageCheck();
				chkimg.checkImage(img);
			} catch (Exception e) {
				errorcode = e.getMessage();
				throw new Exception(e.getMessage());
			}
			//img.scalePercent(7200f/img.getDpiX(), 7200f/img.getDpiY());
			img.scaleToFit(PageSize.A4.getWidth(), PageSize.A4.getHeight());
			//img.setAbsolutePosition(0,  0);
			img.setAlignment(Image.TOP);
			doc.add(img);
			
			doc.close();
			writer.close();
			out.flush();
			retval = out.toByteArray();
		}catch (Exception e) {
			//throw new Exception("Feilet i Png2Pdf: " + e.getMessage());
			throw new Exception(errorcode);			
		}finally{
			try {
				doc.close();
				out.close();
			} catch (Exception e2) {}
		}
		
		return retval;
	}
	
	public byte[] convert(byte[] gifimage)throws Exception{
		byte[] retval = null;
		String errorcode = "0000113";
		Document doc = new Document(PageSize.A4,0,0,0,0);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try{
			PdfWriter writer = PdfWriter.getInstance(doc, out);
			doc.open();
			writer.open();
			Image img = Image.getInstance(gifimage);
			try {
				ImageCheck chkimg = new ImageCheck();
				chkimg.checkImage(img);
			} catch (Exception e) {
				errorcode = e.getMessage();
				throw new Exception(e.getMessage());
			}			
			//img.scalePercent(7200f/img.getDpiX(), 7200f/img.getDpiY());
			img.scaleToFit(PageSize.A4.getWidth(), PageSize.A4.getHeight());
			img.setAlignment(Image.TOP);
			//img.setAbsolutePosition(0,  0);
			
			doc.add(img);
			
			doc.close();
			writer.close();
			out.flush();
			retval = out.toByteArray();
		}catch (Exception e) {
			//throw new Exception("Feilet i Png2Pdf: " + e.getMessage());
			throw new Exception(errorcode);
		}finally{
			try {
				doc.close();
				out.close();
			} catch (Exception e2) {}
		}
		
		return retval;
	}	
}
