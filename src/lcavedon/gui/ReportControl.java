package lcavedon.gui;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import lcavedon.fileio.DataFactory;
import lcavedon.myti.DataException;
import lcavedon.myti.Journey;
import lcavedon.myti.Pass;
import lcavedon.myti.Station;
import lcavedon.myti.User;
import lcavedon.myti.Utils;

public class ReportControl extends AnchorPane {

	@FXML
	private URL location;
	@FXML
	private ResourceBundle resources;
	
	@FXML
	 private TableView<Station> stationTbl;
	
	@FXML
	 private TableView<Journey> journeyTbl;
	private User user;
	@FXML
	private TextArea journeyOutput;
	
	private String inputPath;
	private String outputPath;
	
	
	
	// Add a public no-args constructor
	public ReportControl(){
	}
	
	public ReportControl(MainController mainController) {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("report.fxml"));
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
		System.out.println("Init User Data MyTi GUI");
		loadStationList();
		loadJourneyList();
		
		journeyTbl.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Journey>() {

		    @Override
		    public void changed(ObservableValue<? extends Journey> observable, Journey oldValue, Journey newValue) {
		        try {
					journeyOutput.setText(newValue.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
		    }
		});
		
		
	}

	private void loadJourneyList() throws IOException, DataException {
		System.out.println("\n  All Journeys made using MyTi cards:");
		
		// List all MyTi cards
		List<User> userList = new DataFactory(inputPath,outputPath).listUser();
		 ObservableList<Journey> items =FXCollections.observableArrayList ();
		for (User user : userList ) {
			String userId= user.getId();
			// List list Pass By MyTi cards ID - user ID
			
			System.out.printf("MyTi Card:" + userId);
			List<Pass> passList = new DataFactory(inputPath,outputPath).listPassByUser(userId);
			// HOUR_2 travel passes can buy multiple journey, this Map have key is pass ID 
			int count=0;
			for (Pass pass : passList) {
				++count;
				String purchased = Utils.getWeekday(pass.getStartTime());
				System.out.printf("\n"+ count+ "."+pass.getLengthName() +" " +  "  Travel Pass purchased on " + purchased);
				
				String passId = pass.getId();
				List<Journey> journeyList = new DataFactory(inputPath,outputPath).listJourneyByPassId(passId);
				System.out.println("\n     Journeys on this pass:");
				int index =0;
				if(journeyList.size()==0){
					Journey journey = new Journey();
					journey.setPass(pass);
					journey.setUserId(userId);		
					journey.setPurchased(purchased);
					items.add(journey);
					continue;
				}
				for (Journey journey  : journeyList) {
					journey.setPass(pass);
					journey.setUserId(userId);
					journey.setPurchased(purchased);
					index++;
					System.out.printf("         + "+index+". From %8s to  %8s start at %10s\n",journey.getDepartName(),journey.getArriveName(),journey.getStartTime());
					items.add(journey);
				}
			}
		}
		journeyTbl.setItems(items);
	}

	private void loadStationList() throws IOException {
		Map<String,Station> stationShow = new HashMap<String, Station>() ;
		
		List<Station> stationList = new DataFactory(inputPath,outputPath).listStation();
		for (Station station : stationList) {
			stationShow.put(station.getName(), station);
		}
		List<Journey> journeyList = new DataFactory(inputPath,outputPath).listJourney();
		for (Journey journey : journeyList) {
			String depart = journey.getDepartName();
			String arrive = journey.getArriveName();
			stationShow.get(depart).departCount++;
			stationShow.get(arrive).arriveCount++;
		}
		 Set<String> keys = stationShow.keySet();
		 ObservableList<Station> items =FXCollections.observableArrayList ();
		 for (String key : keys) {
			 Station station = stationShow.get(key);
			 //Central: 1 journeys started here, 0 journeys ended here
			//System.out.printf(" %14s : %3s journeys started here, %3s journeys ended here\n",""+ station.getName(),station.departCount,station.arriveCount);
			 items.add(station);
		}
		 stationTbl.setItems(items);
		
	}

	
	    
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}

}
