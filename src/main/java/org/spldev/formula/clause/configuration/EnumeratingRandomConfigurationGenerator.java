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
package org.spldev.formula.clause.configuration;

import java.util.*;

import org.spldev.formula.clause.*;
import org.spldev.util.data.*;
import org.spldev.util.job.*;
import org.spldev.util.logging.*;

/**
 * Finds certain solutions of propositional formulas.
 *
 * @author Sebastian Krieter
 */
public class EnumeratingRandomConfigurationGenerator extends RandomConfigurationGenerator {

	public static final Identifier<SolutionList> identifier = new Identifier<>();

	@Override
	protected Identifier<SolutionList> getIdentifier() {
		return identifier;
	}

	private List<LiteralList> allConfigurations;

	@Override
	protected void init(InternalMonitor monitor) {
		final AllConfigurationGenerator gen = new AllConfigurationGenerator();
		allConfigurations = Executor.run(gen::execute, solver.getCnf(), monitor)
			.map(SolutionList::getSolutions)
			.orElse(Collections::emptyList, Logger::logProblems);
		if (!allowDuplicates) {
			Collections.shuffle(allConfigurations, getRandom());
		}
	}

	@Override
	public LiteralList get() {
		if (allConfigurations.isEmpty()) {
			return null;
		}
		if (allowDuplicates) {
			return allConfigurations.get(getRandom().nextInt(allConfigurations.size()));
		} else {
			return allConfigurations.remove(allConfigurations.size());
		}
	}

}
