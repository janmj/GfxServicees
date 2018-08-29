package si.tjenester.GfxServices.bean;

import java.io.Serializable;
import java.util.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import si.tjenester.GfxServices.util.JaxbList;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="imagebean")

public class ImageBean implements Serializable {

	private static final long serialVersionUID = 3923251867021234440L;
	
	@XmlAttribute(required=true)
	private String name;
	@XmlAttribute(required=true)
	private String mimetype;
	@XmlAttribute(required=true)
	private byte[] imagedata;
	@XmlAttribute(required=false)
	private ArrayList<String> pages;
	//private JaxbList<String> pages;
	
	public ImageBean(String name, String mimetype, byte[] imagedata) {
		super();
		this.name = name;
		this.mimetype = mimetype;
		this.imagedata = imagedata;
		this.pages = new ArrayList<String>();
		//this.pages = new JaxbList();
	}

	
	public ImageBean(String name, String mimetype, byte[] imagedata, ArrayList<String> pages) {
		super();
		this.name = name;
		this.mimetype = mimetype;
		this.imagedata = imagedata;
		this.pages = pages;
	}
	
	/*
	public ImageBean(String name, String mimetype, byte[] imagedata, JaxbList pages) {
		super();
		this.name = name;
		this.mimetype = mimetype;
		this.imagedata = imagedata;
		this.pages = pages;
	}
	*/
	
	public ImageBean(){
		
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMimetype() {
		return mimetype;
	}

	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}

	public byte[] getImagedata() {
		return imagedata;
	}

	public void setImagedata(byte[] imagedata) {
		this.imagedata = imagedata;
	}

	
	public ArrayList<String> getPages() {
		if(this.pages == null){
			this.pages = new ArrayList<String>();
		}
		return pages;
	}
	
	/*
	public JaxbList<String> getPages() {
		if(this.pages == null){
			this.pages = new JaxbList();
		}
		return pages;
	}
	*/
	
	public void setPages(ArrayList<String> pages) {
		this.pages = pages;
	}
	
	/*
	public void setPages(JaxbList<String> pages) {
		this.pages = pages;
	}*/
	
}
