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
package org.spldev.formula.clauses;

import java.nio.file.*;

import org.spldev.formula.expression.*;
import org.spldev.formula.expression.io.*;
import org.spldev.util.*;
import org.spldev.util.data.*;

/**
 * Abstract creator to derive an element from a {@link CacheHolder}.
 *
 * @author Sebastian Krieter
 */
@FunctionalInterface
public interface CNFProvider extends Provider<CNF> {

	Identifier<CNF> identifier = new Identifier<>();

	@Override
	default Identifier<CNF> getIdentifier() {
		return identifier;
	}

	static CNFProvider empty() {
		return (c, m) -> Result.empty();
	}

	static CNFProvider of(CNF cnf) {
		return (c, m) -> Result.of(cnf);
	}

	static CNFProvider in(CacheHolder cache) {
		return (c, m) -> cache.get(identifier);
	}

	static CNFProvider loader(Path path) {
		return (c, m) -> Provider.load(path, FormulaFormatManager.getInstance()).map(Clauses::convertToCNF);
	}

	static <T> CNFProvider fromFormula() {
		return (c, m) -> Provider.convert(c, FormulaProvider.identifier, new FormulaToCNF(), m);
	}

}
