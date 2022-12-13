package util;

import java.io.FileInputStream;
import java.util.Properties;

public class AppProperties {
	
	private static AppProperties instance;
	private static Properties properties;
	
	private AppProperties() {}
	public static AppProperties getInstance() {
		Logger.trace("AppProperties >> getInstance");
		
		if(instance == null)
			instance = new AppProperties();
		return instance;
	}
	
	public Properties getProperties() {
		Logger.trace("AppProperties >> getProperties");
		if(properties == null)
			properties = _loadProperties();
		return properties;
	}
	
	private Properties _loadProperties() {
		Logger.trace("AppProperties >> _loadProperties");
		
		String rootPath = Thread.currentThread()
				.getContextClassLoader()
				.getResource("").getPath();
		String filePath = rootPath + "../files/app.properties";		// TODO REMOVE THIS BEFORE PUBLISHING
		
		properties = new Properties();
		try {
			properties.load(new FileInputStream(filePath));
			Logger.debug("Properties loaded from '"+filePath+"'.");
		} catch(Exception e) {
			Logger.fatal("Failed fo load properties from '"+filePath+"'.", e);
		}
		
		return properties;
	}
}
