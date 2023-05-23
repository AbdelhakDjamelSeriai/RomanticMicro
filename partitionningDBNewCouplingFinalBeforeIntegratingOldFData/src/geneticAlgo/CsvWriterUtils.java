package geneticAlgo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVWriter;

public class CsvWriterUtils {

	 public static <T> void exportDataToExcel(String fileName, T[][] data) throws FileNotFoundException, IOException
	    {
	        File file = new File(fileName);
	        if (!file.isFile())
	            file.createNewFile();

	        CSVWriter csvWriter = new CSVWriter(new FileWriter(file, true));

	        int rowCount = data.length;
	        String[] content = new String[rowCount];
	        for (int i = 0; i < rowCount; i++)
	        {
	        	content[i] = data[i][i] + "";
	        }
	        csvWriter.writeNext(content);

	        csvWriter.flush();
	        csvWriter.close();
	    }
	 
	 public static void exportDataToExcel(String fileName, int[][] data) throws FileNotFoundException, IOException
	    {
	        File file = new File(fileName);
	        if (!file.isFile())
	            file.createNewFile();

	        CSVWriter csvWriter = new CSVWriter(new FileWriter(file, true));

	        int rowCount = data.length;
	        List<String []> values = new ArrayList<String[]>();
	        String[] content = new String[rowCount];
	        String[] iter = new String[rowCount];
	        for (int i = 0; i < rowCount; i++)
	        {
	        	content[i] = data[i][i] + "";
	        	iter[i] = i+"";
	        }
	        values.add(iter); values.add(content);
	        csvWriter.writeAll(values);
	        
	        csvWriter.flush();
	        csvWriter.close();
	    }

}
