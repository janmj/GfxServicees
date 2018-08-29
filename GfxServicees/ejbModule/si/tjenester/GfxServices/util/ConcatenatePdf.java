package si.tjenester.GfxServices.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.PDFReader;
import org.apache.pdfbox.exceptions.CryptographyException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.StandardDecryptionMaterial;
import org.jboss.logging.Logger;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PRAcroForm;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.SimpleBookmark;

public class ConcatenatePdf {
	Logger logger = Logger.getLogger(getClass());
	public ConcatenatePdf(){
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}
	
	public byte[] concatenate(ArrayList<byte[]> pdfs)throws Exception, CryptographyException{
		byte[] retval = null;
		ByteArrayOutputStream bus = new ByteArrayOutputStream();
		try {
			int pageoffset = 0;
			ArrayList master = new ArrayList();
			Document doc = null;
			PdfCopy writer = null;
			for(int x=0;x<pdfs.size();x++){
				byte[] tmpPdf = pdfs.get(x); 
				//PdfReader reader = new PdfReader(pdfs.get(x));
				try {
					tmpPdf = removeProtection(tmpPdf);
				}catch (CryptographyException e1) {
					logger.info("Feilet i dekryptering" + e1.getMessage());
					throw new CryptographyException("Feilet i dekryptering av PDF: " + e1.getMessage());
				}catch(Exception e1){
					logger.info("Feilet i fjerning av beskytelse: " + e1.getMessage());
				}
				PdfReader reader = new PdfReader(tmpPdf);
				reader.consolidateNamedDestinations();
				int n = reader.getNumberOfPages();
				
				try {
					List bookmarks = SimpleBookmark.getBookmark(reader);
					if(bookmarks != null){
						if(pageoffset!=0){
							SimpleBookmark.shiftPageNumbers(bookmarks, pageoffset, null);
							master.addAll(bookmarks);
						}
					}
				} catch (Exception e) {
					logger.debug("Feilet i uttrekk av bookmarks: " + e.getMessage());
				}
				pageoffset += n;
				if(x==0){
					doc = new Document(reader.getPageSizeWithRotation(1));
					writer = new PdfCopy(doc,bus);
					doc.open();
				}
				PdfImportedPage page;
				for(int i=0;i<n;){
					i++;
					page = writer.getImportedPage(reader, i);
					writer.addPage(page);
				}
				PRAcroForm form = reader.getAcroForm();
				if(form != null){
					//writer.copyAcroForm(reader);
				}
			}
			doc.close();
			retval = bus.toByteArray();
		}catch(CryptographyException e){
			throw new CryptographyException(e.getMessage());
		}catch (Exception e) {
			throw new Exception("Feilet i concatenering av pdf'er: " + e.getMessage());
		}
		return retval;
	}
	
	private byte[] removeProtection(byte[] pdf)throws CryptographyException, Exception{
		byte[] retval = null;
		PDDocument doc = null;
		
		try {
			doc = PDDocument.load(new ByteArrayInputStream(pdf));
			if(doc.isEncrypted()){
				doc.decrypt("");
				logger.info("Fjernet krypptering");
			}
			
			doc.setAllSecurityToBeRemoved(true);
			logger.info("Fjernet beskyttelse");
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			doc.save(bos);
			retval = bos.toByteArray();
		}catch(CryptographyException e){
			throw new CryptographyException("Feilet i dekryptering av PDF: " + e.getMessage());
		} catch (Exception e) {
			throw new Exception("Feilet i removeProtection: " + e.getMessage());
		}finally{
			doc.close();
		}
		return retval;
	}

}
