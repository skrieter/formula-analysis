package org.spldev.formula.clause.configuration.twise;

import java.util.*;

import org.spldev.formula.clause.configuration.twise.IteratorFactory.*;

/**
 * Combines multiple {@link ICombinationIterator iterators} and returns results
 * from each iterator by turns.
 *
 * @author Sebastian Krieter
 */
public class MergeIterator implements ICombinationIterator {

	protected final List<ICombinationIterator> setIterators;
	protected final long numberOfCombinations;
	protected final int t;

	private int iteratorIndex = -1;

	public MergeIterator(int t, List<List<PresenceCondition>> expressionSets, IteratorID id) {
		this.t = t;
		setIterators = new ArrayList<>(expressionSets.size());
		long sumNumberOfCombinations = 0;
		for (final List<PresenceCondition> expressions : expressionSets) {
			final ICombinationIterator iterator = IteratorFactory.getIterator(id, expressions, t);
			setIterators.add(iterator);
			sumNumberOfCombinations += iterator.size();
		}
		numberOfCombinations = sumNumberOfCombinations;
	}

	@Override
	public boolean hasNext() {
		for (final ICombinationIterator iterator : setIterators) {
			if (iterator.hasNext()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public PresenceCondition[] next() {
		for (int i = 0; i < setIterators.size(); i++) {
			iteratorIndex = (iteratorIndex + 1) % setIterators.size();
			final ICombinationIterator iterator = setIterators.get(iteratorIndex);
			if (iterator.hasNext()) {
				final PresenceCondition[] next = iterator.next();
				if (next != null) {
					return next;
				}
			}
		}
		return null;
	}

	@Override
	public long getIndex() {
		long mergedIndex = setIterators.get(iteratorIndex).getIndex();
		for (int i = iteratorIndex - 1; i >= 0; i--) {
			mergedIndex += setIterators.get(i).size();
		}
		return mergedIndex;
	}

	@Override
	public void reset() {
		iteratorIndex = 0;
		for (final ICombinationIterator iterator : setIterators) {
			iterator.reset();
		}
	}

	@Override
	public Iterator<PresenceCondition[]> iterator() {
		return this;
	}

	@Override
	public long size() {
		return numberOfCombinations;
	}

}
