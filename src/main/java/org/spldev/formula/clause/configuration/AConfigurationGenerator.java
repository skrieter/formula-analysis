package org.spldev.formula.clause.configuration;

import java.util.*;
import java.util.concurrent.*;

import org.spldev.formula.clause.*;
import org.spldev.formula.clause.analysis.*;
import org.spldev.formula.clause.solver.*;
import org.spldev.util.job.*;
import org.spldev.util.logging.*;

/**
 * Finds certain solutions of propositional formulas.
 *
 * @author Sebastian Krieter
 */
public abstract class AConfigurationGenerator extends AbstractAnalysis<List<LiteralList>> implements
	IConfigurationGenerator {

	protected final int maxSampleSize;

	private final List<LiteralList> resultList = new ArrayList<>();
	private final LinkedBlockingQueue<LiteralList> resultQueue = new LinkedBlockingQueue<>();

	public AConfigurationGenerator() {
		this(Integer.MAX_VALUE);
	}

	public AConfigurationGenerator(int maxSampleSize) {
		super();
		this.maxSampleSize = maxSampleSize;
	}

	@Override
	public final List<LiteralList> analyze(SatSolver solver, InternalMonitor monitor) throws Exception {
		resultList.clear();
		resultQueue.clear();

		generate(solver, monitor);

		return resultList;
	}

	protected abstract void generate(SatSolver solver, InternalMonitor monitor) throws Exception;

	protected void addResult(LiteralList result) {
		resultList.add(result);
		try {
			resultQueue.put(result);
		} catch (final InterruptedException e) {
			Logger.logError(e);
		}
	}

	@Override
	public LinkedBlockingQueue<LiteralList> getResultQueue() {
		return resultQueue;
	}

}
