package uk.gov.pay.connector.util;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;
import java.io.ByteArrayInputStream;

import static javax.xml.XMLConstants.FEATURE_SECURE_PROCESSING;

public class JsonParse {

    public static <T> T parseToObject(String payload, Class<T> clazz) throws Exception {
        try {
            Gson gson = new Gson();
            return gson.fromJson(payload, clazz);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

}
