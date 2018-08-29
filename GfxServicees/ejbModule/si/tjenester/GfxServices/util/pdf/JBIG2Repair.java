package si.tjenester.GfxServices.util.pdf;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDJpeg;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;

import com.sun.org.apache.xalan.internal.xsltc.compiler.sym;

import si.tjenester.GfxServices.util.io.FileIoHandler;

public class JBIG2Repair {
	int imgcounter = 0;
	
	public JBIG2Repair() {
	}
	
	public static void main(String[] args){
		JBIG2Repair test = new JBIG2Repair();
		try {
			FileIoHandler fio = new FileIoHandler();
			byte[] fil = fio.getfileAsbytea("/home/jmj/test/Gfxtest/JBIG2/1502814610.pdf");
			//byte[] fil = fio.getfileAsbytea("/home/jmj/test/Gfxtest/pdf/wstestPDF.pdf");

			long start = System.currentTimeMillis();
			byte[] res =  test.recompressJBIG2(fil);
			long done = System.currentTimeMillis();
			long time = done-start;
			System.out.println("Tid: "+ time + " ms" );
			fio.savefile(res, "/home/jmj/test/Gfxtest/JBIG2/res.pdf");
			//test.analyzePdf();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	
	public byte[] recompressJBIG2(byte[] pdfdoc)throws Exception{
		byte [] retval = pdfdoc;
		try {
			if(analyzePdf(pdfdoc)){
				retval = recompressPdf(pdfdoc);
			}
		} catch (Exception e) {
			throw new Exception("Feilet i recompressJBIG2");
		}
		return retval;
	}
	
	private boolean analyzePdf(byte[] pdfdoc)throws Exception{
		PDDocument document = null;
		boolean retval = false;
		try {
			InputStream stream = new ByteArrayInputStream(pdfdoc);
			document = PDDocument.load(stream);
		} catch (IOException e) {
			throw new Exception("Kunne ikke åpne fil: " + e.getMessage());
		}
		try{
			List pages = document.getDocumentCatalog().getAllPages();
			Iterator itr = pages.iterator();
			while(itr.hasNext()){
				PDPage page = (PDPage)itr.next();				
				if( checkforJBIG2(page)){
					System.out.println("Filen har bilder komprimert med JBIG2!");
					retval = true;
					return true;
				}else{
					System.out.println("Ingen JBIG2!");
				}
				
			}
		}catch(Exception e){
			throw new Exception("feilet i prosessering av pdf: " + e.getMessage());
		}
		return retval;
	}
	
	private byte[] recompressPdf(byte[] pdfdoc)throws Exception{
		PDDocument document = null;
		PDDocument newdocument = null;
		byte[] retval = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			document = PDDocument.load(new ByteArrayInputStream(pdfdoc));
			newdocument = new PDDocument();
		} catch (IOException e) {
			throw new Exception("Kunne ikke åpne fil: " + e.getMessage());
		}
		try{
			List pages = document.getDocumentCatalog().getAllPages();
			Iterator itr = pages.iterator();
			while(itr.hasNext()){
				PDPage page = (PDPage)itr.next();				
				if( checkforJBIG2(page)){
					System.out.println("rekomprimerer JBIG2...");
					BufferedImage imgOfPage = page.convertToImage();
					float width = imgOfPage.getWidth();					
					float height = imgOfPage.getHeight();
					PDPage newPage = new PDPage(new PDRectangle(width, height));
					newdocument.addPage(newPage);
					PDXObjectImage pimg = new PDJpeg(newdocument, imgOfPage);
					PDPageContentStream cstream = new PDPageContentStream(newdocument, newPage);
					cstream.drawImage(pimg, 0, 0);
					cstream.close();
				}else{
					newdocument.addPage(page);
				}
				
			}
		newdocument.save(bos);
		retval = bos.toByteArray();
		bos.flush();
		
		}catch(Exception e){
			throw new Exception("feilet i prosessering av pdf: " + e.getMessage());
		}finally{
			bos.close();
		}
		return retval;
	}
	
	
	private boolean checkforJBIG2(PDPage page)throws Exception{
		PDResources res = page.getResources();
		Map<String, PDXObject> pElements = null;
		boolean retval = false;
		try {
			try {
				pElements = res.getXObjects();
			} catch (Exception e) {
				throw new Exception("Feilet i henting av pdfObjekter: " + e.getMessage());
			}
			if(pElements != null){
				Iterator imgitr = pElements.keySet().iterator();
				while(imgitr.hasNext()){
					String key = (String)imgitr.next();
					PDXObject pOjb = pElements.get(key);
					if(pOjb.getClass().equals(PDXObjectImage.class)||PDXObjectImage.class.isAssignableFrom(pOjb.getClass())){
						PDXObjectImage image = (PDXObjectImage)pOjb;
						//System.out.println("Bildeformat: " + image.getSuffix());
						COSStream cs =  (COSStream)image.getCOSObject();
						Set set= cs.keySet();
						Iterator it = set.iterator();
						while(it.hasNext()){
							COSName imgkey = (COSName)it.next();
							COSBase imgvalue = cs.getDictionaryObject(imgkey); //(imgkey.getName());
							if(imgvalue.toString().contains("JBIG2Globals")){
								retval = true;
							}
						}	
					}
				}
			}
		} catch (Exception e) {
			throw new Exception("Feilet i checkforImages: " + e.getMessage());
		}
		return retval;
	}
	
	private String getUniqeNumber(){
		String retval = "0";
			try {
				int newnum = imgcounter;
				retval = new String(new Integer(newnum).toString()); 
				imgcounter++;
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		return retval;
	}
		
	
	private void savePageAsImage(PDPage page, String filename)throws Exception{
		
		try {
			BufferedImage img = page.convertToImage();
			File file = new File(filename);
			ImageIO.write(img, "png", file);
		} catch (Exception e) {
			throw new Exception("feilet i savePageAsImage: " + e.getMessage());
		}
		
	}
}
