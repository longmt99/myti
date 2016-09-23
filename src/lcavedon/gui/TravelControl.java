package lcavedon.gui;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import lcavedon.fileio.DataFactory;
import lcavedon.myti.DataException;
import lcavedon.myti.JConstants;
import lcavedon.myti.JConstants.OPTION;
import lcavedon.myti.JConstants.TICKET_STATUS;
import lcavedon.myti.Journey;
import lcavedon.myti.Pass;
import lcavedon.myti.Price;
import lcavedon.myti.Station;
import lcavedon.myti.User;
import lcavedon.myti.Utils;

public class TravelControl  extends VBox{

	@FXML
	private TextArea output;
	@FXML
	private URL location;
	@FXML
	private ResourceBundle resources;
	@FXML
	private ListView<String> cardId;

	@FXML
	private ListView<String> zoneId;

	@FXML
	private ListView<String> lengthId;
	
	@FXML
	private ListView<String> departId;
	@FXML
	private ListView<String> arriveId;
	
	@FXML
	private DatePicker dateId;
	
	@FXML
	private TextField timeId;
	
	@FXML
	private AnchorPane mainPane;
	
	@FXML
	private VBox travelVBox;
	private MainController mainController;
	
	public static String inputPath ="input";
	public static String outputPath ="output";
	
	// Add a public no-args constructor
	public TravelControl(){
	}

	static List<Station> stationList = null;
	static List<Price> priceList = null;
	
	
	public TravelControl(MainController mainController) {
		this.mainController = mainController;
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("travel.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);
		this.inputPath = mainController.inputPath;
		this.outputPath = mainController.outputPath;
		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}
	@FXML
	private void initialize() throws IOException, DataException {
		System.out.println("Init System Data MyTi GUI");
		ObservableList<String> items =FXCollections.observableArrayList ();
		List<User> users = new DataFactory(inputPath,outputPath).listUser();
		for (User user : users) {
			items.add(user.getId());
		}
		cardId.setItems(items);
		
		// Create a collection of stations, priceList for the system
		stationList = new DataFactory(inputPath,outputPath).listStation();
		priceList = new DataFactory(inputPath,outputPath).listPrice();
		
		// Set default
		/*
		cardId.getSelectionModel().selectFirst();
		lengthId.getSelectionModel().selectFirst();
		zoneId.getSelectionModel().selectFirst();
		departId.getSelectionModel().selectFirst();
		arriveId.getSelectionModel().selectFirst();
		dateId.setValue(LocalDate.now());
		String now = Utils.toDateString(Utils.addHour(1, new Date()), JConstants.ddMMyyyyHHmm);
		timeId.setText(now.substring(8));
		*/
		
		output.setText("Add a pass and add journey..." + "\nConfig Path \n   INPUT: ["+ inputPath+ "] \n   OUTPUT: ["+outputPath+"]");
		lengthId.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

		    @Override
		    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		        try {
		        	if(newValue.equals(JConstants.OPTION.ALL_DAY)){
		        		timeId.setText("");
		        		timeId.setDisable(true);
		        	}else{
		        		timeId.setPromptText("Time: ex) 1400");
		        		timeId.setDisable(false);
		        	}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
		    }
		});
	}

	@FXML
	private void purchasePass() {
		try {
			userId = cardId.getSelectionModel().getSelectedItem();		
			
			
			String length = lengthId.getSelectionModel().getSelectedItem();
			String zone = zoneId.getSelectionModel().getSelectedItem();
			
			String inputTime = timeId.getText();
			DateTimeFormatter format = DateTimeFormatter.ofPattern(JConstants.ddMMyyyy);
			
			
			if(Utils.isEmpty(userId)  || Utils.isEmpty(length) || Utils.isEmpty(zone) || Utils.isEmpty(dateId.getValue())){
				output.setText(Utils.showErrorDialog("Data input can't empty: length, zone, userId, date "));
				return;
			}
			if(length.equals(JConstants.OPTION.HOUR_2)){
				if(Utils.isEmpty(inputTime) || inputTime.length()!=4){
					output.setText(Utils.showErrorDialog("Data input is invalid format"));
					return;
				}
			}else{
				inputTime =  "0000";
			}
			user = new DataFactory(inputPath,outputPath).getUser(userId );
			String inputDate = format.format(dateId.getValue()); 
			Date date = Utils.toDate(inputDate+"_235959", JConstants.ddMMyyyy_HHmmss);
			if (date.before(new Date())) {
				output.setText(Utils.showErrorDialog("Input Date " +inputDate +" is invalid"));
				return;
			}
			String startTime = inputDate + inputTime;
			Pass pass = new Pass(length, zone, user);
			pass.setStartTime(startTime);
			pass.setStatus(TICKET_STATUS.CHARGED);
			processTravelPass(pass);
		} catch (Exception e) {
			e.printStackTrace();
			output.setText(Utils.showErrorDialog(e.getMessage()));
			return;
		} 
	}

	
	   
	@FXML
	private void purchaseJourney() {
		try {
			userId = cardId.getSelectionModel().getSelectedItem();		
			String arrive =  arriveId.getSelectionModel().getSelectedItem();
			String depart =  departId.getSelectionModel().getSelectedItem();
			String inputTime = timeId.getText();
			if(Utils.isEmpty(userId) || Utils.isEmpty(arrive) || Utils.isEmpty(depart) || Utils.isEmpty(dateId.getValue())
					||Utils.isEmpty(inputTime)){
				output.setText(Utils.showErrorDialog("Data input can't empty: arrive, depart, userId, date, time "));
				return;
			}
			
			if(depart.equals(arrive)){
				output.setText(Utils.showErrorDialog("Station depart or arrive is invalid"));
				return; 
			}
			
			user = new DataFactory(inputPath,outputPath).getUser(userId );
			Journey journey = new Journey(user);
			
			
			 DateTimeFormatter format = DateTimeFormatter.ofPattern(JConstants.ddMMyyyy);
			String inputDate = format.format(dateId.getValue()); 
			Date date = Utils.toDate(inputDate+"_235959", JConstants.ddMMyyyy_HHmmss);
			if (date.before(new Date())|| inputTime.length() !=4) {
				output.setText(Utils.showErrorDialog("Input DateTime " +inputDate +" "+ inputTime+ " is invalid"));
				return;
			}
			String startTime = inputDate + inputTime;
			journey.setStartTime(startTime);
			journey.setDepartName(depart);
			journey.setArriveName(arrive);
			processJourney(journey);
		} catch (Exception e) {
			e.printStackTrace();
			output.setText(Utils.showErrorDialog(e.getMessage()));
			return;
		} 
		
	}
	

	private Journey journey;
	private Pass pass;
	private User user;
	
	private String userId;

	private List<Pass> travelPassOfUser = null;// User can multiple previous travel pass
	public Pass coverPass = null;// cover travel pass will include new journey 
	
	
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
		this.travelPassOfUser = new DataFactory(inputPath,outputPath).listPassByUser(userId);
		this.journey = journey;
		
		
		journey.setId(Utils.buildJourneyId(user.getId()));
		
			/**
			 *  Try to create the cheapest travel pass, update previous pass of user
			 *  If not found a pass to updating, so have to create new pass,
			 */
			boolean updatedPassSucces =false;
			
			String departZone = new DataFactory(inputPath,outputPath).getStationById(journey.getDepartName()).getZone();
			String arriveZone = new DataFactory(inputPath,outputPath).getStationById(journey.getArriveName()).getZone();
			
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
						double newPrice = new DataFactory(inputPath,outputPath).getPriceByPeriodAndZone(OPTION.ALL_DAY, myPass.getZone());
						System.out.println("The current Travel Pass [" + myPass+"] But the new Journey is outside that time:" + startTime);
						double plusPrice = newPrice-price;
						plusPrice = Math.floor(plusPrice * 100) / 100;
						 double credit = user.getCredit();
						if (credit < price) {
							output.setText(Utils.showErrorDialog("Try upgrade your Travel pass to All day need [$"+plusPrice+"]. But not enough fund [$"+credit+"]"+
												"\n Recharge your ticket please."));
						}
						double rate = user.getDiscount(startTime);
						user.setCredit(credit - plusPrice*rate);
						String confirm = Utils.inputYesNo("\n Try upgrade your Travel pass to All day need [$"+plusPrice+"]. \n  Do you want upgrate to All Day?" );					
						
						if (confirm.equals(OPTION.NO)){
							output.setText(" OPTION.NO ");
							return ; // cancel
						}			
						new DataFactory(inputPath,outputPath).storeUser(user);					
						myPass.setStatus(TICKET_STATUS.UPDATED);
						myPass.setLength(OPTION.ALL_DAY);
						myPass.setPrice(newPrice);
						new DataFactory(inputPath,outputPath).storeTravelPass(myPass);
						journey.setPass(myPass);
						updatedPassSucces =true;
						
						output.setText(Utils.showInformationDialog("Great. Updated Travel pass to All day.\nThis is cheaper" + myPass));
						
						break;
					}else if(is2Hours && isCoverTime){
						Utils.showInformationDialog("Your Journey already fit time 2Hours: " + myPass);
						output.setText("Your Journey already fit time 2Hours: " + myPass);
						updatedPassSucces =true;
						journey.setPass(myPass);
						break;
					}else if(isAllDay && isCoverDate){
						updatedPassSucces =true;
						
						output.setText(Utils.showInformationDialog("Your Travel pass already purchased All day: " + myPass));
						journey.setPass(myPass);
						break;
					}
				}	
			}	
			/**
			 * We could not found a better travel pass, So you need create new travel pass
			 */
			if(!updatedPassSucces){
				output.setText(Utils.showWarningDialog("We could not found a  travel pass, So you need creat new travel pass. Click 'Buy Pass'"));
				return;
			}
	
		new DataFactory(inputPath,outputPath).storeJourney(journey);
		output.setText("Your journey ["+journey.getId()+"] is for ["+journey.getPass().getZoneName()+"] " + "\nStarting at " + journey.getStartTime()+". \nEnjoy your travel!");
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
		this.travelPassOfUser = new DataFactory(inputPath,outputPath).listPassByUser(userId);
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
					output.setText("Try finding previous your Travel Pass [" + myPass+"]");
					double price = myPass.getPrice();
					double newPrice = new DataFactory(inputPath,outputPath).getPriceByPeriodAndZone(OPTION.ALL_DAY, pass.getZone());
					double credit = user.getCredit();
					double rate = user.getDiscount(startTime);
						 
					if(is2Hours && isCoverTime){
						double plusPrice = newPrice-price;
						
						plusPrice = Math.floor(plusPrice * 100) / 100;
						if (credit < price) {
							output.setText(Utils.showErrorDialog("Try upgrade your Travel pass to All day need [$"+plusPrice+"]. \n But not enough fund [$"+credit+"]. Recharge your ticket please"));
							
						}
						
						String confirm = Utils.inputYesNo("\n Try upgrade your Travel pass to All day need [$"+plusPrice+"]. \n Do you want upgrate to All Day?" );
						if (confirm.equals(OPTION.NO)){
							output.setText(" OPTION.NO ");
							return null; // cancel
						}			
						
						user.setCredit(credit - plusPrice*rate);
						new DataFactory(inputPath,outputPath).storeUser(user);					
						myPass.setStatus(TICKET_STATUS.UPDATED);
						myPass.setLength(OPTION.ALL_DAY);
						myPass.setPrice(newPrice);
						new DataFactory(inputPath,outputPath).storeTravelPass(myPass);
						output.setText(Utils.showInformationDialog("Great. Updated Travel pass to All day. This is cheaper" + myPass));		
						return myPass.getId();
					}else if(isAllDay && isCoverDate){
						output.setText(Utils.showInformationDialog("Your Travel pass already purchased All day.\nNo need purchase more, Just use it: " + myPass));
						return myPass.getId();
					}
				}
			}
			// more case
			
			/**
			 * We could not found a better travel pass, So you need create new travel pass
			 */
			// Create new travel pass
			
			String confirm = Utils.inputYesNo("Try finding previous your Travel Pass. \n We could not found a better travel pass, So you need creat new travel pass?");					
			
			if (confirm.equals(OPTION.NO)){
				output.setText(" OPTION.NO ");
				return null; // cancel
			}			
			
			Pass newPass = calculateCredit(pass, user);
			return newPass.getId();
	}
	/**
	 * Process purchase, store ticket, deduce credit of user if
	 * 
	 * @param length
	 * @param zone
	 * @param user
	 * @throws DataException
	 * @throws DataException
	 * @throws IOException
	 * @throws Exception
	 */
	public Pass calculateCredit(Pass pass, User user)
			throws DataException, DataException, IOException {
		double price = 0.0;
		String length = pass.getLength();
		String zone = pass.getZone();
		
		price = new DataFactory(inputPath,outputPath).getPriceByPeriodAndZone(length,zone);
		
		double rate = user.getDiscount(pass.getStartTime());
		price =  price*rate;
		
		String userId = user.getId();

		double credit = user.getCredit();
		if (credit < price) {
			throw new DataException("Not Enough Funds Exception " +credit);
		}
		// Process purchase Travel Pass
		// HOUR_2 travel passes can buy multiple journey, this Map have key is pass ID 
		pass.setId(buildTravelPassId(userId));
		pass.setPrice(price);
		pass.setUserId(userId);
		new DataFactory(inputPath,outputPath).storeTravelPass(pass);

		// Update credit
		user = new DataFactory(inputPath,outputPath).getUser(userId);//load
		user.setCredit(credit - price);
		user.setPurchaseCount(user.getPurchaseCount() + 1);
		new DataFactory(inputPath,outputPath).storeUser(user);
		System.out.println("\nYou purchased success  Travel Pass on MyTi card ["
						+ user.getId() + "], \n Pass Id: " + pass);
		System.out.println("\nYour remaining credit is: $" + user.getCredit());
		output.setText("You purchased success  Travel Pass on MyTi card ["
				+ user.getId() + "], Pass Id: " + pass+ "\nYour remaining credit is: $" + user.getCredit());
		return pass;
	}
	public static String buildTravelPassId(String userId) throws IOException, DataException {	
		User user = new DataFactory(inputPath,outputPath).getUser(userId);
		int uniqueId = user.getUniqueId();
		++uniqueId;  
		user.setUniqueId(uniqueId);
		new DataFactory(inputPath,outputPath).storeUser(user);
		return userId + "_" + String.format("%02d",uniqueId);
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
