package org.spldev.formula.clause.cli;

import java.util.*;

import org.spldev.formula.clause.configuration.*;
import org.spldev.util.*;
import org.spldev.util.extension.*;

/**
 * Create a new instance of a {@link ConfigurationGenerator}.
 *
 * @author Sebastian Krieter
 */
public interface ConfigurationGeneratorAlgorithm extends Extension {

	Result<ConfigurationGenerator> parseArguments(List<String> args);

	String getName();
	
}
