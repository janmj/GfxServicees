/**
 * 
 */
package si.tjenester.GfxServices.util.gfxvalidation;

/**
 * @author janmj
 *
 */
public class ImageCheckException extends Exception {

	/**
	 * 
	 */
	public ImageCheckException() {
	}

	/**
	 * @param message
	 */
	public ImageCheckException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public ImageCheckException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ImageCheckException(String message, Throwable cause) {
		super(message, cause);
	}

}
