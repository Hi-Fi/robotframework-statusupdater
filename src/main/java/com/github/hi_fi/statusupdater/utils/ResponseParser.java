package com.github.hi_fi.statusupdater.utils;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.github.hi_fi.statusupdater.qc.infrastructure.Entities;
import com.github.hi_fi.statusupdater.qc.infrastructure.EntityMarshallingUtils;
import com.github.hi_fi.statusupdater.qc.infrastructure.Entities.Entity;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class ResponseParser {

	public static List<Entity> parseResponseToEntities(HttpResponse response) {
		String responseString = parseResponseToString(response);
		List<Entity> entityList = new ArrayList<Entity>();
		try {
			entityList = EntityMarshallingUtils.marshal(Entities.class, responseString).getEntity();
		} catch (JAXBException e) {
			throw new RuntimeException(String.format("Parsing error when trying to parse %s. \nError message was %s.",
					responseString, e.getMessage()));
		}
		return entityList;
	}
	
	public static JsonElement parseResponseToJson(HttpResponse response) {
		String responseString = parseResponseToString(response);
		try {
			return new JsonParser().parse(responseString);
		} catch (JsonSyntaxException e) {
			throw new RuntimeException(String.format("Parsing error when trying to parse %s. \nError message was %s.",
					responseString, e.getMessage()));
		}
	}

	public static Document parseResponseToXML(HttpResponse response) {
		String responseString = parseResponseToString(response);
		try {
			return DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.parse(new InputSource(new StringReader(responseString)));
		} catch (SAXException e) {
			throw new RuntimeException(String.format("Parsing error when trying to parse %s. \nError message was %s.",
					responseString, e.getMessage()));
		} catch (IOException e) {
			throw new RuntimeException(String.format("Parsing error when trying to parse %s. \nError message was %s.",
					responseString, e.getMessage()));
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(String.format("Parsing error when trying to parse %s. \nError message was %s.",
					responseString, e.getMessage()));
		}
	}

	public static String parseResponseToString(HttpResponse response) {
		try {
			return EntityUtils.toString(response.getEntity());
		} catch (ParseException e) {
			throw new RuntimeException(String.format("Error in response parsing. Error message: %s", e.getMessage()));
		} catch (IOException e) {
			throw new RuntimeException(
					String.format("IO Error in response parsing. Error message: %s", e.getMessage()));
		}
	}
	
   public static JsonElement parseStringToJson(String responseString) {
        try {
            return new JsonParser().parse(responseString);
        } catch (JsonSyntaxException e) {
            throw new RuntimeException(String.format("Parsing error when trying to parse %s. \nError message was %s.",
                    responseString, e.getMessage()));
        }
    }

}
