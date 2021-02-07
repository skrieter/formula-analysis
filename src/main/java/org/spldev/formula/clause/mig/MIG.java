package org.spldev.formula.clause.mig;

import java.util.*;

import org.spldev.formula.clause.*;
import org.spldev.formula.clause.mig.Vertex.*;
import org.spldev.formula.clause.mig.visitor.*;
import org.spldev.formula.clause.solver.*;

/**
 * Adjacency list implementation for a feature graph.
 *
 * @author Sebastian Krieter
 */
public class MIG {

	public static int getVertexIndex(int literal) {
		return literal < 0
			? (-(literal + 1)) << 1
			: (((literal - 1) << 1) + 1);
	}

	public static int getVertexIndex(Vertex vertex) {
		return getVertexIndex(vertex.getVar());
	}

	private final List<Vertex> adjList;
	private final CNF cnf;

	public MIG(CNF cnf) {
		this.cnf = cnf;
		final int numVariables = cnf.getVariableMap().size();
		adjList = new ArrayList<>(numVariables << 1);
		for (int i = 0; i < numVariables; i++) {
			addVertex();
		}
	}

	private void addVertex() {
		final int nextID = size() + 1;
		adjList.add(new Vertex(-nextID));
		adjList.add(new Vertex(nextID));
	}

	public void copyValues(MIG other) {
		adjList.addAll(other.adjList);
	}

	public Traverser traverse() {
		return new Traverser(this);
	}

	public Vertex getVertex(int literal) {
		return adjList.get(getVertexIndex(literal));
	}

	public List<Vertex> getVertices() {
		return Collections.unmodifiableList(adjList);
	}

	public int size() {
		return adjList.size() >> 1;
	}

	public CNF getCnf() {
		return cnf;
	}

	public void addClause(LiteralList clause) {
		final int[] literals = clause.getLiterals();
		switch (clause.size()) {
		case 0:
			throw new RuntimeContradictionException();
		case 1: {
			final int literal = literals[0];
			final Vertex vertex = getVertex(literal);
			final Vertex complementVertex = getVertex(-literal);
			if (literal > 0) {
				vertex.setStatus(Status.Core);
				complementVertex.setStatus(Status.Dead);
			} else if (literal < 0) {
				vertex.setStatus(Status.Dead);
				complementVertex.setStatus(Status.Core);
			} else {
				throw new RuntimeContradictionException();
			}
			break;
		}
		case 2: {
			final Vertex vertex1 = getVertex(literals[0]);
			final Vertex vertex2 = getVertex(literals[1]);
			final Vertex complementVertex1 = getVertex(-literals[0]);
			final Vertex complementVertex2 = getVertex(-literals[1]);
			complementVertex1.addStronglyConnected(vertex2);
			complementVertex2.addStronglyConnected(vertex1);
			break;
		}
		default: {
			for (final int literal1 : literals) {
				getVertex(-literal1).addWeaklyConnected(clause);
			}
			break;
		}
		}
	}

	public void removeClause(LiteralList clause) {
		final int[] literals = clause.getLiterals();
		switch (clause.size()) {
		case 0:
			throw new RuntimeContradictionException();
		case 1: {
			break;
		}
		case 2: {
			getVertex(-literals[0]).getStrongEdges().remove(getVertex(literals[1]));
			getVertex(-literals[1]).getStrongEdges().remove(getVertex(literals[0]));
			break;
		}
		default: {
			for (final int literal : literals) {
				final Vertex vertex = getVertex(-literal);
				// TODO increase performance
				vertex.getComplexClauses().remove(clause);
			}
			break;
		}
		}
	}

}
