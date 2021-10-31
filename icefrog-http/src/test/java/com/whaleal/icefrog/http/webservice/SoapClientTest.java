package com.whaleal.icefrog.http.webservice;

import com.whaleal.icefrog.core.lang.Console;
import com.whaleal.icefrog.core.util.CharsetUtil;
import org.junit.Ignore;
import org.junit.Test;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

/**
 * SOAP相关单元测试
 *
 * @author Looly
 * @author wh
 */
public class SoapClientTest {

    @Test
    @Ignore
    public void requestTest() {
        SoapClient client = SoapClient.create("http://www.webxml.com.cn/WebServices/IpAddressSearchWebService.asmx")
                .setMethod("web:getCountryCityByIp", "http://WebXml.com.cn/")
                .setCharset(CharsetUtil.CHARSET_GBK)
                .setParam("theIpAddress", "218.21.240.106");

        Console.log(client.getMsgStr(true));

        Console.log(client.send(true));
    }

    @Test
    @Ignore
    public void requestForMessageTest() throws SOAPException {
        SoapClient client = SoapClient.create("http://www.webxml.com.cn/WebServices/IpAddressSearchWebService.asmx")
                .setMethod("web:getCountryCityByIp", "http://WebXml.com.cn/")
                .setParam("theIpAddress", "218.21.240.106");

        SOAPMessage message = client.sendForMessage();
        Console.log(message.getSOAPBody().getTextContent());
    }
}
