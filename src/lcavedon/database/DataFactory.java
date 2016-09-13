package lcavedon.database;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import lcavedon.myti.DataException;
import lcavedon.myti.JConstants.DATA;
import lcavedon.myti.Journey;
import lcavedon.myti.Pass;
import lcavedon.myti.Price;
import lcavedon.myti.Station;
import lcavedon.myti.User;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

/**
 * Load data row from CVS file, 
 * @TODO will replace by DataFactory structure
 *
 */
public class DataFactory {

	private static final String PATH = "data/";
	private static final String TXT = ".txt";
	
	 private static SqlSessionFactory sqlSessionFactory;
	 
	    static {
	        try {
	 
	            String resource = "lcavedon/database/config.xml";
	            Reader reader = Resources.getResourceAsReader(resource);
	 
	            if (sqlSessionFactory == null) {
	                sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
	            }
	        }
	        catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	    
	    
	public static User getUser(String userId) throws IOException, DataException {
		SqlSession session = sqlSessionFactory.openSession();
		try {
			MytiMapper mapper = session.getMapper(MytiMapper.class);
			return mapper.getUser(userId);
		} finally {
			session.close();
		}
	}
	public static List<User> listUser() throws IOException {
		List<User> dataList = new ArrayList<User>();
		Properties properties = new Properties();
		FileInputStream  in =null;
		try {
		   in = new FileInputStream(PATH + User.class.getSimpleName()+TXT);
		
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
	public static List<Station> listStation() {
		List<Station> dataList = null;
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(DATA.STATION));
			String line;
			dataList = new ArrayList<Station>();
			Station station = null;
				while ((line = br.readLine()) != null) {
					if(line.startsWith("#")){
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
	public static List<Price> listPrice() {
		List<Price> dataList = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(DATA.PRICE));
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
	
	public static void storeTravelPass(Pass pass) throws IOException {
		FileInputStream  in =null;
		Properties properties = new Properties();
		try {
			in = new FileInputStream(PATH + Pass.class.getSimpleName() + TXT);
			properties.load(in);
			properties.setProperty(pass.getId(), pass.toOuput());
			FileOutputStream out = new FileOutputStream(PATH + Pass.class.getSimpleName() + TXT);
			properties.store(out, "#id, userId, length, zone, price,status,startTime,endTime");
			out.close();
		} finally {
			in.close();
		}
	}
	public static void storeJourney(Journey journey) throws IOException {
		FileInputStream  in =null;
		Properties properties = new Properties();
		try {
			in = new FileInputStream(PATH + Journey.class.getSimpleName() + TXT);
			properties.load(in);
			properties.setProperty(journey.getId(), journey.toOutput());
			FileOutputStream out = new FileOutputStream(PATH + Journey.class.getSimpleName() + TXT);
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
	public static void storeUser(User user) throws IOException {
		FileInputStream  in =null;
		Properties properties = new Properties();
		try {
			in = new FileInputStream(PATH + User.class.getSimpleName() + TXT);
			properties.load(in);
			properties.setProperty(user.getId(), user.toOutput());
			FileOutputStream out = new FileOutputStream(PATH + User.class.getSimpleName() + TXT);
			properties.store(out, "id, user_number, credit, type, name, email, purchase_count");
			out.close();
		} finally {
			in.close();
		}
	}
	public static List<Pass> listPassByUser(String userId) throws IOException {
		List<Pass> dataList = new ArrayList<Pass>();
		Properties properties = new Properties();
		FileInputStream  in =null;
		try {
		   in = new FileInputStream(PATH + Pass.class.getSimpleName()+TXT);
		
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
	public static void main(String[] args) throws Exception {
		List<Journey> journeyList = listJourney();
		List<Station> stationList = listStation();
		Map<Integer,Station> stationCount = new HashMap<Integer, Station>() ;
		
		for (Station station : stationList) {
			stationCount.put(station.getId(), station);
		}
		
		for (Journey journey : journeyList) {
			int depart = journey.getDepart();
			int arrive = journey.getArrive();
			stationCount.get(depart).departCount++;
			stationCount.get(arrive).arriveCount++;
		}
		 Set<Integer> keys = stationCount.keySet();
		 for (Integer key : keys) {
			 System.out.println(key+ " : "+ stationCount.get(key).departCount + " - " + stationCount.get(key).arriveCount);
		}
		
	}
	public static List<Journey> listJourney() throws IOException {
		List<Journey> dataList = new ArrayList<Journey>();
		Properties properties = new Properties();
		FileInputStream  in =null;
		try {
		   in = new FileInputStream(PATH + Journey.class.getSimpleName()+TXT);
		
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
	public static List<Journey> listJourneyByPassId(String passId) throws IOException {
		List<Journey> dataList = new ArrayList<Journey>();
		Properties properties = new Properties();
		FileInputStream  in =null;
		try {
		   in = new FileInputStream(PATH + Journey.class.getSimpleName()+TXT);
		
			properties.load(in);
			Set<Object> keys = properties.keySet();
			List<Station> stationList = listStation();
			for (Object journeyId : keys){
				String journeyContent = properties.getProperty((String) journeyId);
				Journey journey = new Journey(StringUtils.split(journeyContent,","));
				if(journey.getPassId().equals(passId)){
					journey.setId(journeyId.toString());
					journey.setStationName(stationList);
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
	public static Station getStationById(int stationId) throws DataException {
		List<Station> stationList = listStation();
		for (Station station : stationList) {
			int id = station.getId();
			if(id==stationId){
				return station;
			}
		}
		throw new DataException("Station Id" + stationId + " is invalid");
	}
	public static double getPriceByPeriodAndZone(String length, String zone) throws DataException {
		List<Price> priceList = listPrice();
		for (Price prc : priceList) {
			if (prc.getLength().equals(length) && prc.getZone().equals(zone)) {
				return prc.getPrice();
			}
		}
		throw new DataException("Period or Zone Id [" + length+"] [" +zone+ "] is invalid");
	}
}
