package no.si.gfxmodule.test.GfxModuleTest;

import java.net.URL;
import java.util.ArrayList;

import javax.xml.namespace.QName;

import gfxservices.tjenester.si.GfxService;
import gfxservices.tjenester.si.GfxService_Service;
import gfxservices.tjenester.si.Imagebean;
import gfxservices.tjenester.si.Validatebean;
import no.si.gfxmodule.test.GfxModuleTest.utils.IOHandler;

public class SimulateGfxClient {
	private GfxService gfxservice;
	final static QName SERVICE = new QName("urn:si.tjenester.GfxServices", "GfxService");
	private String serviceURL="http://localhost:8080/GfxServicees/GfxService/GfxService?wsdl";
	
	public SimulateGfxClient()throws Exception{
		
	}
	
	public String makePdf(String inpath, String xslid)throws Exception{
		String retval = null;
		try {
			gfxservice = getGfxService();
			IOHandler ioh = new IOHandler();
			String xmldoc = new String(ioh.getfileAsbytea(inpath), "UTF-8");
			String pdf = gfxservice.makePdf(xmldoc, xslid);
			retval = pdf;
		} catch (Exception e) {
			throw new Exception("Feilet i makePdf: " + e.getMessage());
		}
		return retval;
	}
	
	public String makePdfA(String inpath, String xslid)throws Exception{
		String retval = null;
		try {
			gfxservice = getGfxService();
			IOHandler ioh = new IOHandler();
			String xmldoc = new String(ioh.getfileAsbytea(inpath), "UTF-8");
			String pdf = gfxservice.makePdfA(xmldoc, xslid);
			retval = pdf;
		} catch (Exception e) {
			throw new Exception("Feilet i makePdf: " + e.getMessage());
		}
		return retval;
	}
	
	public byte[] convertGfxToPdf(ArrayList<Imagebean> images)throws Exception{
		byte[] retval = null;
		try {
			gfxservice = getGfxService();
			retval = gfxservice.convertGfxToPdf(images);
		} catch (Exception e) {
			throw new Exception("Feilet i convertGfxToPdf: " + e.getMessage());
		}
		return retval;
	}
	
	public byte[] convertXmlGfxToPdf(String xml, String xsltID, ArrayList<Imagebean> images) throws Exception{
		byte[] retval = null;
		try {
			gfxservice = getGfxService();
			retval = gfxservice.convertXmlGfxToPdf(xml, xsltID, images);
		} catch (Exception e) {
			throw new Exception("Feilet i convertXmlGfxToPdf: " + e.getMessage());
		}
		return retval;
	}

	public Boolean validateImage(Imagebean image)throws Exception{
		Boolean retval = new Boolean(false);
		try {
			gfxservice = getGfxService();
			retval = gfxservice.validateImage(image);
		} catch (Exception e) {
			throw new Exception("Feilet i validateImage: " + e.getMessage());
		}
		return retval;
	}
	
	public Validatebean validateImageSC(Imagebean image, String imgid)throws Exception{
		Validatebean retval = null;
		try {
			gfxservice = getGfxService();
			retval = gfxservice.validateImageSC(image, imgid);
		} catch (Exception e) {
			throw new Exception("Feilet i validateImageSC: " + e.getMessage());
		}
		return retval;
	}
	
	private GfxService getGfxService()throws Exception{
		GfxService retval = null;
		try {
			URL wsdlUrl = new URL(serviceURL);
			GfxService_Service service = new GfxService_Service(wsdlUrl, SERVICE);
			retval = service.getGfxServicePort();
		} catch (Exception e) {
			throw new Exception("Feilet i getGfxService: " + e.getMessage());
		}
		return retval;
	}
	

}
