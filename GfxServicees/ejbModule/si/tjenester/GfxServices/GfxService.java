package si.tjenester.GfxServices;

import java.rmi.RemoteException;
import java.util.ArrayList;


import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import javax.xml.ws.soap.SOAPFaultException;

import org.apache.commons.codec.binary.Base64;


import si.tjenester.GfxServices.bean.ImageBean;
import si.tjenester.GfxServices.bean.ImgValidateBean;
import si.tjenester.GfxServices.util.Gfx2PdfFactory;
import si.tjenester.GfxServices.util.GfxVerifyFactory;
import si.tjenester.GfxServices.util.PdfToolsFacade;
import si.tjenester.GfxServices.util.convert.Xml2Pdf;
import si.tjenester.GfxServices.util.db.ResourceHandler;
import si.tjenester.GfxServices.util.gfxConvert.ConvertFacade;
import si.tjenester.GfxServices.util.log.Logger;
import si.tjenester.GfxServices.util.pdf.JBIG2Repair;


/**
 * Session Bean implementation class GfxServicees
 */
@Stateless
@WebService(endpointInterface="si.tjenester.GfxServices.GfxServiceRemote", 
			targetNamespace="urn:si.tjenester.GfxServices",
			serviceName="GfxService",
			name="GfxService")

@SOAPBinding(style=Style.DOCUMENT)
@TransactionAttribute(TransactionAttributeType.NEVER)

public class GfxService implements GfxServiceRemote {

	@WebMethod
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public byte[] convertGfxToPdf(ArrayList<ImageBean> images) throws SOAPFaultException, RemoteException {
		byte[] retval = null;
		try {
			long start = System.currentTimeMillis();
			String logmsg = new String(new Long(getSizeofGfx(images)).toString());
			Logger.logMessage("Størrelse på motatte filer er: " + logmsg + " bytes");
			Gfx2PdfFactory gfxfactory = new Gfx2PdfFactory();
			retval = gfxfactory.makePdf(images);
			Logger.logMessage("Størrelse på prosessert fil er: " + new String(new Long(retval.length).toString() )+ " bytes");
			Logger.logMessage("Konverterte vellykket...");
			long ferdig = System.currentTimeMillis();
			long tid = (ferdig-start);
			Logger.logMessage("Prossesringstid GfxToPdf: " + new Long(tid).toString() + "ms");
		}catch (SOAPFaultException se){
			Logger.logError("Feilet i convertGfxToPdf",  se.getMessage());
			throw new SOAPFaultException(se.getFault());
		} catch (Exception e) {
			Logger.logError("Feilet i convertGfxToPdf", e.getMessage());
			throw new RemoteException("Feilet i convertGfxToPdf: " + e.getMessage());
		}
		return retval;
	}
	
	@WebMethod
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public byte[] convertXmlGfxToPdf(String xml, String xsltID, ArrayList<ImageBean> images)throws RemoteException{
		byte[] retval = null;
		ImageBean frontpage = new ImageBean();
		try {
			Xml2Pdf xml2pdf = new Xml2Pdf();
			ResourceHandler rsh = new ResourceHandler();
			String xsltpath = rsh.getResourceString(xsltID);
			byte[] tmpPdf = xml2pdf.makePdf(xml.getBytes("UTF-8"), xsltpath);
			frontpage.setImagedata(tmpPdf);
			frontpage.setMimetype("application/pdf");
			frontpage.setName("frontpage.pdf");
			images.add(0, frontpage);
		}catch (Exception e) {
			Logger.logError("Feilet i Oppretting av PDF fra XML : ",  e.getMessage());
			throw new RemoteException("Feilet i Oppretting av PDF fra XML : " + e.getMessage());
		}
		try{
			Gfx2PdfFactory gfxfactory = new Gfx2PdfFactory();
			retval = gfxfactory.makePdf(images);
		}catch (SOAPFaultException se){
			throw new SOAPFaultException(se.getFault());
		}catch (Exception e) {
			Logger.logError("Feilet i convertXmlGfxToPdf",  e.getMessage());
			throw new RemoteException("Feilet i convertXmlGfxToPdf" + e.getMessage());
		}
		return retval;
	}

	@WebMethod
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public Boolean validateImage(ImageBean image) throws SOAPFaultException, RemoteException {
		Boolean retval = new Boolean(false);
		try {
			long start = System.currentTimeMillis();
			GfxVerifyFactory gfxv = new GfxVerifyFactory();
			retval = gfxv.validateImage(image);
			long ferdig = System.currentTimeMillis();
			long resulat = ferdig - start;
			Logger.logMessage("Prossesringstid validate: " + new Long(resulat).toString() + "ms");
		} catch(SOAPFaultException se){
			throw new SOAPFaultException(se.getFault());
		} catch (Exception e) {
			Logger.logError("Feilet i validateImage", e.getMessage());
			throw new RemoteException("Feilet i validateImage, ukjent feil: " + e.getMessage());
		}
		return retval;
	}

	@WebMethod
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public ImgValidateBean validateImageSC(ImageBean image, String imgid)throws SOAPFaultException, RemoteException {
		ImgValidateBean retval = new ImgValidateBean();
		retval.setValidate(new Boolean(false));
		retval.setImgid(imgid);
		try {
			GfxVerifyFactory gfxv = new GfxVerifyFactory();
			retval.setValidate(gfxv.validateImage(image));
		}catch (SOAPFaultException se){
			throw new SOAPFaultException(se.getFault());
		} catch (Exception e) {
			Logger.logError("Feilet i validateimage", e.getMessage());
			throw new RemoteException("Feilet i validateImage, ukjent feil: " + e.getMessage());
		}
		return retval;
	}

	@WebMethod
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public String makePdf(String xmldoc, String xsltID) throws RemoteException {
		String retval = "";
		try {
			Xml2Pdf xml2pdf = new Xml2Pdf();
			ResourceHandler rsh = new ResourceHandler();
			String xsltpath = rsh.getResourceString(xsltID);
			byte[] pdfdoc = xml2pdf.makePdf(xmldoc.getBytes("UTF-8"), xsltpath);
			pdfdoc = Base64.encodeBase64(pdfdoc);
			retval = new String(pdfdoc);
		} catch (Exception e) {
			Logger.logError("Feilet i makePdf", e.getMessage());
			throw new RemoteException("Feilet i makePdf: " + e.getMessage());
		}
		return retval;
	}
	@WebMethod
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public String makePdfA(String xmldoc, String xsltID) throws RemoteException {
		String retval = "";
		try {
			Xml2Pdf xml2pdf = new Xml2Pdf();
			ResourceHandler rsh = new ResourceHandler();
			String xsltpath = rsh.getResourceString(xsltID);
			byte[] pdfdoc = xml2pdf.makePdf(xmldoc.getBytes("UTF-8"), xsltpath);
			PdfToolsFacade pdftools = new PdfToolsFacade();
			if(!pdftools.verifyPDFA(pdfdoc)){
				pdfdoc = pdftools.conovertToPDFA(pdfdoc);
			}
			pdfdoc = Base64.encodeBase64(pdfdoc);
			retval = new String(pdfdoc);
		} catch (Exception e) {
			Logger.logError("Feilet i makePdfA", e.getMessage());
			throw new RemoteException("Feilet i makePdfA: " + e.getMessage());
		}
		return retval;
	}

	@WebMethod
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public ArrayList<ImageBean> splitMTifftoJpeg(ImageBean image)throws RemoteException {
		try{
			ConvertFacade conv = new ConvertFacade();
			return conv.splitTiffToJpeg(image.getImagedata());
		}catch (Exception e) {
			Logger.logError("Feilet i splitMTifftoJpeg", e.getMessage());
			throw new RemoteException(e.getMessage());
		}
	}

	@WebMethod
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public ArrayList<ImageBean> makePreview(ImageBean document, int scalewidth, String mimetype)throws RemoteException {
		ArrayList<ImageBean> retval = new ArrayList<ImageBean>();
		try {
			ConvertFacade cf = new ConvertFacade();
			retval = cf.splitAndmakePreview(document, scalewidth, mimetype);
		} catch (Exception e) {
			Logger.logError("Feilet i makePreview", e.getMessage());
			throw new RemoteException(e.getMessage());
		}
		return retval;
	}
	
	@WebMethod
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public ImageBean checkAndInvertTiff(ImageBean tiffimage)throws RemoteException{
		ImageBean retval = null;
		try {
			ConvertFacade cf = new ConvertFacade();
			retval = cf.checkAndInvertTiff(tiffimage.getImagedata());
		} catch (Exception e) {
			Logger.logError("Feilet i invertering av tiff: ", e.getMessage());
			throw new RemoteException(e.getMessage());
		}
		return retval;
	}
	
	@WebMethod
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public ImageBean checkAndRecompressJBIG2(ImageBean tocheck)throws RemoteException {
		long starttime = System.currentTimeMillis();
		ImageBean retval = null;
		try {
			JBIG2Repair jbr = new JBIG2Repair();
			retval = new ImageBean();
			retval.setImagedata(jbr.recompressJBIG2(tocheck.getImagedata()));
			retval.setMimetype("application/pdf");
			retval.setName(tocheck.getName());
			long endtime = System.currentTimeMillis();
			System.out.println("Tid brukt: " + (endtime-starttime) + "ms");
		} catch (Exception e) {
			throw new RemoteException("Feilet i JBIG2 behandling: " + e.getMessage());
		}
		return retval;
	}

	private long getSizeofGfx(ArrayList<ImageBean> images){
		long retval = 0;
		for(int x=0;x<images.size();x++){
			byte[] tmpimage = images.get(x).getImagedata();
			long tmpsize = tmpimage.length;
			retval = retval + tmpsize;
		}
		return retval;
	}
}
