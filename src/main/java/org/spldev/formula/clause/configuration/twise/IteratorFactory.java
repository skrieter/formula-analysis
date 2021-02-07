package org.spldev.formula.clause.configuration.twise;

import java.util.*;

/**
 * Instantiates an implementation of {@link ICombinationIterator}.
 *
 * @author Sebastian Krieter
 */
public class IteratorFactory {

	public enum IteratorID {
		InverseDefault, Default, Lexicographic, InverseLexicographic, RandomPartition, Partition
	}

	public static ICombinationIterator getIterator(IteratorID id, List<PresenceCondition> expressions, int t) {
		switch (id) {
		case Default:
			return new InverseDefaultIterator(t, expressions);
		case InverseDefault:
			return new DefaultIterator(t, expressions);
		case InverseLexicographic:
			return new InverseLexicographicIterator(t, expressions);
		case Lexicographic:
			return new LexicographicIterator(t, expressions);
		case Partition:
			return new PartitionIterator(t, expressions);
		case RandomPartition:
			return new RandomPartitionIterator(t, expressions);
		default:
			return null;
		}
	}
}
