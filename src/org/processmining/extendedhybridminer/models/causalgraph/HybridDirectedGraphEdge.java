package org.processmining.extendedhybridminer.models.causalgraph;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import org.processmining.extendedhybridminer.models.hybridpetrinet.PartialPlaceEvaluation;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.AttributeMap.ArrowType;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphEdge;

/**
 * Created by demas on 27/07/16.
 */
public class HybridDirectedGraphEdge extends AbstractDirectedGraphEdge<HybridDirectedGraphNode, HybridDirectedGraphNode> {

	// Update by sabi (along with the respective setters and getters that are below)
	private double directSuccession;
	private double ODSD, IDSD;
	private double directSuccessionDependency;
	//private double roundedDSD;
	private double abDependencyMetric;
	//private double roundedABDM;
	private double causalityMetric;
	//private double roundedCM;
	private Set<PartialPlaceEvaluation<?>> candidatePlaces;
		
   public HybridDirectedGraphEdge(HybridDirectedGraphNode source, HybridDirectedGraphNode target) {
        super(source, target);
		getAttributeMap().put(AttributeMap.EDGEEND, ArrowType.ARROWTYPE_TECHNICAL);
		getAttributeMap().put(AttributeMap.EDGEENDFILLED, true);
		this.candidatePlaces = new HashSet<PartialPlaceEvaluation<?>>();
    }
   
   
    /**
     * SETTERS
     */
     public void setDirectSuccession(double directSuccession)
     {
      	this.directSuccession=directSuccession;
      	//this.roundedDSD = Math.round(directSuccession * 100.0)/100.0;
     }
        
     public void setIDSD_ODSD(double ODSD,double IDSD)
     {
    	 this.ODSD=ODSD;
         this.IDSD=IDSD;
     }
        
     public void setDirectSuccessionDependency(double directSuccessionDependency)
     {
    	 this.directSuccessionDependency=directSuccessionDependency;
     }
        
     public void setAbDependencyMetric(double abDependencyMetric)
     {
    	 this.abDependencyMetric=abDependencyMetric;
    	 //this.roundedABDM = Math.round(abDependencyMetric * 100.0)/100.0;
     }
        
     public void setCausalityMetric(double causalityMetric)
     {
    	 this.causalityMetric=causalityMetric;
    	 //this.roundedCM = Math.round(causalityMetric * 100.0)/100.0;
     }
        
     public void setEdgeColor(Color color)
     {
    	 getAttributeMap().put(AttributeMap.EDGECOLOR,color);
     }
     
     /**
      * GETTERS
      */
      public double getDirectSuccession()
      {
    	  return this.directSuccession;
      }
        
      public double getODSD()
      {
    	  return this.ODSD;
      }
        
      public double getIDSD()
      {
    	  return this.IDSD;
      }
       
      public double getDirectSuccessionDependency()
      {
    	  return this.directSuccessionDependency;
      }
        
      public double getAbDependencyMetric()
      {
    	  return this.abDependencyMetric;
      }

      public double getCausalityMetric()
      {
    	  return this.causalityMetric;
      }
      
      
      public String toString() {
      	return this.getSource()+" -> "+this.getTarget();
      }      
      
      @Override
      public boolean equals(Object e) {
    	if (e == null) {
    		return false;
    	}
    	if (!(e instanceof HybridDirectedGraphEdge)) {
    		return false;
    	}
    	HybridDirectedGraphEdge ee = (HybridDirectedGraphEdge) e;
    	return (ee.getSource().equals(this.getSource()) && ee.getTarget().equals(this.getTarget()));
      }

	public void addPlace(PartialPlaceEvaluation<?> singleton) {
		this.candidatePlaces.add(singleton);
	}
	
	public Set<PartialPlaceEvaluation<?>> getPlaces() {
		return this.candidatePlaces;
	}
	
	public void setPlacesToConflicting() {
		for (PartialPlaceEvaluation<?> p: this.candidatePlaces) {
			p.setConfliting(true);
		}
	}
	
}