package org.spldev.formula.clause.configuration.twise;

import java.util.*;

/**
 * Presence condition combination iterator that iterates of the combinations in
 * lexicographical order.
 *
 * @author Sebastian Krieter
 */
public class LexicographicIterator extends ACombinationIterator {

	private final int[] c;

	public LexicographicIterator(int t, List<PresenceCondition> expressions) {
		super(t, expressions);
		c = new int[t];
		for (int i = 0; i < (c.length - 1); i++) {
			c[i] = i;
		}
		c[t - 1] = t - 2;
	}

	@Override
	protected int[] computeCombination(long index) {
		int i = t;
		for (; i > 0; i--) {
			final int ci = ++c[i - 1];
			if (ci < ((n - t) + i)) {
				break;
			}
		}
		if ((i == 0) && (c[i] == ((n - t) + 1))) {
			return null;
		}

		for (; i < t; i++) {
			if (i == 0) {
				c[i] = 0;
			} else {
				c[i] = c[i - 1] + 1;
			}
		}
		return c;
	}

	@Override
	protected long nextIndex() {
		return 0;
	}

	@Override
	public long getIndex() {
		long index = 0;
		for (int i = 0; i < c.length; i++) {
			index += binomialCalculator.binomial(c[i], i + 1);
		}
		return index;
	}

	@Override
	public void reset() {
		super.reset();
		for (int i = 0; i < (c.length - 1); i++) {
			c[i] = i;
		}
		c[t - 1] = t - 2;
	}
}
