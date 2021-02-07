package org.spldev.formula.clause.analysis;

import org.spldev.formula.clause.*;
import org.spldev.formula.clause.solver.*;
import org.spldev.formula.clause.solver.SatSolver.*;
import org.spldev.util.data.*;
import org.spldev.util.job.*;

/**
 * Determines whether a given {@link CNF} is satisfiable and returns the found
 * solution.
 *
 * @author Sebastian Krieter
 */
public class HasSolutionAnalysis extends AbstractAnalysis<Boolean> {

	public static final Identifier<Boolean> identifier = new Identifier<>();

	@Override
	public Identifier<Boolean> getIdentifier() {
		return identifier;
	}

	@Override
	public Boolean analyze(SatSolver solver, InternalMonitor monitor) throws Exception {
		final SatResult hasSolution = solver.hasSolution();
		switch (hasSolution) {
		case FALSE:
			return false;
		case TIMEOUT:
			reportTimeout();
			return false;
		case TRUE:
			return true;
		default:
			throw new AssertionError(hasSolution);
		}
	}

}
