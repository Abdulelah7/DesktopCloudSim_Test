package southampton.ecs.desktopcloudsim;

/*
 * This class represents a node in the FTA system, 
 * each node has a list of trace records 
 */
public class Node {
	private int id;
	//just for host record
	int mips;
	int ram;
	int storage;
	int bw;
	int cores;
	
	
	public Node()
	{
	}
	
	public Node(int hostId)
	{
		
	}
	public void setCpu(int mips)
	{
		this.mips = mips;
	}
	public int getCpu()
	{
		return this.mips;
	}
	public void setRam(int ram)
	{
		this.ram = ram;
	}
	public int getRam()
	{
		return this.ram;
	}
	public void setStorage(int storage)
	{
		this.storage = storage;
	}
	public int getStorage()
	{
		return this.storage;
	}
	public void setBw(int bw)
	{
		this.bw = bw;
	}
	public int getBw()
	{
		return this.bw;
	}
	public void setCores(int cores)
	{
		this.cores = cores;
	}
	public int getCores()
	{
		return this.cores;
	}
	public void setNodeId(int id)
	{
		this.id = id;
	}

	public int getNodeId()
	{
		return this.id;
	}
}
