package vdab.extnodes.communication;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

import com.extsrc.Base64;
import com.lcrc.af.AnalysisCompoundData;
import com.lcrc.af.AnalysisData;
import com.lcrc.af.AnalysisDataDef;
import com.lcrc.af.AnalysisEvent;
import com.lcrc.af.constants.HTTPMethodType;
import com.lcrc.af.constants.SpecialText;
import com.lcrc.af.constants.TimeUnit;
import com.lcrc.af.util.AnalysisDataUtility;
import com.lcrc.af.util.ControlDataBuffer;
import com.lcrc.af.util.IconUtility;
import com.lcrc.af.util.StringUtility;

import vdab.api.node.HTTPService_A;

public class TextingService extends HTTPService_A{
	static {
		CommunicationServiceProvider.getEnum();
	}
	private static String ENDPOINT_NEXMO = "https://rest.nexmo.com/sms/json";
	private static String ENDPOINT_TWILIO= "https://api.twilio.com/2010-04-01/Accounts/";
	private String c_ApiKey;
	private String c_ApiSecret;
	private String c_AccountSID;
	private String c_Token;
	private String c_Text;
	private String c_DaText;
	
	private String c_PhoneNumber;
	private String c_From;
	private Integer c_Provider = Integer.valueOf(CommunicationServiceProvider.NEXMO);
	
	private Boolean c_DropRepeatReports = Boolean.FALSE;
	private AnalysisCompoundData c_CurrentData ;
	private AnalysisEvent c_CurrentEvent;
	private AnalysisEvent c_LastEvent;
	
	public Integer get_IconCode(){
		return  IconUtility.getIconHashCode("node_hobolink");
	}
	public void set_Provider(Integer provider){
		 c_Provider = provider;
	}
	public Integer get_Provider(){
		return c_Provider;
	}
	public void set_ApiKey(String key){
		 c_ApiKey = key;
	}
	public String get_ApiKey(){
		return c_ApiKey;
	}
	public AnalysisDataDef def_ApiKey(AnalysisDataDef theDataDef){	
		if (c_Provider.intValue() != CommunicationServiceProvider.NEXMO)
			theDataDef.disable();
		return theDataDef;
	}
	public void set_ApiSecret(String secret){
		 c_ApiSecret = secret;
	}
	public String get_ApiSecret(){
		return c_ApiSecret;
	}
	public AnalysisDataDef def_ApiSecret(AnalysisDataDef theDataDef){	
		if (c_Provider.intValue() != CommunicationServiceProvider.NEXMO)
			theDataDef.disable();
		return theDataDef;
	}
	public void set_AccountSID(String sid){
		 c_AccountSID = sid;
	}
	public String get_AccountSID(){
		return c_AccountSID;
	}
	public AnalysisDataDef def_AccountSID(AnalysisDataDef theDataDef){	
		if (c_Provider.intValue() != CommunicationServiceProvider.TWILIO)
			theDataDef.disable();
		return theDataDef;
	}
	public void set_Token(String token){
		 c_Token = token;
	}
	public String get_Token(){
		return c_Token;
	}
	public AnalysisDataDef def_Token(AnalysisDataDef theDataDef){	
		if (c_Provider.intValue() != CommunicationServiceProvider.TWILIO)
			theDataDef.disable();
		return theDataDef;
	}
	public void set_Text(String text){
		 c_Text=text;
	}
	public String get_Text(){
		return c_Text;
	}
	public void set_From(String from){
		 c_From= from;
	}
	public String get_From(){
		return c_From;
	}
	public String get_PhoneNumber(){
		return c_PhoneNumber;
	}
	
	public void set_PhoneNumber(String number){
		c_PhoneNumber = number;
	}
	@Override
	public void _init(){
		super._init();
		set_HTTPMethod(Integer.valueOf(HTTPMethodType.POST));
	}
	@Override
	public void _start(){
		super._start();
	}
	@Override
	public String buildCompleteURL(AnalysisEvent ev) {

		StringBuilder sb = new StringBuilder();
		switch (c_Provider.intValue()){
		case CommunicationServiceProvider.NEXMO:
			sb.append(ENDPOINT_NEXMO);
			break;

		case CommunicationServiceProvider.TWILIO:
			sb.append(ENDPOINT_TWILIO);
			sb.append(c_AccountSID);
			sb.append("/Messages.json");
			break;


		default:
			setError("Unsupported Service Provider ");
			_disable();
			return null;

		}

		return sb.toString();
	}	
	@Override
	public String buildPost(AnalysisEvent ev){
		HashMap<String,String> params = new HashMap<String,String>();
		try {
			switch (c_Provider.intValue()){
			case CommunicationServiceProvider.NEXMO:
				params.put("api_key", c_ApiKey);	
				params.put("api_secret", c_ApiSecret);		
				params.put("to", URLEncoder.encode(c_PhoneNumber, "UTF-8"));
				params.put("text", URLEncoder.encode(c_Text, "UTF-8"));
				params.put("from", URLEncoder.encode(c_From, "UTF-8"));
				break;

			case CommunicationServiceProvider.TWILIO:
				params.put("To", URLEncoder.encode(c_PhoneNumber, "UTF-8"));
				params.put("Body", URLEncoder.encode(c_Text, "UTF-8"));
				params.put("From", URLEncoder.encode(c_From, "UTF-8"));
				break;

			}
		}
		catch (Exception e){
			setError("Unable to build post e>"+e);
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (Entry<String, String> param : params.entrySet()) {
            if (sb.length() != 0) 
            	sb.append("&");
            sb.append(param.getKey());
            sb.append('=');
            sb.append(param.getValue());	
        }
		return sb.toString();
	}

	public void setupHTTPConnection(HttpURLConnection con)  {
		switch (c_Provider.intValue()){
		case CommunicationServiceProvider.NEXMO:
			break;

		case CommunicationServiceProvider.TWILIO:
			StringBuilder sb = new StringBuilder();
			sb.append(c_AccountSID);
			sb.append(":");
			sb.append(c_Token);		
			String strAuth = Base64.encodeBytes(sb.toString().getBytes());
			con.setRequestProperty("Authorization", "Basic " + strAuth);
			break;
		}
	}

	/**
	public synchronized void processEvent(AnalysisEvent ev){	
		if (! ev.isTriggerEvent()){	

			HashMap<String,String> tvMap = AnalysisDataUtility.buildTagValueMap(ev.getAnalysisData());

			// Build body with tags if necessary
			c_DaText = getTemplateAttribute( "Text", tvMap);
			if (c_DaText == null)
				c_DaText = c_Text;
		}
		super.processEvent(ev);
	}
	**/


}
