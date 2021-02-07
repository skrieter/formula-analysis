package org.spldev.formula.clause;

import java.io.*;
import java.util.*;

import org.spldev.formula.*;

// TODO rename, as it can represent both CNF and DNF
/**
 * Represents an instance of a satisfiability problem in CNF.
 *
 * @author Sebastian Krieter
 */
public class CNF implements Serializable {

	private static final long serialVersionUID = -7716526687669886274L;

	protected ClauseList clauses;
	protected VariableMap variables;

	public CNF(VariableMap mapping, ClauseList clauses) {
		variables = mapping;
		this.clauses = clauses;
	}

	public CNF(VariableMap mapping, List<LiteralList> clauses) {
		variables = mapping;
		this.clauses = new ClauseList(clauses);
	}

	public CNF(VariableMap mapping) {
		variables = mapping;
		clauses = new ClauseList();
	}

	public void setClauses(ClauseList clauses) {
		this.clauses = clauses;
	}

	public void addClause(LiteralList clause) {
		clauses.add(clause);
	}

	public void addClauses(Collection<LiteralList> clauses) {
		this.clauses.addAll(clauses);
	}

	public void setVariableMap(VariableMap variables) {
		this.variables = variables;
	}

	public VariableMap getVariableMap() {
		return variables;
	}

	public ClauseList getClauses() {
		return clauses;
	}

	@Override
	public int hashCode() {
		return Objects.hash(variables, clauses);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}
		final CNF other = (CNF) obj;
		return Objects.equals(variables, other.variables) && Objects.equals(clauses, other.clauses);
	}

	@Override
	public String toString() {
		return "CNF\n\tvariables=" + variables + "\n\tclauses=" + clauses;
	}

	/**
	 * Creates a new clause list from this CNF with all clauses adapted to a new
	 * variable mapping.
	 *
	 * @param newVariableMap the new variables
	 * @return an adapted cnf, {@code null} if there are old variables names the are
	 *         not contained in the new variables.
	 */
	public Optional<CNF> adapt(VariableMap newVariableMap) {
		return Optional.of(new CNF(newVariableMap, clauses.adapt(variables, newVariableMap)));
	}

	public CNF randomize(Random random) {
		final List<String> shuffledVariableNames = new ArrayList<>(variables.getNames());
		Collections.shuffle(shuffledVariableNames, random);
		final VariableMap newVariableMap = new VariableMap(shuffledVariableNames);

		final ClauseList adaptedClauseList = clauses.adapt(variables, newVariableMap);
		Collections.shuffle(adaptedClauseList, random);

		return new CNF(newVariableMap, adaptedClauseList);
	}

}
