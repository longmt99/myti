package lcavedon.myti;


/**
 * Journey bean
 */
public class Journey {
	private String id;
	private String passId;
	private String userId;
	private String departName;
	private String arriveName;
	private String startTime;
	private String purchased;
	private User user;
	private boolean newPass;
	private Pass pass;
	
	 
	public Journey(String[] split) {
		this.passId = split[0];
		this.startTime = split[1];
		this.departName = split[2];
		this.arriveName = split[3];
	}
	public Journey() {
	}

	public String toOutput() {
		return passId+ "," + this.startTime+"," + this.departName+"," + this.arriveName+","   ;
	}
	
	public String toString() {
		String zone_ = this.pass.getZoneName();
		String journeyDetails ="";
		if(Utils.isNotEmpty(this.id)){
			journeyDetails = "Journey ID: "+ this.id +" for ["+ zone_ +"]"+" starting at ["+ this.startTime+"] "   
					+ "\nFrom station ["+this.departName+"] to [" + this.arriveName+"]";
		}
		return journeyDetails +"\nTravel pass"+this.pass ;
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
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getPurchased() {
		return purchased;
	}
	public void setPurchased(String purchased) {
		this.purchased = purchased;
	}
	
}
