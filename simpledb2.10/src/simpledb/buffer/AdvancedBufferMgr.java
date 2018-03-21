/**
 * CS4432-Project1: This is the advanced version of BasicBufferMgr.
 * Author: Yihong Zhou (yzhou8)
 * 			Mengwen Li (mli2)
 */
package simpledb.buffer;

import java.util.ArrayList;
import java.util.Hashtable;

import simpledb.file.*;

/**
 * Manages the pinning and unpinning of buffers to blocks.
 * @author Edward Sciore
 *
 */
class AdvancedBufferMgr {
	/**
	 * CS4432-Project1:
	 * This defines the constant for switching between LRU and CLOCK policy.
	 */
	public static final int LRU = 0;
	public static final int CLOCK = 1;
	private Buffer[] bufferpool;
	/**
	 * CS4432-Project1:
	 * Defines the ArrayList to store the empty frames, 
	 * also defines the hash table to record the block and its corresponding frame number.
	 */
	private ArrayList<Buffer> emptyFrame;
	private Hashtable<Block, Integer> blockRecord;
	private int numAvailable;
	/**
	 * CS4432-Project1:
	 * Defines the policyConfig flag to switch between clock and LRU policies.
	 * Also defines the clock pointer.
	 * To switch between clock policy and LRU policy, change the policyConfig variable.
	 */
	private static int policyConfig = CLOCK;
	//Clock pointer.
	private int clkPtr = 0;

	//private boolean printFlg = false;

	/**
	 * Creates a buffer manager having the specified number 
	 * of buffer slots.
	 * This constructor depends on both the {@link FileMgr} and
	 * {@link simpledb.log.LogMgr LogMgr} objects 
	 * that it gets from the class
	 * {@link simpledb.server.SimpleDB}.
	 * Those objects are created during system initialization.
	 * Thus this constructor cannot be called until 
	 * {@link simpledb.server.SimpleDB#initFileAndLogMgr(String)} or
	 * is called first.
	 * @param numbuffs the number of buffer slots to allocate
	 */
	AdvancedBufferMgr(int numbuffs) {
		/**
		 * CS4432-Project1:
		 * Initialize emptyFrame and blockRecord.
		 */
		emptyFrame = new ArrayList<Buffer>();
		blockRecord = new Hashtable<Block, Integer>();
		bufferpool = new Buffer[numbuffs];
		numAvailable = numbuffs;
		/**
		 * CS4432-Project1:
		 * Set the index number for buffers and add all the buffers to empty list.
		 */
		for (int i=0; i<numbuffs; i++)
		{
			bufferpool[i] = new Buffer();
			bufferpool[i].setFrameNum(i);
			emptyFrame.add(bufferpool[i]);
		}

	}

	/**
	 * Flushes the dirty buffers modified by the specified transaction.
	 * @param txnum the transaction's id number
	 */
	synchronized void flushAll(int txnum) {
		for (Buffer buff : bufferpool)
			if (buff.isModifiedBy(txnum))
				buff.flush();
	}

	/**
	 * Pins a buffer to the specified block. 
	 * If there is already a buffer assigned to that block
	 * then that buffer is used;  
	 * otherwise, an unpinned buffer from the pool is chosen.
	 * Returns a null value if there are no available buffers.
	 * @param blk a reference to a disk block
	 * @return the pinned buffer
	 */
	synchronized Buffer pin(Block blk) {
		Buffer buff = findExistingBuffer(blk);
		if (buff == null) 
		{	 
			buff = chooseUnpinnedBuffer();
			if (buff == null)
				return null;    
			buff.assignToBlock(blk);
			/**
			 * CS4432-Project1:
			 * Push the block and its corresponding frame number to hash table.
			 */
			blockRecord.put(blk, buff.getFrameNum());
			//         if(printFlg)
			//         {
			//        	 if(policyConfig == LRU)
			//        	 {
			//        		 System.out.println("After LRU: ");
			//            	 System.out.println(this);
			//        	 } 
			//         }     	 
		}
		/**
		 * CS4432-Project1:
		 * Set the corresponding parameters in LRU and CLOCK policies.
		 */
		if(policyConfig == LRU)
		{
			buff.setTime(System.currentTimeMillis());
		}
		if(policyConfig == CLOCK)
		{
			buff.setRef(); 
		}
		if (!buff.isPinned())
		{
			numAvailable--;
		}
		buff.pin();
		return buff;
	}

	/**
	 * Allocates a new block in the specified file, and
	 * pins a buffer to it. 
	 * Returns null (without allocating the block) if 
	 * there are no available buffers.
	 * @param filename the name of the file
	 * @param fmtr a pageformatter object, used to format the new block
	 * @return the pinned buffer
	 */
	synchronized Buffer pinNew(String filename, PageFormatter fmtr) {
		Buffer buff = chooseUnpinnedBuffer();
		if (buff == null)
			return null;
		buff.assignToNew(filename, fmtr);	
		/**
		 * CS4432-Project1:
		 * Set the corresponding parameters in LRU and CLOCK policies.
		 */
		if(policyConfig == LRU)
		{
			buff.setTime(System.currentTimeMillis());       	 
		}
		if(policyConfig == CLOCK)
		{
			buff.setRef(); 
		}
		/**
		 * CS4432-Project1:
		 * Put the newly added block to hash table.
		 */
		blockRecord.put(buff.block(), buff.getFrameNum());
		numAvailable--;
		buff.pin();
		//      System.out.println("Empty frame count: " + emptyFrame.size());
		//      if(printFlg)
		//      {
		//     	 if(policyConfig == LRU)
		//     	 {
		//     		 System.out.println("After LRU: ");
		//         	 System.out.println(this);
		//     	 } 
		//      }
		return buff;
	}

	/**
	 * Unpins the specified buffer.
	 * @param buff the buffer to be unpinned
	 */
	synchronized void unpin(Buffer buff) {
		buff.unpin();
		/**
		 * CS4432-Project1:
		 * Set the last access time in LRU.
		 */
		if(policyConfig == LRU)
		{
			buff.setTime(System.currentTimeMillis());       	 
		}
		if (!buff.isPinned())
		{
			numAvailable++;
		}
	}

	/**
	 * Returns the number of available (i.e. unpinned) buffers.
	 * @return the number of available buffers
	 */
	int available() {
		return numAvailable;
	}

	private Buffer findExistingBuffer(Block blk) {
		//      for (Buffer buff : bufferpool) {
		//         Block b = buff.block();
		//         if (b != null && b.equals(blk))
		//            return buff;
		//      }
		//      return null;
		/**
		 * CS4432-Project1:
		 * Check the hash table to find the existing buffer.
		 */
		if(blockRecord.containsKey(blk))
		{
			return bufferpool[blockRecord.get(blk)];
		}
		return null;
	}

	private Buffer chooseUnpinnedBuffer() {
		/**
		 * CS4432-Project1:
		 * First check whether there are free buffers in the emptyFrame list.
		 * If free buffer is found, return.
		 */
		if(!emptyFrame.isEmpty())
		{
			return emptyFrame.remove(0);
		}
		/**
		 * CS4432-Project1:
		 * If the free buffer is not found, use LRU or CLOCK policy depending on policyConfig variable.
		 * After the buffer is found, it is removed from the hash table.
		 */
		Buffer buf;
		if(policyConfig == LRU)
		{
			buf = lruPolicy();
		}
		else
		{
			buf = clockPolicy();
		}
		if(buf != null)
		{
			blockRecord.remove(buf.block());
		}
		return buf;
		//	   
		//	   //Need to change all this to clock or LRU.
		//	   //Include removing from blockRecord in policy!!!!!
		//	   for (Buffer buff : bufferpool)
		//	   {
		//		   if (!buff.isPinned())
		//		   {
		//			   blockRecord.remove(buff.block());
		//			   return buff;
		//		   }
		//			   
		//	   }	   
		//	   return null;
	}

	/**
	 * CS4432-Project1:
	 * Function for LRU replacement policy.
	 */
	private Buffer lruPolicy()
	{
		long minTime = Long.MAX_VALUE;
		int index = -1;
		//Loop through the buffer pool and find the unpinned buffer with least time.
		for(int i=0; i<bufferpool.length; i++)
		{
			if(!bufferpool[i].isPinned())
			{
				long tempTime = bufferpool[i].getTime();
				if(tempTime < minTime)
				{
					minTime = tempTime;
					index = i;
				}	
			}
		}
		//If all buffers are pinned, return null.
		if(index == -1)
		{
			return null;
		}
		return bufferpool[index];
	}
	/**
	 * CS4432-Project1:
	 * Function for clock replacement policy.
	 */
	private Buffer clockPolicy()
	{
		int i;
		//First check whether all buffers are pinned. If this is the case, return null.
		for(i=0; i<bufferpool.length; i++)
		{
			if(!bufferpool[i].isPinned())
			{
				break;
			}
		}
		if(i == bufferpool.length)
		{
			return null;
		}
		Buffer buffer = null;
		//Use clock policy to find target.
		while(buffer == null)
		{
			if(!bufferpool[clkPtr].isPinned() && bufferpool[clkPtr].getRef() == 0)
			{
				buffer = bufferpool[clkPtr];
			}
			else if(!bufferpool[clkPtr].isPinned() && bufferpool[clkPtr].getRef() == 1)
			{
				bufferpool[clkPtr].unsetRef();
				clkPtr++;
			}
			else if(bufferpool[clkPtr].isPinned())
			{
				clkPtr++;
			}
			if(clkPtr >= bufferpool.length)
			{
				clkPtr = 0;
			}
		}
		return buffer;
	}

	/**
	 * CS4432-Project1:
	 * toString function.
	 */
	@Override
	public String toString()
	{
		String result = "";
		for(int i=0; i<bufferpool.length; i++)
		{
			result += bufferpool[i].toString();
			if(policyConfig == CLOCK)
			{
				result += " Reference bit: " + bufferpool[i].getRef();
			}
			else if(policyConfig == LRU)
			{
				result += " last access time: " + bufferpool[i].getTime();
			}
			result += '\n';
		}
		if(policyConfig == CLOCK)
		{
			result += "clock pointer location: " + clkPtr + '\n';
		}
		return result;
	}
}
