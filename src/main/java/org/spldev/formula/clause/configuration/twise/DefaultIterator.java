package org.spldev.formula.clause.configuration.twise;

import java.util.*;

/**
 * Presence condition combination iterator that uses the combinatorial number
 * system to enumerate all combinations and then iterates from first to last
 * combination.
 *
 * @author Sebastian Krieter
 */
public class DefaultIterator extends ACombinationIterator {

	public DefaultIterator(int t, List<PresenceCondition> expressions) {
		super(t, expressions);
	}

	@Override
	protected long nextIndex() {
		return counter - 1;
	}

}
