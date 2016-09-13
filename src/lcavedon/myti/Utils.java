package lcavedon.myti;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lcavedon.database.DataFactory;
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
	public static String buildTravelPassId(String userId) throws IOException, DataException {	
		User user = DataFactory.getUser(userId);
		int uniqueId = user.getUniqueId();
		++uniqueId;  
		user.setUniqueId(uniqueId);
		DataFactory.storeUser(user);
		return userId + "_" + String.format("%02d",uniqueId);
	}
	
	public static String buildJourneyId(String userId) throws DataException {
		return userId+toDateString(new Date(), JConstants.HHmmss);
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
		if(Utils.isEmpty(inputType) || !"abc".contains(inputType) || inputType.equals(OPTION.A)){
			return USER_TYPE.ADULT;//Default
		}else if(inputType.equals(OPTION.B)){
			return USER_TYPE.JUNIOR;
		}else if(inputType.equals(OPTION.C)){
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
