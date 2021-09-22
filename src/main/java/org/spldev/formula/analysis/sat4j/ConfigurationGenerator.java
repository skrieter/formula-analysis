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
package org.spldev.formula.analysis.sat4j;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.spldev.formula.*;
import org.spldev.formula.analysis.*;
import org.spldev.formula.clauses.*;
import org.spldev.util.job.*;

/**
 * Interface for configuration generators. Can be used as a {@link Supplier} or
 * to get a {@link Stream} or a {@link SolutionList} of configurations.
 * 
 * @author Sebastian Krieter
 */
public interface ConfigurationGenerator extends Analysis<SolutionList>, Supplier<LiteralList>,
	Spliterator<LiteralList> {

	void init(ModelRepresentation rep, InternalMonitor monitor);

	int getLimit();

	void setLimit(int limit);

	boolean isAllowDuplicates();

	void setAllowDuplicates(boolean allowDuplicates);

}
