package org.spldev.formula.clause.solver;

import static org.sat4j.core.LiteralsUtils.*;

import java.util.*;

import org.sat4j.core.*;
import org.sat4j.minisat.core.*;
import org.spldev.formula.clause.*;

/**
 * Uses a sample of configurations to achieve a phase selection that corresponds
 * to a uniform distribution of configurations in the configuration space.
 *
 * @author Sebastian Krieter
 */
public class UniformRandomSelectionStrategy implements IPhaseSelectionStrategy {

	private static final long serialVersionUID = 1L;

	public static final Random RAND = new Random(123456789);

	private final LinkedList<LiteralList> usedSamples = new LinkedList<>();
	private final LinkedList<LiteralList> notUsedSamples = new LinkedList<>();

	private final int[] model;
	private final int[] ratio;

	public UniformRandomSelectionStrategy(List<LiteralList> sample) {
		usedSamples.addAll(sample);
		model = new int[sample.get(0).size()];
		ratio = new int[sample.get(0).size()];

		for (final LiteralList solution : usedSamples) {
			final int[] literals = solution.getLiterals();
			for (int i = 0; i < literals.length; i++) {
				if (literals[i] > 0) {
					ratio[i]++;
				}
			}
		}
	}

	public void undo(int var) {
		final int literal = model[var - 1];
		if (literal != 0) {
			updateRatioUnset(literal);
			model[var - 1] = 0;
		}
	}

	@Override
	public void assignLiteral(int p) {
		final int literal = LiteralsUtils.toDimacs(p);
		model[LiteralsUtils.var(p) - 1] = literal;
		updateRatioSet(literal);
	}

	@Override
	public void init(int nlength) {
	}

	@Override
	public void init(int var, int p) {
	}

	@Override
	public int select(int var) {
		return (RAND.nextInt(usedSamples.size()) < ratio[var - 1]) ? posLit(var) : negLit(var);
	}

	@Override
	public void updateVar(int p) {
	}

	@Override
	public void updateVarAtDecisionLevel(int q) {
	}

	@Override
	public String toString() {
		return "uniform random phase selection";
	}

	private void updateRatioUnset(int literal) {
		for (final Iterator<LiteralList> iterator = notUsedSamples.iterator(); iterator.hasNext();) {
			final LiteralList solution = iterator.next();
			final int[] literals = solution.getLiterals();
			if (literals[Math.abs(literal) - 1] == -literal) {
				iterator.remove();
				usedSamples.addFirst(solution);
				for (int j = 0; j < literals.length; j++) {
					if (literals[j] > 0) {
						ratio[j]++;
					}
				}
			}
		}
	}

	private void updateRatioSet(int literal) {
		for (final Iterator<LiteralList> iterator = usedSamples.iterator(); iterator.hasNext();) {
			final LiteralList solution = iterator.next();
			final int[] literals = solution.getLiterals();
			if (literals[Math.abs(literal) - 1] == -literal) {
				iterator.remove();
				notUsedSamples.addFirst(solution);
				for (int j = 0; j < literals.length; j++) {
					if (literals[j] > 0) {
						ratio[j]--;
					}
				}
			}
		}
	}

}
