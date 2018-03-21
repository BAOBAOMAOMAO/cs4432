/**
 * CS4432
 */

package simpledb.planner;

import java.util.ArrayList;
import java.util.Iterator;

import simpledb.index.Index;
import simpledb.parse.CreateIndexData;
import simpledb.parse.DeleteData;
import simpledb.parse.InsertData;
import simpledb.parse.ModifyData;
import simpledb.query.Constant;
import simpledb.query.Plan;
import simpledb.query.SelectPlan;
import simpledb.query.TablePlan;
import simpledb.query.UpdateScan;
import simpledb.server.SimpleDB;
import simpledb.tx.Transaction;

public class IndexUpdatePlanner extends BasicUpdatePlanner
{
	simpledb.index.planner.IndexUpdatePlanner iup = new simpledb.index.planner.IndexUpdatePlanner();
	/**
	 * CS4432
	 * @param data
	 * @param tx
	 * @return
	 */
	public int executeDelete(DeleteData data, Transaction tx) {
		return iup.executeDelete(data, tx);
	}
	@Override
	public int executeModify(ModifyData data, Transaction tx) {
		return iup.executeModify(data, tx);
	}
	@Override
	public int executeInsert(InsertData data, Transaction tx) {
		return iup.executeInsert(data, tx);
	}
	@Override
	public int executeCreateIndex(CreateIndexData data, Transaction tx) {
		SimpleDB.mdMgr().createIndex(data.indexType(), data.indexName(), data.tableName(), data.fieldName(), tx);
		return 0;  
	}

}
