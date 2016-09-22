package lcavedon.myti;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import lcavedon.fileio.DataFactory;
import lcavedon.myti.JConstants.OPTION;
import lcavedon.myti.JConstants.USER_TYPE;


/**
 * Common functions can be use every where 
 * 
 *
 */
public class Utils {
	
	/**
	 * Generate ID never dupplicate , unique
	 * @param userId
	 * @return
	 * @throws DataException 
	 * @throws IOException 
	 */

	public static String showWarningDialog(String content){
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Warning ");
		alert.setHeaderText(null);
		alert.setContentText(content);

		alert.showAndWait();
		return content;
	}
	
	public static String showErrorDialog(String content){
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error Dialog");
		alert.setHeaderText(null);
		alert.setContentText(content);
		alert.showAndWait();
		return content;
		
	}
	public static String showInformationDialog(String content){
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Information Dialog");
		alert.setHeaderText(null);
		alert.setContentText(content);

		alert.showAndWait();
		
		return content;
	}
	public static String inputYesNo( String content){
		
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation Dialog YES - NO");
		alert.setHeaderText(null);
		alert.setContentText(content);

		ButtonType yes = new ButtonType("YES");
		ButtonType no = new ButtonType("NO", ButtonData.CANCEL_CLOSE);

		alert.getButtonTypes().setAll(yes, no);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == yes){
			return OPTION.YES;
		}else{
			return OPTION.NO;
		}
		
	}
	public static String buildJourneyId(String userId) throws DataException {
		return userId+toDateString(new Date(), JConstants.HHmmss);
	}
	public static void replateFile(String inFile, String outFile) throws FileNotFoundException, IOException {
		InputStream in = new FileInputStream(new File(inFile));
		Path copyTo = Paths.get(outFile);
		Files.copy(in, copyTo, StandardCopyOption.REPLACE_EXISTING);
		in.close();
	}
	public static String toDateString(Date date,String format) throws DataException {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		try {
			return dateFormat.format(date);
		} catch (Exception e) {
			System.out.print(e.getMessage());
			throw new DataException("Exception for the date and time " + date);
		}
	}
	public static Timestamp toTimestamp(String dateString, String format) throws Exception {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		Date date = null;
		try {
			date = dateFormat.parse(dateString);
			return new Timestamp(date.getTime());
		} catch (Exception e) {
			throw new DataException("Exception for the date and time " + dateString);
		}
	}
	public static Date toDate(String dateString, String format) throws DataException {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		Date date = null;
		try {
			date = dateFormat.parse(dateString);
			return date;
		} catch (Exception e) {
			throw new DataException("Exception for the date and time " + dateString);
		}
	}
	public static Date addHour(int hour, Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.HOUR, hour);
		return cal.getTime();
	}
	/**
	 * Check null or "" or "null" or " "
	 * 
	 * @param str
	 * @return
	 */

	public static boolean isEmpty(Object str) {
		if (str == null || str.toString().trim().length() == 0
				|| str.toString().trim().equalsIgnoreCase("null")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isNotEmpty(Object str){
		if (str== null || str.toString().trim().length()==0 
				|| str.toString().trim().equalsIgnoreCase("null")) {
			return false;
		}else{
			return true;
		}
	}
	public static String getUserType(String inputType) {
		if(inputType.equals(USER_TYPE.JUNIOR)){
			return USER_TYPE.JUNIOR;
		}else if(inputType.equals(USER_TYPE.SENIOR)){
			return USER_TYPE.SENIOR;
		}else{
			return USER_TYPE.ADULT;//Default
		}
		
	}
	/**
	 * Select all station
	 * @param stationList
	 * @return
	 */
	public static String buildStationSelection(List<Station> stationList) {
		return buildStationSelection(null,stationList);
	}
	/**
	 * Select station by specified ZONE
	 * @param stationList
	 * @return
	 */
	public static String buildStationSelection(String zone, List<Station> stationList) {
		String selectionString="";
		for (Station stt : stationList) {
			if(Utils.isEmpty(zone) || stt.getZone().equals(zone)){
				selectionString+="\n"+ " "+ stt.getId()+"." +stt.getName();
			}	
		}
		return selectionString;
	}
	
	/**
	 * Return MON, TUE, SUN ...
	 * @param startTime: 170820161538
	 * @return
	 * @throws DataException 
	 */
	public static String getWeekday(String startTime) throws DataException {
		Date date = toDate(startTime,JConstants.ddMMyyyyHHmm);
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);
		calendar.setTime(date);
		
		String weekDay = dayFormat.format(calendar.getTime());
		return weekDay +" at "+ startTime.substring(8);
	}
}
