package org.processmining.extendedhybridminer.models.hybridpetrinet.candidateplaceiterator;

import java.util.HashSet;
import java.util.Set;

import org.processmining.models.graphbased.directed.AbstractDirectedGraphEdge;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;

public class EdgeCliqueNew_FV_NOTUSED <N extends AbstractDirectedGraphNode, E extends AbstractDirectedGraphEdge>{


	private Set<E> edges;
	private Set<N> ingoingNodes;
	private Set<N> outgoingNodes;
	
	public EdgeCliqueNew_FV_NOTUSED() {
		edges = new HashSet<E>();
		ingoingNodes = new HashSet<N>();
		outgoingNodes = new HashSet<N>();
	}
	
	public void addEdge(E edge){
		edges.add(edge);
		ingoingNodes.add((N) edge.getTarget());
		outgoingNodes.add((N) edge.getSource());
	}

	public Set<N> getIngoingNodes() {
		return ingoingNodes;
	}

	public Set<N> getOutgoingNodes() {
		return outgoingNodes;
	}
	
	public boolean contains(E edge){
		return edges.contains(edge);
	}
	
	public void setEdges(Set<E> edges) {
		this.edges.addAll(edges);
	}

	public void setIngoingNodes(Set<N> ingoingNodes) {
		this.ingoingNodes.addAll(ingoingNodes);
	}

	public void setOutgoingNodes(Set<N> outgoingNodes) {
		this.outgoingNodes.addAll(outgoingNodes);
	}

	
	protected EdgeCliqueNew_FV_NOTUSED clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		EdgeCliqueNew_FV_NOTUSED<N,E> edgeCliqueClone = new EdgeCliqueNew_FV_NOTUSED<N,E>();
		edgeCliqueClone.setEdges(edges);
		edgeCliqueClone.setIngoingNodes(ingoingNodes);
		edgeCliqueClone.setOutgoingNodes(outgoingNodes);
		
		return edgeCliqueClone;
	}

	

	public String toString() {
		String toString = "EDGES "+edges+"\n";
		toString=toString+"INPUT NODES "+ingoingNodes;
		toString=toString+"OUTPUT NODES"+outgoingNodes;

		return toString;
	}
}
