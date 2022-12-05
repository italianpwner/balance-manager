package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.List;

public class FileUtils {
	
	static Logger logger = Logger.getInstance();
	
	public static List<String> read(String fileName) {
		logger.trace("FileUtils >> read");
		
		BufferedReader reader = null;
		List<String> list = new LinkedList<String>();
		
		try {
			FileReader fr = new FileReader(fileName);
			reader = new BufferedReader(fr);
			
			String line = reader.readLine();
			while(line != null) {
				logger.data(line);
				list.add(line);
				line = reader.readLine();
			}
			
		} catch(Exception e) {
			logger.fatal(e.getMessage());
		} finally {
			try {
				reader.close();
			} catch(Exception e) {
				logger.fatal(e.getMessage());
			}
		}
		
		return list;
	}
	
	public static void write(String fileName, List<String> data) {
		logger.trace("FileUtils >> write");
		
		FileWriter fw = null;
		BufferedWriter bw = null;
		
		try {
			File file = new File(fileName);
			fw = new FileWriter(file, true);
			bw = new BufferedWriter(fw);
			
			for(String s: data) {
				bw.append(s);
				bw.newLine();
			}
			
		} catch(Exception e) {
			logger.fatal(e.getMessage());
		} finally {
			try {
				bw.close();
				fw.close();
			} catch(Exception e) {
				logger.fatal(e.getMessage());
			}
		}
	}
	
	public static void deleteContents(String fileName) {
		logger.trace("FileUtils >> deleteContents");
		
		FileWriter fw = null;
		BufferedWriter bw = null;
		
		try {
			File file = new File(fileName);
			fw = new FileWriter(file, false);
			bw = new BufferedWriter(fw);
			bw.write("");
			
		} catch(Exception e) {
			logger.fatal(e.getMessage());
		} finally {
			try {
				bw.close();
				fw.close();
			} catch(Exception e) {
				logger.fatal(e.getMessage());
			}
		}
	}

}
