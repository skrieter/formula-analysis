package org.spldev.formula.clause.analysis;

import org.spldev.formula.clause.*;
import org.spldev.formula.clause.solver.*;
import org.spldev.util.job.*;

/**
 * Basic analysis interface.
 *
 * @param <T> Type of the analysis result.
 *
 * @author Sebastian Krieter
 */
public interface Analysis<T> extends MonitorableFunction<CNF, T> {

	LiteralList getAssumptions();

	void setAssumptions(LiteralList assumptions);

	T execute(SatSolver solver, InternalMonitor monitor) throws Exception;

}
