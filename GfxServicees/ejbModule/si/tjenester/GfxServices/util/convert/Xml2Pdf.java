package si.tjenester.GfxServices.util.convert;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;

import si.tjenester.GfxServices.util.io.FileIoHandler;

public class Xml2Pdf {
	private FopFactory fopfactory = null;
	private FOUserAgent fouseragent=null;
	
	public Xml2Pdf(){
		
	}
	public static void main(String[] args){
		Xml2Pdf xp = new Xml2Pdf();
		try {
			FileIoHandler ioh = new FileIoHandler();
			byte[] xmlfile = ioh.getfileAsbytea("/home/janmj/Develop/workspace-3.7/PDFTest/xml/test1.xml");
			String xsltpath = "/home/janmj/Develop/workspace-3.7/PDFTest/xsl/test1.xsl";
			byte [] respdf = xp.makePdf(xmlfile, xsltpath);
			ioh.savefile(respdf, "/home/janmj/test/Gfxtest/respdf.pdf");
		} catch (Exception e) {
			System.err.println("Feilet i Xml2pdf : " + e.getMessage());
		}
	}
	
	public byte[] makePdf(byte[] xml, String xsltpath)throws Exception{
		byte[] retval=null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		File xsltfile = new File(xsltpath);
		try {
			fopfactory = getFopfactory();
			fouseragent = getFOUserAgent();
		} catch (Exception e) {
			throw new Exception("Feilet i initiering av FOP: " +e.getMessage() );
		}
		
		try {
			Fop fop = fopfactory.newFop(MimeConstants.MIME_PDF, fouseragent, out);
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer(new StreamSource(xsltfile));
			transformer.setParameter("vasrionParam", "2.0");
			Source src = new StreamSource(new ByteArrayInputStream(xml));
			Result res = new SAXResult(fop.getDefaultHandler());
			transformer.transform(src, res);
			retval = out.toByteArray();
			out.close();
		} catch (Exception e) {
			throw new Exception("Feilet i FOP transformering: " + e.getMessage());
		}finally{
			try {
				out.close();
			} catch (Exception e2) {}
		}
		
		return retval;
	}
	
	private FopFactory getFopfactory()throws Exception{
		try{
			fopfactory = FopFactory.newInstance();
			fopfactory.setUserConfig("userconfig.xml");
		}catch (Exception e) {
			throw new Exception("Feilet i init av FOP: " + e.getMessage());
		}
		return fopfactory;
	}
	
	private FOUserAgent getFOUserAgent(){
		fouseragent = fopfactory.newFOUserAgent();
		//fouseragent.getRendererOptions().put("pdf-a-mode", "PDF/A-1b");
		//fouseragent.getRendererOptions().put("pdf-x-mode", "PDF/X-3:2003");
		return fouseragent;
	}
	
}
