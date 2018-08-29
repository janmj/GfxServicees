package si.tjenester.GfxServices.util.gfxConvert;

import java.io.File;
import java.util.ArrayList;

import si.tjenester.GfxServices.bean.ImageBean;
import si.tjenester.GfxServices.util.convert.Pdf2Gfx;
import si.tjenester.GfxServices.util.log.Logger;

public class ConvertFacade {

	public ConvertFacade() {
		// TODO Auto-generated constructor stub
	}
	
	public ArrayList<ImageBean> splitTiffToJpeg(byte[] tiffimage)throws Exception{
		TiffUtil tu = new TiffUtil();
		ArrayList<ImageBean> retval = new ArrayList<ImageBean>();
		try {
			retval = tu.splitTiff(tiffimage);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		return retval;
	}
	
	public ImageBean checkAndInvertTiff(byte[] tiffimage)throws Exception{
		ImageBean retval = new ImageBean();
		TiffUtil tu = new TiffUtil();
		try {
			if(tu.checkTiffInvert(tiffimage)){
				tu.invertMultipleTiffs(tiffimage, "/tmp/temp.tif");
				byte[] inverted=tu.getfileAsbytea("/tmp/temp.tif");
				retval.setImagedata(inverted);
				retval.setName("inverted.tif");
				retval.setMimetype("image/tiff");
				try {
					File tmpFile = new File("/tmp/test.tiff");
					tmpFile.delete();
				} catch (Exception e) {
					Logger.logError("Feilet i sletting av temporær tiff: ", e.getMessage());
				}
			}else{
				retval.setImagedata(tiffimage);
				retval.setName("original.tif");
				retval.setMimetype("image/tiff");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return retval;
	}
	
	public ArrayList<ImageBean>splitAndmakePreview(ImageBean document, int scalewidth, String mimetype)throws Exception{
		ArrayList<ImageBean> retval = new ArrayList<ImageBean>();
		ImagescaleUtil scale = new ImagescaleUtil();
		try {
			if(document.getMimetype().equalsIgnoreCase("image/tiff")){
				TiffUtil tu = new TiffUtil();
				retval = tu.splitTiff(document.getImagedata());
				for(int x=0;x<retval.size();x++){
					byte[] tmpimg = retval.get(x).getImagedata();
					tmpimg = scale.scaleImage(tmpimg, scalewidth, mimetype);
					retval.get(x).setImagedata(tmpimg);
					retval.get(x).setMimetype(mimetype);
				}
			}else if(document.getMimetype().equalsIgnoreCase("application/pdf")){
				Pdf2Gfx p2g = new Pdf2Gfx();
				retval = p2g.convert(document.getImagedata());
				for(int x=0;x<retval.size();x++){
					byte[] tmpimg = retval.get(x).getImagedata();
					tmpimg = scale.scaleImage(tmpimg, scalewidth, mimetype);
					retval.get(x).setImagedata(tmpimg);
					retval.get(x).setMimetype(mimetype);
				}
			}else{
				throw new Exception("Ikke støttet format");
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		return retval;
	}

}
