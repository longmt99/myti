package lcavedon.myti;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;
import lcavedon.database.DataFactory;
import lcavedon.myti.JConstants.OPTION;
import lcavedon.myti.JConstants.TICKET_STATUS;
import lcavedon.myti.JConstants.USER_TYPE;

import org.junit.Test;

public class MyTiTest extends TestCase {

	/**
	 * Test case generate travel pass ID always unique
	 * @throws IOException
	 * @throws DataException
	 */
	@Test
	public void testGenerateID_never_dupplicate() throws IOException, DataException {
		String userId = "lc";
		String passIdUnique1 = Utils.buildTravelPassId(userId);
		System.out.println(passIdUnique1);
		String passIdUnique2 = Utils.buildTravelPassId(userId);
		System.out.println(passIdUnique2);
		assertTrue(!passIdUnique1.equals(passIdUnique2));
	}
	
	/**
	 * Test case test Case User Create Travel Pass Nomal, No fit, no checking cheaper
	 * It's first time create tranvel pass
	 * Not discount for Adult, normal  user Junior)
	 * Note: clean all data at pass.txt
	 * @throws IOException
	 * @throws DataException
	 */
	@Test
	public void testCaseUserCreateTravelPassNomal() throws IOException, DataException {
		String userId = "lc";
		User user = DataFactory.getUser(userId);
		assertEquals(USER_TYPE.ADULT,user.getType());
		assertTrue(user.getCredit()>10);
		Pass pass = new Pass(OPTION.A, OPTION.A, user);
		String startTime = Utils.toDateString(new Date(), JConstants.ddMMyyyyHHmm);
		pass.setStartTime(startTime);
		pass.setStatus(TICKET_STATUS.CHARGED);
		String passID = new TicketManager(user).processTravelPass(pass);
		assertNotNull(passID);
		
		List<Pass> passList = DataFactory.listPassByUser(userId);
		assertTrue(passList.size()==1);
		Pass result = passList.get(0);
		assertEquals(OPTION.A,result.getLength());
		assertEquals(OPTION.A,result.getZone());
		assertEquals(3.5d,result.getPrice());
		
	}
	/**
	 * Travel pass but Not SUNDAY for Discount Half price
	 * @throws IOException
	 * @throws DataException
	 */
	@Test
	public void testCaseUserCreateTravelPassButDiscountHalf() throws IOException, DataException {
		String userId = "lc1";
		User user = DataFactory.getUser(userId);
		assertEquals(USER_TYPE.SENIOR,user.getType());
		assertTrue(user.getCredit()>10);
		Pass pass = new Pass(OPTION.A, OPTION.A, user);
		String startTime = Utils.toDateString(new Date(), JConstants.ddMMyyyyHHmm);
		pass.setStartTime(startTime);
		pass.setStatus(TICKET_STATUS.CHARGED);
		String passID = new TicketManager(user).processTravelPass(pass);
		assertNotNull(passID);
		
		List<Pass> passList = DataFactory.listPassByUser(userId);
		assertTrue(passList.size()==1);
		Pass result = passList.get(0);
		assertEquals(OPTION.A,result.getLength());
		assertEquals(OPTION.A,result.getZone());
		assertEquals(1.75,result.getPrice());
	}
	/**
	 *  Travel pass SUNDAY for free Senior
	 * @throws IOException
	 * @throws DataException
	 */
	@Test
	public void testCaseUserCreateTravelPassButFree() throws IOException, DataException {
		String userId = "lc1";
		User user = DataFactory.getUser(userId);
		assertTrue(user.getCredit()>10);
		Pass pass = new Pass(OPTION.A, OPTION.A, user);
		String startTime = "110920161200";// SUNDAY for free Senior
		pass.setStartTime(startTime);
		pass.setStatus(TICKET_STATUS.CHARGED);
		String passID = new TicketManager(user).processTravelPass(pass);
		assertNotNull(passID);
		
		List<Pass> passList = DataFactory.listPassByUser(userId);
		assertTrue(passList.size()==1);
		Pass result = passList.get(0);
		assertEquals(OPTION.A,result.getLength());
		assertEquals(OPTION.A,result.getZone());
		assertEquals(1.75,result.getPrice());
	}
}
