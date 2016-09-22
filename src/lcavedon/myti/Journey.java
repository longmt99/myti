package lcavedon.myti;


/**
 * Journey bean
 */
public class Journey {
	
	private String id;
	private String passId;
	//private int depart;
	//private int arrive;
	private String departName;
	private String arriveName;
	private String startTime;
	private User user;
	private boolean newPass;
	private Pass pass;
	
	 
	public Journey(String[] split) {
		this.passId = split[0];
		this.startTime = split[1];
		
	}
	public Journey() {
	}

	public String toOutput() {
		return passId+ "," + this.startTime+"," + this.departName+"," + this.arriveName+","   ;
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
