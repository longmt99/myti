package lcavedon.myti;

/**
 * Price bean (length,zone,price)
 */
public class Price {
	
	private String length;
	private String zone;
	 private double price;
	
	 
	public Price(String[] split) {
		this.length = split[0];
		this.zone = split[1];
		this.price = Double.parseDouble(split[2]);
	}
	public Price() {
	}
	public String toString() {
		return length + "-" + zone + " [$"+price+"]";
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
		return this.length+ this.zone;
	}
}
