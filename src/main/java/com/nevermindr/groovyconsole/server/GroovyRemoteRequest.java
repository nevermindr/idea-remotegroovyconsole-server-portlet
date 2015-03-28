package com.nevermindr.groovyconsole.server;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;

class GroovyRemoteRequest {
    public String fullScript;
    @SuppressWarnings("unused")
    public String selectedScript;

    public String returnSeparator;
    public String errorSeparator;

    public void parseInputData(InputStream inputStream) throws IOException, ParserConfigurationException, SAXException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String read = br.readLine();
        while (read != null) {
            sb.append(read);
            if (read.contentEquals("</execute>"))
                break;
            read = br.readLine();
        }

        InputSource is = new InputSource(new StringReader(sb.toString()));
        Document xmlDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);

        NodeList nList = xmlDoc.getElementsByTagName("fullScript");
        this.fullScript = nList.item(0).getTextContent();

        nList = xmlDoc.getElementsByTagName("selectedScript");
        this.selectedScript = nList.item(0).getTextContent();

        this.returnSeparator = xmlDoc.getElementsByTagName("returnSeparator").item(0).getTextContent();
        this.errorSeparator = xmlDoc.getElementsByTagName("errorSeparator").item(0).getTextContent();
    }
}
