package com.bellagnech.springlite.di.xml;

import com.bellagnech.springlite.di.BeanDefinition;
import com.bellagnech.springlite.di.BeanDefinitionRegistry;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Bean definition reader for XML bean definitions.
 * Reads XML bean configuration using JAXB and registers bean definitions with the registry.
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
            JAXBContext jaxbContext = JAXBContext.newInstance(BeansDefinition.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            BeansDefinition beansDefinition = (BeansDefinition) jaxbUnmarshaller.unmarshal(inputStream);
            
            processBeanDefinitions(beansDefinition);
        } catch (JAXBException e) {
            throw new Exception("Error parsing XML bean definitions", e);
        }
    }
    
    /**
     * Process bean definitions from the parsed XML.
     * 
     * @param beansDefinition the parsed beans definition object
     */
    private void processBeanDefinitions(BeansDefinition beansDefinition) {
        List<BeanElement> beanElements = beansDefinition.getBeans();
        
        for (BeanElement beanElement : beanElements) {
            BeanDefinition beanDefinition = new BeanDefinition();
            beanDefinition.setId(beanElement.getId());
            beanDefinition.setClassName(beanElement.getClassName());
            beanDefinition.setScope(beanElement.getScope());
            
            // Additional metadata can be stored for properties
            // This will be used in the next chunk for dependency injection
            
            registry.registerBeanDefinition(beanDefinition);
        }
    }
}
