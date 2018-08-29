package si.tjenester.GfxServices.util.log;

import si.tjenester.GfxServices.util.db.DbLogHandler;

public final class Logger {
	private static DbLogHandler dblog = null;
	
	public Logger(){
		
	}
	
	public static void logMessage(String message){
		getDblog().logMessage(message);
	}
	
	public static void logError(String message, String exception){
		getDblog().logError(exception, message);
	}

	private static DbLogHandler getDblog(){
		try{
			if(dblog.equals(null)){
				dblog = new DbLogHandler();
			}
		}catch (Exception e) {
			dblog = new DbLogHandler();
		}
		return dblog;
	}
	
}
