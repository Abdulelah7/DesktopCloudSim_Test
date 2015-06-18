package southampton.ecs.desktopcloudsim;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ReadTraceData {
	ArrayList<Node> hostList;

	public ReadTraceData()
	{
		hostList = new ArrayList<Node>();
	}
	
	/**
	 * reads traces in the trace file
	 * @param fileName
	 * @return returns a host list of nodes
	 */
	public ArrayList<Node> getHostList(String fileName)
	{
		try
		{
			hostList = processHostList(fileName);
		} catch (final FileNotFoundException e) {
		} catch (final IOException e) {
		}
		return hostList;
	}
	private ArrayList<Node> processHostList(String fileName) throws IOException, FileNotFoundException 
	{
		ArrayList<Node> tmpHostList = new ArrayList<Node>();
		File file = new File(fileName);
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			
			reader.readLine(); 		//jump first line cos it is a header row
			while (reader.ready()) 
			{
				String[] tokens = reader.readLine().split("\\s+");
				
				Node node = new Node();
				node.setNodeId(Integer.parseInt(tokens[0]));
				node.setCpu(Integer.parseInt(tokens[1]));
				node.setRam(Integer.parseInt(tokens[2]));
				node.setStorage(Integer.parseInt(tokens[3]));
				node.setBw(Integer.parseInt(tokens[4]));
				node.setCores(Integer.parseInt(tokens[5]));
				
				tmpHostList.add(node);

			}
	
			reader.close();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		return tmpHostList;
	}
}
