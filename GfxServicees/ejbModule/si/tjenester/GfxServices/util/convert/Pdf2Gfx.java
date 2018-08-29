package si.tjenester.GfxServices.util.convert;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import si.tjenester.GfxServices.bean.ImageBean;

public class Pdf2Gfx {

	public Pdf2Gfx() {
		
	}
	
	public ArrayList<ImageBean> convert(byte[] pdfdoc)throws Exception{
		PDDocument doc = null;
		ArrayList<ImageBean> retval = new ArrayList<ImageBean>();
		try {
			doc = PDDocument.load(new ByteArrayInputStream(pdfdoc));
			ArrayList<PDPage> plist = new ArrayList<PDPage>(doc.getDocumentCatalog().getAllPages());
			for(int x=0;x<plist.size();x++){
				PDPage page = plist.get(x);
				BufferedImage img = page.convertToImage();
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				ImageIO.write(img, "jpg", bout);
				//TODO legg til lagring til IMagebean her..
				ImageBean tmpimage = new ImageBean();
				tmpimage.setName("");
				tmpimage.setMimetype("image/jpeg");
				tmpimage.setImagedata(bout.toByteArray());
				retval.add(tmpimage);
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}finally{
			doc.close();
		}
		return retval;
	}
	
	public byte[] removePages(byte[] pdfdoc, ArrayList<String> pages)throws Exception{
		byte[] pdfretur = null;
		PDDocument pdoc = null;
		ByteArrayOutputStream pout = null;
		try {
			pdoc = PDDocument.load(new ByteArrayInputStream(pdfdoc));
			int numpages = pdoc.getNumberOfPages();
			System.out.println(numpages);
			ArrayList<PDPage> tmppages = new ArrayList<PDPage>();
			for(int x=0;x<numpages;x++){
				if(!checkPage(x+1, pages)){
					PDPage tmppage = (PDPage)pdoc.getDocumentCatalog().getAllPages().get(x);
					tmppages.add(tmppage);
					System.out.println("Fjernet side: " + (x+1));
				}
			}
			int numremove = tmppages.size();
			for(int x=0;x<numremove;x++){
				boolean removetest = pdoc.removePage(tmppages.get(x));
				System.out.println(" Fjernet vellykket: " + removetest);
			}
		pout = new ByteArrayOutputStream();
		pdoc.save(pout);
		pdfretur = pout.toByteArray();
		//pdoc.close();
		pout.close();
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}finally{
			pdoc.close();
			pout.close();
		}
		return pdfretur;
	}
	
	private boolean checkPage(int page, ArrayList<String> pages){
		boolean retval = false;
		for(int x=0;x<pages.size();x++){
			int toCompare = new Integer(pages.get(x)).intValue();
			if(page == toCompare){
				retval = true;
			}
		}
		//System.out.println("sjekket side: " + page + " skal fjernes: " + retval);
		return retval;
	}
}
