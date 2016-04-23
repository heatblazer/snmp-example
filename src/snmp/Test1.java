package snmp;


import java.io.IOException;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.*;
import org.snmp4j.TransportMapping;

import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;


public class Test1 {

	Snmp snmp = null;
	String address = null;
	
	public Test1(String add)
	{
		address = add;
	}
	
	public static void main(String args[])
	{
		Test1 client = new Test1("udp:127.0.0.1/161");
		try {
			client.start();
		
			String sysDesc = client.getAsString(new OID(".1.3.6.1.2.1.1.1.0"));
			System.out.println(sysDesc);
		} catch (Exception ex) {
			
		} 
		
	}
	
	private void start() throws IOException {
		TransportMapping transport = new DefaultUdpTransportMapping();
		snmp = new Snmp(transport);
		transport.listen();
	}
	
	
	public String getAsString(OID oid) throws IOException 
	{
		
		ResponseEvent evt = get(new OID[] { oid });
		StringBuilder strb = new StringBuilder();
		strb.append(new String("<html><h1>"));
		strb.append(evt.getResponse().get(0).getVariable().toString());
		strb.append(new String("</h1></html>"));
		return strb.toString();
	
	}
	
	public ResponseEvent get(OID oids[]) throws IOException 
	{
		PDU pdu = new PDU();
		for (OID oid : oids ) {
			pdu.add(new VariableBinding(oid));
		}
		pdu.setType(PDU.GET);
		
		ResponseEvent evn = snmp.send(pdu,  getTarget(), null);
		if ( evn != null ) {
			return evn;
		} else {
			throw new RuntimeException("GET timed out");
		}
	}
	
	private Target getTarget() 
	{
		Address targetAddress = GenericAddress.parse(address);
		CommunityTarget target = new CommunityTarget();
		
		target.setCommunity(new OctetString("public"));
		target.setAddress(targetAddress);
		target.setRetries(2);
		target.setTimeout(1500);
		target.setVersion(SnmpConstants.version2c);
		return target;	
	}
}//end Test1
