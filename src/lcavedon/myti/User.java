package lcavedon.myti;




/**
 * User bean
 */
public class User {
	
	private int userNo;
	private String id;
	 private double credit;
	 private String type;
	 private String name; 
	 private String email;
	 private int purchaseCount;
	 private double discount;
	 private int uniqueId;
	 
	public User(String[] split) {
		this.userNo = Integer.parseInt(split[0].trim());
		this.credit = Double.parseDouble(split[1].trim());
		this.type = split[2].trim();
		this.name = split[3].trim();
		this.email = split[4].trim();
		this.purchaseCount = Integer.parseInt(split[5].trim());
		this.uniqueId = Integer.parseInt(split[6].trim());
	}
	public User() {
	}
	public User(String id) {
		this.id = id;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public double getCredit() {
		return credit;
	}
	public void setCredit(double credit) {
		this.credit = Math.floor(credit * 100) / 100;
	}
	public int getUserNo() {
		return userNo;
	}
	public void setUserNo(int userNo) {
		this.userNo = userNo;
	}
	public String toOutput() {
		return this.userNo +","+ this.credit+","+this.type+"," +this.name+"," +this.email+","  +this.purchaseCount+"," +this.uniqueId+",";
	}
	public String toString() {
		return this.userNo +","+ this.credit+","+this.type+"," +this.name+"," +this.email+","  +this.purchaseCount+",";
	}
	public int getPurchaseCount() {
		return purchaseCount;
	}
	public void setPurchaseCount(int purchaseCount) {
		this.purchaseCount = purchaseCount;
	}
	public double getDiscount() {
		return discount;
	}
	public void setDiscount(double discount) {
		this.discount = discount;
	}
	/**
	 *  SeniorMyTi ticket holders travel free on Sundays
	 *  they do not have to pay for any journeys. 
	 *  discountRate is set at 0.5 (i.e. half-price)
	 * @param startTime
	 * @return
	 * @throws DataException 
	 */
	public double getDiscount(String startTime) throws DataException {
		String date = Utils.getWeekday(startTime);
		if(type.equals(JConstants.USER_TYPE.SENIOR) || type.equals(JConstants.USER_TYPE.JUNIOR)){
			if(date.contains("Sunday") && type.equals(JConstants.USER_TYPE.SENIOR)){
				return 0;
			}else{
				return 0.5;
			}
		}
		return 1;
	}
	public int getUniqueId() {
		return uniqueId;
	}
	public void setUniqueId(int uniqueId) {
		this.uniqueId = uniqueId;
	}
}
