package lcavedon.myti;

public class JConstants {

	public interface CARD {
		public final static double CREDIT_LIMIT = 100.0;  // maximum allowed credit
		public final static double LEGAL_MULTIPLE = 5.00; // multiple that we can re-charge by

		// we need to keep track of all (successful) purchases made
		public final static int MAX_PURCHASES = 100;
	    // create a structure to keep track of travel passes purchased
	}
	public interface USER_TYPE {
		public static final String ADULT = "Adult";
		public static final String JUNIOR = "Junior";
		public static final String SENIOR = "Senior";
	}
	public interface OPTION {
		public static final String A = "a";
		public static final String B = "b";
		public static final String C = "c";
		public static final String X = "x";
	}
	public interface PERIOD {
		public static final String HOURS = "2 Hours";
		public static final String ALL_DAY = "All day";
	}
	public interface ZONE {
		public static final String Z1 = "Zone 1";
		public static final String Z2 = "Zone 1 and 2";
	}
	
	public interface TICKET_STATUS {
		public static final String CHARGED = "charged";
		public static final String REQUIRED = "required";
		public static final String USED = "used";
		public static final String UPDATED = "updated";
		public static final String EXPIRED = "expired"; //@TODO
		public static final String FREE = "free";
	}
	public interface DATA {
		public static final String STATION = "data/STATION.csv";
		public static final String PRICE = "data/PRICE.csv";
	}
	
	public static final String ddMMyyyy_HHmmss = "ddMMyyyy_HHmmss";
	public static final String ddMMyyyyHHmm = "ddMMyyyyHHmm";
	public static final String ddMMyyyy = "ddMMyyyy";
	public static final String HHmmss = "HHmmss";
	
}
