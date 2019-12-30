package vdab.extnodes.communication;
import com.lcrc.af.datatypes.AFEnum;
public class CommunicationServiceProvider {

	public static final int NEXMO = 1;
	public static final int TWILIO = 2;

	private static AFEnum s_CommunicationServiceProvider = new AFEnum("CommunicationServiceProvider")
	.addEntry(NEXMO, "Nexmo")
	.addEntry(TWILIO, "Twilio")
	;
	public static AFEnum getEnum(){
		return s_CommunicationServiceProvider ;
	}
}
