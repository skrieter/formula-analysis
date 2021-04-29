package org.spldev.formula.clause.cli;

import java.util.*;

import org.spldev.formula.clause.configuration.*;
import org.spldev.util.*;
import org.spldev.util.cli.*;

/**
 * Finds certain solutions of propositional formulas.
 *
 * @author Sebastian Krieter
 */
public abstract class AConfigurationGeneratorAlgorithm<T extends AConfigurationGenerator> implements
	ConfigurationGeneratorAlgorithm {

	public Result<ConfigurationGenerator> parseArguments(List<String> args) {
		final T gen = createConfigurationGenerator();
		try {
			for (final ListIterator<String> iterator = args.listIterator(); iterator.hasNext();) {
				final String arg = iterator.next();
				if (!parseArgument(gen, arg, iterator)) {
					throw new IllegalArgumentException("Unkown argument " + arg);
				}
			}
			return Result.of(gen);
		} catch (Exception e) {
			return Result.empty(e);
		}
	}

	protected abstract T createConfigurationGenerator();

	protected boolean parseArgument(T gen, String arg, ListIterator<String> iterator)
		throws IllegalArgumentException {
		switch (arg) {
		case "-l":
			gen.setLimit(Integer.parseInt(CLI.getArgValue(iterator, arg)));
			break;
		case "-s":
			gen.setRandom(new Random(Long.parseLong(CLI.getArgValue(iterator, arg))));
			break;
		default:
			return false;
		}
		return true;
	}

	@Override
	public String getId() {
		return getClass().getCanonicalName();
	}

}
