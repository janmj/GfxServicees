package si.tjenester.GfxServices.util;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import javax.xml.bind.annotation.XmlElement;

@XmlRootElement(name="List")
public class JaxbList<T>{
	protected List<String> list;
	
	public JaxbList(){}
	
	public JaxbList(List<String> list){
		this.list = list;
	}
	
	@XmlElement(name="item")
	public List<String> getList(){
		return list;
	}
}
