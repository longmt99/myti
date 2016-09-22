package lcavedon.fileio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import lcavedon.myti.DataException;
import lcavedon.myti.JConstants.DATA;
import lcavedon.myti.Journey;
import lcavedon.myti.Pass;
import lcavedon.myti.Price;
import lcavedon.myti.Station;
import lcavedon.myti.User;
import lcavedon.myti.Utils;

/**
 * Load data row from CVS file, 
 * @TODO will replace by DataFactory structure
 *
 */
@SuppressWarnings("unused")
public class DataFactory {

	//private String PATH = "data/";
	
	private static final String TXT = ".txt";
	private static  String input;
	
	private static  String output;
	
	@SuppressWarnings("static-access")
	public DataFactory(String input,String output){
		this.input =input;
		this.output =output;
	}
	public User getUser(String userId) throws IOException, DataException  {
		FileInputStream  in =null;
		Properties properties = new Properties();
		try {
		   in = new FileInputStream(input + User.class.getSimpleName()+TXT);
		
			properties.load(in);
		
			String userData  = properties.getProperty(userId);
			if(Utils.isEmpty(userData)){
				return null;
			}
			User user = new User(StringUtils.split(userData,","));
			user.setId(userId);
			return user;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}finally{
			in.close();
		}
	}
	public  List<User> listUser() throws IOException {
		List<User> dataList = new ArrayList<User>();
		Properties properties = new Properties();
		FileInputStream  in =null;
		try {
		   in = new FileInputStream(input + User.class.getSimpleName()+TXT);
		
			properties.load(in);
			Set<Object> keys = properties.keySet();
			for (Object object : keys) {
				String data = properties.get(object).toString();
				String[] split =StringUtils.split(data,",");
				User user = new User(split);
				user.setId(object.toString());
				dataList.add(user);
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}finally{
			in.close();
		}
		
		return dataList;
	}
	
	/**
	 * Load constants station
	 * @return
	 */
	public  List<Station> listStation() {
		List<Station> dataList = null;
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(input + DATA.STATION));
			String line;
			dataList = new ArrayList<Station>();
			Station station = null;
				while ((line = br.readLine()) != null) {
					if(line.startsWith("#") || line.trim().length()<=0){
						continue;
					}
					String[] split = StringUtils.split(line,",");
					station = new Station(split);
					dataList.add(station);
				}
				
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataList;
	}
	
	/**
	 * Load constants price for system
	 * @return
	 */
	public  List<Price> listPrice() {
		List<Price> dataList = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(input+DATA.PRICE));
			String line;
			dataList = new ArrayList<Price>();
			Price price = null;
				while ((line = br.readLine()) != null) {
					if(line.startsWith("#")){
						continue;
					}
					String[] split = StringUtils.split(line,",");
					price = new Price(split);
					dataList.add(price);
				}
			
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataList;
	}
	
	public  void storeTravelPass(Pass pass) throws IOException {
		FileInputStream  in =null;
		Properties properties = new Properties();
		try {
			in = new FileInputStream(input + Pass.class.getSimpleName() + TXT);
			properties.load(in);
			properties.setProperty(pass.getId(), pass.toOuput());
			FileOutputStream out = new FileOutputStream(input + Pass.class.getSimpleName() + TXT);
			properties.store(out, "#id, userId, length, zone, price,status,startTime,endTime");
			out.close();
		} finally {
			in.close();
		}
	}
	public  void storeJourney(Journey journey) throws IOException {
		FileInputStream  in =null;
		Properties properties = new Properties();
		try {
			in = new FileInputStream(input + Journey.class.getSimpleName() + TXT);
			properties.load(in);
			properties.setProperty(journey.getId(), journey.toOutput());
			FileOutputStream out = new FileOutputStream(input + Journey.class.getSimpleName() + TXT);
			properties.store(out, "id, passId, startTime, endTime,depart,arrive");
			out.close();
		} finally {
			in.close();
		}
	}
	
	/**
	 * Create or replace update
	 * @param user
	 * @throws IOException
	 */
	public  void storeUser(User user) throws IOException {
		FileInputStream  in =null;
		Properties properties = new Properties();
		try {
			in = new FileInputStream(input + User.class.getSimpleName() + TXT);
			properties.load(in);
			properties.setProperty(user.getId(), user.toOutput());
			FileOutputStream out = new FileOutputStream(input + User.class.getSimpleName() + TXT);
			properties.store(out, "id, user_number, credit, type, name, email, purchase_count");
			out.close();
		} finally {
			in.close();
		}
	}
	public  List<Pass> listPassByUser(String userId) throws IOException {
		List<Pass> dataList = new ArrayList<Pass>();
		Properties properties = new Properties();
		FileInputStream  in =null;
		try {
		   in = new FileInputStream(input + Pass.class.getSimpleName()+TXT);
		
			properties.load(in);
			Set<Object> passes = properties.keySet();
			for (Object passId : passes){
				String passContent = properties.getProperty((String) passId);
				Pass pass = new Pass(StringUtils.split(passContent,","));
				if(pass.getUserId().equals(userId)){
					pass.setId(passId.toString());
					dataList.add(pass);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}finally{
			in.close();
		}
		return dataList;
	}
	public  void main(String[] args) throws Exception {
		
		List<Station> stationList = listStation();
		List<Journey> journeyList = listJourney();
		Map<Integer,Station> stationCount = new HashMap<Integer, Station>() ;
		
		for (Station station : stationList) {
			stationCount.put(station.getId(), station);
		}
		
		for (Journey journey : journeyList) {
			String depart = journey.getDepartName();
			String arrive = journey.getArriveName();
			stationCount.get(depart).departCount++;
			stationCount.get(arrive).arriveCount++;
		}
		 Set<Integer> keys = stationCount.keySet();
		 for (Integer key : keys) {
			 System.out.println(key+ " : "+ stationCount.get(key).departCount + " - " + stationCount.get(key).arriveCount);
		}
		
	}
	public  List<Journey> listJourney() throws IOException {
		List<Journey> dataList = new ArrayList<Journey>();
		Properties properties = new Properties();
		FileInputStream  in =null;
		try {
		   in = new FileInputStream(input + Journey.class.getSimpleName()+TXT);
		
			properties.load(in);
			Set<Object> keys = properties.keySet();
			for (Object journeyId : keys){
				String journeyContent = properties.getProperty((String) journeyId);
				Journey journey = new Journey(StringUtils.split(journeyContent,","));
					journey.setId(journeyId.toString());
					dataList.add(journey);
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}finally{
			in.close();
		}
		return dataList;
	}
	public  List<Journey> listJourneyByPassId(String passId) throws IOException {
		List<Journey> dataList = new ArrayList<Journey>();
		Properties properties = new Properties();
		FileInputStream  in =null;
		try {
		   in = new FileInputStream(input + Journey.class.getSimpleName()+TXT);
		
			properties.load(in);
			Set<Object> keys = properties.keySet();
			for (Object journeyId : keys){
				String journeyContent = properties.getProperty((String) journeyId);
				Journey journey = new Journey(StringUtils.split(journeyContent,","));
				if(journey.getPassId().equals(passId)){
					journey.setId(journeyId.toString());
					dataList.add(journey);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}finally{
			in.close();
		}
		return dataList;
	}
	public  Station getStationById(String stationName) throws DataException {
		List<Station> stationList = listStation();
		for (Station station : stationList) {
			String name = station.getName();
			if(name.equals(stationName)){
				return station;
			}
		}
		throw new DataException("Station Name" + stationName + " is invalid");
	}
	public  double getPriceByPeriodAndZone(String length, String zone) throws DataException {
		List<Price> priceList = listPrice();
		for (Price prc : priceList) {
			if (prc.getLength().equals(length) && prc.getZone().equals(zone)) {
				return prc.getPrice();
			}
		}
		throw new DataException("Period or Zone Id [" + length+"] [" +zone+ "] is invalid");
	}
	public void replateFile() throws FileNotFoundException, IOException {
		String inFile =  input + Journey.class.getSimpleName()+TXT;
	    String outFile = output + Journey.class.getSimpleName()+TXT;
		Utils.replateFile(inFile, outFile);
		
		inFile =  input + Pass.class.getSimpleName()+TXT;
	    outFile = output + Pass.class.getSimpleName()+TXT;
		Utils.replateFile(inFile, outFile);
		
		inFile =  input + User.class.getSimpleName()+TXT;
	    outFile = output + User.class.getSimpleName()+TXT;
		Utils.replateFile(inFile, outFile);
		
	}
}
