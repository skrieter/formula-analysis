package org.spldev.formula.clause.configuration;

import java.util.*;

/**
 * Finds random valid solutions of propositional formulas.
 *
 * @author Sebastian Krieter
 */
public abstract class ARandomConfigurationGenerator extends AConfigurationGenerator {

	protected boolean allowDuplicates = false;

	public ARandomConfigurationGenerator() {
		super();
		setRandom(new Random());
	}

	public boolean isAllowDuplicates() {
		return allowDuplicates;
	}

	public void setAllowDuplicates(boolean allowDuplicates) {
		this.allowDuplicates = allowDuplicates;
	}

}
