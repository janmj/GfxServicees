package no.si.gfxmodule.test.GfxModuleTest;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.junit.Test;

import gfxservices.tjenester.si.Imagebean;
import gfxservices.tjenester.si.Validatebean;
import no.si.gfxmodule.test.GfxModuleTest.utils.IOHandler;

public class GfxTest{
	private SimulateGfxClient gfxclient;
	private String xmlpath = "/home/janmj/Develop/workspace-3.7/PDFTest/xml/test1.xml";
	private String xslid= "test";
	private String pdfpath = "/home/janmj/test/Gfxtest/pdf/test1.pdf";
	
	//@Test
	public void testMakePdf(){
		IOHandler ioh = new IOHandler();
		try{
			gfxclient = new SimulateGfxClient();
			String pdf = gfxclient.makePdf(xmlpath, xslid);
			ioh.savefile(Base64.getDecoder().decode(pdf.getBytes()), pdfpath);
		}catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	//@Test
	public void testMakePdfA(){
		IOHandler ioh = new IOHandler();
		try{
			gfxclient = new SimulateGfxClient();
			String pdf = gfxclient.makePdfA(xmlpath, xslid);
			ioh.savefile(Base64.getDecoder().decode(pdf.getBytes()), pdfpath);
		}catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	//@Test
	public void testconvertGfxToPdf(){
		IOHandler ioh = new IOHandler();
		try {
			gfxclient = new SimulateGfxClient();
			byte[] result = gfxclient.convertGfxToPdf(loadimages());
			ioh.savefile(result, "/home/janmj/test/Gfxtest/output/testconvertGfxToPdf.pdf");
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	//@Test
	public void testconvertXmlGfxToPdf(){
		IOHandler ioh = new IOHandler();
		try {
			String xml = new String(ioh.getfileAsbytea(xmlpath), "UTF-8");
			gfxclient = new SimulateGfxClient();
			byte[] retpdf = gfxclient.convertXmlGfxToPdf(xml, xslid, loadimages());
			ioh.savefile(retpdf, "/home/janmj/test/Gfxtest/output/testconvertXmlGfxToPdf.pdf");
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	//@Test
	public void  testvalidateImage(){
		IOHandler ioh = new IOHandler();
		try {
			Imagebean image = new Imagebean();
			image .setName("TestBilde");
			image.setMimetype("image/jpeg");
			byte[] imgData = ioh.getfileAsbytea("/home/janmj/test/Gfxtest/jpg/0.jpg");
			//byte[] imgData = ioh.getfileAsbytea("/home/janmj/test/Gfxtest/jpg/0-liten.jpg");
			image.setImagedata(imgData);
			gfxclient = new SimulateGfxClient();
			Boolean result = gfxclient.validateImage(image);
			assertTrue(result);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
	}

	@Test
	public void  testvalidatePDF(){
		IOHandler ioh = new IOHandler();
		try {
			Imagebean image = new Imagebean();
			image .setName("Testpdf");
			image.setMimetype("application/pdf");
			byte[] imgData = ioh.getfileAsbytea("/home/janmj/test/Gfxtest/pdf/sec-pdf-test/17087032809_4.pdf");
			image.setImagedata(imgData);
			gfxclient = new SimulateGfxClient();
			Boolean result = gfxclient.validateImage(image);
			assertTrue(result);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
	}
	//@Test
	public void  testvalidateImageSC(){
		IOHandler ioh = new IOHandler();
		try {
			Imagebean image = new Imagebean();
			image .setName("TestBilde");
			image.setMimetype("image/jpeg");
			byte[] imgData = ioh.getfileAsbytea("/home/janmj/test/Gfxtest/jpg/0.jpg");
			//byte[] imgData = ioh.getfileAsbytea("/home/janmj/test/Gfxtest/jpg/0-liten.jpg");
			image.setImagedata(imgData);
			gfxclient = new SimulateGfxClient();
			Validatebean vbean =  gfxclient.validateImageSC(image, "123456789");
			assertTrue(vbean.isValidate());
			assertEquals(vbean.getImgid(), "123456789");
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	//@Test
	public void testExifHandling(){
		IOHandler ioh = new IOHandler();
		try {
			gfxclient = new SimulateGfxClient();
			byte[] result = gfxclient.convertGfxToPdf(loadimagesExifTest());
			ioh.savefile(result, "/home/janmj/test/Gfxtest/output/testExifHandling.pdf");
		} catch (Exception e) {
			fail(e.getMessage());
		}		
	}

	private ArrayList<Imagebean> loadimagesExifTest()throws Exception{
		ArrayList<Imagebean> retval = new ArrayList<>();
		IOHandler ioh = new IOHandler();
		try{
			
			Imagebean image1 = new Imagebean();
			image1.setName("Bilde1");
			image1.setMimetype("image/jpeg");
			byte[] tmpimg1 = ioh.getfileAsbytea("/home/janmj/test/Gfxtest/jpg/exif-test/Portrait_1.jpg");
			image1.setImagedata(tmpimg1);
			retval.add(image1);
			
			Imagebean image2 = new Imagebean();
			image2.setName("Bilde2");
			image2.setMimetype("image/jpeg");
			byte[] tmpimg2 = ioh.getfileAsbytea("/home/janmj/test/Gfxtest/jpg/exif-test/Portrait_6.jpg");
			image2.setImagedata(tmpimg2);
			retval.add(image2);
			/*
			Imagebean image3 = new Imagebean();
			image3.setName("Bilde3");
			image3.setMimetype("image/png");
			byte[] tmpimg3 = ioh.getfileAsbytea("/home/janmj/test/Gfxtest/png/0.png");
			image3.setImagedata(tmpimg3);
			retval.add(image3);
			
			Imagebean image4 = new Imagebean();
			image4.setName("Bilde4");
			image4.setMimetype("image/tiff");
			//byte[] tmpimg4 = ioh.getfileAsbytea("/home/janmj/test/Gfxtest/tiff/0.tif");
			byte[] tmpimg4 = ioh.getfileAsbytea("/home/janmj/test/Gfxtest/tiff/0.tif");
			image4.setImagedata(tmpimg4);
			retval.add(image4);
			
			Imagebean image5 = new Imagebean();
			image5.setName("Bilde5");
			image5.setMimetype("application/pdf");
			byte[] tmpimg5 = ioh.getfileAsbytea("/home/janmj/test/Gfxtest/pdf/test1.pdf");
			image5.setImagedata(tmpimg5);
			retval.add(image5);
			*/
		}catch (Exception e) {
			throw new Exception("Feilet i loadimages: " +e.getMessage());
		}
		return retval;
	}

	
	private ArrayList<Imagebean> loadimages()throws Exception{
		ArrayList<Imagebean> retval = new ArrayList<>();
		IOHandler ioh = new IOHandler();
		try{
			
			Imagebean image1 = new Imagebean();
			image1.setName("Bilde1");
			image1.setMimetype("image/jpeg");
			byte[] tmpimg1 = ioh.getfileAsbytea("/home/janmj/test/Gfxtest/jpg/0.jpg");
			image1.setImagedata(tmpimg1);
			retval.add(image1);
			
			Imagebean image2 = new Imagebean();
			image2.setName("Bilde2");
			image2.setMimetype("image/gif");
			byte[] tmpimg2 = ioh.getfileAsbytea("/home/janmj/test/Gfxtest/gif/0.gif");
			image2.setImagedata(tmpimg2);
			retval.add(image2);

			Imagebean image3 = new Imagebean();
			image3.setName("Bilde3");
			image3.setMimetype("image/png");
			byte[] tmpimg3 = ioh.getfileAsbytea("/home/janmj/test/Gfxtest/png/0.png");
			image3.setImagedata(tmpimg3);
			retval.add(image3);
			
			Imagebean image4 = new Imagebean();
			image4.setName("Bilde4");
			image4.setMimetype("image/tiff");
			//byte[] tmpimg4 = ioh.getfileAsbytea("/home/janmj/test/Gfxtest/tiff/0.tif");
			byte[] tmpimg4 = ioh.getfileAsbytea("/home/janmj/test/Gfxtest/tiff/0.tif");
			image4.setImagedata(tmpimg4);
			retval.add(image4);
			
			Imagebean image5 = new Imagebean();
			image5.setName("Bilde5");
			image5.setMimetype("application/pdf");
			byte[] tmpimg5 = ioh.getfileAsbytea("/home/janmj/test/Gfxtest/pdf/test1.pdf");
			image5.setImagedata(tmpimg5);
			retval.add(image5);
			
		}catch (Exception e) {
			throw new Exception("Feilet i loadimages: " +e.getMessage());
		}
		return retval;
	}

}
