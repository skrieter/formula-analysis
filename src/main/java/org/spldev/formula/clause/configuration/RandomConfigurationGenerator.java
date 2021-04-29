package org.spldev.formula.clause.configuration;

import java.util.*;

import org.spldev.formula.clause.*;
import org.spldev.formula.clause.LiteralList.*;
import org.spldev.formula.clause.solver.*;
import org.spldev.formula.clause.solver.SatSolver.*;
import org.spldev.util.data.*;
import org.spldev.util.job.*;

/**
 * Generates random configurations for a given propositional formula.
 *
 * @author Sebastian Krieter
 */
public class RandomConfigurationGenerator extends ARandomConfigurationGenerator {

	public static final Identifier<List<LiteralList>> identifier = new Identifier<>();

	@Override
	public Identifier<List<LiteralList>> getIdentifier() {
		return identifier;
	}

	@Override
	protected void generate(SatSolver solver, InternalMonitor monitor) throws Exception {
		monitor.setTotalWork(maxSampleSize);
		solver.setSelectionStrategy(SelectionStrategy.RANDOM);

		for (int i = 0; i < maxSampleSize; i++) {
			solver.shuffleOrder(getRandom());
			final int[] solution = solver.findSolution();
			if (solution == null) {
				break;
			}
			final LiteralList result = new LiteralList(solution, Order.INDEX, false);
			addResult(result);
			monitor.step();
			if (!allowDuplicates) {
				try {
					solver.addClause(result.negate());
				} catch (final RuntimeContradictionException e) {
					break;
				}
			}
		}
	}
	
}
