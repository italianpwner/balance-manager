package util;

import java.io.FileInputStream;
import java.util.Properties;

public class AppProperties {
	
	private static Logger logger =
			Logger.getInstance();
	
	private static AppProperties instance;
	private static Properties properties;
	
	private AppProperties() {}
	public static AppProperties getInstance() {
		logger.trace("AppProperties >> getInstance");
		
		if(instance == null)
			instance = new AppProperties();
		return instance;
	}
	
	public Properties getProperties() {
		logger.trace("AppProperties >> getProperties");
		if(properties == null)
			properties = _loadProperties();
		return properties;
	}
	
	private Properties _loadProperties() {
		logger.trace("AppProperties >> _loadProperties");
		
		String rootPath = Thread.currentThread()
				.getContextClassLoader()
				.getResource("").getPath();
		String filePath = rootPath + "../files/app.properties";		// TODO REMOVE THIS BEFORE PUBLISHING
		
		properties = new Properties();
		try {
			properties.load(new FileInputStream(filePath));
			logger.debug("Properties loaded from '"+filePath+"'.");
		} catch(Exception e) {
			logger.fatal("Failed fo load properties from '"+filePath+"'.", e);
		}
		
		return properties;
	}
}
