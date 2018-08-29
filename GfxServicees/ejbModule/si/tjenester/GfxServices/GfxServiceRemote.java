package si.tjenester.GfxServices;

import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.ejb.Remote;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.WebFault;
import javax.xml.ws.soap.SOAPFaultException;

import si.tjenester.GfxServices.bean.ImageBean;
import si.tjenester.GfxServices.bean.ImgValidateBean;

@Remote
@WebService(targetNamespace="urn:si.tjenester.GfxServices",name="GfxService")

public interface GfxServiceRemote {
	
	@WebMethod
	public byte[] convertGfxToPdf(@WebParam(name="images") ArrayList<ImageBean> images)throws javax.xml.ws.soap.SOAPFaultException, RemoteException;
	@WebMethod
	public byte[] convertXmlGfxToPdf(@WebParam(name="xml")String xml, @WebParam(name="xslt")String xslt,@WebParam(name="images") ArrayList<ImageBean> images)throws RemoteException;
	@WebMethod
	public Boolean validateImage(@WebParam(name="image")ImageBean image) throws javax.xml.ws.soap.SOAPFaultException, RemoteException;
	@WebMethod
	public ImgValidateBean validateImageSC(@WebParam(name="image") ImageBean image, @WebParam(name="imgid")String imgid)throws javax.xml.ws.soap.SOAPFaultException, RemoteException;
	@WebMethod
	public String makePdf(@WebParam(name="xmldoc")String xmldoc, @WebParam(name="xsltID")String xsltID)throws RemoteException;
	@WebMethod
	public String makePdfA(@WebParam(name="xmldoc")String xmldoc, @WebParam(name="xsltID")String xsltID)throws RemoteException;
	@WebMethod
	public ArrayList<ImageBean> splitMTifftoJpeg(@WebParam(name="image")ImageBean image) throws RemoteException; 
	@WebMethod
	public ArrayList<ImageBean> makePreview(@WebParam(name="document")ImageBean document, @WebParam(name="scalewidth") int scalewidth, @WebParam(name="mimetype")String mimetype)throws RemoteException;
	@WebMethod
	public ImageBean checkAndInvertTiff(@WebParam(name="tiffimage") ImageBean tiffimage)throws RemoteException;
	@WebMethod
	public ImageBean checkAndRecompressJBIG2(@WebParam(name="tocheck")ImageBean tocheck)throws RemoteException;
}
