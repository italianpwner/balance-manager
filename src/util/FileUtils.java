package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.List;

public class FileUtils {
	
	public static List<String> read(String fileName) {
		Logger.trace("FileUtils >> read");
		Logger.debug("Reading from file '"+fileName+"'...");
		
		BufferedReader reader = null;
		List<String> list = new LinkedList<String>();
		
		try {
			FileReader fr = new FileReader(fileName);
			reader = new BufferedReader(fr);
			
			String line = reader.readLine();
			while(line != null) {
				Logger.data(line);
				list.add(line);
				line = reader.readLine();
			}
			
		} catch(Exception e) {
			Logger.fatal("Failed to read from file '"+fileName+"'", e);
		} finally {
			try {
				reader.close();
			} catch(Exception e) {
				Logger.fatal("Failed to close reader", e);
			}
		}
		
		return list;
	}
	
	public static void write(String fileName, List<String> data) {
		Logger.trace("FileUtils >> write");
		Logger.info("Writing to file '"+fileName+"'...");
		
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
			Logger.fatal("Failed to write to file '"+fileName+"'", e);
		} finally {
			try {
				bw.close();
				fw.close();
			} catch(Exception e) {
				Logger.fatal("Failed to close writer", e);
			}
		}
	}
	
	public static void deleteContents(String fileName) {
		Logger.trace("FileUtils >> deleteContents");
		Logger.info("Deleting contents of file '"+fileName+"'...");
		
		FileWriter fw = null;
		BufferedWriter bw = null;
		
		try {
			File file = new File(fileName);
			fw = new FileWriter(file, false);
			bw = new BufferedWriter(fw);
			bw.write("");
			
		} catch(Exception e) {
			Logger.fatal("Failed to write to file '"+fileName+"'", e);
		} finally {
			try {
				bw.close();
				fw.close();
			} catch(Exception e) {
				Logger.fatal("Failed to close writer", e);
			}
		}
	}

}
