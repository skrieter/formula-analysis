package org.spldev.formula.clause.configuration;

import java.util.*;

/**
 * Finds random valid solutions of propositional formulas.
 *
 * @author Sebastian Krieter
 */
public abstract class ARandomConfigurationGenerator extends AConfigurationGenerator {

	protected boolean allowDuplicates = false;

	public ARandomConfigurationGenerator(int maxNumber) {
		super(maxNumber);
		setRandom(new Random());
	}

	public boolean isAllowDuplicates() {
		return allowDuplicates;
	}

	public void setAllowDuplicates(boolean allowDuplicates) {
		this.allowDuplicates = allowDuplicates;
	}

}
