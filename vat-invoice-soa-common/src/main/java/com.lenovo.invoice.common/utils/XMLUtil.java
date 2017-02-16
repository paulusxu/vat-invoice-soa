package com.lenovo.invoice.common.utils;


import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by bob on 2015/6/13.
 */
public class XMLUtil {
    public static Map<String, String> xmlElements(String xmlDoc) {
        Logger logger = Logger.getLogger(XMLUtil.class);
        String btcpCode = "";
        Map<String, String> map = new HashMap<String, String>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder
                    .parse(new InputSource(new StringReader(xmlDoc)));

            Element root = doc.getDocumentElement();
            NodeList books = root.getChildNodes();
            if (books != null) {
                Node book = books.item(0);
                String values = book.getFirstChild().getNodeValue();
                map.put("code", values);
                if (values.equals("200")) {
                    book = books.item(1);

                    /*btcpCode = book.getFirstChild().getNodeValue();// 获取BTCP返回的BTCPCode*/
                    map.put("ShipmentsNo", book.getFirstChild().getNodeValue());


                } else {
                    book = books.item(2);
                    map.put("Message", book.getFirstChild().getNodeValue());
                    map.put("ShipmentsNo", "");
                }
            }
        } catch (Exception e) {
            logger.error("============程序错误" + e.getMessage());
        }
        return map;
    }

    public static Map<String, String> parseXml(String xmlDoc) {
        Logger logger = Logger.getLogger(XMLUtil.class);
        Map<String, String> map = new HashMap<String, String>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder
                    .parse(new InputSource(new StringReader(xmlDoc)));

            Element root = doc.getDocumentElement();
            NodeList books = root.getChildNodes();
            if (books != null) {
                for (int i = 0; i < books.getLength(); i++) {
                    Node book = books.item(i);
                    if (book.getFirstChild() != null) {
                        map.put(book.getNodeName(), book.getFirstChild().getNodeValue());
                    }
                }
            }
        } catch (Exception e) {
            logger.error("============程序错误" + e.getMessage());
        }
        return map;
    }

    public static Map<String, String> parseREXml(String xmlDoc) {
        Logger logger = Logger.getLogger(XMLUtil.class);
        Map<String, String> map = new HashMap<String, String>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder
                    .parse(new InputSource(new StringReader(xmlDoc)));

            Element root = doc.getDocumentElement();
            NodeList books = root.getChildNodes();
            if (books != null) {
                for (int i = 0; i < books.getLength(); i++) {
                    Node book = books.item(i);
                    if (book.getNodeName().equals("Items")) {
                        NodeList items = book.getChildNodes();
                        for (int k = 0; k < items.getLength(); k++) {
                            System.out.print(items.item(k).getNodeName());
                        }
                    }
                }

            }
        } catch (Exception e) {
            logger.error("============程序错误" + e.getMessage());
        }
        return map;
    }

    /**
     * 将对象直接转换成String类型�? XML输出
     *
     * @param obj
     * @return
     */
    public static String convertToXml(Object obj) {
        // 创建输出�?
        StringWriter sw = new StringWriter();
        try {
            // 利用jdk中自带的转换类实�?
            JAXBContext context = JAXBContext.newInstance(obj.getClass());

            Marshaller marshaller = context.createMarshaller();
            // 格式化xml输出的格�?
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
                    Boolean.FALSE);
            // 将对象转换成输出流形式的xml
            marshaller.marshal(obj, sw);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return sw.toString();
    }

    /**
     * 将对象根据路径转换成xml文件
     *
     * @param obj
     * @param path
     * @return
     */
    public static void convertToXml(Object obj, String path) {
        try {
            // 利用jdk中自带的转换类实�?
            JAXBContext context = JAXBContext.newInstance(obj.getClass());

            Marshaller marshaller = context.createMarshaller();
            // 格式化xml输出的格�?
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
                    Boolean.FALSE);
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.FALSE);
            // 将对象转换成输出流形式的xml
            // 创建输出�?
            FileWriter fw = null;
            try {
                fw = new FileWriter(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
            marshaller.marshal(obj, fw);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    /**
     * 将String类型的xml转换成对�?
     */
    public static <T> T convertXmlStrToObject(Class<T> clazz, String xmlStr) {
        Object xmlObject = null;
        try {
            JAXBContext context = JAXBContext.newInstance(clazz);
            // 进行将Xml转成对象的核心接�?
            Unmarshaller unmarshaller = context.createUnmarshaller();
            StringReader sr = new StringReader(xmlStr);
            xmlObject = unmarshaller.unmarshal(sr);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return (T) xmlObject;
    }

    @SuppressWarnings("unchecked")
    /**
     * 将file类型的xml转换成对�?
     */
    public static <T> T convertXmlFileToObject(Class<T> clazz, String xmlPath) {
        Object xmlObject = null;
        try {
            JAXBContext context = JAXBContext.newInstance(clazz);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            FileReader fr = null;
            try {
                fr = new FileReader(xmlPath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            xmlObject = unmarshaller.unmarshal(fr);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return (T) xmlObject;
    }

    public static void main(String[] args) {
//        Invoice invoice = new Invoice();
//        invoice.setCustomerName("myname");
//        invoice.setTaxNo("2312412");
//        System.out.println(XMLUtil.convertToXml(invoice));
//        String xml="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><Order><Code>405</Code><Message>xml参数为空</Message><ShipmentsNo></ShipmentsNo></Order>";
//        BtcpResult result=XMLUtil.convertXmlStrToObject(BtcpResult.class, xml);
//        REOrder reOrder=new REOrder();
//        ZREOrder zsdOrder=new ZREOrder();
//        zsdOrder.setCustMail("11");
//
//        System.out.print(convertToXml(zsdOrder));
//        BtcpResult result=new BtcpResult();
//        result.setCode("501");
//        result.setShipmentsNo("");
//        result.setMessage("dfsd");
//        System.out.print(convertToXml(result));
    }

}
