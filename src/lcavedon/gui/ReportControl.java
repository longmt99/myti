package lcavedon.gui;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import lcavedon.fileio.DataFactory;
import lcavedon.myti.DataException;
import lcavedon.myti.JConstants;
import lcavedon.myti.Journey;
import lcavedon.myti.JConstants.CARD;
import lcavedon.myti.Station;
import lcavedon.myti.User;
import lcavedon.myti.Utils;

public class ReportControl extends AnchorPane {

	@FXML
	private URL location;
	@FXML
	private ResourceBundle resources;
	@FXML
	private ListView<String> cardId;
	
	@FXML
	 private TableView<Station> stationTbl;
	
	private User user;
	
	private String userId;
	
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
		ObservableList<String> items =FXCollections.observableArrayList ();
		List<User> users = new DataFactory(inputPath,outputPath).listUser();
		for (User user : users) {
			items.add(user.getId());
		}
		cardId.setItems(items);
		cardId.getSelectionModel().selectFirst();
		loadStationList();
		loadJourneyList();
		
	}

	private void loadJourneyList() {
		
	}

	private void loadStationList() throws IOException {
		Map<Integer,Station> stationShow = new HashMap<Integer, Station>() ;
		
		List<Station> stationList = new DataFactory(inputPath,outputPath).listStation();
		for (Station station : stationList) {
			stationShow.put(station.getId(), station);
		}
		List<Journey> journeyList = new DataFactory(inputPath,outputPath).listJourney();
		for (Journey journey : journeyList) {
			String depart = journey.getDepartName();
			String arrive = journey.getArriveName();
			stationShow.get(depart).departCount++;
			stationShow.get(arrive).arriveCount++;
		}
		 Set<Integer> keys = stationShow.keySet();
		 ObservableList<Station> items =FXCollections.observableArrayList ();
		 for (Integer key : keys) {
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
