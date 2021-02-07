package org.spldev.formula.clause.configuration;

import java.util.*;
import java.util.concurrent.*;

import org.spldev.formula.clause.*;
import org.spldev.formula.clause.analysis.*;

/**
 * Generates certain configurations for a given propositional formulas.
 *
 * @author Sebastian Krieter
 */
public interface IConfigurationGenerator extends Analysis<List<LiteralList>> {

	LinkedBlockingQueue<LiteralList> getResultQueue();

}
