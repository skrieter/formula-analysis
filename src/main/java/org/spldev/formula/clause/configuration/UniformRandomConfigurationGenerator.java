package org.spldev.formula.clause.configuration;

import java.util.*;

import org.spldev.formula.clause.*;
import org.spldev.formula.clause.LiteralList.*;
import org.spldev.formula.clause.solver.*;
import org.spldev.util.data.*;
import org.spldev.util.job.*;

/**
 * Finds certain solutions of propositional formulas.
 *
 * @author Sebastian Krieter
 */
public class UniformRandomConfigurationGenerator extends ARandomConfigurationGenerator {

	public static final Identifier<List<LiteralList>> identifier = new Identifier<>();

	@Override
	public Identifier<List<LiteralList>> getIdentifier() {
		return identifier;
	}

	private int sampleSize = 1000;

	public UniformRandomConfigurationGenerator(int maxNumber) {
		super(maxNumber);
	}

	@Override
	protected void generate(SatSolver solver, InternalMonitor monitor) throws Exception {
		monitor.setTotalWork(maxSampleSize + sampleSize);

		final ARandomConfigurationGenerator gen = new RandomConfigurationGenerator(sampleSize);
		gen.setAllowDuplicates(false);
		gen.setRandom(getRandom());
		final List<LiteralList> sample = gen.execute(solver, monitor.subTask(sampleSize));
		if (sample.size() < maxSampleSize) {
			for (final LiteralList solution : sample) {
				addResult(solution);
			}
			return;
		} else if (sample.isEmpty()) {
			return;
		}

		solver.setSelectionStrategy(sample);

		for (int i = 0; i < maxSampleSize; i++) {
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

	public int getSampleSize() {
		return sampleSize;
	}

	public void setSampleSize(int sampleSize) {
		this.sampleSize = sampleSize;
	}

}
