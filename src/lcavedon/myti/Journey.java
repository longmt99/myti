package lcavedon.myti;

import java.util.List;

/**
 * Journey bean
 */
public class Journey {
	
	private String id;
	private String passId;
	private int depart;
	private int arrive;
	private String departName;
	private String arriveName;
	private String startTime;
	private User user;
	private boolean newPass;
	private Pass pass;
	
	 
	public Journey(String[] split) {
		this.passId = split[0];
		this.startTime = split[1];
		this.depart = Integer.parseInt(split[2]);
		this.arrive = Integer.parseInt(split[3]);
		
	}
	public Journey() {
	}

	public String toOutput() {
		return passId+ "," + this.startTime+"," + this.depart+"," + this.arrive+","   ;
	}
	
	public String toString() {
		String zone_ = this.pass.getZoneName();		
		return "ID: "+ this.id +" for ["+ zone_ +"]"+" starting at ["+ this.startTime+"] "   
				+ " From station ["+this.departName+"] to [" + this.arriveName+"]"   ;
	}
	public Journey(User user) {
		this.user =user;
	}
	
	
	public String getId(){
		return this.id;
	}
	
	public int getDepart() {
		return depart;
	}
	public void setDepart(int depart) {
		this.depart = depart;
	}
	public int getArrive() {
		return arrive;
	}
	public void setArrive(int arrive) {
		this.arrive = arrive;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDepartName() {
		return departName;
	}
	public void setDepartName(String departName) {
		this.departName = departName;
	}
	public String getArriveName() {
		return arriveName;
	}
	public void setArriveName(String arriveName) {
		this.arriveName = arriveName;
	}
	public void setStationName(List<Station> stationList) {
		for (Station station : stationList) {
			int id = station.getId();
			if(id==depart){
				this.departName=station.getName();
			}else if(id==arrive){
				this.arriveName=station.getName();
			}
		}
		
	}
	public boolean isNewPass() {
		return newPass;
	}
	public void setNewPass(boolean isNewPass) {
		this.newPass = isNewPass;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getPassId() {
		return passId;
	}
	public void setPassId(String passId) {
		this.passId = passId;
	}
	public Pass getPass() {
		return pass;
	}
	public void setPass(Pass pass) {
		this.pass = pass;
		setPassId(pass.getId());
	}
	
}
