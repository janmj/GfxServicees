package si.tjenester.GfxServices.util.convert;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

import si.tjenester.GfxServices.util.gfxvalidation.ImageCheck;
import si.tjenester.GfxServices.util.io.FileIoHandler;

/*
import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import com.lowagie.text.pdf.codec.TiffImage;
*/

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.io.RandomAccessSourceFactory;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.RandomAccessFileOrArray;
import com.itextpdf.text.pdf.codec.TiffImage;

public class Tiff2Pdf {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Tiff2Pdf test = new Tiff2Pdf();
		try {
			//test.convertTest("/home/janmj/test/Gfxtest/tiff/blDok.tif", "/home/janmj/test/Gfxtest/pdf/out.pdf");
			FileIoHandler fio = new FileIoHandler();
			byte [] tiffimage = fio.getfileAsbytea("/home/janmj/test/Gfxtest/tiff/out_1.tif");
			byte [] pdffile = test.convert(tiffimage);
			fio.savefile(pdffile, "/home/janmj/test/Gfxtest/pdf/out_1.pdf");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public Tiff2Pdf(){

	}

	public byte[] convert(byte[] tiffimage)throws Exception{
		Document doc = new Document(PageSize.A4,0,0,0,0);
		RandomAccessFileOrArray ra = null;
		byte [] retval = null;
		String errorcode = "0000114";
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			PdfWriter writer = PdfWriter.getInstance(doc, out);
			int pages = 0;
			doc.open();
			PdfContentByte cb = writer.getDirectContentUnder();
			int comps = 0;
			ra = new RandomAccessFileOrArray(tiffimage);
			comps = TiffImage.getNumberOfPages(ra);
			for(int x=0;x<comps;x++){
				Image img = TiffImage.getTiffImage(ra, x+1);
				try {
					ImageCheck imgchk = new ImageCheck();
					imgchk.checkImage(img);
				} catch (Exception e) {
					errorcode = e.getMessage();
					throw new Exception(e.getMessage());
				}
				img.scalePercent(7200f/img.getDpiX(), 7200f/img.getDpiY());
				doc.setPageSize(new Rectangle(img.getScaledWidth(), img.getScaledHeight()));
				img.setAbsolutePosition(0,  0);
				//img.setAlignment(Image.TOP);
				cb.addImage(img);
				doc.newPage();
				++pages;
			}
			doc.close();
			out.flush();
			retval = out.toByteArray();
		} catch (Exception e) {
			//throw new Exception("Feilet i konvertering tiff - pdf: " + e.getMessage());
			//System.out.println(e.getMessage());
			e.printStackTrace();
			throw new Exception(errorcode);
		}finally{
			try {
				ra.close();
				doc.close();
			} catch (Exception e2) {}
		}
		return retval;
	}
	
	public byte[] convert(byte[] tiffimage, ArrayList<String>pages)throws Exception{
		Document doc = new Document(PageSize.A4,0,0,0,0);
		RandomAccessFileOrArray ra = null;
		byte [] retval = null;
		String errorcode = "0000114";
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			PdfWriter writer = PdfWriter.getInstance(doc, out);
			doc.open();
			PdfContentByte cb = writer.getDirectContentUnder();
			int comps = 0;
			ra = new RandomAccessFileOrArray(tiffimage);
			comps = TiffImage.getNumberOfPages(ra);
			for(int x=0;x<comps;x++){
				Image img = TiffImage.getTiffImage(ra, x+1);
				try {
					ImageCheck imgchk = new ImageCheck();
					imgchk.checkImage(img);
				} catch (Exception e) {
					errorcode = e.getMessage();
					throw new Exception(e.getMessage());
				}
				img.scalePercent(7200f/img.getDpiX(), 7200f/img.getDpiY());
				doc.setPageSize(new Rectangle(img.getScaledWidth(), img.getScaledHeight()));
				img.setAbsolutePosition(0,  0);
				if(checkPage(x+1, pages)){
					cb.addImage(img);
					doc.newPage();					
				}
				
			}
			doc.close();
			out.flush();
			retval = out.toByteArray();
		} catch (Exception e) {
			//throw new Exception("Feilet i konvertering tiff - pdf: " + e.getMessage());
			throw new Exception(errorcode);
		}finally{
			try {
				ra.close();
				doc.close();
			} catch (Exception e2) {}
		}
		return retval;
	}
	
	private boolean checkPage(int page, ArrayList<String> pages){
		boolean retval = false;
		for(int x=0;x<pages.size();x++){
			int toCompare = new Integer(pages.get(x)).intValue();
			if(page == toCompare){
				retval = true;
			}
		}
		return retval;
	}
	
	private void convertTest(String tiffFileInn, String tiffFileout)throws Exception{
		Document doc = new Document(PageSize.A4,0,0,0,0);
		RandomAccessFileOrArray ra = null;
		try {
			PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(tiffFileout));
			int pages = 0;
			doc.open();
			PdfContentByte cb = writer.getDirectContentUnder();
			int comps = 0;
			ra = new RandomAccessFileOrArray(tiffFileInn);
			comps = TiffImage.getNumberOfPages(ra);
			for(int x=0;x<comps;x++){
				Image img = TiffImage.getTiffImage(ra, x+1);
				img.scalePercent(7200f/img.getDpiX(), 7200f/img.getDpiY());
				doc.setPageSize(new Rectangle(img.getScaledWidth(), img.getScaledHeight()));
				img.setAbsolutePosition(0,  0);
				cb.addImage(img);
				doc.newPage();
				++pages;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}finally{
			try {
				ra.close();
				doc.close();
			} catch (Exception e2) {
				
			}
		}
	}
}
