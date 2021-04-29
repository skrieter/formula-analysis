package org.spldev.formula.clause.cli;

import org.spldev.formula.clause.configuration.*;

/**
 * Finds certain solutions of propositional formulas.
 *
 * @author Sebastian Krieter
 */
public class EnumeratingAlgorithm extends AConfigurationGeneratorAlgorithm<EnumeratingRandomConfigurationGenerator> {

	@Override
	protected EnumeratingRandomConfigurationGenerator createConfigurationGenerator() {
		return new EnumeratingRandomConfigurationGenerator();
	}
	
	@Override
	public String getName() {
		return "enum";
	}

}
