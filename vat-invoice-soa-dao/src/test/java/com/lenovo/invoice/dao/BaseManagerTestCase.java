package com.lenovo.invoice.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.util.MissingResourceException;
import java.util.ResourceBundle;


@ContextConfiguration(locations = {
        "classpath:/spring-config-test.xml",
})


public abstract class BaseManagerTestCase extends AbstractJUnit4SpringContextTests {

    /**
     * A simple logger
     */
    protected final Log log = LogFactory.getLog(getClass());
    /**
     * The resourceBundle
     */
    protected ResourceBundle rb;

    /**
     * Default constructor will set the ResourceBundle if needed.
     */
    public BaseManagerTestCase() {
        // Since a ResourceBundle is not required for each class, just
        // do a simple check to see if one exists
        String className = this.getClass().getName();

        try {
            rb = ResourceBundle.getBundle(className);
        } catch (MissingResourceException mre) {
            // log.warn("No resource bundle found for: " + className);
        }
    }
}
