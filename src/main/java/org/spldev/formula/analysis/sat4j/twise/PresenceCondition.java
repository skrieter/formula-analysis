/* -----------------------------------------------------------------------------
 * Formula-Analysis Lib - Library to analyze propositional formulas.
 * Copyright (C) 2021  Sebastian Krieter
 * 
 * This file is part of Formula-Analysis Lib.
 * 
 * Formula-Analysis Lib is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * Formula-Analysis Lib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Formula-Analysis Lib.  If not, see <https://www.gnu.org/licenses/>.
 * 
 * See <https://github.com/skrieter/formula-analysis> for further information.
 * -----------------------------------------------------------------------------
 */
package org.spldev.formula.analysis.sat4j.twise;

import java.util.*;

import org.spldev.formula.clauses.*;

/**
 * Represents a presence condition as an expression.
 *
 * @author Sebastian Krieter
 */
public class PresenceCondition extends ClauseList {

	private static final long serialVersionUID = -292364320078721008L;

	private transient final TreeSet<Integer> groups = new TreeSet<>();

	public PresenceCondition() {
		super();
	}

	public PresenceCondition(ClauseList otherClauseList) {
		super(otherClauseList);
	}

	public PresenceCondition(Collection<? extends LiteralList> c) {
		super(c);
	}

	public PresenceCondition(int size) {
		super(size);
	}

	public void addGroup(int group) {
		groups.add(group);
	}

	public Set<Integer> getGroups() {
		return groups;
	}

	@Override
	public String toString() {
		return "Expression [" + super.toString() + "]";
	}

}
