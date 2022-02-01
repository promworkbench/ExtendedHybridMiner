package org.processmining.extendedhybridminer.models.hybridpetrinet.candidateplaceiterator;

public enum CandidatePlaceIteratorEnum {
	OUTPUT_THEN_INPUT_NUMBER, //OutputThenInputOrderingIterator
	OUTPUT_THEN_INPUT_NUMBER_OPT, //NodesOrderingIteratorOptNew
	EDGE_NUMBER, //EdgesOrderingIterator
	EDGE_NUMBER_OPT, // EdgesOrderingIteratorOpt
	EDGE_NUMBER_OPT_NEW, //EdgesOrderingIteratorOptNew_FV
	EDGES_FIRST, //EdgesFirst
	MINIMAL, //MinimalEdgeOrdering
	MINIMAL_BACKWARDS //MinimalEdgeOrderingBackwards
}
