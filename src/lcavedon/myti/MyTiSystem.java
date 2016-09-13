package lcavedon.myti;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import lcavedon.myti.JConstants.CARD;
import lcavedon.myti.JConstants.OPTION;
import lcavedon.myti.JConstants.TICKET_STATUS;

import org.apache.commons.lang.StringUtils;

/**
 * Implementation of basic MyTi system outline. A MyTi card can be used to
 * purchase travel passes: 2Hour or All Day, Zone 1 or Zones 1+2. Each type of
 * travel pass has different prices. This program loops through options of
 * purchasing a pass, topping up the MyTi card, or viewing current credit.
 * 
 * This is an *outline* --- you will need to add menu options and fill in the
 * implementation details for all the functionality
 * 
 * @author lcavedon
 *
 */
public class MyTiSystem {

	// create a new Scanner from standard input
	static Scanner input = new Scanner(System.in);
	static User user = null;// TODO  LONGMT changed
	static List<Station> stationList = null;
	static List<Price> priceList = null;

	/**
	 * main program: this contains the main menu loop
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		System.out.println("==== Welcome to MyTi! ===");

		initSystemData();

		// main menu loop: print menu, then do something depending on selection
		int option =-1;
		do {
			printMainMenu();
			do {
				String optionStr = input.next();
				try {
					option = Integer.parseInt(optionStr);
				} catch (Exception e) {
					System.out.println("Invalid number option");
					printMainMenu();					
				}
			} while (option == -1);
			

			// perform correct action, depending on selection
			switch (option) {
			case 1:
				try {
					purchasePass();
				} catch (DataException  e) {
					System.err.println(e.getMessage());
				}
				break;
			case 2:
				try {
					recharge();
				} catch (DataException e) {
					System.err.println(e.getMessage());
				}
				break;
			case 3:
				try {
					showCredit();
				} catch (DataException | IOException e) {
					System.err.println(e.getMessage());
				}
				break;
			case 4:
				try {
					purchaseJourney();
				} catch (DataException | IOException  e) {
					System.err.println(e.getMessage());
				}
				break;
			case 5:
				try {
					TicketManager.showAllJourneyByMyTiCard();
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
				break;
			case 6:
				try {
					TicketManager.showStationStatistics();
				} catch (IOException e) {
					System.err.println(e.getMessage());
				}
				break;
			case 7:
				try {
					addNewUser();
				} catch (DataException | IOException e) {
					System.err.println(e.getMessage());
				}
				break;

			case 0:
				System.out.println("Goodbye!"); // will quit---do nothing
				break;
			default:
				// if no legal option selected, just go round and choose again
				System.out.println("Invalid option!");
				break;
			}

		} while (option != 0);

		// finishing processing ... close the input stream
		input.close();
	}

	
	/**
	 * Load constant - static data
	 */
	private static void initSystemData() {
		// Create a collection of stations, priceList for the system
		stationList = DataFactory.listStation();
		priceList = DataFactory.listPrice();
	}

	
	
	
	
	
	/*
	 * Print the main menu (you have to modify / add options)
	 */
	static void printMainMenu() {
		System.out.println();
		System.out.println("Select an option:");
		System.out.println("1. Buy a Travel Pass using a MyTi card");
		System.out.println("2. Recharge a MyTi card");
		System.out.println("3. Show remaining credit for a MyTi card");
		System.out.println("4. Purchase a Journey using a MyTi card");
		System.out.println("5. Print all Journeys made using all MyTi cards");
		System.out.println("6. Show Station statistics");
		System.out.println("7. Add a new User");
		System.out.println("0. Quit");
		System.out.print("Your option: ");
	}
	
	
	
	

	/*
	 * buy a travel pass using MyTi credit
	 */
	static void purchasePass() throws 	DataException, IOException{
		// first, get the MyTi card that we plan to use to purchase Travel Pass
		System.out.println();

		System.out.println("What is the ID of the MyTi pass:");
		inputUserId();

		if (user.getPurchaseCount() + 1 > CARD.MAX_PURCHASES) {
			throw new DataException("Travel passes purchased max " + CARD.MAX_PURCHASES);
		}
		// ... else we found a matching MyTi card: continue with purchasing
		// Travel Pass
		// print time options
		String length = inputPeriod();
		if (length.equals(OPTION.X))
			return; // cancel
		if (!length.equals(OPTION.A) && !length.equals(OPTION.B)) {
			System.out.println("You have selected an illegal option. Please try again.");
			purchasePass();
			return;
		}
		
		String now = Utils.toDateString(Utils.addHour(1, new Date()), JConstants.ddMMyyyyHHmm);
		System.out.println("\nWhat day is your Travel:? format ddMMyyyy ex: "+now.substring(0,8));
		String inputDate = input.next();// Utils.toDateString(new Date(), JConstants.ddMMyyyy);// 
		Date date = Utils.toDate(inputDate+"_235959", JConstants.ddMMyyyy_HHmmss);
		if (date.before(new Date()) || inputDate.length() !=8) {
			throw new DataException("Input Date " +inputDate +" is invalid");
		}
		String inputTime ="0000";
		if (length.equals(OPTION.A)){
			System.out.println("\nWhat time is your Travel:? format HHmm ex: "+ now.substring(8));
			inputTime = input.next();// "1300";//"1900";//Utils.toDateString(new Date(), JConstants.ddMMyyyyHHmm).substring(8);// 
			
			if (inputTime.equals(OPTION.X))return; // cancel
		}
		String startTime = inputDate + inputTime;
		
		
		Date nowDate = Utils.toDate(startTime, JConstants.ddMMyyyyHHmm);
		
		if (nowDate.before(new Date())) {
			throw new DataException("Input Date " +inputDate +" is invalid");
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(nowDate);
		
		String week = cal .getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.US);
		//String startTime = Utils.toDateString(nowDate, JConstants.ddMMyyyyHHmm);
		System.out.println("\nDay of purchase: " +week +" " + startTime.substring(0,8));
		if(length.equals(OPTION.A)){
			System.out.println("Time of purchase: " + startTime.substring(8));
		}
		System.out.println("For which zones:");
		System.out.println(OPTION.A + ". Zone 1");
		System.out.println(OPTION.B + ". Zones 1 and 2");
		System.out.println(OPTION.X + ". cancel");
		System.out.println("Your selection: ");
		String zone = input.next();// OPTION.A;//
		if (zone.equals(OPTION.X))
			return; // cancel
		if (!zone.equals(OPTION.A) && !zone.equals(OPTION.B)) {
			System.out.println("You have selected an illegal option. Please try again.");
			purchasePass();
			return;
		}
		Pass pass = new Pass(length, zone, user);
		pass.setStartTime(startTime);
		// first check if valid options were selected
		if ((!length.equals(OPTION.A) && !length.equals(OPTION.B))
				|| (!zone.equals(OPTION.A) && !zone.equals(OPTION.B))) {
			System.out.println("You have selected an illegal option. Please try again.");
			// if not, then re-try purchasing a pass
			purchasePass();
			return;
		} else {
			// selected legal options --- purchase a Travel Pass on this MyTi
			// card
			// NOTE: you will need to Check Credit before finalising the
			// purchase!
			// --- Raise an Exception if there is not enough credit
			pass.setStatus(TICKET_STATUS.CHARGED);
			new TicketManager(user).processTravelPass(pass);

		}
	}

	/**
	 * input user Id and load user details. if invalid throw exception
	 * 
	 * @throws InvalidUserIdException
	 * @throws IOException
	 * @throws Exception
	 */
	private static void inputUserId() throws DataException, IOException {
		String id =  input.next();//"lc";//
		// CODE GOES HERE: look up MyTi card matching this id --- if no match,
		// return null
		user = DataFactory.getUser(id);
		if (user == null) {
			System.out.println("No MyTi Card matching that ID was found ...");
			throw new DataException("Invalid UserId Exception: " + id);
		}
	}

	public static String inputPeriod(){
		System.out.println("\nHow long do you need a pass for:");
		System.out.println(OPTION.A + ". 2 Hours");
		System.out.println(OPTION.B + ". All Day");
		System.out.println(OPTION.X + ". cancel");
		System.out.println("Your selection: ");
		String length = input.next();// OPTION.B;// OPTION.A;//
		return length;
	}

	public static String inputYesNo(){
		System.out.println("\n Confirm YES or NO:");
		System.out.println(OPTION.A + ". YES");
		System.out.println(OPTION.B + ". NO");
		System.out.println("Your selection: ");
		String length = input.next();// OPTION.B;// OPTION.A;//
		return length;
	}
	
	
	
	
	
	/*
	 * Recharge a MyTi card
	 */
	static void recharge() throws DataException, IOException {
		// CODE HERE: Get MyTi card id from user and find matching MyTiCard
		System.out.println("What is the ID of the MyTi recharge:");
		inputUserId();

		System.out.println("How much credit do you want to add: ");
		// read charge amount from input Scanner
		double amount =0;
		try {
			amount = input.nextDouble(); // 5;//
		} catch (Exception e) {
			throw new DataException("Amount numberic invalid");
		}
			

		if (amount % JConstants.CARD.LEGAL_MULTIPLE != 0) {
			throw new DataException("Sorry, you can only add multiples of $"+CARD.LEGAL_MULTIPLE);
		} else if (amount + user.getCredit() > CARD.CREDIT_LIMIT) {
			// check that it does not go above max amount (raise Exception if it does
			throw new DataException("Sorry, the max amount of credit allowed is $"+CARD.CREDIT_LIMIT);
		}else if (amount<=0){
			throw new DataException("Amount numberic invalid");
		}

		user.setCredit(user.getCredit() + amount);
		//add that credit to the MyTiCard
		DataFactory.storeUser(user);
		System.out.println("OK. You recharged success MyTi card ["+ user.getId() + "] credit now: $" + user.getCredit());
	}

	
	
	/*
	 * Show the remaining credit on MyTi card
	 */
	static void showCredit() throws  DataException,
			IOException {

		// CODE HERE: Get MyTi card id from user and find matching MyTiCard
		System.out.println("What is the ID of the MyTi to show the Credit:");
		inputUserId();
		// CODE HERE: Display credit for that MyTiCard
		System.out.println("Your credit for MyTiCard: " + user.getCredit());

	}
	
	
	
	
	
	/*
	 * Purchase a Journey using MyTi
	 */
	static void purchaseJourney() throws DataException, IOException {
		// First, get the MyTi card that we plan to use to purchase Journey
		System.out.println("\nWhat is the ID of The MyTi card:");
		
		inputUserId();
		
		TicketManager ticketMan = new TicketManager(user);
		Journey journey = new Journey(user);
		
		String now = Utils.toDateString(Utils.addHour(1, new Date()), JConstants.ddMMyyyyHHmm);
		System.out.println("\nWhat day is your Journey:? format ddMMyyyy ex: "+now.substring(0,8));
		String inputDate = input.next();// Utils.toDateString(new Date(), JConstants.ddMMyyyy);// 
		Date date = Utils.toDate(inputDate+"_235959", JConstants.ddMMyyyy_HHmmss);
		if (date.before(new Date()) || inputDate.length() !=8) {
			throw new DataException("Input Date" +inputDate +" is invalid");
		}
		
		System.out.println("\nWhat time is your Journey:? format HHmm ex: "+ now.substring(8));
		String inputTime = input.next();// "1300";//"1900";//Utils.toDateString(new Date(), JConstants.ddMMyyyyHHmm).substring(8);// 
		
		if (inputTime.equals(OPTION.X))return; // cancel
		
		String startTime = inputDate + inputTime;
		try {
			if (inputTime.length() != 4) {
				throw new DataException("Input time " +inputTime +" is invalid");
			}
		 	journey.setStartTime(startTime);
		} catch (Exception e) {
			throw new DataException("startTime" +startTime  +" is invalid");
		}

		System.out.println("\nForm which is depart-arrive: format ex: 1-2");
		
		System.out.println(Utils.buildStationSelection(stationList));
		System.out.println(OPTION.X + ". cancel");
		System.out.println("Your selection: ");

		String station = input.next();// "1-2";//
		if (station.equals(OPTION.X))
			return; // cancel
		String[] split = StringUtils.split(station, "-");
		if (split.length != 2) {
			throw new DataException("");
		}
		journey.setDepart(Integer.parseInt(split[0]));
		journey.setArrive(Integer.parseInt(split[1]));
		journey.setStationName(stationList);
		

		ticketMan.processJourney(journey);
	}

	
	
	
	
	
	/*
	 * Add new user
	 */
	static void addNewUser() throws IOException, DataException {

		// CODE HERE: Get MyTi card id from user and find matching MyTiCard
		System.out.println("What is user ID :");

		String id = input.next();// "lc_existed";//
		// CODE GOES HERE: look up MyTi card matching this id --- if no match,
		// return null
		//try {
			user = DataFactory.getUser(id);
			if (user != null) {
				System.out.println("MyTi Card matching that ID is existed ...");
				throw new DataException("MyTi Card "+id+" is existed");
			} else {
				user = new User(id);
			}
	

		System.out.println();
		System.out.println("For which user type:");
		System.out.println(OPTION.A + ". Adult");
		System.out.println(OPTION.B + ". Junior");
		System.out.println(OPTION.C + ". Senior");
		System.out.println(OPTION.X + ". Cancel");
		System.out.println("Your selection: ");
		String typeInput = input.next();// OPTION.A;//
		if (typeInput.equals(OPTION.X))
			return; // cancel
		user.setType(Utils.getUserType(typeInput));
		System.out.println("User name:");
		String userName = input.next();// "Lawrence Cavedon
		
		System.out.println("User email address:");
		String email = input.next();// "lawrence@cavedon.com
		
		user.setName(userName);
		user.setEmail(email);
		user.setPurchaseCount(0);
		user.setCredit(0);
		DataFactory.storeUser(user);
		System.out.println("Your account [" + id
				+ "] created success, Now credit for MyTiCard: "
				+ user.getCredit());

	}

	
	
	
	
	


}
