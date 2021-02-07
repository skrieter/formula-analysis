package org.spldev.formula.clause.transform;

import java.util.*;

import org.spldev.formula.clause.*;

/**
 * Used by {@link CNFSlicer}.
 *
 * @author Sebastian Krieter
 */
public class DirtyClause extends LiteralList {

	private static final long serialVersionUID = 521679259725560031L;

	private int relevance;

	public static DirtyClause createClause(int[] newLiterals, int curFeature) {
		final HashSet<Integer> literalSet = new HashSet<>(newLiterals.length << 1);

		for (final int literal : newLiterals) {
			if (curFeature != Math.abs(literal)) {
				if (literalSet.contains(-literal)) {
					return null;
				} else {
					literalSet.add(literal);
				}
			}
		}

		return getClauseFromSet(literalSet);
	}

	public static DirtyClause createClause(int[] newLiterals, int curFeature, int[] helper) {
		int uniqueVarCount = newLiterals.length;
		for (int i = 0; i < newLiterals.length; i++) {
			final int l = newLiterals[i];
			final int index = Math.abs(l);
			if (index == curFeature) {
				newLiterals[i] = 0;
				uniqueVarCount--;
			} else {
				final int h = helper[index];
				if (h == 0) {
					helper[index] = l;
				} else {
					if (h != l) {
						for (int j = 0; j < i; j++) {
							helper[Math.abs(newLiterals[j])] = 0;
						}
						return null;
					} else {
						newLiterals[i] = 0;
						uniqueVarCount--;
					}
				}
			}
		}
		final int[] uniqueVarArray = new int[uniqueVarCount];
		int k = 0;
		for (int i = 0; i < newLiterals.length; i++) {
			final int l = newLiterals[i];
			helper[Math.abs(l)] = 0;
			if (l != 0) {
				uniqueVarArray[k++] = l;
			}
		}

		return new DirtyClause(uniqueVarArray);
	}

	public static DirtyClause createClause(int[] newLiterals) {
		final HashSet<Integer> literalSet = new HashSet<>(newLiterals.length << 1);

		for (final int literal : newLiterals) {
			if (literalSet.contains(-literal)) {
				return null;
			} else {
				literalSet.add(literal);
			}
		}

		return getClauseFromSet(literalSet);
	}

	private static DirtyClause getClauseFromSet(final HashSet<Integer> literalSet) {
		final int[] newLiterals = new int[literalSet.size()];
		int i = 0;
		for (final int lit : literalSet) {
			newLiterals[i++] = lit;
		}
		return new DirtyClause(newLiterals);
	}

	public DirtyClause(int[] literals) {
		super(literals);
		relevance = 0;
	}

	public DirtyClause(DirtyClause clause) {
		super(clause);
		relevance = clause.relevance;
	}

	boolean computeRelevance(DirtyFeature[] map) {
		for (final int literal : literals) {
			final DirtyFeature df = map[Math.abs(literal)];
			if (df != null) {
				relevance++;
				if (literal > 0) {
					df.incPositive();
				} else {
					df.incNegative();
				}
			}
		}
		return ((relevance > 0) && (relevance < literals.length));
	}

	public boolean delete(DirtyFeature[] map) {
		if (literals.length > 1) {
			final boolean mixed = ((relevance > 0) && (relevance < literals.length));
			for (final int literal : literals) {
				final DirtyFeature df = map[Math.abs(literal)];
				if (df != null) {
					if (literal > 0) {
						df.decPositive();
					} else {
						df.decNegative();
					}
					if (mixed) {
						df.decMixed();
					}
				}
			}
			return mixed;
		} else {
			for (final int literal : literals) {
				final DirtyFeature df = map[Math.abs(literal)];
				if (df != null) {
					if (literal > 0) {
						df.decPositive();
					} else {
						df.decNegative();
					}
				}
			}
		}
		return false;
	}

	public int getRelevance() {
		return relevance;
	}

	@Override
	public DirtyClause clone() {
		return new DirtyClause(this);
	}

}
