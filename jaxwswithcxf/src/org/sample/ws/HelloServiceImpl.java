package org.sample.ws;

import java.io.IOException;

import javax.jws.WebMethod;
import javax.jws.WebService;

import javax.jws.*;
import javax.jws.soap.*;
import javax.jws.soap.SOAPBinding.*;

import org.kobjects.base64.Base64;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.ksoap2.transport.Transport;
import org.xmlpull.v1.XmlPullParserException;


@WebService(targetNamespace = "http://ws.sample.org/", wsdlLocation="http://localhost:8080/jaxwswithcxf/services/HelloServiceImplPort?wsdl", portName = "HelloServiceImplPort", serviceName = "HelloServiceImplService")
@WebService(targetNamespace = "http://ws.sample.org/",
portName = "HelloServiceImplPort",
serviceName = "HelloServiceImplService",
endpointInterface = "org.sample.ws.HelloServiceImpl",
wsdlLocation = "WebContent/wsdl/helloserviceimpl.wsdl")
wsdlLocation = "http://ws.sample.org/HelloServiceImplService")
@SOAPBinding(style=Style.RPC, use=Use.LITERAL)
public class HelloServiceImpl {

	private final int soapVersion = SoapEnvelope.VER11;
	private static final int SERVICE = 1;
	private static final int COUNTRY = 1;
	private static final String WEBAPIKEY = "f54e1dab";
    private final String endpointUri = "https://webapi.allegro.pl/service.php";
    private final String namespace = "https://webapi.allegro.pl/service.php";
	
	@WebMethod(action = "getVersion")
	public String getVersion() {
		return "1.0";
	}

	@WebMethod(action = "hello")
	public String hello(String user) {
		return "Hello " + user + "!";
	}
	
	@WebMethod(action = "doQuerySysStatus")
	public long doQuerySysStatus() {
		PropertyInfo sysvar = createPropertyInfo("sysvar", SERVICE);
        PropertyInfo countryId = createPropertyInfo("countryId", COUNTRY);
        PropertyInfo webapiKey = createPropertyInfo("webapiKey", WEBAPIKEY);
 
        SoapObject response = null;
		try {
			response = this.sendSoap("DoQuerySysStatusRequest", "#doQuerySysStatus", sysvar, countryId, webapiKey);
		} catch (SoapFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return Long.parseLong(response.getPropertyAsString("verKey"));
	}
	
	protected SoapObject sendSoap(String methodName, String soapAction, PropertyInfo... properties) throws SoapFault {
        SoapObject request = new SoapObject(this.namespace, methodName);
        for (PropertyInfo property : properties) {
            request.addPropertyIfValue(property);
        }
 
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(this.soapVersion);
        envelope.implicitTypes = true; // removes i:type attribute for SimpleTypes
        envelope.setAddAdornments(false); // removes id & c:root attributes
        envelope.setOutputSoapObject(request);
 
        Transport transport = new HttpTransportSE(this.endpointUri);
        try {
            transport.call(soapAction, envelope);
        } catch (XmlPullParserException | IOException e) {
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
 
        if (envelope.bodyIn instanceof SoapFault) {
            throw (SoapFault) envelope.bodyIn;
        }
 
        return (SoapObject) envelope.bodyIn;
    }
	
	
	protected PropertyInfo createPropertyInfo(String key, Object value) {
        PropertyInfo property = new PropertyInfo();
        property.setName(key);
        property.setValue(value);
        property.setNamespace(this.namespace);
        return property;
    }
}