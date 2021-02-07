package org.spldev.formula.clause.configuration.twise;

import java.util.*;

/**
 * Presence condition combination supplier that uses the combinatorial number
 * system to enumerate all combinations and then alternately iterates over
 * certain randomized partitions of the combination space.
 *
 * @author Sebastian Krieter
 */
public class RandomPartitionSupplier implements ICombinationSupplier<int[]> {

	protected final PresenceCondition[] nextCombination;

	protected final int t, n;
	protected final long numCombinations;
	protected final BinomialCalculator binomialCalculator;

	protected long counter = 0;

	protected final int[][] dim;
	private final int[] pos;
	private final int radix;

	public RandomPartitionSupplier(int t, int n) {
		this(t, n, new Random(42));
	}

	public RandomPartitionSupplier(int t, int n, Random random) {
		this.t = t;
		this.n = n;
		binomialCalculator = new BinomialCalculator(t, n);
		nextCombination = new PresenceCondition[t];
		numCombinations = binomialCalculator.binomial(n, t);

		final int numDim = 4 * t;
		radix = (int) Math.ceil(Math.pow(numCombinations, 1.0 / numDim));
		dim = new int[numDim][radix];
		pos = new int[numDim];

		for (int i = 0; i < dim.length; i++) {
			final int[] dimArray = dim[i];
			for (int j = 0; j < radix; j++) {
				dimArray[j] = j;
			}
		}

		for (int i = 0; i < dim.length; i++) {
			final int[] dimArray = dim[i];
			for (int j = dimArray.length - 1; j >= 0; j--) {
				final int index = random.nextInt(j + 1);
				final int a = dimArray[index];
				dimArray[index] = dimArray[j];
				dimArray[j] = a;
			}
		}
	}

	@Override
	public int[] get() {
		return computeCombination(nextIndex());
	}

	protected long nextIndex() {
		if (counter++ >= numCombinations) {
			return -1;
		}
		int result;
		do {
			result = 0;
			for (int i = 0; i < pos.length; i++) {
				result += Math.pow(radix, i) * dim[i][pos[i]];
			}
			for (int i = pos.length - 1; i >= 0; i--) {
				final int p = pos[i];
				if ((p + 1) < radix) {
					pos[i] = p + 1;
					break;
				} else {
					pos[i] = 0;
				}
			}
		} while (result >= numCombinations);

		return result;
	}

	protected int[] computeCombination(long index) {
		if (index < 0) {
			return null;
		}
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
	public long size() {
		return numCombinations;
	}

}
