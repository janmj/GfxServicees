package si.tjenester.GfxServices.util.gfxConvert;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import si.tjenester.GfxServices.util.io.FileIoHandler;

import com.sun.media.jai.codec.JPEGEncodeParam;
/*
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
*/
import com.sun.media.jai.codec.PNGEncodeParam;
import com.sun.media.jai.codecimpl.JPEGCodec;
import com.sun.media.jai.codecimpl.JPEGImageEncoder;
//import com.sun.media.jai.codecimpl.PNGCodec;
import com.sun.media.jai.codecimpl.PNGImageEncoder;



public class ImagescaleUtil {

	public ImagescaleUtil(){
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ImagescaleUtil test = new ImagescaleUtil();
		try {
			FileIoHandler fio = new FileIoHandler();
			byte[] testimg = fio.getfileAsbytea("/home/janmj/test/Gfxtest/pdf2img/respdf_0.jpg");
			byte[] thumb = test.scaleImage(testimg, 150, "");
			fio.savefile(thumb, "/home/janmj/test/Gfxtest/pdf2img/respdf_0_thumb.png");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public byte[] scaleImage(byte[] imgdata, int scalewidth, String mimetype)throws Exception{
		byte[] retval = null;
		
		try {
			//laster bildet..
			Image img;
			try {
				img = Toolkit.getDefaultToolkit().createImage(imgdata);
				MediaTracker tracker = new MediaTracker(new Component(){});
				tracker.addImage(img, 0);
				tracker.waitForID(0);
			} catch (Exception e) {
				throw new Exception("Feilet i parsing av bildet.. " + e.getMessage());
			}
			//Oppretter internt bufferedImage objekt
			int width = img.getWidth(null);
			int height = img.getHeight(null);
			BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			//oppretter en context å tegne på..
			Graphics2D biContext = bi.createGraphics();
			biContext.drawImage(img,0,0,null);
			//Skalerer og lagrer.
			Image thumb = bi.getScaledInstance(scalewidth, -1, Image.SCALE_SMOOTH);
			
			if(mimetype.equalsIgnoreCase("image/jpeg")){
				retval = saveAsJpeg(thumb);
			}else if(mimetype.equalsIgnoreCase("image/png")){
				retval = saveAsPng(thumb);
			}else{
				throw new Exception("Ikke støttet mimetype for skalering av bilde!");
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}	
		return retval;
	}
	
	private byte[] saveAsJpeg(Image imgtosave)throws Exception{
		byte[] retval = null;
		try {
			try {
				MediaTracker tracker = new MediaTracker(new Component(){});
				tracker.addImage(imgtosave, 0);
				tracker.waitForID(0);
			} catch (Exception e) {
				throw new Exception("Feilet i parsing av thumbnail... " + e.getMessage());
			}
			int outwidth = imgtosave.getWidth(null);
			int outheight = imgtosave.getHeight(null);
			BufferedImage bi = new BufferedImage(outwidth, outheight, BufferedImage.TYPE_INT_RGB);
			Graphics2D bicontext = bi.createGraphics();
			bicontext.drawImage(imgtosave, 0, 0, null);
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			/*
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(bout);
			JPEGEncodeParam jparm = JPEGCodec.getDefaultJPEGEncodeParam(bi);
			jparm.setQuality( 75, true);
			encoder.encode(bi, jparm);
			*/
			ImageIO.write(bi, "jpg", bout);
			retval = bout.toByteArray();
			bout.flush();
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		return retval;
	}
	
	private byte[] saveAsPng(Image imgtosave)throws Exception{
		byte[] retval = null;
		try {
			try {
				MediaTracker tracker = new MediaTracker(new Component(){});
				tracker.addImage(imgtosave, 0);
				tracker.waitForID(0);
			} catch (Exception e) {
				throw new Exception("Feilet i parsing av thumbnail... " + e.getMessage());
			}
			int outwidth = imgtosave.getWidth(null);
			int outheight = imgtosave.getHeight(null);
			BufferedImage bi = new BufferedImage(outwidth, outheight, BufferedImage.TYPE_INT_RGB);
			Graphics2D bicontext = bi.createGraphics();
			bicontext.drawImage(imgtosave, 0, 0, null);
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			PNGEncodeParam param = PNGEncodeParam.getDefaultEncodeParam(bi);
			PNGImageEncoder encoder = new PNGImageEncoder(bout, param);

			encoder.encode(bi);
			retval = bout.toByteArray();
			bout.flush();
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		return retval;
	}
}
