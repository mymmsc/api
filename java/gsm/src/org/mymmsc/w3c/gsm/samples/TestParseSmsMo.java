/**
 * 
 */
package org.mymmsc.w3c.gsm.samples;

import org.mymmsc.w3c.gsm.pdu.PduContext;
import org.mymmsc.w3c.gsm.pdu.PduParser;
import org.mymmsc.w3c.gsm.pdu.sms.SmsSubmitPdu;

/**
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-gsm 6.3.9
 */
public class TestParseSmsMo {

	/**
	 * 
	 */
	public TestParseSmsMo() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		PduParser pp = new PduParser();

		SmsSubmitPdu pdu = new SmsSubmitPdu();
		String tmpDeliver = "0891683108100005F0040BA13108108300F0000890116141855423800032003000300039002F00310031002F00310036002000310034003A00350035003A0033003000205C0A656C76845BA26237003A60A85F53524D76845E1062374F59989D5DF25C0F4E8E003100305143002C4E3A4E0D5F7154CD60A876846B635E38901A4FE1002C8BF760A85C3D5FEB5145503C4EA48D39002E8C228C22000A";
		PduContext pdu2 = pp.parsePdu(tmpDeliver);
		System.out.println(pdu2.toString());
		pdu.setDataBytes(tmpDeliver.getBytes());
		System.out.println(pdu.toString());
	}

}
