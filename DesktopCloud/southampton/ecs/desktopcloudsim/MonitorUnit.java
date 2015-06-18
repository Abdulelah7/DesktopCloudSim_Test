package southampton.ecs.desktopcloudsim;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.Cloudlet;


public class MonitorUnit {
	private static double simulationTime;
	private static double cloudletAvgExecTime;
	private static double cloudletMicroAvgExecTime;
	
	private static double availability;
	private static double utilisation;

	private static ArrayList<Cloudlet> finishedCloudletList;
	private static ArrayList<Vm> vmMigratedlist;
	private static ArrayList<Vm> vmDestroyedlist;
	
	private static int machineFailureCounter;
	private static int idleMachineNumber;

	public final static double MONITORING_INTERVAL = (int) Constants.SIMULATION_LIMIT / 24; 	
	
	private static String outputFileName;

	public static void initialize()
	{		
		simulationTime = 0;
		cloudletAvgExecTime = 0;
		cloudletMicroAvgExecTime = 0;
		
		availability = 0;
		utilisation = 0;
		
		finishedCloudletList = new ArrayList<Cloudlet>();
		vmMigratedlist = new ArrayList<Vm>();
		vmDestroyedlist = new ArrayList<Vm>();
		
		machineFailureCounter = 0;
		idleMachineNumber = 0;
		
		outputFileName = Constants.inputFolderPath + Constants.outputFileName + ".txt";
		
		writeHeaders();
	}
	
	public static void setSimulationTime(double time)
	{
		simulationTime = time;
	}
	
	public static double getSimulationTime()
	{
		return simulationTime;
	}
	
	public static void setCloudletAvgExecTime(double time)
	{
		cloudletAvgExecTime = time;
	}
	
	public static double getCloudletAvgExecTime()
	{
		return cloudletAvgExecTime;
	}
	
	public static void setCloudletMicroAvgExecTime(double time)
	{
		cloudletMicroAvgExecTime = time;
	}
	
	public static double getCloudletMicroAvgExecTime()
	{
		return cloudletMicroAvgExecTime;
	}
	
	public static void setFinishedCloudletList(ArrayList<Cloudlet> cloudletList)
	{
		finishedCloudletList = cloudletList;
	}
	
	public static void addCloudlet(Cloudlet cloudlet)
	{
		finishedCloudletList.add(cloudlet);
	}
	
	public static ArrayList<Cloudlet> getFinishedCloudletList()
	{
		return finishedCloudletList;
	}
	
	public static void setAavailability(double aval)
	{
		availability = aval;
	}
	
	public static double getAvailability()
	{
		return availability;
	}
	
	public static void setUtilisation(double util)
	{
		utilisation = util;
	}
	
	public static double getUtilisation()
	{
		return utilisation;
	}
	
	public static void addMigratedVm(Vm vm)
	{
		vmMigratedlist.add(vm);
	}
	
	public static ArrayList<Vm> getVmMigratedlist()
	{
		return vmMigratedlist;
	}
	
	public static int getNumberOfMigratedVm()
	{
		return vmMigratedlist.size();
	}

	public static void addDestroyedVm(Vm vm)
	{
		vmDestroyedlist.add(vm);
	}
	
	public static void addMachineFailure()
	{
		machineFailureCounter++;
	}
	
	public static void removeMachineFailure()
	{
		machineFailureCounter--;
	}
	
	public static int getMachineFailure()
	{
		return machineFailureCounter;
	}
	
	public static void setIdleMachineNumber(int numebrOfMachines)
	{
		idleMachineNumber = numebrOfMachines;
	}
	
	
	public static Datacenter setMonitoringEvents(Datacenter datacenter)
	{
		initialize();
		datacenter.setMonitoringEvents(Constants.SIMULATION_LIMIT);
		return datacenter;
	}
	
	/**
	 * writes a header row for monitor outputfile
	 */
	private static void writeHeaders()
	{
		String fileTxt =  "time\t\t" + "availability(%)\t\t" + "utilisation(%)\t\t" + "#_idle_machines\t\t" + "#_migrated_VMs\t\t"
				+ "#_destroyed_VMs\t\t" + "#_machines_failures\t\t" + "avg_exec_time\t\t"
				+ "MICRO_avg_exec_time\t" + "#_executed_cloudlet\t" + "#_success_cloudlet\t" + "#_failed_cloudlet\t";
		
		//System.out.println(fileTxt);
		writeTxtfile(fileTxt, false);			//false means no append writing 
	}
	
	public static void writeEvent()
	{
		String indent = "\t\t";
		String fileTxt =  simulationTime/MonitorUnit.MONITORING_INTERVAL + indent + indent + availability + indent + indent + utilisation + indent + indent + idleMachineNumber 
				+ indent + indent + indent + vmMigratedlist.size() + indent + indent + vmDestroyedlist.size() + indent +  indent + indent + machineFailureCounter 
				+ indent + indent + indent + cloudletAvgExecTime + indent + indent + cloudletMicroAvgExecTime + indent + indent 
				+ finishedCloudletList.size() + indent + indent + (finishedCloudletList.size() - Calculation.calculateCloudletFailedCounter(finishedCloudletList)) 
				+ indent + indent + Calculation.calculateCloudletFailedCounter(finishedCloudletList);
		
		//System.out.println(fileTxt);
		writeTxtfile(fileTxt, true);		
	}
	
	private static void writeTxtfile(String txt, boolean append)
	{
		BufferedWriter writer = null;
		try
		{
		    writer = new BufferedWriter( new FileWriter( outputFileName, append));
		    writer.write(txt);		
		    writer.newLine();
		}
		catch ( IOException e)
		{
		}
		finally
		{
		    try
		    {
		        if ( writer != null)
		        writer.close( );
		    }
		    catch ( IOException e)
		    {
		    }
		}
	}
}
