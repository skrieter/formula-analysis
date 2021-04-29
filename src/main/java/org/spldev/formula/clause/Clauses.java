package org.spldev.formula.clause;

import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

import org.spldev.formula.*;
import org.spldev.formula.clause.transform.*;
import org.spldev.formula.expression.*;
import org.spldev.util.*;
import org.spldev.util.data.*;
import org.spldev.util.job.*;
import org.spldev.util.logging.*;

/**
 * Represents an instance of a satisfiability problem in CNF.
 *
 * @author Sebastian Krieter
 */
public final class Clauses {

	private Clauses() {
	}

	public static LiteralList getVariables(Collection<LiteralList> clauses) {
		return new LiteralList(clauses.stream().flatMapToInt(c -> Arrays.stream(c.getLiterals())).distinct().toArray());
	}

	/**
	 * Negates all clauses in the list (applies De Morgan).
	 *
	 * @param clauses collection of clauses
	 * @return A newly construct {@code ClauseList}.
	 */
	public static Stream<LiteralList> negate(Collection<LiteralList> clauses) {
		return clauses.stream().map(LiteralList::negate);
	}

	public static Result<LiteralList> adapt(LiteralList clause, VariableMap oldVariables,
		VariableMap newVariables) {
		return clause.adapt(oldVariables, newVariables);
	}

	public static int adapt(int literal, VariableMap oldVariables,
		VariableMap newVariables) {
		final String name = oldVariables.getName(Math.abs(literal)).get();
		final int index = newVariables.getIndex(name).orElse(0);
		return literal < 0 ? -index : index;
	}

	public static CNF slice(CNF cnf, Collection<String> dirtyVariableNames) {
		return Executor.run(new CNFSlicer(dirtyVariableNames, cnf.getVariableMap()), cnf).get();
	}

	public static CNF convertToCNF(Formula formula) {
		return Executor.run(new FormulaToCNF(), formula).get();
	}

	public static CNF convertToCNF(Formula formula, VariableMap variableMap) {
		final FormulaToCNF function = new FormulaToCNF();
		function.setVariableMapping(variableMap);
		return Executor.run(function, formula).get();
	}

	public static CNF convertToDNF(Formula formula) {
		final CNF cnf = Executor.run(new FormulaToCNF(), formula).get();
		return new CNF(cnf.getVariableMap(), convertNF(cnf.getClauses()));
	}

	public static CNF convertToDNF(Formula formula, VariableMap variableMap) {
		final FormulaToCNF function = new FormulaToCNF();
		function.setVariableMapping(variableMap);
		final CNF cnf = Executor.run(function, formula).get();
		return new CNF(variableMap, convertNF(cnf.getClauses()));
	}

	/**
	 * Converts CNF to DNF and vice-versa.
	 *
	 * @param clauses list of clauses
	 * @return A newly construct {@code ClauseList}.
	 */
	public static List<LiteralList> convertNF(List<LiteralList> clauses) {
		final List<LiteralList> convertedClauseList = new ArrayList<>();
		convertNF(clauses, convertedClauseList, new int[clauses.size()], 0);
		return convertedClauseList;
	}

	private static void convertNF(List<LiteralList> cnf, List<LiteralList> dnf, int[] literals, int index) {
		if (index == cnf.size()) {
			int[] newClauseLiterals = new int[literals.length];
			int count = 0;
			for (int literal : literals) {
				if (literal != 0) {
					newClauseLiterals[count++] = literal;
				}
			}
			if (count < newClauseLiterals.length) {
				dnf.add(new LiteralList(Arrays.copyOf(newClauseLiterals, count)));
			} else {
				dnf.add(new LiteralList(newClauseLiterals));
			}
		} else {
			HashSet<Integer> literalSet = new HashSet<>();
			for (int i = 0; i <= index; i++) {
				literalSet.add(literals[i]);
			}
			int redundantCount = 0;
			final int[] literals2 = cnf.get(index).getLiterals();
			for (final int literal : literals2) {
				if (!literalSet.contains(-literal)) {
					if (!literalSet.contains(literal)) {
						literals[index] = literal;
						convertNF(cnf, dnf, literals, index + 1);
					} else {
						redundantCount++;
					}
				}
			}
			literals[index] = 0;
			if (redundantCount == literals2.length) {
				convertNF(cnf, dnf, literals, index + 1);
			}
		}
	}

	public static CNF open(Path path) {
		return Provider.load(path, CNFFormatManager.getInstance()).orElse(Logger::logProblems);
	}

	public static Result<CNF> load(Path path) {
		return Provider.load(path, CNFFormatManager.getInstance());
	}

	public static Result<CNF> load(Path path, Cache cache) {
		return cache.get(CNFProvider.loader(path));
	}

	public static Cache createCache(Path path) {
		final Cache cache = new Cache();
		cache.set(CNFProvider.loader(path));
		return cache;
	}

	public static Cache createCache(CNF cnf) {
		final Cache cache = new Cache();
		cache.set(CNFProvider.of(cnf));
		return cache;
	}

}
