package si.tjenester.GfxServices.util.convert;

import java.io.ByteArrayOutputStream;

import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputField;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;

import si.tjenester.GfxServices.util.gfxvalidation.ImageCheck;
import si.tjenester.GfxServices.util.gfxvalidation.ImageCheckException;
import si.tjenester.GfxServices.util.io.FileIoHandler;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

public class Jpeg2Pdf {

	public Jpeg2Pdf(){
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Jpeg2Pdf test = new Jpeg2Pdf();
		try {
			FileIoHandler fio = new FileIoHandler();
			//byte [] tiffimage = fio.getfileAsbytea("/home/janmj/test/Gfxtest/jpg/0.jpg");
			byte [] tiffimage = fio.getfileAsbytea("/home/janmj/test/Gfxtest/jpg/exif-test/Portrait_6.jpg");
			//byte [] pdffile = test.convert(tiffimage);
			byte [] pdffile = test.convertTest(tiffimage);
			fio.savefile(pdffile, "/home/janmj/test/Gfxtest/pdf/out_jpg.pdf");					
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public byte[] convert(byte[] jpgimage)throws Exception{
		byte[] retval = null;
		String errorcode = "0000111";
		Document doc = new Document(PageSize.A4,0,0,0,0);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try{
			jpgimage = ExifHandling(jpgimage);
			PdfWriter writer = PdfWriter.getInstance(doc, out);
			doc.open();
			writer.open();
			Image img = Image.getInstance(jpgimage);
			try {
				ImageCheck imgchk = new ImageCheck();
				imgchk.checkImage(img);
			} catch (ImageCheckException e) {
				//Logger.logError("Feilet i grafikkkonvertering med kode: " + errorcode, e.getMessage());
				errorcode = e.getMessage();
				throw new Exception(e.getMessage());
			}
			img.scaleToFit(PageSize.A4.getWidth(), PageSize.A4.getHeight());
			//img.setAbsolutePosition(0,  0);
			img.setAlignment(Image.TOP);
			doc.add(img);
			doc.close();
			writer.close();
			out.flush();
			retval = out.toByteArray();
		}catch (Exception e) {
			//Logger.logError("Feilet i grafikkkonvertering med kode: " + errorcode, e.getMessage());
			throw new Exception(errorcode);
		}finally{
			try {
				doc.close();
				out.close();
			} catch (Exception e2) {}
		}
		
		return retval;
	}	
	
	private byte[] ExifHandling(byte[] jpgimage)throws Exception{
		byte[] retval = null;
		final int ORIENTATION = 0x0112;
		try {
			ImageMetadata metadata = Imaging.getMetadata(jpgimage);
			
			if(metadata instanceof JpegImageMetadata){
				JpegImageMetadata jpegmetadata = (JpegImageMetadata)metadata;
				TiffImageMetadata exifdata = jpegmetadata.getExif();
				TiffOutputSet outputset = exifdata.getOutputSet();
				TiffOutputDirectory exifdirectory = outputset.getRootDirectory();
				
				TiffOutputField orientationfield = exifdirectory.findField(ORIENTATION);
				if(orientationfield == null){
					retval = jpgimage;
				}else{
					javaxt.io.Image image = new javaxt.io.Image(jpgimage);
					image.rotate();
					exifdirectory.removeField(ORIENTATION);
					retval = image.getByteArray();
				}
			}else{
				retval = jpgimage;
			}
			
		} catch (Exception e) {
			throw new Exception("Feilet i EXIF håndtering: " + e.getMessage());
		}
		return retval;
	}
	
	private byte[] convertTest(byte[] jpgimage)throws Exception{
		byte[] retval = null;
		Document doc = new Document(PageSize.A4,0,0,0,0);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try{
			jpgimage = ExifHandling(jpgimage);
			PdfWriter writer = PdfWriter.getInstance(doc, out);
			doc.open();
			writer.open();
			Image img = Image.getInstance(jpgimage);
			/*Må disable bildesjekk når jeg ikke kjører i app-serveren
			try {
				ImageCheck imgchk = new ImageCheck();
				imgchk.checkImage(img);
			} catch (Exception e) {
				throw new Exception(e.getMessage());
			}
			*/
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
			throw new Exception("Feilet i Jpeg2Pdf: " + e.getMessage());
		}finally{
			try {
				doc.close();
				out.close();
			} catch (Exception e2) {}
		}
		
		return retval;
	}	
}
