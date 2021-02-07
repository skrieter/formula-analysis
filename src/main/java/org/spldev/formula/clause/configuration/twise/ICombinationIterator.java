package org.spldev.formula.clause.configuration.twise;

import java.util.*;

/**
 * An iterator for combinations of {@link PresenceCondition presence
 * conditions}.
 *
 * @author Sebastian Krieter
 */
public interface ICombinationIterator extends Iterator<PresenceCondition[]>, Iterable<PresenceCondition[]> {

	long getIndex();

	void reset();

	long size();

}
