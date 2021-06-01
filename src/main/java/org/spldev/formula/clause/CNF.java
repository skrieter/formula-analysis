/* -----------------------------------------------------------------------------
 * Formula-Analysis-Lib - Library to analyze propositional formulas.
 * Copyright (C) 2021  Sebastian Krieter
 * 
 * This file is part of Formula-Analysis-Lib.
 * 
 * Formula-Analysis-Lib is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * Formula-Analysis-Lib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Formula-Analysis-Lib.  If not, see <https://www.gnu.org/licenses/>.
 * 
 * See <https://github.com/skrieter/formula> for further information.
 * -----------------------------------------------------------------------------
 */
package org.spldev.formula.clause;

import java.io.*;
import java.util.*;

import org.spldev.formula.*;
import org.spldev.util.*;

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

	public VariableMap getVariables() {
		return variables;
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
	public Result<CNF> adapt(VariableMap newVariableMap) {
		return clauses.adapt(variables, newVariableMap).map(c -> new CNF(newVariableMap, c));
	}

	public CNF randomize(Random random) {
		final List<String> shuffledVariableNames = new ArrayList<>(variables.getNames());
		Collections.shuffle(shuffledVariableNames, random);
		final VariableMap newVariableMap = new VariableMap(shuffledVariableNames);

		final ClauseList adaptedClauseList = clauses.adapt(variables, newVariableMap).get();
		Collections.shuffle(adaptedClauseList, random);

		return new CNF(newVariableMap, adaptedClauseList);
	}

}
