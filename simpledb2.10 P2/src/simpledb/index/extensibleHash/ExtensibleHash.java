/**
 * CS4432 Task 2
 */

package simpledb.index.extensibleHash;

import java.util.ArrayList;

import simpledb.index.Index;
import simpledb.query.Constant;
import simpledb.query.TableScan;
import simpledb.record.RID;
import simpledb.record.Schema;
import simpledb.record.TableInfo;
import simpledb.tx.Transaction;

public class ExtensibleHash implements Index
{
	public static int MAX_TUPLES = 4;
	
	private int globalDepth = 1;
	/**
	 * Bucket: 
	 * private int localDepth;
	 * private int numRecords;
	 * private int bucketId;
	 */
	//Global table.
	private ArrayList<Bucket> buckets;
	private String idxname;
	private Schema sch;
	private Transaction tx;
	private Constant searchkey = null;
	private TableScan ts = null;
	private int tsBlockNum;
	private int bucketCnt = 0;

	public ExtensibleHash(String idxname, Schema sch, Transaction tx)
	{
		//read index from existing files
		TableInfo ti = indexFile(idxname);
		TableScan is = new TableScan(ti,tx);
		buckets = new ArrayList<Bucket>();
		while(is.next()){
			this.globalDepth = is.getInt("globalDepth");
			Bucket bucket = new Bucket(is.getInt("localDepth"),is.getInt("numberRecords"),is.getInt("bucketId"));
			buckets.add(bucket);
		}
		int max = 0;
		for(int i = 0; i < buckets.size(); i++){
			if(buckets.get(i).getBucketId() > max){
				max = buckets.get(i).getBucketId();
			}
		}
		this.bucketCnt = max;
		////////////////////////
		this.idxname = idxname;
		this.sch = sch;
		this.tx = tx;
		if(buckets.size() == 0){
			buckets.add(new Bucket(1, 0, bucketCnt));
			bucketCnt ++;
			buckets.add(new Bucket(1, 0, bucketCnt));
			bucketCnt ++;
		}
		System.out.println("Extensible hashing created !!!!!!!!!!!!!!!!!!!!!");
	}
	
	public TableInfo indexFile(String idxname){
		Schema indexSchema = new Schema();
		indexSchema.addIntField("globalDepth");
		indexSchema.addIntField("localDepth");
		indexSchema.addIntField("numberRecords");
		indexSchema.addIntField("bucketId");
		return new TableInfo("ExtensibleHashIndex" + idxname, indexSchema);
	}
	
	public void writeGlobalIndexFile()
	{
		TableInfo ti = this.indexFile(this.idxname);
		TableScan ts = new TableScan(ti, tx);
		//Empty the table.
		while(ts.next())
		{
			ts.delete();
		}
		for(int i=0; i<buckets.size(); i++)
		{
			Bucket b = buckets.get(i);
			ts.insert();
			ts.setInt("globalDepth", this.globalDepth);
			ts.setInt("localDepth", b.getLocalDepth());
			ts.setInt("numberRecords", b.getNumRecords());
			ts.setInt("bucketId", b.getBucketId());
		}
	}
	
	@Override
	public void beforeFirst(Constant searchkey) {
		close();
		this.searchkey = searchkey;
		int hc = searchkey.hashCode();
		System.out.println("The hash code for searchkey " + searchkey.toString() + " is " + hc);
		String binHc = Integer.toBinaryString(hc);
		String hashCode = binHc.substring(binHc.length() - this.globalDepth);
		int hashLoc = Integer.parseInt(hashCode, 2);
		String tblname = idxname + buckets.get(hashLoc).getBucketId();
		TableInfo ti = new TableInfo(tblname, sch);
		ts = new TableScan(ti, tx);
		System.out.println("The hash location in beforeFirst is " + hashLoc);
		this.tsBlockNum = hashLoc;
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean next() {
		while(ts.next())
		{
			if(ts.getVal("dataval").equals(searchkey))
			{
				return true;
			}
		}
		return false;
	}
	@Override
	public RID getDataRid() {
		int blknum = ts.getInt("block");
		int id = ts.getInt("id");
		return new RID(blknum, id);
	}

	@Override
	public void insert(Constant dataval, RID datarid) {
		// TODO Auto-generated method stub
		this.beforeFirst(dataval);
		System.out.println("In insert, tsBlockNum is " + this.tsBlockNum);
		Bucket b = this.buckets.get(this.tsBlockNum);
		if(b.getNumRecords() < MAX_TUPLES)
		{
			ts.insert();
			ts.setInt("block", datarid.blockNumber());
			ts.setInt("id", datarid.id());
			ts.setVal("dataval", dataval);
			b.addNumRecords();
			System.out.println("Number of records here is " + b.getNumRecords());
			System.out.println("In array list: " + this.buckets.get(this.tsBlockNum).getNumRecords());
		}
		//Split here.
		else
		{
			System.out.println("Needs to split");
			this.split();
			this.insert(dataval, datarid);
		}
		System.out.println("After insert ~~~~~~~~~~~~~~~~~~~~");
		for(int i=0; i<buckets.size(); i++)
		{
			System.out.println("Bucket number " + i + ": size is " + buckets.get(i).getNumRecords());
		}
		this.writeGlobalIndexFile();
	}
	
	public void insert(Constant dataval, RID datarid, TableScan ts) {
		// TODO Auto-generated method stub
		this.beforeFirst(dataval);
		Bucket b = this.buckets.get(this.tsBlockNum);
		if(b.getNumRecords() < MAX_TUPLES)
		{
			ts.insert();
			ts.setInt("block", datarid.blockNumber());
			ts.setInt("id", datarid.id());
			ts.setVal("dataval", dataval);
			b.addNumRecords();
			System.out.println("Number of records here is " + b.getNumRecords());
			System.out.println("In array list: " + this.buckets.get(this.tsBlockNum).getNumRecords());
		}
		//Split here.
		else
		{
			this.split();
			this.insert(dataval, datarid);
		}
		this.writeGlobalIndexFile();
	}

	@Override
	public void delete(Constant dataval, RID datarid) {
		 beforeFirst(dataval);
		 while(next()){
			 if(getDataRid().equals(datarid)){
				 ts.delete();
				 return;
			 }
		 }
		 this.writeGlobalIndexFile();
	}
	
	public void delete(Constant dataval, RID datarid, TableScan ts)
	{		 
		beforeFirst(dataval);
		while(next()){
			if(getDataRid().equals(datarid)){
				ts.delete();
				return;
			}
		}
		this.writeGlobalIndexFile();
	}

	@Override
	public void close() {
		if(ts != null)
		{
			ts.close();
		}
	}

	private void split()
	{
		//Get the block that is full.
		Bucket b = this.buckets.get(this.tsBlockNum);
		String oTn = idxname + b.getBucketId();
		TableInfo origTi = new TableInfo(oTn, sch);
		TableScan origTs = new TableScan(origTi, tx);
		if(b.getLocalDepth() < this.globalDepth)
		{
			//Get the local depth.
			int ld = b.getLocalDepth();
			Bucket newBucket = new Bucket(ld + 1, 0, this.bucketCnt);
			this.bucketCnt ++;
			String binTsBlockNum = Integer.toBinaryString(this.tsBlockNum);
			String newBucketCmp = "1" + binTsBlockNum.substring(binTsBlockNum.length() - ld);
			b.addLocalDepth();
			String spTn = idxname + newBucket.getBucketId();
			TableInfo splittedTi = new TableInfo(spTn, sch);
			TableScan newTs = new TableScan(splittedTi, tx);
			while(origTs.next())
			{
				String oldString = Integer.toBinaryString(origTs.getVal("dataval").hashCode());
				//Check whether the number should go to the new bucket or the old one.
				//If equals, go to the new one.
				if(oldString.substring(oldString.length() - ld - 1).equals(newBucketCmp))
				{
					//Insert this record into new table.
					this.insert(origTs.getVal("dataval"), origTs.getRid(), newTs);
					newBucket.addNumRecords();
					//Delete here.
					this.delete(origTs.getVal("dataval"), origTs.getRid(), origTs);
					b.decNumRecords();
				}
			}
			for(int i=0; i<buckets.size(); i++)
			{
				String binCnt = Integer.toBinaryString(i);
				//Change the pointer in the table.
				if(binCnt.substring(binCnt.length() - ld - 1).equals(newBucketCmp))
				{
					buckets.set(i, newBucket);
				}
			}
		}
		else
		{
			this.globalDepth ++;
			//Double the global index ArrayList.
			for(int i=0; i<buckets.size(); i++)
			{
				buckets.add(buckets.get(i));
			}
			buckets.get(this.tsBlockNum).addLocalDepth();
			int dirtyIndex = Integer.parseInt("1" + Integer.toBinaryString(this.tsBlockNum), 2);
			Bucket newBucket = new Bucket(this.globalDepth, 0, this.bucketCnt);
			//Create a new file for the new bucket.
			String spTn = idxname + newBucket.getBucketId();
			TableInfo splittedTi = new TableInfo(spTn, sch);
			TableScan newTs = new TableScan(splittedTi, tx);
			//Put the new bucket into the ArrayList.
			buckets.set(dirtyIndex, newBucket);
			//Split values between the old table row and the newly added row.
			while(origTs.next())
			{
				String oldString = Integer.toBinaryString(origTs.getVal("dataval").hashCode());
				//Check whether the number should go to the new bucket or the old one.
				//If equals, go to the new one.
				if(oldString.substring(oldString.length() - this.globalDepth).equals(Integer.toBinaryString(dirtyIndex)))
				{
					//Insert this record into new table.
					this.insert(origTs.getVal("dataval"), origTs.getRid(), newTs);
					buckets.get(dirtyIndex).addNumRecords();
					//Delete here.
					this.delete(origTs.getVal("dataval"), origTs.getRid(), origTs);
					b.decNumRecords();
				}
			}
		}
	}
	
//	@Override
//	String toString()
//	{
//		
//	}
}
