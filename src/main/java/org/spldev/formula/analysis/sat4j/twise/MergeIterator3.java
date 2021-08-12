/* -----------------------------------------------------------------------------
 * Formula-Analysis Lib - Library to analyze propositional formulas.
 * Copyright (C) 2021  Sebastian Krieter
 * 
 * This file is part of Formula-Analysis Lib.
 * 
 * Formula-Analysis Lib is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * Formula-Analysis Lib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Formula-Analysis Lib.  If not, see <https://www.gnu.org/licenses/>.
 * 
 * See <https://github.com/skrieter/formula-analysis> for further information.
 * -----------------------------------------------------------------------------
 */
package org.spldev.formula.analysis.sat4j.twise;

import java.util.*;

import org.spldev.formula.clauses.*;

/**
 * Combines multiple {@link ICombinationSupplier supplies} of {@link ClauseList}
 * and returns results from each supplier by turns.
 *
 * @author Sebastian Krieter
 */
public class MergeIterator3 implements ICombinationSupplier<ClauseList> {

	private final List<List<PresenceCondition>> expressionSets;
	private final ICombinationSupplier<int[]>[] suppliers;
	private final long numberOfCombinations;

	private final List<ClauseList> buffer = new ArrayList<>();
	private final TWiseCombiner combiner;
	private final PresenceCondition[] nextCombination;

	private int bufferIndex = 0;
	private final int maxIteratorIndex;

	@SuppressWarnings("unchecked")
	public MergeIterator3(int t, int n, List<List<PresenceCondition>> expressionSets) {
		this.expressionSets = expressionSets;

		maxIteratorIndex = expressionSets.size() - 1;
		suppliers = new ICombinationSupplier[expressionSets.size()];
		combiner = new TWiseCombiner(n);
		nextCombination = new PresenceCondition[t];

		long sumNumberOfCombinations = 0;
		for (int i = 0; i <= maxIteratorIndex; i++) {
			final ICombinationSupplier<int[]> supplier = new RandomPartitionSupplier(t, expressionSets.get(i).size());
			suppliers[i] = supplier;
			sumNumberOfCombinations += supplier.size();
		}
		numberOfCombinations = sumNumberOfCombinations;
	}

	@Override
	public ClauseList get() {
		if (buffer.isEmpty()) {
			for (int i = 0; i <= maxIteratorIndex; i++) {
				final ICombinationSupplier<int[]> supplier = suppliers[i];
				if (supplier != null) {
					final int[] js = supplier.get();
					if (js != null) {
						final List<PresenceCondition> expressionSet = expressionSets.get(i);
						for (int j = 0; j < js.length; j++) {
							nextCombination[j] = expressionSet.get(js[j]);
						}
						final ClauseList combinedCondition = new ClauseList();
						combiner.combineConditions(nextCombination, combinedCondition);
						buffer.add(combinedCondition);
					} else {
						suppliers[i] = null;
					}
				}
			}
			if (buffer.isEmpty()) {
				return null;
			}
		}
		final ClauseList remove = buffer.get(bufferIndex++);
		if (bufferIndex == buffer.size()) {
			buffer.clear();
			bufferIndex = 0;
		}
		return remove;
	}

	@Override
	public long size() {
		return numberOfCombinations;
	}

}
