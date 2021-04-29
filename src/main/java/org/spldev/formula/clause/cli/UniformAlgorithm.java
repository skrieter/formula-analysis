package org.spldev.formula.clause.cli;

import org.spldev.formula.clause.configuration.*;

/**
 * Finds certain solutions of propositional formulas.
 *
 * @author Sebastian Krieter
 */
public class UniformAlgorithm extends AConfigurationGeneratorAlgorithm<UniformRandomConfigurationGenerator> {

	@Override
	protected UniformRandomConfigurationGenerator createConfigurationGenerator() {
		return new UniformRandomConfigurationGenerator();
	}
	
	@Override
	public String getName() {
		return "urandom";
	}

}
