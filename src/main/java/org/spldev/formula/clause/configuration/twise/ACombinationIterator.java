package org.spldev.formula.clause.configuration.twise;

import java.util.*;

/**
 * Abstract iterator that implements parts of {@link ICombinationIterator}.
 *
 * @author Sebastian Krieter
 */
public abstract class ACombinationIterator implements ICombinationIterator {

	protected final List<PresenceCondition> expressions;
	protected final PresenceCondition[] nextCombination;

	protected final int t, n;
	protected final long numCombinations;
	protected final BinomialCalculator binomialCalculator;

	protected long counter = 0;
	private long index = 0;

	public ACombinationIterator(int t, List<PresenceCondition> expressions) {
		this(t, expressions, new BinomialCalculator(t, expressions.size()));
	}

	public ACombinationIterator(int t, List<PresenceCondition> expressions, BinomialCalculator binomialCalculator) {
		this.t = t;
		this.expressions = expressions;
		n = expressions.size();
		this.binomialCalculator = binomialCalculator;
		nextCombination = new PresenceCondition[t];
		numCombinations = binomialCalculator.binomial(n, t);
	}

	@Override
	public boolean hasNext() {
		return counter < numCombinations;
	}

	@Override
	public PresenceCondition[] next() {
		if (counter++ >= numCombinations) {
			return null;
		}
		index = nextIndex();
		final int[] computeCombination = computeCombination(index);

		for (int j = 0; j < nextCombination.length; j++) {
			nextCombination[j] = expressions.get(computeCombination[j]);
		}
		return nextCombination;
	}

	@Override
	public long getIndex() {
		return index;
	}

	protected abstract long nextIndex();

	@Override
	public void reset() {
		counter = 0;
		index = 0;
	}

	protected int[] computeCombination(long index) {
		final int[] combination = new int[t];
		for (int i = t; i > 0; i--) {
			if (index <= 0) {
				combination[i - 1] = i - 1;
			} else {
				final double root = 1.0 / i;
				final int p = (int) Math.ceil(Math.pow(index, root) * Math.pow(binomialCalculator.factorial(i), root));
				for (int j = p; j <= n; j++) {
					if (binomialCalculator.binomial(j, i) > index) {
						combination[i - 1] = j - 1;
						index -= binomialCalculator.binomial(j - 1, i);
						break;
					}
				}
			}
		}
		return combination;
	}

	@Override
	public Iterator<PresenceCondition[]> iterator() {
		return this;
	}

	@Override
	public long size() {
		return numCombinations;
	}

}
