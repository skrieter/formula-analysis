package org.spldev.formula.clause;

import java.io.*;
import java.util.*;

import org.spldev.formula.*;

/**
 * Represents an instance of a satisfiability problem in CNF.
 *
 * @author Sebastian Krieter
 */
public class ClauseList extends ArrayList<LiteralList> implements Cloneable, Serializable {

	private static final long serialVersionUID = -4298323253677967328L;

	public ClauseList() {
		super();
	}

	public ClauseList(int size) {
		super(size);
	}

	public ClauseList(Collection<? extends LiteralList> c) {
		super(c);
	}

	public ClauseList(ClauseList otherClauseList) {
		super(otherClauseList.size());
		otherClauseList.stream().map(LiteralList::clone).forEach(this::add);
	}

	@Override
	public ClauseList clone() {
		return new ClauseList(this);
	}

	/**
	 * Negates all clauses in the list (applies De Morgan).
	 *
	 * @return A newly construct {@code ClauseList}.
	 */
	public ClauseList negate() {
		final ClauseList negatedClauseList = new ClauseList();
		stream().map(LiteralList::negate).forEach(negatedClauseList::add);
		return negatedClauseList;
	}

	public ClauseList adapt(VariableMap oldVariableMap, VariableMap newVariableMap) {
		final ClauseList adaptedClauseList = new ClauseList();
		stream() //
			.map(clause -> clause.adapt(oldVariableMap, newVariableMap)) //
			.forEach(adaptedClauseList::add);
		return adaptedClauseList;
	}

	/**
	 * Converts CNF to DNF and vice-versa.
	 *
	 * @return A newly construct {@code ClauseList}.
	 */
	public ClauseList convert() {
		final ClauseList convertedClauseList = new ClauseList();
		convert(this, convertedClauseList, new int[size()], 0);
		return convertedClauseList;
	}

	private void convert(ClauseList cnf, ClauseList dnf, int[] literals, int index) {
		if (index == cnf.size()) {
			final LiteralList literalSet = new LiteralList(Arrays.copyOf(literals, literals.length)).clean();
			if (literalSet != null) {
				dnf.add(literalSet);
			}
		} else {
			for (final int literal : cnf.get(index).getLiterals()) {
				literals[index] = literal;
				convert(cnf, dnf, literals, index + 1);
			}
		}
	}

}
