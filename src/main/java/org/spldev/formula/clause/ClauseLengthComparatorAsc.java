package org.spldev.formula.clause;

import java.util.*;

/**
 * Compares clauses by he number of literals.
 *
 * @author Sebastian Krieter
 */
public class ClauseLengthComparatorAsc implements Comparator<LiteralList> {

	@Override
	public int compare(LiteralList o1, LiteralList o2) {
		return o1.getLiterals().length - o2.getLiterals().length;
	}

}
