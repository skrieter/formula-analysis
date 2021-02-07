package org.spldev.formula.clause.mig.io;

import java.util.*;

import org.spldev.formula.*;
import org.spldev.formula.clause.*;
import org.spldev.formula.clause.mig.*;

/**
 * Computes a textual representation of the feature relationships in a modal
 * implication graph.
 *
 * @author Sebastian Krieter
 */
public class MIGDependenciesWriter {

	public String write(final MIG mig, final VariableMap variables) {
		final StringBuilder sb = new StringBuilder();
		sb.append("X ALWAYS Y := If X is selected then Y is selected in every valid configuration.\n");
		sb.append(
			"X MAYBE  Y := If X is selected then Y is selected in at least one but not all valid configurations. \n");
		sb.append("X NEVER  Y := If X is selected then Y cannot be selected in any valid configuration.\n\n");

		final List<Vertex> adjList = mig.getVertices();
		for (final Vertex vertex : adjList) {
			if (!vertex.isCore() && !vertex.isDead()) {
				final int var = vertex.getVar();
				if (var > 0) {
					final String name = variables.getName(var).get();
					for (final Vertex otherVertex : vertex.getStrongEdges()) {
						if (!otherVertex.isCore() && !otherVertex.isDead()) {
							sb.append(name);
							if (otherVertex.getVar() > 0) {
								sb.append(" ALWAYS ");
							} else {
								sb.append(" NEVER ");
							}
							sb.append(variables.getName(otherVertex.getVar()));
							sb.append("\n");
						}
					}
					for (final LiteralList clause : vertex.getComplexClauses()) {
						for (final int otherVar : clause.getLiterals()) {
							if ((otherVar > 0) && (var != otherVar)) {
								sb.append(name);
								sb.append(" MAYBE ");
								sb.append(variables.getName(otherVar));
								sb.append("\n");
							}
						}
					}
				}
			}
		}
		return sb.toString();
	}

}
