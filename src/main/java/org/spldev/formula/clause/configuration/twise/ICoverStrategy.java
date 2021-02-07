package org.spldev.formula.clause.configuration.twise;

import org.spldev.formula.clause.*;

/**
 * A strategy for covering a given {@link ClauseList expressions} within a list
 * of {@link TWiseConfiguration solutions}.
 *
 * @author Sebastian Krieter
 */
interface ICoverStrategy {

	enum CombinationStatus {
		NOT_COVERED, COVERED, INVALID,
	}

	CombinationStatus cover(final ClauseList nextCondition);

}
