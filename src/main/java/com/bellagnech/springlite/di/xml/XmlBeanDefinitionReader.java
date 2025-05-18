package com.bellagnech.springlite.di.xml;

import com.bellagnech.springlite.di.BeanDefinition;
import com.bellagnech.springlite.di.BeanDefinitionRegistry;
import com.bellagnech.springlite.di.PropertyValue;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Bean definition reader for XML bean definitions.
 * Reads XML bean configuration using DOM and registers bean definitions with the registry.
 */
public class XmlBeanDefinitionReader {
    
    private final BeanDefinitionRegistry registry;
    
    public XmlBeanDefinitionReader(BeanDefinitionRegistry registry) {
        this.registry = registry;
    }
    
    /**
     * Load bean definitions from the given XML file path.
     * 
     * @param xmlFilePath the XML file path
     * @throws Exception if loading or parsing fails
     */
    public void loadBeanDefinitions(String xmlFilePath) throws Exception {
        File file = new File(xmlFilePath);
        try (InputStream inputStream = new FileInputStream(file)) {
            loadBeanDefinitions(inputStream);
        } catch (IOException e) {
            throw new Exception("Failed to load XML bean definitions from " + xmlFilePath, e);
        }
    }
    
    /**
     * Load bean definitions from the given input stream.
     * 
     * @param inputStream the XML input stream
     * @throws Exception if loading or parsing fails
     */
    public void loadBeanDefinitions(InputStream inputStream) throws Exception {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();
            
            processBeanDefinitions(document);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new Exception("Error parsing XML bean definitions", e);
        }
    }
    
    /**
     * Process bean definitions from the parsed XML document.
     * 
     * @param document the parsed XML document
     */
    private void processBeanDefinitions(Document document) {
        NodeList beanNodes = document.getElementsByTagName("bean");
        
        for (int i = 0; i < beanNodes.getLength(); i++) {
            Node beanNode = beanNodes.item(i);
            
            if (beanNode.getNodeType() == Node.ELEMENT_NODE) {
                Element beanElement = (Element) beanNode;
                
                String id = beanElement.getAttribute("id");
                String className = beanElement.getAttribute("class");
                String scope = beanElement.getAttribute("scope");
                
                BeanDefinition beanDefinition = new BeanDefinition();
                beanDefinition.setId(id);
                beanDefinition.setClassName(className);
                if (scope != null && !scope.isEmpty()) {
                    beanDefinition.setScope(scope);
                }
                
                // Process property elements
                NodeList propertyNodes = beanElement.getElementsByTagName("property");
                for (int j = 0; j < propertyNodes.getLength(); j++) {
                    Node propertyNode = propertyNodes.item(j);
                    
                    if (propertyNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element propertyElement = (Element) propertyNode;
                        
                        String name = propertyElement.getAttribute("name");
                        String value = propertyElement.getAttribute("value");
                        String ref = propertyElement.getAttribute("ref");
                        
                        if (ref != null && !ref.isEmpty()) {
                            // This is a reference to another bean
                            beanDefinition.addPropertyValue(new PropertyValue(name, ref, true));
                        } else if (value != null && !value.isEmpty()) {
                            // This is a value
                            beanDefinition.addPropertyValue(new PropertyValue(name, value));
                        }
                    }
                }
                
                registry.registerBeanDefinition(beanDefinition);
            }
        }
    }
}
