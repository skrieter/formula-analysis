package org.spldev.formula.clause.analysis;

import java.util.*;

import org.spldev.formula.clause.*;
import org.spldev.formula.clause.solver.*;
import org.spldev.util.*;
import org.spldev.util.data.*;
import org.spldev.util.job.*;

/**
 * Base class for an analysis using a {@link SatSolver sat solver}.
 *
 * @param <T> Type of the analysis result.
 *
 * @author Sebastian Krieter
 */
public abstract class AbstractAnalysis<T> implements Analysis<T>, Provider<T> {

	protected LiteralList assumptions = null;

	private Random random = new Random(112358);

	private boolean timeoutOccured = false;
	private boolean throwTimeoutException = true;
	private int timeout = 1000;

	@Override
	public Result<T> apply(Cache formula, InternalMonitor monitor) {
		return formula.get(CNFProvider.identifier).flatMap(cnf -> Executor.run(this, cnf, monitor));
	}

	@Override
	public final T execute(CNF cnf, InternalMonitor monitor) {
		return execute(initSolver(cnf), monitor);
	}

	@Override
	public final T execute(SatSolver solver, InternalMonitor monitor) {
		Objects.nonNull(solver);
		solver.setTimeout(timeout);
		if (assumptions != null) {
			solver.assignmentPushAll(assumptions.getLiterals());
		}
		assumptions = new LiteralList(solver.getAssignmentArray());
		timeoutOccured = false;

		monitor.checkCancel();
		try {
			return analyze(solver, monitor);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		} finally {
			solver.assignmentClear(0);
		}
	}

	protected SatSolver initSolver(CNF cnf) throws RuntimeContradictionException {
		return new Sat4JSolver(cnf);
	}

	protected abstract T analyze(SatSolver solver, InternalMonitor monitor) throws Exception;

	protected final void reportTimeout() throws RuntimeTimeoutException {
		timeoutOccured = true;
		if (throwTimeoutException) {
			throw new RuntimeTimeoutException();
		}
	}

	@Override
	public final LiteralList getAssumptions() {
		return assumptions;
	}

	@Override
	public final void setAssumptions(LiteralList assumptions) {
		this.assumptions = assumptions;
	}

	public final boolean isThrowTimeoutException() {
		return throwTimeoutException;
	}

	public final void setThrowTimeoutException(boolean throwTimeoutException) {
		this.throwTimeoutException = throwTimeoutException;
	}

	public final boolean isTimeoutOccured() {
		return timeoutOccured;
	}

	public Random getRandom() {
		return random;
	}

	public void setRandom(Random random) {
		this.random = random;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

}
