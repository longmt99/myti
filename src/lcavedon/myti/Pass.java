package lcavedon.myti;

import java.util.Date;
import java.util.List;

import lcavedon.myti.JConstants.OPTION;
import lcavedon.myti.JConstants.PERIOD;
import lcavedon.myti.JConstants.ZONE;

/**
 * Travel Pass bean
 */
public class Pass {
	
	private String id;
	private String userId;
	private String length;
	private String zone;
	private String status;	
	private double price;	
	private String startTime;
	public List<Journey> journeys;
	
	 
	public Pass(String[] split) {
		this.userId = split[0];
		this.length = split[1];
		this.zone = split[2];
		this.price = Double.parseDouble(split[3]);
		this.status = split[4];
		this.startTime = split[5];
	}
	public Pass() {
	}
	public Pass(String length, String zone, User user) {
		this.length = length;
		this.zone = zone;
		this.userId = user.getId();
	}
	public Pass(Journey journey) {
		this.userId = journey.getUser().getId();
	}
	public boolean isHours(){
		return JConstants.OPTION.A.equals(length);
	}
	public boolean isAllDay(){
		return JConstants.OPTION.B.equals(length);
	}
	
	/**
	 * Check cover time if pass is 2hours
	 * @return
	 * @throws DataException 
	 */
	public boolean isFit2Hours(Date startTimeInput) throws DataException{
		Date start =  Utils.toDate(startTime, JConstants.ddMMyyyyHHmm);
		Date end =Utils.addHour(2, start);
	 	return (!startTimeInput.before(start)) && end.after(startTimeInput);
	}
	public String toOuput() {
		return this.userId +","+ length+","+zone+","+ this.price+","
				+ this.status +","+ this.startTime+",";
	}
	public String getZoneName() {
		return this.zone.equals(OPTION.A) ? ZONE.Z1 : ZONE.Z2;
	}
	public String getLengthName() {
		return this.length.equals(OPTION.A) ? PERIOD.HOURS : PERIOD.ALL_DAY;
	}
	
	public String toString() {
		String length_ = this.length.equals(OPTION.A) ? PERIOD.HOURS : PERIOD.ALL_DAY;
		String zone_ = this.zone.equals(OPTION.A) ? ZONE.Z1 : ZONE.Z2;		
		
		String content = "ID: "+ this.id +" In period "+ " ["+ length_+"] "	;
		if(length.equals(OPTION.A)){
			content += " Start time: " + this.startTime;
		}else{
			content += " Time of purchase: " + this.startTime.substring(0,8);
		}
		String priceString = " at ["+zone_+"] price $"+ this.price+" ";
		if(price==0d){
			priceString = " just FREE for senior MyTi on every Sunday ";
		}
		content +=priceString;
		return content;
	}
	/**
	 * Check is Cover Date '301120161300' cover  '30112016'
	 * @param dateOfJourney
	 * @return
	 */
	public boolean isCoverDate(String dateOfJourney){
		return this.startTime.startsWith(dateOfJourney);
	}
	/**
	 * Check is Cover Date '301120161300' cover  '301120161400'
	 * @param dateOfJourney
	 * @return
	 * @throws DataException 
	 */
	public boolean isCoverTime(String dateOfJourney) throws DataException{
		Date start =  Utils.toDate(startTime, JConstants.ddMMyyyyHHmm);
		Date end =Utils.addHour(2, start);
	 	Date startTimeInput = Utils.toDate(dateOfJourney, JConstants.ddMMyyyyHHmm);
		return (!start.after(startTimeInput)) && end.after(startTimeInput);
	}
	/**
	 * PassTraval cover zone of journey
	 * @param departZone
	 * @param arriveZone
	 * @return
	 */
	public boolean isCoverZone(String departZone,String arriveZone){
		return zone.equals(JConstants.OPTION.B) || (zone.equals(departZone)&&zone.equals(arriveZone));
	}
	public String getLength() {
		return length;
	}
	public void setLength(String length) {
		this.length = length;
	}
	public String getZone() {
		return zone;
	}
	public void setZone(String zone) {
		this.zone = zone;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	
	public String getId(){
		return this.id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public List<Journey> getJourneys() {
		return journeys;
	}
	public void setJourneys(List<Journey> journeys) {
		this.journeys = journeys;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	
}
