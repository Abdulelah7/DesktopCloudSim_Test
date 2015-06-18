package southampton.ecs.desktopcloudsim;

public class FailureEvent {
	private int nodeId;
	private long startTime;
	private long finishTime;
	
	public FailureEvent()
	{
		
	}

	public void setNodeId(int nodeId)
	{
		this.nodeId = nodeId;
	}
	public int getNodeId()
	{
		return this.nodeId;
	}
	public void setStartTime(long time)
	{
		this.startTime = time;
	}
	public long getStartTime()
	{
		return this.startTime;
	}
	public void setFinishTime(long finishTime)
	{
		this.finishTime = finishTime;
	}
	public long getFinishTime()
	{
		return this.finishTime;
	}
}
