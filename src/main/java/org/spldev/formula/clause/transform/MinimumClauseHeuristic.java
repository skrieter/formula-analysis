package org.spldev.formula.clause.transform;

import java.util.*;

/**
 * Returns features dependent on the current clauses in the formula.
 *
 * @author Sebastian Krieter
 */
public class MinimumClauseHeuristic implements Iterator<DirtyFeature> {

	protected final DirtyFeature[] map;
	protected final int maxIndex;
	protected int curIndex = 0;
	protected int realCurIndex = 0;

	public MinimumClauseHeuristic(DirtyFeature[] map, int length) {
		this.map = map;
		maxIndex = length;
	}

	@Override
	public boolean hasNext() {
		return maxIndex != curIndex;
	}

	@Override
	public DirtyFeature next() {
		if (!hasNext()) {
			return null;
		}
		realCurIndex = getNextIndex();
		final DirtyFeature ret = map[realCurIndex];
		map[realCurIndex] = null;
		curIndex++;
		return ret;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	public int size() {
		return maxIndex - curIndex;
	}

	protected int getNextIndex() {
		DirtyFeature smallestFeature = map[1];
		int minIndex = 1;
		for (int i = 2; i < map.length; i++) {
			final DirtyFeature next = map[i];
			if ((smallestFeature == null) || ((next != null) && ((smallestFeature.getClauseCount() - next
				.getClauseCount()) > 0))) {
				smallestFeature = next;
				minIndex = i;
			}
		}
		return minIndex;
	}

}
