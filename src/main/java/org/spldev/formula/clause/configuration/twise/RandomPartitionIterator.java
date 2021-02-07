package org.spldev.formula.clause.configuration.twise;

import java.util.*;

/**
 * Presence condition combination iterator that uses the combinatorial number
 * system to enumerate all combinations and then alternately iterates over
 * certain randomized partitions of the combination space.
 *
 * @author Sebastian Krieter
 */
public class RandomPartitionIterator extends PartitionIterator {

	public RandomPartitionIterator(int t, List<PresenceCondition> expressions) {
		this(t, expressions, new Random(42));
	}

	public RandomPartitionIterator(int t, List<PresenceCondition> expressions, Random random) {
		super(t, expressions, 4);

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

}
