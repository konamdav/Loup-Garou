package log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;


/* classe statique permettant d'écrire des logs dans un fichier. */
public class Log {

	private static PrintWriter pw;
	private static File logFile;
	private static Priority priority;
	private static BufferedWriter bw;

	/* méthode d'initialisation de la classe statique permettant de définir un fichier par défaut ou de le créer s'il n'existe pas */
	public static void setup(String logFilePath) 
	{
		try {

			priority = Priority.INFO;
			 logFile = new File(logFilePath);
	         if(!logFile.exists()) {
	        	 logFile.createNewFile();
	         } 
		} catch (Exception ex) {
			System.out.println("Le fichier spécifié est introuvable");
		}
	}
	/* écrit le message passé en paramètre précédé de son niveau de priorité et de la date dans le fichier de log.
	 * le niveau de priorité est celui actuellement défini dans la classe */
	public static void writeLog(String log) throws LogException
	{
		String time = "" + LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute() + ":"
				+ LocalDateTime.now().getSecond();
		 FileWriter fileWritter;
		try {
			fileWritter = new FileWriter(logFile.getName(),true);
			bw = new BufferedWriter(fileWritter);
	         String data ="["+time+"]"+" "+priority+" : "+log+"\r\n";
	         bw.write(data);
	         bw.close();
		} catch (IOException e) {
			throw new LogException(e.getMessage());
		}
         
	}
	/* écrit un log avec le niveau de priorité passé en paramètre */
	public static void writeLog(String log, Priority prio) throws LogException{
		setPriority(prio);
		writeLog(log);
	}
	/* insère un séparateur dans le fichier de log */
	public static void writeSeparator() throws LogException {
		 FileWriter fileWritter;
		try {
			fileWritter = new FileWriter(logFile.getName(),true);
			 bw = new BufferedWriter(fileWritter);
	         String data = "\r\n------------------------------\r\n\r\n";
	         bw.write(data);
	         bw.close();
		} catch (IOException e) {
			throw new LogException(e.getMessage());
		}
        
	}
	/* retourne le chemin du fichier de log */
	public static String getLogFilePath() {
		return logFile.exists() ? 
				 "le fichier spécifié est introuvable ou la méthode setup() n'a pas été appelée":logFile.getAbsolutePath();
	}
	/* change le fichier de log */
	public static void setLogFilePath(String logFilePath) {
		setup(logFilePath);
	}
	 /* change le niveau de priorité */
	public static void setPriority(Priority prio) {
		priority = prio;
	}
	/* efface le contenu du fichier de log */
	public static void clearLogFile() {
		FileWriter fileWritter;
		try {
			fileWritter = new FileWriter(logFile.getName());
			 PrintWriter pw = new PrintWriter(fileWritter);
	         pw.print("");
	         pw.close();
		} catch (IOException e) {
			//System.out.println(e.getMessage());
		}

	}

}