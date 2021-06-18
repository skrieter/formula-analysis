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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.spldev.formula.clause.LiteralList;
import org.spldev.formula.clause.analysis.AbstractAnalysis;
import org.spldev.formula.clause.solver.SatSolver;
import org.spldev.util.data.Identifier;
import org.spldev.util.job.InternalMonitor;

/**
 * Finds certain solutions of propositional formulas.
 *
 * @author Sebastian Krieter
 */
public class ConfigurationSampler extends AbstractAnalysis<List<LiteralList>> {

	public static final Identifier<List<LiteralList>> identifier = new Identifier<>();

	@Override
	public Identifier<List<LiteralList>> getIdentifier() {
		return identifier;
	}

	private final ConfigurationGenerator generator;

	private int maxSampleSize = Integer.MAX_VALUE;

	public ConfigurationSampler(ConfigurationGenerator generator) {
		this.generator = generator;
	}

	public ConfigurationSampler(ConfigurationGenerator generator, int maxSampleSize) {
		this.generator = generator;
		this.maxSampleSize = maxSampleSize;
	}

	public int getLmit() {
		return maxSampleSize;
	}

	public void setLimit(int limit) {
		this.maxSampleSize = limit;
	}

	@Override
	public final List<LiteralList> analyze(SatSolver solver, InternalMonitor monitor) throws Exception {
		monitor.setTotalWork(maxSampleSize);
		generator.init(solver);
		return StreamSupport.stream(generator, false) //
				.limit(maxSampleSize) //
				.peek(c -> monitor.step()) //
				.collect(Collectors.toList());
	}

}