package southampton.ecs.desktopcloudsim;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

public class runExperiment {
	private static List<Cloudlet> cloudletList;
	private static List<Vm> vmlist;
	private static boolean injectFailureFlag = true;
	

	public static void main(String[] args) {

		Log.printLine("Running Desktop Cloud Experiment...");
		//Log.disable();
		runSimulation();
		Log.printLine("finish Desktop Cloud Experiment...\n");
		
		/*Log.enable();
		Log.printLine("Running Traditional Cloud Experiment...");
		Log.disable();
		injectFailureFlag = false;
		runSimulation();**/
		
		Log.printLine("finish...");		
	}
	public static void runSimulation()
	{
		try
		{
			int num_user = 1;  
			Calendar calendar = Calendar.getInstance();
			boolean trace_flag = false;  // mean trace events
	
			CloudSim.init(num_user, calendar, trace_flag);
			
			@SuppressWarnings("unused")
			Datacenter datacenter = createDatacenter("Datacenter_0", injectFailureFlag);
					
			DatacenterBroker broker = createBroker();
			int brokerId = broker.getId();
	
			String workloadFile = System.getProperty("user.dir") + "/setupData/planetlab/20110303"; // PlanetLab workload
			cloudletList = createPlanetLabCloudletList(brokerId, workloadFile);
	
			vmlist = createVmList(brokerId);
			
			broker.submitVmList(vmlist);
			broker.submitCloudletList(cloudletList);
	
			//CloudSim.terminateSimulation(Constants.SIMULATION_LIMIT);
			CloudSim.startSimulation();
			
			List<Cloudlet> cloudletSubmittedList = broker.getCloudletSubmittedList();//broker.getCloudletReceivedList(); 
	
			
			CloudSim.stopSimulation();
	
			printDetails(MonitorUnit.getFinishedCloudletList());
			

			//printDetails(MonitorUnit.getSubmittedCloudletList());
			
			
	    	//printCloudletList(cloudletSubmittedList);

		}
		catch (Exception e) 
		{
			e.printStackTrace();
			Log.printLine("The simulation has been terminated due to an unexpected error");
		}
		
	}
	
	private static Host creatHost(int hostId)
	{
		int mips = 6 * 1000 + 900;
		int ram = 36 * 512;; //host memory (MB)
		long storage = 60 * 1000; //host storage
		int bw = 20 * 300;
		List<Pe> peList = new ArrayList<Pe>();
		peList.add(new Pe(0, new PeProvisionerSimple(mips)));
		
		Host host = new Host(hostId, new RamProvisionerSimple(ram), new BwProvisionerSimple(bw), storage, peList, new VmSchedulerTimeShared(peList));
		return host;
	}
	
	public static List<Vm> createVmList(int brokerId) {
		List<Vm> vms = new ArrayList<Vm>();
		int vmType = 2; 
		int bw = 300;		
		for (int i = 0; i < Constants.VM_NUMBER; i++) {
			/* vms.add(new Vm(i, brokerId,	Constants.VM_MIPS[vmType], Constants.VM_PES[vmType],
					Constants.VM_RAM[vmType],bw,Constants.VM_SIZE,"Xen", new CloudletSchedulerSpaceShared())); /**/
			vms.add(new Vm(i, brokerId,	Constants.VM_MIPS[vmType], Constants.VM_PES[vmType],
					Constants.VM_RAM[vmType],bw,Constants.VM_SIZE,"Xen", new CloudletSchedulerTimeShared())); 
		}
		return vms;
	}
	
	public static List<Cloudlet> createPlanetLabCloudletList(int brokerId, String inputFolderName)
			throws FileNotFoundException {
		List<Cloudlet> list = new ArrayList<Cloudlet>();

		long fileSize = 300;
		long outputSize = 300;
		UtilizationModel utilizationModel = new UtilizationModelFull();

		File inputFolder = new File(inputFolderName);
		File[] files = inputFolder.listFiles();
		
	
		//cloudletLength = Constants.CLOUDLET_LENGTH * (int) MonitorUnit.MONITORING_INTERVAL;		
		//System.out.println("MonitorUnit.MONITORING_INTERVAL : " + MonitorUnit.MONITORING_INTERVAL);
		//System.out.println("cloudletLength : " + cloudletLength);

		//Constants.VM_NUMBER =  files.length;

		int eventNumber = (int) (Constants.SIMULATION_LIMIT/MonitorUnit.MONITORING_INTERVAL);
		int numberOfCloudList = files.length;
		long cloudletLength;
		int id = 1;
		int intLength = 100;
		cloudletLength = (long) MonitorUnit.MONITORING_INTERVAL * intLength ;
		
		//Constants.VM_NUMBER = files.length;
		
		for(int eventCounter = 1; eventCounter <= eventNumber; eventCounter++)
		{
			//length = (long) MonitorUnit.MONITORING_INTERVAL * intLength;
			
			for (; id <= eventCounter * numberOfCloudList ; id++)
			{			
				Cloudlet cloudlet = new Cloudlet(id, cloudletLength, Constants.CLOUDLET_PES,
						fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
				
				cloudlet.setUserId(brokerId);
				cloudlet.setExecStartTime((eventCounter -1) * MonitorUnit.MONITORING_INTERVAL);/**/
				
				list.add(cloudlet);
			}
		}

		return list;
	}
	
	private static List<Host> createHostList()
	{
		String traceFileName = System.getProperty("user.dir") + "/setupData/NotreDame_spec.txt";

		ReadTraceData traceReader = new ReadTraceData();
		ArrayList<Node> tmpHostList = traceReader.getHostList(traceFileName);
		
		List<Host> hostList = new ArrayList<Host>();
		
		for (Node node: tmpHostList)
		{
			List<Pe> peList = new ArrayList<Pe>();
			
			/*for (int i=0; i < node.getCores(); i++)
			{
				peList.add(new Pe(i, new PeProvisionerSimple(node.getCpu())));
			}/**/
			
			//for (int i=0; i < node.getCores(); i++)
			{
				peList.add(new Pe(0, new PeProvisionerSimple(node.getCpu())));
			}
			
			hostList.add(
	    			new Host(node.getNodeId(), 
	    				new RamProvisionerSimple(node.getRam()),
	    				new BwProvisionerSimple(node.getBw()),
	    				node.getStorage(),
	    				peList,
	    				new VmSchedulerTimeShared(peList)
	    			)
	    		); 
		}

		return hostList;
	}

	private static Datacenter createDatacenter(String name, boolean setFailureFlag)
	{

		String arch = "x86";     
		String os = "Linux";         
		String vmm = "Xen";
		double time_zone = 10.0;        
		double cost = 3.0;              
		double costPerMem = 0.05;		
		double costPerStorage = 0.001;	
		double costPerBw = 0.0;			
		LinkedList<Storage> storageList = new LinkedList<Storage>();	

		List<Host> hostList = createHostList();

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                arch, os, vmm, hostList, time_zone, cost, costPerMem, costPerStorage, costPerBw);

		Datacenter datacenter = null;
		try {
			datacenter = new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
			if (setFailureFlag)
				datacenter = failureInjection(datacenter);

			datacenter =  MonitorUnit.setMonitoringEvents(datacenter);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return datacenter;
	}
	
	private static Datacenter failureInjection(Datacenter datacenter)
	{
		String traceFileName = System.getProperty("user.dir") + "/setupData/NotreDameTrace/1month.txt";

		ReadFailureData failureReader = new ReadFailureData();
		ArrayList<FailureEvent> failureEventArray = failureReader.getFailureEventArray(traceFileName);

		int dayOrder = 2;	//1 means the first day
		for (FailureEvent failureEvent: failureEventArray)
		{
			long eventTime = failureEvent.getStartTime() - (long) ((dayOrder -1) * Constants.SIMULATION_LIMIT);
			
			if (eventTime >= 0 && eventTime <= Constants.SIMULATION_LIMIT)		//stop injection failures after simulation limit
			{
				datacenter.issueFailure(failureEvent.getNodeId(),eventTime, failureEvent.getFinishTime() - (long) ((dayOrder -1) * Constants.SIMULATION_LIMIT));
				//datacenter.issueFailure(failureEvent.getNodeId(),eventTime);
			}
		}
		return datacenter;
	}

	private static DatacenterBroker createBroker(){

		DatacenterBroker broker = null;
		try {
			broker = new DatacenterBroker("Broker");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return broker;
	}

	/**
	 * Prints the Cloudlet objects
	 * @param list  list of Cloudlets
	 */
	private static void printCloudletList(List<Cloudlet> list) {
		int size = list.size();
		Cloudlet cloudlet;

		String indent = "    ";
		Log.printLine();
		Log.printLine("========== OUTPUT ==========");
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent + "VM ID" + indent + "Time" + indent + "Start Time" + indent + "Finish Time");

		DecimalFormat dft = new DecimalFormat("###.##");
		for (int i = 0; i < size; i++) {
			cloudlet = list.get(i);
			Log.print(indent + cloudlet.getCloudletId() + indent + indent);

			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS)			
				Log.print("SUCCESS");
			else if (cloudlet.getCloudletStatus() == Cloudlet.CANCELED || cloudlet.getCloudletStatus() == Cloudlet.FAILED)
				Log.print("FAILED");
			else if (cloudlet.getCloudletStatus() == Cloudlet.QUEUED)
				Log.print("QUEUED");
			else if (cloudlet.getCloudletStatus() == Cloudlet.INEXEC)
				Log.print("INEXEC");
			else
				Log.print(cloudlet.getCloudletStatus());


			Log.printLine(indent + indent + cloudlet.getVmId() +
						indent + indent + dft.format(cloudlet.getActualCPUTime()) + indent + indent + dft.format(cloudlet.getExecStartTime())+
						indent + indent + dft.format(cloudlet.getFinishTime()));
			
		}

	}
	
	private static void printDetails(List<Cloudlet> cloudletList) {
		int cloudletFailedCounter = Calculation.calculateCloudletFailedCounter(cloudletList);
		int cloudletsNumber = cloudletList.size();
		double avgTime, avgMicroTime, cloudletExecutionTime;

		String indent = "    ";
		
		Log.enable();
		Constants.printingFlag = true;		//allow printing
		
		Log.printLine("Number of executed cloudlets: " + cloudletsNumber + indent + "sucess number: " + (cloudletsNumber - cloudletFailedCounter) 
				+ indent + "failure number (non sucess): " + cloudletFailedCounter + indent + "failure rate: " + Calculation.round((double) 100 * cloudletFailedCounter/cloudletsNumber) );
		
		avgTime = Calculation.calculateAverageTime(cloudletList);
		Log.printLine("average execution time : " + avgTime );
		
		avgMicroTime = Calculation.calculateAverageTime(getMigratedCloudletList(cloudletList));
		Log.printLine("average MICRO execution time : " + avgMicroTime  );
		
		cloudletExecutionTime = Calculation.getCloudExecutionTime(cloudletList);
		Log.printLine("time difference perecentage : " + Calculation.timeDifference(avgMicroTime, cloudletExecutionTime)  +"%");

	}

	
	public static List<Cloudlet> getMigratedCloudletList(List<Cloudlet> cloudletList)
	{
		List<Cloudlet> migratedCloudletList = new ArrayList<Cloudlet> ();
		
		for(Vm vm: MonitorUnit.getVmMigratedlist())
		{
			for(Cloudlet cloudlet: cloudletList)
			{
				if (cloudlet.getVmId() == vm.getId())
					migratedCloudletList.add(cloudlet);
			}			
		}
		return migratedCloudletList;
	}

}
