package org.spldev.formula.clause.mig;

import org.spldev.formula.clause.*;
import org.spldev.util.job.*;

/**
 * Adjacency matrix implementation for a feature graph.
 *
 * @author Sebastian Krieter
 */

public class MIGBuilder extends MIGBuilder2 implements MonitorableFunction<CNF, MIG> {

	@Override
	public MIG execute(CNF cnf, InternalMonitor monitor) throws Exception {
		monitor.setTotalWork(113);

		init(cnf);
		monitor.step();

//		if (!getCoreFeatures(cnf, monitor.subTask(10))) {
//			return null;
//		}

//		addClauses(cnf, monitor.subTask(100));

//		bfsStrong();
		monitor.step();

		finish();
		monitor.step();
		return mig;
	}

}
