package org.spldev.formula.clause.configuration.twise;

import java.util.*;

/**
 * Presence condition combination iterator that reverses the order of
 * {@link LexicographicIterator}.
 *
 * @author Sebastian Krieter
 */
public class InverseLexicographicIterator extends ACombinationIterator {

	private final int[] c;

	public InverseLexicographicIterator(int t, List<PresenceCondition> expressions) {
		super(t, expressions);
		c = new int[t];
		for (int i = t; i > 0; i--) {
			c[t - i] = n - i;
		}
		c[t - 1] = n;
	}

	@Override
	protected int[] computeCombination(long index) {
		counter++;
		int i = t - 1;
		for (; i >= 0; i--) {
			if (i == 0) {
				c[i]--;
			} else if ((c[i - 1] + 1) < c[i]) {
				c[i]--;
				return c;
			} else {
				c[i] = (n - t) + i;
			}
		}
		if (c[0] < 0) {
			return null;
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

}
