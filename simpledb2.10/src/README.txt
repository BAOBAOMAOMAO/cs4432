Task 2.1)
AdvancedBufferMgr: ArrayList<Buffer> emptyFrame;
					public AdvancedBufferMgr(int numbuffs);
					private Buffer chooseUnpinnedBuffer();

Task 2.2)
Buffer: private int frameNumber;
AdvancedBufferMgr: Hashtable<Block, Integer> blockRecord;

Task 2.3)
