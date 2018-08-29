package si.tjenester.GfxServices.util.gfxConvert;

import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.IIOImage;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.media.jai.JAI;
import javax.media.jai.NullOpImage;
import javax.media.jai.OpImage;
import javax.media.jai.PlanarImage;

import com.sun.media.jai.codec.ByteArraySeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codec.TIFFDecodeParam;

import com.sun.media.imageio.plugins.tiff.BaselineTIFFTagSet;
import com.sun.media.imageio.plugins.tiff.TIFFTag;
import com.sun.media.imageioimpl.plugins.tiff.TIFFField;
import com.sun.media.imageioimpl.plugins.tiff.TIFFIFD;
import com.sun.media.imageioimpl.plugins.tiff.TIFFImageMetadata;
import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codec.TIFFDecodeParam;
import si.tjenester.GfxServices.bean.ImageBean;



public class TiffUtil {
	
	public TiffUtil(){
		System.setProperty("com.sun.media.jai.disableMediaLib", "true");
	}
	
	public ArrayList<ImageBean> splitTiff(byte[] tiffimage)throws Exception{
		ArrayList<ImageBean> retval = new ArrayList<ImageBean>();
		
		try{
			SeekableStream s = new ByteArraySeekableStream(tiffimage);
			TIFFDecodeParam param = new TIFFDecodeParam();
			ImageDecoder dec = ImageCodec.createImageDecoder("tiff", s, param);
			ArrayList<PlanarImage> imgList = new ArrayList<PlanarImage>();
			for(int x=0;x<dec.getNumPages();x++){
				
				RenderedImage img = new NullOpImage(dec.decodeAsRenderedImage(x),
													null,
													null,
													OpImage.OP_IO_BOUND);
				if(checkImgInvert(img)){
					System.out.println("Fargefeil! Inverterer...");
					img = JAI.create("invert", img);
				}
				PlanarImage p_img =  PlanarImage.wrapRenderedImage(img); //JAI.create("", img);
				imgList.add(p_img);
			}
			for(PlanarImage pi: imgList){
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				ImageIO.write(pi, "jpg", bout);
				ImageBean imgbean =  new ImageBean();
				imgbean.setImagedata(bout.toByteArray());
				imgbean.setMimetype("image/jpeg");
				imgbean.setName("");
				retval.add(imgbean);
				try {
					bout.flush();
					bout.close();
				} catch (Exception e) {
					System.out.println("Problem med lukking av bytearray i splittiff.. :" + e.getMessage());
				}
			}
		}catch (Exception e){
			throw new Exception("Feilet i splitTiff: " + e.getMessage());
		}
		return retval;
	}
	
	private boolean checkImgInvert(RenderedImage img)throws Exception{
		boolean retval = false;
		try {
			ColorModel cmdl = img.getColorModel();
			//cmdl = cmdl.getRGBdefault();
			int width = 15;
			int height = 15;
			int total = height * width;


			for(int x=0;x<total;x++){
				int rgbtmp = cmdl.getRGB(x);
				if(rgbtmp==-16777216){
					retval=true;
				}else if(rgbtmp==-1){
					retval = false;
				}
			}			
		} catch (Exception e) {
			throw new Exception("Feilet i chkImg: " + e.getMessage());
		}
		return retval;
	}
	
	public boolean checkTiffInvert(byte[] image)throws Exception {
		boolean retval = false;
		try {
			SeekableStream s = new ByteArraySeekableStream(image);
			RenderedImage img = JAI.create("stream", s);
			ColorModel cmld = img.getColorModel();
			if(cmld.getPixelSize()>8){
				//Kan ikke sjekke for invertering ved høyere bits pr pixel
				return false;
			}
			int width = 15;
			int height = 15;
			int total = height * width;
			for(int x=0;x<total;x++){
				int rgbtmp = cmld.getRGB(x);
				if(rgbtmp==-16777216){
					retval = true;
				}else if(rgbtmp==-1){
					retval=false;
				}
			}
				
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		return retval;
	}
	
	public void invertMultipleTiffs(byte[] image, String tmpfilename)throws Exception{
		SeekableStream s = new ByteArraySeekableStream(image);
		TIFFDecodeParam param = new TIFFDecodeParam();
		ImageDecoder dec = ImageCodec.createImageDecoder("tiff", s, param);
		System.out.println(dec.getNumPages());
		ArrayList list = new ArrayList();
		for(int x=0;x<dec.getNumPages();x++){
			RenderedImage img = new NullOpImage(dec.decodeAsRenderedImage(x), 
												null, 
												null,
												OpImage.OP_IO_BOUND);
			ColorModel cmdl = img.getColorModel();		
			System.out.println("Blå: " + cmdl.getBlue(1));
			System.out.println("Rød: " + cmdl.getRed(1));
			System.out.println("Grønn: " + cmdl.getGreen(1));
			
			PlanarImage imgout = JAI.create("invert", img);
			list.add(imgout);
		}
		
		//return saveMtiff(list);
		saveMtiffFile(list,tmpfilename);
	}
	
	//Denne brukes ikke da retur av bytearray funker dårlig med JAI???
	private byte[] saveMtiff(ArrayList images)throws Exception{
		ImageOutputStream ios = null;
		ByteArrayOutputStream outputStream = null;
		byte[] retval = null;
		try{
			ImageWriter imgwriter = (ImageWriter)ImageIO.getImageWritersByMIMEType("image/tiff").next();
			ImageWriteParam param = imgwriter.getDefaultWriteParam();
			if(param.canWriteCompressed()){
				param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
				param.setCompressionType("CCITT T.6");
				param.setCompressionQuality(0.7f);
			}
			if(param.canWriteTiles()){
				param.setTilingMode(ImageWriteParam.MODE_DISABLED);
			}
			
			outputStream = new ByteArrayOutputStream();
			ios = ImageIO.createImageOutputStream(outputStream);
			ios.setByteOrder(ByteOrder.LITTLE_ENDIAN);
			imgwriter.setOutput(ios);
			imgwriter.prepareWriteSequence(null); 
			for(int x=0;x<images.size();x++){
				IIOMetadata metadata = getMetadata(imgwriter, (PlanarImage)images.get(x), param);
				IIOImage tmpimg = new IIOImage((PlanarImage)images.get(x), null, metadata);				
				imgwriter.writeToSequence(tmpimg, param);
			}
			ios.flush();
			//ios.close();
			outputStream.flush();
			retval = outputStream.toByteArray();
		}catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}finally{
			ios.flush();
			ios.close();
			outputStream.flush();
			outputStream.close();
		}
		return retval;
	}

	private void saveMtiffFile(ArrayList images, String tmpfilename)throws Exception{
		ImageOutputStream ios = null;
		FileOutputStream outputStream = null;
		byte[] retval = null;
		try{
			ImageWriter imgwriter = (ImageWriter)ImageIO.getImageWritersByMIMEType("image/tiff").next();
			ImageWriteParam param = imgwriter.getDefaultWriteParam();
			if(param.canWriteCompressed()){
				param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
				param.setCompressionType("CCITT T.6");
				//param.setCompressionType("LZV");
				param.setCompressionQuality(0.7f);
			}
			if(param.canWriteTiles()){
				param.setTilingMode(ImageWriteParam.MODE_DISABLED);
			}
			
			outputStream = new FileOutputStream(tmpfilename);
			ios = ImageIO.createImageOutputStream(outputStream);
			ios.setByteOrder(ByteOrder.LITTLE_ENDIAN);
			imgwriter.setOutput(ios);
			imgwriter.prepareWriteSequence(null);
			for(int x=0;x<images.size();x++){
				IIOMetadata metadata = getMetadata(imgwriter, (PlanarImage)images.get(x), param);
				IIOImage tmpimg = new IIOImage((PlanarImage)images.get(x), null, metadata);				
				imgwriter.writeToSequence(tmpimg, param);
			}
			//Gjør ikke dette da det av en eller annen grun ikke fungerer i denne context'en....
			//retval = getfileAsbytea("/tmp/test.tiff");
		}catch (Exception e) {
			//e.printStackTrace();
			throw new Exception(e.getMessage());
		}finally{
			ios.flush();
			ios.close();
			outputStream.flush();
			outputStream.close();
		}
		//return retval;
	}

	public byte[] getfileAsbytea(String path)throws Exception{
		byte[] retval = null;
		InputStream is = null;
		try {
			File infil = new File(path);
			is = new FileInputStream(infil);
			long length = infil.length();
			if(length>Integer.MAX_VALUE){
				throw new Exception("Filen " + path+ " er for stor!" );
			}
			retval = new byte[(int)length];
			int offset=0;
			int numRead=0;
			while(offset < retval.length && (numRead=is.read(retval,offset,retval.length-offset))>=0){
				offset+=numRead;
			}
		} catch (Exception e) {
			throw new Exception("Feilet i fil til byteArray: " + e.getMessage());
		}finally{
			is.close();
		}
		
		return retval;
	}
	
	private static IIOMetadata getMetadata(ImageWriter imageWriter, RenderedImage image,ImageWriteParam param) {
		TIFFImageMetadata tiffMetadata = (TIFFImageMetadata) getIIOMetadata(image,imageWriter, param);
		TIFFIFD rootIFD = tiffMetadata.getRootIFD();
		BaselineTIFFTagSet base = BaselineTIFFTagSet.getInstance();
		long[][] X_DPI = new long[][] {{200,1}}; 
		long[][] Y_DPI = new long[][] {{200,1}}; 
		
		
		
		//PhotometricInterpretation
		rootIFD.addTIFFField(new TIFFField(base.getTag(262), 0));

		//Compression
		rootIFD.addTIFFField(new TIFFField(base.getTag(259), 4));

		//ResolutionUnit
		rootIFD.addTIFFField(new TIFFField(base.getTag(296), 2));

		//XResolution
		//rootIFD.addTIFFField(new TIFFField(base.getTag(282), 200));
		rootIFD.addTIFFField(new TIFFField(base.getTag(282), TIFFTag.TIFF_RATIONAL,1,X_DPI));

		//YResolution
		//rootIFD.addTIFFField(new TIFFField(base.getTag(283), 200));
		rootIFD.addTIFFField(new TIFFField(base.getTag(283), TIFFTag.TIFF_RATIONAL,1,Y_DPI));

		//BitsPerSample
		rootIFD.addTIFFField(new TIFFField(base.getTag(258), 1));

		//RowsPerStrip
		rootIFD.addTIFFField(new TIFFField(base.getTag(278), image.getHeight()));

		//FillOrder
		rootIFD.addTIFFField(new TIFFField(base.getTag(266), 1));

		return tiffMetadata;
	}

	private static IIOMetadata getIIOMetadata(RenderedImage image, ImageWriter imageWriter,ImageWriteParam param) {
		ImageTypeSpecifier spec = ImageTypeSpecifier.createFromRenderedImage(image);
		IIOMetadata metadata = imageWriter.getDefaultImageMetadata(spec, param);
		return metadata;
	}
}
