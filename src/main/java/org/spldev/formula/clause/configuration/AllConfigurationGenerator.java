package org.spldev.formula.clause.configuration;

import java.util.*;

import org.spldev.formula.clause.*;
import org.spldev.formula.clause.LiteralList.*;
import org.spldev.formula.clause.solver.*;
import org.spldev.util.data.*;
import org.spldev.util.job.*;

/**
 * Generates all configurations for a given propositional formula.
 *
 * @author Sebastian Krieter
 */
public class AllConfigurationGenerator extends AConfigurationGenerator {

	public static final Identifier<List<LiteralList>> identifier = new Identifier<>();

	@Override
	public Identifier<List<LiteralList>> getIdentifier() {
		return identifier;
	}

	@Override
	protected void generate(SatSolver solver, InternalMonitor monitor) throws Exception {
		for (int i = 0; i < maxSampleSize; i++) {
			final int[] solution = solver.findSolution();
			if (solution == null) {
				break;
			}
			final LiteralList result = new LiteralList(solution, Order.INDEX, false);
			addResult(result);
			try {
				solver.addClause(result.negate());
			} catch (final RuntimeContradictionException e) {
				break;
			}
		}
	}

}
