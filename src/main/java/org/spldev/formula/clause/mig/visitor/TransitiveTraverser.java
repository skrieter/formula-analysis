package org.spldev.formula.clause.mig.visitor;

import java.util.*;
import java.util.Map.*;

import org.sat4j.core.*;
import org.sat4j.specs.*;
import org.spldev.formula.clause.*;
import org.spldev.formula.clause.mig.*;
import org.spldev.formula.clause.mig.visitor.Visitor.*;

public class TransitiveTraverser extends ATraverser {

	public TransitiveTraverser(MIG mig) {
		super(mig);
	}

	@Override
	public void traverse(int... curLiterals) {
		final HashMap<LiteralList, VecInt> complexClauseMap = new HashMap<>();
		Arrays.fill(dfsMark, false);

		traverseStrong(complexClauseMap, curLiterals);
		mainLoop: while (true) {
			for (final Iterator<Entry<LiteralList, VecInt>> entryIterator = complexClauseMap.entrySet()
				.iterator(); entryIterator.hasNext();) {
				final Entry<LiteralList, VecInt> entry = entryIterator.next();
				final VecInt v = entry.getValue();
				if (v != null) {
					for (final IteratorInt iterator = v.iterator(); iterator.hasNext();) {
						final int literal = iterator.next();
						if (currentConfiguration[Math.abs(literal) - 1] == 0) {
							final int vertexIndex = MIG.getVertexIndex(literal);
							if (!dfsMark[vertexIndex]) {
								dfsMark[vertexIndex] = true;
								final Vertex vertex = mig.getVertex(literal);
								boolean changed = false;
								final VisitResult visitWeakResult = visitor.visitWeak(literal);
								switch (visitWeakResult) {
								case Cancel:
									return;
								case Continue:
									changed |= addComplexClauses(complexClauseMap, vertex) > 0;
									break;
								case Select:
									changed |= attemptStrongSelect(literal, complexClauseMap);
									break;
								case Skip:
									break;
								default:
									throw new AssertionError(visitWeakResult);
								}
								changed |= processComplexClauses(complexClauseMap);
								if (changed) {
									continue mainLoop;
								}
							}
						}
					}
				}
			}
			break;
		}
	}

	@Override
	public void traverseStrong(int... curLiterals) {
		traverseStrong(new HashMap<>(), curLiterals);
	}

	private void traverseStrong(final HashMap<LiteralList, VecInt> complexClauseMap, int... curLiterals) {
		boolean changed = false;
		for (final int curLiteral : curLiterals) {
			changed |= attemptStrongSelect(curLiteral, complexClauseMap);
		}
		if (changed) {
			processComplexClauses(complexClauseMap);
		}
	}

	private boolean processComplexClauses(final HashMap<LiteralList, VecInt> complexClauseMap) {
		boolean changedInLoop, changed = false;
		do {
			changedInLoop = false;
			final List<VecInt> unitClauses = new LinkedList<>();
			for (final Entry<LiteralList, VecInt> entry : complexClauseMap.entrySet()) {
				final VecInt v = entry.getValue();
				if (v != null) {
					for (int j = v.size() - 1; j >= 0; j--) {
						final int literal = v.get(j);
						final int value = currentConfiguration[Math.abs(literal) - 1];
						if (value != 0) {
							if (value == literal) {
								entry.setValue(null);
							} else {
								v.delete(j);
							}
							changed = true;
						}
					}

					if (v.size() == 1) {
						entry.setValue(null);
						unitClauses.add(v);
					}
				}
			}

			for (final VecInt v : unitClauses) {
				changedInLoop |= attemptStrongSelect(v.get(0), complexClauseMap);
			}
			changed |= changedInLoop;
		} while (changedInLoop);
		return changed;
	}

	private boolean attemptStrongSelect(final int curLiteral, final HashMap<LiteralList, VecInt> complexClauseMap) {
		final int modelIndex = Math.abs(curLiteral) - 1;
		if (currentConfiguration[modelIndex] == 0) {
			currentConfiguration[modelIndex] = curLiteral;

			final VisitResult visitStrongResult = visitor.visitStrong(curLiteral);
			switch (visitStrongResult) {
			case Cancel:
				// TODO
				return false;
			case Skip:
				return false;
			case Select:
			case Continue:
				break;
			default:
				throw new AssertionError(visitStrongResult);
			}
			final Vertex curVertex = mig.getVertex(curLiteral);
			if (complexClauseMap != null) {
				addComplexClauses(complexClauseMap, curVertex);
			}
			for (final Vertex strongEdge : curVertex.getStrongEdges()) {
				attemptStrongSelect(strongEdge.getVar(), complexClauseMap);
			}
			if (complexClauseMap != null) {
				return false;
			}
			return true;
		}
		return false;
	}

	private int addComplexClauses(final HashMap<LiteralList, VecInt> complexClauseMap, final Vertex vertex) {
		int added = 0;
		final List<LiteralList> complexClauses = vertex.getComplexClauses();
		for (final LiteralList clause : complexClauses) {
			if (!complexClauseMap.containsKey(clause)) {
				complexClauseMap.putIfAbsent(clause, new VecInt(Arrays.copyOf(clause.getLiterals(), clause.size())));
				added++;
			}
		}
		return added;
	}

}
