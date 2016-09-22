package lcavedon.myti;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lcavedon.fileio.DataFactory;
import lcavedon.myti.JConstants.OPTION;
import lcavedon.myti.JConstants.TICKET_STATUS;

/**
 * Process Travel pass and Journey of user
 *
 */
public class TicketManager {

	private Journey journey;
	private Pass pass;
	private User user;

	private List<Pass> travelPassOfUser = null;// User can multiple previous travel pass
	public Pass coverPass = null;// cover travel pass will include new journey 
	public TicketManager(User user) throws IOException{
		this.user = user;
		String userId = user.getId();
		this.travelPassOfUser = DataFactory.listPassByUser(userId);
	}
	/**
	 * Check You do not have a travel pass covering that day/time.
	 * @param startTime
	 * @throws HaveNotCoverPassException 
	 */
	public void checkTravelPassByTime(Journey journey) throws DataException {
		this.journey =journey;
		String startTime = journey.getStartTime();
		String dateOfJourney = startTime.substring(0,8);// ddMMyyyy
		
		for (Pass pass : travelPassOfUser) {
			if(pass.isAllDay() && pass.isCoverDate(dateOfJourney)){
				/**
				 * 1.If the current Travel Pass is an All Day and we are in the day and the pass covers the
				 *  journey zones, then a new Travel Pass does not need to be purchased;
				 */
				System.out.println("\nCurrent Travel Pass is an All Day and we are in the day and the pass covers the  journey zones,"
									+" then a new Travel Pass does not need to be purchased");
				System.out.println("Your travel pass: "+pass);
				this.coverPass = pass;
				return;
			}else if(pass.isHours()){
				Date startJourney = Utils.toDate(journey.getStartTime(), JConstants.ddMMyyyyHHmm);
				/**2. If the new Journey fits inside the 2 Hours of the current Travel Pass, then a new
				 * Travel Pass does not need to be purchased; 
				 */
				if(pass.isFit2Hours(startJourney)){
					System.out.println("\nThe new Journey fits inside the 2 Hours of the current Travel Pass,"
							+ " then a new Travel Pass does not need to be purchased");
					this.coverPass = pass;
					return;
				}
			}
		}
		throw new DataException("You do not have a travel pass covering date/time: "+ startTime);
	}
	public void checkTravelPassByZone(Journey journey) throws  DataException {
		this.journey =journey;
		int depart = journey.getDepart();
		int arrive = journey.getArrive();
		String departZone = DataFactory.getStationById(depart).getZone();
		String arriveZone = DataFactory.getStationById(arrive).getZone();
		if(!this.coverPass.isCoverZone(departZone,arriveZone)){
			throw new DataException("You do not have a travel pass covering Zone: "+ journey.getDepartName()+" - " + journey.getArriveName());
		}
		
	}
	/**
	 * Best for journeys
	 * 1. Check have a travel pass covering that day/time.
	 * 2. Cheapest ticket
	 * 
	 * @return 
	 * @throws IOException 
	 * @throws DataException 
	 * @throws DataException 
	 * @throws InvalidInputDataException 
	 */
	public void processJourney(Journey journey) throws IOException, DataException {
		this.journey = journey;
		
		
		journey.setId(Utils.buildJourneyId(user.getId()));
		
			/**
			 *  Try to create the cheapest travel pass, update previous pass of user
			 *  If not found a pass to updating, so have to create new pass,
			 */
			boolean updatedPassSucces =false;
			
			String departZone = DataFactory.getStationById(journey.getDepart()).getZone();
			String arriveZone = DataFactory.getStationById(journey.getArrive()).getZone();
			
			String startTime = journey.getStartTime();
			String dateOfJourney = startTime.substring(0,8);// ddMMyyyy
			
			for (Pass myPass : travelPassOfUser) {
				boolean is2Hours = myPass.isHours();
				boolean isAllDay = myPass.isAllDay();
				boolean isCoverDate = myPass.isCoverDate(dateOfJourney);
				boolean isCoverTime = myPass.isCoverTime(startTime);
				boolean isCoverZone = myPass.isCoverZone(departZone, arriveZone);
				if(isCoverZone) {
					System.out.println("Trying to fit Journey to previous your Travel Pass [" + myPass+"]");
					if(is2Hours && !isCoverTime){
						double price = myPass.getPrice();
						double newPrice = DataFactory.getPriceByPeriodAndZone(OPTION.B, myPass.getZone());
						System.out.println("The current Travel Pass [" + myPass+"] But the new Journey is outside that time:" + startTime);
						double plusPrice = newPrice-price;
						plusPrice = Math.floor(plusPrice * 100) / 100;
						 double credit = user.getCredit();
						if (credit < price) {
							System.out.println("Try upgrade your Travel pass to All day need [$"+plusPrice+"]. But not enough fund [$"+credit+"]");
							throw new DataException("Not enough fund [$"+credit+"]. Recharge your ticket please");
						}
						double rate = user.getDiscount(startTime);
						user.setCredit(credit - plusPrice*rate);
						System.out.println("\n Try upgrade your Travel pass to All day need [$"+plusPrice+"]. Do you want upgrate to All Day?" );
						String confirm = MyTiSystem.inputYesNo();					
						if (!confirm.equals(OPTION.A) && !confirm.equals(OPTION.B)) {
							System.out.println("You have selected an illegal option.");
							throw new DataException("You have selected an illegal option " +confirm);
						}
						if (confirm.equals(OPTION.B)){
							return ; // cancel
						}			
						DataFactory.storeUser(user);					
						myPass.setStatus(TICKET_STATUS.UPDATED);
						myPass.setLength(OPTION.B);
						myPass.setPrice(newPrice);
						DataFactory.storeTravelPass(myPass);
						journey.setPass(myPass);
						updatedPassSucces =true;
						System.out.println("Great. Updated Travel pass to All day. This is cheaper" + myPass); 
						break;
					}else if(is2Hours && isCoverTime){
						System.out.println("Your Journey already fit time 2Hours: " + myPass);
						updatedPassSucces =true;
						journey.setPass(myPass);
						break;
					}else if(isAllDay && isCoverDate){
						updatedPassSucces =true;
						System.out.println("Your Travel pass already purchased All day: " + myPass);
						journey.setPass(myPass);
						break;
					}
				}	
			}	
			/**
			 * We could not found a better travel pass, So you need create new travel pass
			 */
			if(!updatedPassSucces){
				System.out.println("\nWe could not found a  travel pass, So you need creat new travel pass");
				String length = MyTiSystem.inputPeriod();
				if (length.equals(OPTION.X))
					return; // cancel
				if (!length.equals(OPTION.A) && !length.equals(OPTION.B)) {
					System.out.println("You have selected an illegal option.");
					throw new DataException("You have selected an illegal option " +length);
				}
				// 
				Pass pass = new Pass();
				pass.setLength(length);
				pass.setStartTime(startTime);
				if(departZone.equals(arriveZone)){
					pass.setZone(departZone);
				}else{
					pass.setZone(JConstants.OPTION.B);
				}
				pass.setStatus(TICKET_STATUS.REQUIRED);
				calculateCredit(pass, user);
				journey.setPass(pass);
			}
	
		DataFactory.storeJourney(journey);
		System.out.println("Your journey is for ["+journey.getPass().getZoneName()+"] " + "starting at " + journey.getStartTime()+". \nEnjoy your travel!");
	}
	
	/**
	 * Best for travel pass
	 * 1. Check have a travel pass covering that day/time.
	 * 2. Cheapest travel pass
	 * 
	 * @return 
	 * @throws IOException 
	 * @throws DataException 
	 * @throws DataException 
	 * @throws InvalidInputDataException 
	 */
	public String processTravelPass(Pass pass) throws IOException,  DataException {
		this.pass = pass;
		
			/**
			 *  Try to create the cheapest travel pass, update previous pass of user
			 *  If not found a pass to updating, so have to create new pass,
			 */
			String zone = pass.getZone();
			String startTime = pass.getStartTime();
			String dateOfJourney = startTime.substring(0,8);// ddMMyyyy
			
			for (Pass myPass : travelPassOfUser) {
				boolean is2Hours = myPass.isHours();
				boolean isAllDay = myPass.isAllDay();
				boolean isCoverDate = myPass.isCoverDate(dateOfJourney);
				boolean isCoverTime = myPass.isCoverTime(startTime);
				boolean isCoverZone = myPass.getZone().equals(zone);
				if(isCoverZone) {
					System.out.println("Try finding previous your Travel Pass [" + myPass+"]");
					
					double price = myPass.getPrice();
					double newPrice = DataFactory.getPriceByPeriodAndZone(OPTION.B, pass.getZone());
					double credit = user.getCredit();
					double rate = user.getDiscount(startTime);
						 
					if(is2Hours && isCoverTime){
						double plusPrice = newPrice-price;
						
						plusPrice = Math.floor(plusPrice * 100) / 100;
						if (credit < price) {
							System.out.println("Try upgrade your Travel pass to All day need [$"+plusPrice+"]. But not enough fund [$"+credit+"]");
							throw new DataException("Not enough fund [$"+credit+"]. Recharge your ticket please");
						}
						
						System.out.println("\n Try upgrade your Travel pass to All day need [$"+plusPrice+"]. Do you want upgrate to All Day?" );
						String confirm = MyTiSystem.inputYesNo();					
						if (!confirm.equals(OPTION.A) && !confirm.equals(OPTION.B)) {
							System.out.println("You have selected an illegal option.");
							throw new DataException("You have selected an illegal option " +confirm);
						}
						if (confirm.equals(OPTION.B)){
							return null; // cancel
						}			
						
						user.setCredit(credit - plusPrice*rate);
						DataFactory.storeUser(user);					
						myPass.setStatus(TICKET_STATUS.UPDATED);
						myPass.setLength(OPTION.B);
						myPass.setPrice(newPrice);
						DataFactory.storeTravelPass(myPass);
						System.out.println("Great. Updated Travel pass to All day. This is cheaper" + myPass);
						return myPass.getId();
					}else if(isAllDay && isCoverDate){
						System.out.println("Your Travel pass already purchased All day. No need purchase more, Just use it: " + myPass);
						return myPass.getId();
					}
				}
			}
			// more case
			
			/**
			 * We could not found a better travel pass, So you need create new travel pass
			 */
			// Create new travel pass 
			System.out.println("\nWe could not found a better travel pass, So you need creat new travel pass");
			String confirm = MyTiSystem.inputYesNo();					
			if (!confirm.equals(OPTION.A) && !confirm.equals(OPTION.B)) {
				System.out.println("You have selected an illegal option.");
				throw new DataException("You have selected an illegal option " +confirm);
			}
			if (confirm.equals(OPTION.B)){
				return null; // cancel
			}			
			
			Pass newPass = calculateCredit(pass, user);
			return newPass.getId();
	}
	
	/**
	 * Process purchase, store ticket, deduce credit of user if
	 * (length.equals(OPTION.HOUR_2) && zones.equals(OPTION.HOUR_2)) { // CODE HERE:
	 * purchase a 2 Hour Zone 1 Travel Pass on this MyKi card; } else if
	 * (length.equals(OPTION.HOUR_2) && zones.equals(OPTION.ALL_DAY)) { // CODE HERE:
	 * purchase a 2 Hour Zone 2 Travel Pass on this MyKi card; } else if
	 * (length.equals(OPTION.ALL_DAY) && zones.equals(OPTION.HOUR_2)) { // CODE HERE:
	 * purchase an All Day Zone 1 Travel Pass on this MyKi card; } else if
	 * (length.equals(OPTION.ALL_DAY) && zones.equals(OPTION.ALL_DAY)) { // CODE HERE:
	 * purchase an All Day Zone 2 Travel Pass on this MyKi card; }
	 * 
	 * @param length
	 * @param zone
	 * @param user
	 * @throws DataException
	 * @throws DataException
	 * @throws IOException
	 * @throws Exception
	 */
	public static Pass calculateCredit(Pass pass, User user)
			throws DataException, DataException, IOException {
		double price = 0.0;
		String length = pass.getLength();
		String zone = pass.getZone();
		
		price = DataFactory.getPriceByPeriodAndZone(length,zone);
		
		double rate = user.getDiscount(pass.getStartTime());
		price =  price*rate;
		
		String userId = user.getId();

		double credit = user.getCredit();
		if (credit < price) {
			throw new DataException("Not Enough Funds Exception " +credit);
		}
		// Process purchase Travel Pass
		// HOUR_2 travel passes can buy multiple journey, this Map have key is pass ID 
		pass.setId(Utils.buildTravelPassId(userId));
		pass.setPrice(price);
		pass.setUserId(userId);
		DataFactory.storeTravelPass(pass);

		// Update credit
		user = DataFactory.getUser(userId);//load
		user.setCredit(credit - price);
		user.setPurchaseCount(user.getPurchaseCount() + 1);
		DataFactory.storeUser(user);
		System.out.println("\nYou purchased success  Travel Pass on MyTi card ["
						+ user.getId() + "], \n Pass Id: " + pass);
		System.out.println("Your remaining credit is: $" + user.getCredit());
		return pass;
	}
	public static void showAllJourneyByMyTiCard() throws IOException, DataException {
		System.out.println("\n  All Journeys made using MyTi cards:");
		
		// List all MyTi cards
		List<User> userList = DataFactory.listUser();
		
		for (User user : userList ) {
			String userId= user.getId();
			// List list Pass By MyTi cards ID - user ID
			System.out.printf("MyTi Card:" + userId);
			List<Pass> passList = DataFactory.listPassByUser(userId);
			// HOUR_2 travel passes can buy multiple journey, this Map have key is pass ID 
			int count=0;
			for (Pass pass : passList) {
				++count;
				System.out.printf("\n"+ count+ "."+pass.getLengthName() +" " +  "  Travel Pass purchased on " + Utils.getWeekday(pass.getStartTime()));
				String passId = pass.getId();
				List<Journey> journeyList = DataFactory.listJourneyByPassId(passId);
				System.out.println("\n     Journeys on this pass:");
				int index =0;
				for (Journey journey  : journeyList) {
					index++;
					System.out.printf("         + "+index+". From %8s to  %8s start at %10s\n",journey.getDepartName(),journey.getArriveName(),journey.getStartTime());
				}
			}
		}
		
	}

	public static void showStationStatistics() throws IOException {
		System.out.println("\n Station travel statistics:");
		Map<Integer,Station> stationShow = new HashMap<Integer, Station>() ;
		
		List<Station> stationList = DataFactory.listStation();
		for (Station station : stationList) {
			stationShow.put(station.getId(), station);
		}
		List<Journey> journeyList = DataFactory.listJourney();
		for (Journey journey : journeyList) {
			int depart = journey.getDepart();
			int arrive = journey.getArrive();
			stationShow.get(depart).departCount++;
			stationShow.get(arrive).arriveCount++;
		}
		 Set<Integer> keys = stationShow.keySet();
		 for (Integer key : keys) {
			 Station station = stationShow.get(key);
			 //Central: 1 journeys started here, 0 journeys ended here
			System.out.printf(" %14s : %3s journeys started here, %3s journeys ended here\n",""+ station.getName(),station.departCount,station.arriveCount);
		}
	}
	public Journey getJourney() {
		return journey;
	}
	public void setJourney(Journey journey) {
		this.journey = journey;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Pass getPass() {
		return pass;
	}
	public void setPass(Pass pass) {
		this.pass = pass;
	}
	
}
