package util;

import java.io.FileInputStream;

public class Properties {
	
	static Logger logger = Logger.getInstance();
	
	private static Properties instance;
	private static java.util.Properties properties;
	
	private Properties() {}
	public static Properties getInstance() {
		logger.trace("Properties >> getInstance");
		
		if(instance == null)
			instance = new Properties();
		return instance;
	}
	
	public java.util.Properties getProperties() {
		logger.trace("Properties >> getProperties");
		if(properties == null)
			properties = loadProperties();
		return properties;
	}
	
	private java.util.Properties loadProperties() {
		logger.trace("Properties >> loadProperties");
		
		String rootPath = Thread.currentThread()
				.getContextClassLoader()
				.getResource("").getPath();
		String filePath = rootPath + "../files/app.properties";		// TODO REMOVE THIS BEFORE PUBLISHING
		
		logger.info("Loading properties from '"+filePath+"'");
		
		properties = new java.util.Properties();
		try {
			properties.load(new FileInputStream(filePath));
			logger.info("Properties loaded.");
		} catch(Exception e) {
			logger.fatal(e.getMessage());
		}
		
		return properties;
	}
}
