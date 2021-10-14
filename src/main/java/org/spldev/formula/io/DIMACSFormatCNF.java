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
package org.spldev.formula.io;

import java.util.*;

import org.spldev.formula.clauses.*;
import org.spldev.formula.expression.io.*;
import org.spldev.util.io.format.*;

/**
 * Serializes a {@link CNF} to a String in DIMACS format.
 *
 * @author Sebastian Krieter
 */
public class DIMACSFormatCNF implements Format<CNF> {

	public static final String ID = DIMACSFormatCNF.class.getCanonicalName();

	@Override
	public String serialize(CNF cnf) {
		Objects.requireNonNull(cnf);

		final StringBuilder sb = new StringBuilder();

		// Variables
		int index = 1;
		for (final String name : cnf.getVariables().getNames()) {
			sb.append(DIMACSConstants.COMMENT_START);
			sb.append(index++);
			sb.append(' ');
			sb.append(name);
			sb.append(System.lineSeparator());
		}

		// Problem
		sb.append(DIMACSConstants.PROBLEM);
		sb.append(' ');
		sb.append(DIMACSConstants.CNF);
		sb.append(' ');
		sb.append(cnf.getVariables().size());
		sb.append(' ');
		sb.append(cnf.getClauses().size());
		sb.append(System.lineSeparator());

		// Clauses
		for (final LiteralList clause : cnf.getClauses()) {
			for (final int l : clause.getLiterals()) {
				sb.append(l);
				sb.append(' ');
			}
			sb.append(DIMACSConstants.CLAUSE_END);
			sb.append(System.lineSeparator());
		}

		return sb.toString();
	}

	@Override
	public boolean supportsSerialize() {
		return true;
	}

	@Override
	public DIMACSFormatCNF getInstance() {
		return this;
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public String getName() {
		return "DIMACS";
	}

	@Override
	public String getFileExtension() {
		return "dimacs";
	}

}
