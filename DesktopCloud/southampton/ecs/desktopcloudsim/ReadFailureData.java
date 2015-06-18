package southampton.ecs.desktopcloudsim;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ReadFailureData {
	ArrayList<FailureEvent> failureEventArray;
	
	public ReadFailureData()
	{
		failureEventArray = new ArrayList<FailureEvent>();
	}
	
	public ArrayList<FailureEvent> getFailureEventArray(String fileName)
	{
		try
		{
			failureEventArray = processFailureEventArray(fileName);
		} catch (final FileNotFoundException e) {
		} catch (final IOException e) {
		}
		return failureEventArray;
	}
	private ArrayList<FailureEvent> processFailureEventArray(String fileName) throws IOException, FileNotFoundException 
	{
		ArrayList<FailureEvent> tmpFailureEventArray = new ArrayList<FailureEvent>();
		File file = new File(fileName);
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

			reader.readLine();			//jump the first line cos it is a heading row 
			while (reader.ready()) 
			{
				String[] tokens = reader.readLine().split("\\s+");
				
				FailureEvent failureEvent = new FailureEvent();
				failureEvent.setNodeId(Integer.parseInt(tokens[0]));
				failureEvent.setStartTime(Integer.parseInt(tokens[1]));
				failureEvent.setFinishTime(Integer.parseInt(tokens[2]));
				
				tmpFailureEventArray.add(failureEvent);

			}
	
			reader.close();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		return tmpFailureEventArray;
	}
}
