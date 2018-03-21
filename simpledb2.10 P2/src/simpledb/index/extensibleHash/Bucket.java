package simpledb.index.extensibleHash;

public class Bucket 
{
	private int localDepth;
	private int numRecords;
	private int bucketId;
	
	public Bucket(int localDepth, int numRecords, int bucketId)
	{
		this.localDepth = localDepth;
		this.numRecords = numRecords;
		this.bucketId = bucketId;
	}
	
	public void addNumRecords()
	{
		this.numRecords ++;
	}
	
	public void decNumRecords()
	{
		this.numRecords --;
	}
	
	public void addLocalDepth()
	{
		this.localDepth ++;
	}
	
	public int getNumRecords()
	{
		return this.numRecords;
	}

	public int getBucketId()
	{
		return this.bucketId;
	}
	
	public int getLocalDepth()
	{
		return this.localDepth;
	}
}
