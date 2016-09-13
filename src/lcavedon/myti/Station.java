package lcavedon.myti;

/**
 * Station bean
 */
public class Station {
	
	 private int id;
	 private String name;
	 private String zone;
	 public int departCount =0;
	 public int arriveCount=0;
	public Station(String[] split) {
		this.id = Integer.parseInt(split[0].trim());
		this.name = split[1].trim();
		this.zone = split[2].trim();
	}
	public String toString() {
		return this.id +","+ this.name +","+ zone +"("+departCount+","+arriveCount+") ";
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getZone() {
		return zone;
	}
	public void setZone(String zone) {
		this.zone = zone;
	}
	public String buildSelection() {
		return name;
	} 
	
}
