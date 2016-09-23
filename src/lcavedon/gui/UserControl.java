package lcavedon.gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import lcavedon.fileio.DataFactory;
import lcavedon.myti.DataException;
import lcavedon.myti.JConstants;
import lcavedon.myti.JConstants.CARD;
import lcavedon.myti.User;
import lcavedon.myti.Utils;

public class UserControl extends AnchorPane {

	@FXML
	private URL location;
	@FXML
	private ResourceBundle resources;
	@FXML
	private ListView<String> cardId;
	
	@FXML
	private Label creditForLbl;
	@FXML
	private ChoiceBox<String> addCreditBox;
	
	@FXML
	private TextField creditOutputTxt;
	
	
	@FXML
	private TextField newUserTxt;
	@FXML
	private TextField userNameTxt;
	
	@FXML
	private TextField emailTxt;
	
	@FXML
	private ChoiceBox<String> userTypeBox;
	
	@FXML
	private ChoiceBox<String> initCreditBox;
	
	
	@FXML
	private TextField userOutputTxt;
	
	private User user;
	
	private String userId;
	
	private MainController mainController;
	private String inputPath;
	private String outputPath;
	
	// Add a public no-args constructor
	public UserControl(){
	}
	
	public UserControl(MainController mainController) {
		this.mainController = mainController;
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("user.fxml"));
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
		loadUserList();
		
		// Set default
		cardId.getSelectionModel().selectFirst();
		initCreditBox.getSelectionModel().selectFirst();
		addCreditBox.getSelectionModel().selectFirst();
		userTypeBox.getSelectionModel().selectFirst();
		showCredit();
		cardId.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

		    @Override
		    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		        try {
					showCredit();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (DataException e) {
					e.printStackTrace();
				}
		    }
		});
	}

	public void loadUserList() throws IOException {
		ObservableList<String> items =FXCollections.observableArrayList ();
		List<User> users = new DataFactory(inputPath,outputPath).listUser();
		for (User user : users) {
			items.add(user.getId());
		}
		cardId.setItems(items);
	}
	
	@FXML
	private void addNewUser() {
		try {
			userId = newUserTxt.getText();	
			String userName = userNameTxt.getText();
			String email = emailTxt.getText();
			if(Utils.isEmpty(userId)  || Utils.isEmpty(userName) || Utils.isEmpty(email)){
				userOutputTxt.setText(Utils.showErrorDialog("Data input can not not empty: userId, Name, email."));
				return;
			}
			user = new DataFactory(inputPath,outputPath).getUser(userId);
			if (user != null) {
				System.out.println("MyTi Card matching that ID is existed ...");
				userOutputTxt.setText(Utils.showErrorDialog("MyTi Card "+userId+" is existed"));
				return;
			} else {
				user = new User(userId);
			}

			String typeInput = userTypeBox.getSelectionModel().getSelectedItem();
			user.setType(typeInput);
			user.setName(userName);
			user.setEmail(email);
			user.setCredit(Double.parseDouble(initCreditBox.getSelectionModel().getSelectedItem()));
			user.setPurchaseCount(100);
			new DataFactory(inputPath,outputPath).storeUser(user);
			
			userOutputTxt.setText(Utils.showInformationDialog("Created success [" + userId+ "] and Init credit [$"+ user.getCredit()+"]"));
			loadUserList();
		} catch (Exception e) {
			e.printStackTrace();
			Utils.showErrorDialog(e.getMessage());
			userOutputTxt.setText(e.getMessage());
			return;
		} 
	}

	@FXML
	private void recharge() {
		try {
			userId = cardId.getSelectionModel().getSelectedItem();	
			user = new DataFactory(inputPath,outputPath).getUser(userId);
			double amount =0;
			try {
				amount = Double.parseDouble(addCreditBox.getSelectionModel().getSelectedItem());// 5;//
			} catch (Exception e) {
				creditOutputTxt.setText(Utils.showErrorDialog("Amount numberic invalid"));
				return;
			}
				
	
			if (amount % JConstants.CARD.LEGAL_MULTIPLE != 0) {
				Utils.showErrorDialog("Sorry, you can only add multiples of $"+CARD.LEGAL_MULTIPLE);
				return;
			} else if (amount + user.getCredit() > CARD.CREDIT_LIMIT) {
				// check that it does not go above max amount (raise Exception if it does
				Utils.showErrorDialog("Sorry, the max amount of credit allowed is $"+CARD.CREDIT_LIMIT);
				return;
			}else if (amount<=0){
				creditOutputTxt.setText(Utils.showErrorDialog("Amount numberic invalid"));
				return;
			}
	
			user.setCredit(user.getCredit() + amount);
			//add that credit to the MyTiCard
			new DataFactory(inputPath,outputPath).storeUser(user);
			creditOutputTxt.setText(Utils.showInformationDialog("Recharged [$"+amount+"] to ["+ user.getId() + "]"));
			showCredit();
		} catch (IOException | DataException e) {
			e.printStackTrace();
			creditOutputTxt.setText(Utils.showErrorDialog(e.getMessage()));
			return;
		}
		
	}
	
	private void showCredit() throws IOException, DataException {
		userId = cardId.getSelectionModel().getSelectedItem();	
		if(Utils.isNotEmpty(userId)){
			userId = cardId.getSelectionModel().getSelectedItem();	
			user = new DataFactory(inputPath,outputPath).getUser(userId);
			creditForLbl.setText("Credit for ["+ user.getId() + "] is $"+user.getCredit()+"");
		}	
	}
	    
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}

}
