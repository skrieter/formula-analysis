package org.spldev.formula.clause.configuration.twise;

import java.util.*;

/**
 * Presence condition combination iterator that reverses the order of
 * {@link DefaultIterator}.
 *
 * @author Sebastian Krieter
 */
public class InverseDefaultIterator extends ACombinationIterator {

	public InverseDefaultIterator(int t, List<PresenceCondition> expressions) {
		super(t, expressions);
	}

	@Override
	protected long nextIndex() {
		return numCombinations - counter;
	}

}
