package org.spldev.formula.clause.configuration.twise;

import java.util.*;

import org.spldev.formula.clause.*;

/**
 * Uses a {@link RandomPartitionSupplier} to construct a combined presence
 * condition for every combination.
 *
 * @author Sebastian Krieter
 */
public class SingleIterator implements ICombinationSupplier<ClauseList> {

	private final List<PresenceCondition> expressionSet;
	private final ICombinationSupplier<int[]> supplier;
	private final long numberOfCombinations;

	private final TWiseCombiner combiner;
	private final PresenceCondition[] nextCombination;

	public SingleIterator(int t, int n, List<PresenceCondition> expressionSet) {
		this.expressionSet = expressionSet;

		combiner = new TWiseCombiner(n);
		nextCombination = new PresenceCondition[t];

		supplier = new RandomPartitionSupplier(t, expressionSet.size());
		numberOfCombinations = supplier.size();
	}

	@Override
	public ClauseList get() {
		final int[] js = supplier.get();
		if (js != null) {
			for (int j = 0; j < js.length; j++) {
				nextCombination[j] = expressionSet.get(js[j]);
			}
			final ClauseList combinedCondition = new ClauseList();
			combiner.combineConditions(nextCombination, combinedCondition);
			return combinedCondition;
		} else {
			return null;
		}
	}

	@Override
	public long size() {
		return numberOfCombinations;
	}

}
