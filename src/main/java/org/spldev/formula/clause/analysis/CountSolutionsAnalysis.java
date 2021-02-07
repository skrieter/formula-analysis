package org.spldev.formula.clause.analysis;

import org.spldev.formula.clause.*;
import org.spldev.formula.clause.LiteralList.*;
import org.spldev.formula.clause.solver.*;
import org.spldev.formula.clause.solver.SatSolver.*;
import org.spldev.util.data.*;
import org.spldev.util.job.*;

/**
 * Attempts to count the number of possible solutions of a given {@link CNF}.
 *
 * @author Sebastian Krieter
 */
public class CountSolutionsAnalysis extends AbstractAnalysis<Long> {

	public static final Identifier<Long> identifier = new Identifier<>();

	@Override
	public Identifier<Long> getIdentifier() {
		return identifier;
	}

	@Override
	public Long analyze(SatSolver solver, InternalMonitor monitor) throws Exception {
		solver.setGlobalTimeout(true);
		long solutionCount = 0;
		SatResult hasSolution = solver.hasSolution();
		while (hasSolution == SatResult.TRUE) {
			solutionCount++;
			final int[] solution = solver.getSolution();
			try {
				solver.addClause(new LiteralList(solution, Order.INDEX, false).negate());
			} catch (final RuntimeContradictionException e) {
				break;
			}
			hasSolution = solver.hasSolution();
		}
		return hasSolution == SatResult.TIMEOUT ? -(solutionCount + 1) : solutionCount;
	}

}
