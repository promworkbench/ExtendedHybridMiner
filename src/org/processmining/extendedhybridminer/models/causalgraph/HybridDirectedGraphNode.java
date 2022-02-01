package org.processmining.extendedhybridminer.models.causalgraph;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;

/**
 * Created by demas on 27/07/16.
 */
public class HybridDirectedGraphNode extends AbstractDirectedGraphNode {

    private final ExtendedCausalGraph graph;

    public HybridDirectedGraphNode(ExtendedCausalGraph graph) {
    	super();
        this.graph = graph;
    }

    public HybridDirectedGraphNode(ExtendedCausalGraph graph, String label) {
        this.graph = graph;
		getAttributeMap().put(AttributeMap.LABEL, label);
    }
    
  
    @Override
    public ExtendedCausalGraph getGraph() {
        return this.graph;
    }

	public String getLabel() {
		return super.getLabel();
	}

	//The equals method is inherited from AbstractGraphNode, and it is based on the id of the node!
    
}
