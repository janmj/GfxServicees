package si.tjenester.GfxServices.util;

import si.tjenester.GfxServices.util.db.ResourceHandler;
import si.tjenester.GfxServices.util.io.FileIoHandler;

import com.pdftools.NativeLibrary;
import com.pdftools.pdf2pdf.Pdf2PdfAPI;
import com.pdftools.pdfvalidator.PdfError;
import com.pdftools.pdfvalidator.PdfValidatorAPI;

public class PdfToolsFacade {

	public PdfToolsFacade() {
		// TODO Auto-generated constructor stub
	}
	
	public static void main(String[] args){
		PdfToolsFacade test = new PdfToolsFacade();
		try {
			FileIoHandler fio = new FileIoHandler();
			//byte[] testpdf = fio.getfileAsbytea("/home/janmj/test/Gfxtest/pdf/wstest_1.pdf");
			byte[] testpdf = fio.getfileAsbytea("/home/janmj/test/Gfxtest/resWSpdf.pdf");
			System.out.println(test.verifyPDFA(testpdf));
			if(test.verifyPDFA(testpdf)){
				System.out.println("Filen er gyldig PDF/A");
			}else{
				System.out.println("Konverterer...");
				byte[] tmppdf = test.conovertToPDFA(testpdf);
				System.out.println(test.verifyPDFA(tmppdf));
			}
		} catch (Exception e) {
			System.err.println("Feil: " + e.getMessage());
		}
	}
	
	public byte[] conovertToPDFA(byte[] pdftoconvert)throws Exception{
		ResourceHandler rsh = new ResourceHandler();
		byte[] retval = null;
		byte[][] workarray = null;
		byte[] errmsg = null;
		Pdf2PdfAPI pdfconv = new Pdf2PdfAPI();
		String cprofile="";
		try {
			//cprofile = rsh.getResourceString("PDFACOLORPROFILE");
			cprofile = "/etc/ImageMagick/sRGB.icm";
		} catch (Exception e) {
			throw new Exception("Fant ikke fargeprofil. Kan ikke convertere til PDF/A " + e.getMessage());
		}

		try {
			pdfconv.setOutputIntentProfile(cprofile);
			pdfconv.setReportSummary(true);
			workarray = pdfconv.convertMem(pdftoconvert, "");
			for(int x=0;x<workarray.length;x++){
				if(x==0){
					retval = workarray[x].clone();
				}else if(x==1){
					errmsg = workarray[x].clone();
				}
			}
		} catch (Exception e) {
			throw new Exception("Feilet i convertering til PDF/A :" +new String(errmsg) + e.getMessage());
		}finally{
			pdfconv.destroyObject();
		}
		return retval;
	}
	
	public boolean verifyPDFA(byte[] pdftoverify)throws Exception{
		boolean retval = true;
		try{
			PdfValidatorAPI verifyer = new PdfValidatorAPI();
			verifyer.setStopOnError(false);
			verifyer.setReportingLevel(2);
			if(verifyer.open(pdftoverify, "", NativeLibrary.COMPLIANCE.ePDFA1b)){
				verifyer.validate();
			}
			PdfError err = verifyer.getFirstError();
			if(err != null){
				retval = false;
				while(err != null){
					int ierr = err.getErrorCode();
					String errMsg = err.getMessage();
					int pagenum = err.getPageNo();
					int iCount = err.getCount();
					System.err.println(ierr + " : " + errMsg + " : " + pagenum + " : " + iCount);
					err = verifyer.getNextError();
				}
			}
			verifyer.close();
			verifyer.destroyObject();
		}catch (Exception e) {
			throw new Exception("Feilet i verifisering av PDF/A : " + e.getMessage());
		}
		return retval;
	}
}
