package si.tjenester.GfxServices.util.convert;

import java.io.ByteArrayOutputStream;

import si.tjenester.GfxServices.util.gfxvalidation.ImageCheck;
import si.tjenester.GfxServices.util.gfxvalidation.ImageCheckException;
import si.tjenester.GfxServices.util.io.FileIoHandler;
import si.tjenester.GfxServices.util.log.Logger;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.RandomAccessFileOrArray;
import com.itextpdf.text.pdf.codec.GifImage;
import com.itextpdf.text.pdf.codec.TiffImage;

public class Gif2Pdf {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Gif2Pdf test = new Gif2Pdf();
		try {
			FileIoHandler fio = new FileIoHandler();
			byte [] tiffimage = fio.getfileAsbytea("/home/janmj/test/Gfxtest/gif/0.gif");	
			//byte [] pdffile = test.convert(tiffimage);
			byte [] pdffile = test.convertTest_1(tiffimage);
			fio.savefile(pdffile, "/home/janmj/test/Gfxtest/pdf/out_gif.pdf");		
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public Gif2Pdf(){
		
	}
	
	public byte[] convert(byte[] gifImage)throws Exception{
		byte[] retval = null;
		String errorcode = "0000112";
		Document doc = new Document(PageSize.A4,0,0,0,0);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try{
			PdfWriter writer = PdfWriter.getInstance(doc, out);
			writer.open();
			doc.open();
			Image img = Image.getInstance(gifImage);
			try {
				ImageCheck imgchk = new ImageCheck();
				imgchk.checkImage(img);
			} catch (ImageCheckException e) {
				throw new Exception(e.getMessage());
			}
			img.scaleToFit(PageSize.A4.getWidth(), PageSize.A4.getHeight());
			img.setAlignment(Image.TOP);
			doc.add(img);
			
			//cleanup..
			doc.close();
			writer.close();
			out.flush();
			retval = out.toByteArray();
		}catch (Exception e) {
			throw new Exception(errorcode);
		}finally{
			try {
				doc.close();
				out.close();
			} catch (Exception e2) {}
		}
		return retval;
	}
	
	private byte[] convertTest(byte[] gifimage)throws Exception{
		byte[] retval = null;
		Document doc = new Document(PageSize.A4,0,0,0,0);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		RandomAccessFileOrArray ra = null;
		try{
			PdfWriter writer = PdfWriter.getInstance(doc, out);
			doc.open();
			PdfContentByte cb = writer.getDirectContentUnder();
			ra = new RandomAccessFileOrArray(gifimage);
			GifImage gimg = new GifImage(gifimage);
			Image img = gimg.getImage(1);
			//img.g
			img.scalePercent(7200f/img.getDpiX(), 7200f/img.getDpiY());
			doc.setPageSize(new Rectangle(img.getScaledWidth(), img.getScaledHeight()));
			img.setAbsolutePosition(0,  0);
			cb.addImage(img);
			doc.newPage();
			
			doc.close();
			out.flush();
			retval = out.toByteArray();
		}catch (Exception e) {
			throw new Exception("Feilet i Gif2Pdf: " + e.getMessage());
		}finally{
			try {
				doc.close();
				ra.close();
			} catch (Exception e2) {}
		}
		
		return retval;
	}
	
	private byte[] convertTest_1(byte[] gifImage)throws Exception{
		byte[] retval = null;
		Document doc = new Document(PageSize.A4,0,0,0,0);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try{
			PdfWriter writer = PdfWriter.getInstance(doc, out);
			writer.open();
			doc.open();
			Image img = Image.getInstance(gifImage);
			img.scaleToFit(PageSize.A4.getWidth(), PageSize.A4.getHeight());
			
			System.out.println(img.getDpiX() + "" + img.getDpiY());
			System.out.println("Width: " + img.getWidth() + " Height: " + img.getHeight());
			
			doc.add(img);
			
			//cleanup..
			doc.close();
			writer.close();
			out.flush();
			retval = out.toByteArray();
		}catch (Exception e) {
			throw new Exception("Feilet i convertTest: " + e.getMessage());
		}finally{
			try {
				doc.close();
				out.close();
			} catch (Exception e2) {}
		}
		return retval;
	}
	
}
