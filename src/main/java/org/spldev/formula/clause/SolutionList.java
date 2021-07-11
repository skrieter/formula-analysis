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
import java.util.stream.*;

import org.spldev.formula.*;

/**
 * Represents an instance of a satisfiability problem in CNF.
 *
 * @author Sebastian Krieter
 */
public class SolutionList implements Serializable {

	private static final long serialVersionUID = 3882530497452645334L;

	protected final List<LiteralList> solutions;
	protected VariableMap variables;

	public SolutionList() {
		solutions = new ArrayList<>();
	}

	public SolutionList(VariableMap mapping, List<LiteralList> solutions) {
		variables = mapping;
		this.solutions = solutions;
	}

	public void addSolution(LiteralList clause) {
		solutions.add(clause);
	}

	public void addSolutions(Collection<LiteralList> clauses) {
		solutions.addAll(clauses);
	}

	public void setVariables(VariableMap variables) {
		this.variables = variables;
	}

	public VariableMap getVariables() {
		return variables;
	}

	public List<LiteralList> getSolutions() {
		return solutions;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((solutions == null) ? 0 : solutions.hashCode());
		result = (prime * result) + ((variables == null) ? 0 : variables.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}
		final SolutionList other = (SolutionList) obj;
		if (solutions == null) {
			if (other.solutions != null) {
				return false;
			}
		} else if (!solutions.equals(other.solutions)) {
			return false;
		}
		if (variables == null) {
			if (other.variables != null) {
				return false;
			}
		} else if (!variables.equals(other.variables)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "CNF\n\tvariables=" + variables + "\n\tsolutions=" + solutions;
	}

	private String literalToString(int literal) {
		final Optional<String> name = variables.getName(Math.abs(literal));
		return name.isEmpty()
			? "?"
			: (literal > 0 ? "" : "-") + name.get();
	}

	public String getSolutionsString() {
		final StringBuilder sb = new StringBuilder();
		for (final LiteralList clause : solutions) {
			sb.append("(");
			final List<String> literals = Arrays.stream(clause.literals)
				.mapToObj(this::literalToString)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
			for (final String literal : literals) {
				sb.append(literal);
				sb.append(", ");
			}
			if (!literals.isEmpty()) {
				sb.delete(sb.length() - 2, sb.length());
			}
			sb.append("), ");
		}
		if (!solutions.isEmpty()) {
			sb.delete(sb.length() - 2, sb.length());
		}
		return sb.toString();
	}

	public Stream<LiteralList> getInvalidSolutions(CNF cnf) {
		return solutions.stream() //
			.filter(s -> cnf.getClauses().stream() //
				.anyMatch(clause -> s.containsAll(clause.negate())));
	}

	public Stream<LiteralList> getValidSolutions(CNF cnf) {
		return solutions.stream() //
			.filter(s -> cnf.getClauses().stream() //
				.allMatch(clause -> s.hasDuplicates(clause)));
	}

}
