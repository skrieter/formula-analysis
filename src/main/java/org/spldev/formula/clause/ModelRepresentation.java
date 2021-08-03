package org.spldev.formula.clause;

import org.spldev.formula.expression.*;
import org.spldev.formula.expression.atomic.literal.*;
import org.spldev.util.*;
import org.spldev.util.data.*;
import org.spldev.util.logging.*;

public class ModelRepresentation {

	private final CacheHolder cache = new CacheHolder();
	private final Formula formula;
	private final VariableMap variables;

	public ModelRepresentation(Formula formula) {
		this(formula, Formulas.createVariableMapping(formula));
	}

	public ModelRepresentation(Formula formula, VariableMap variables) {
		this.formula = formula;
		this.variables = variables;
		cache.set(FormulaProvider.of(formula));
	}

	public <T> Result<T> getResult(Provider<T> provider) {
		return cache.get(provider, null);
	}

	public <T> T get(Provider<T> provider) {
		return cache.get(provider).orElse(Logger::logProblems);
	}

	public CacheHolder getCache() {
		return cache;
	}

	public Formula getFormula() {
		return formula;
	}

	public VariableMap getVariables() {
		return variables;
	}

}
