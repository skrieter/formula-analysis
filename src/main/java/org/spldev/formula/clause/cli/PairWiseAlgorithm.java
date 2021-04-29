package org.spldev.formula.clause.cli;

import org.spldev.formula.clause.configuration.*;

/**
 * Generates configurations for a given propositional formula such that two-wise
 * feature coverage is achieved.
 *
 * @author Sebastian Krieter
 */
public class PairWiseAlgorithm extends AConfigurationGeneratorAlgorithm<PairWiseConfigurationGenerator> {

	@Override
	protected PairWiseConfigurationGenerator createConfigurationGenerator() {
		return new PairWiseConfigurationGenerator();
	}
	
	@Override
	public String getName() {
		return "incling";
	}

}
