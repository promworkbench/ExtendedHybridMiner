package org.processmining.extendedhybridminer.models.hybridpetrinet;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.processmining.extendedhybridminer.models.causalgraph.HybridDirectedGraphEdge;
import org.processmining.extendedhybridminer.models.causalgraph.HybridDirectedSureGraphEdge;
import org.processmining.extendedhybridminer.models.causalgraph.HybridDirectedUncertainGraphEdge;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetImpl;
import org.processmining.models.semantics.petrinet.Marking;

/**
 * Created by demas on 27/07/16.
 */
public class ExtendedHybridPetrinet extends PetrinetImpl {
    protected Map<String, Transition> labelTransitionsMap;
    protected Color surePlaceColor;
    protected Color sureColor;
    protected Color unsureColor;
    protected Color LDColor;
	public Marking initialMarking;
	public Collection<Marking> finalMarkings;
	

    public ExtendedHybridPetrinet(String label) {
        super(label);
        this.labelTransitionsMap = new HashMap<>();
        this.initialMarking = new Marking();
		this.finalMarkings = new ArrayList<Marking>();
    }
     
    public void setColors() {
    	this.sureColor = Color.BLUE.darker();
    	this.unsureColor = Color.RED.darker();
    	this.surePlaceColor = Color.GREEN.darker().darker();
    	this.LDColor = Color.ORANGE;
    }
    
    public void setColors(Color c1, Color c2, Color c3) {
    	this.sureColor = c1;
    	this.unsureColor = c2;
    	this.LDColor = c3;
    	if (smallRange(c1, c2)) {
    		this.surePlaceColor = c1;
    	} else {
    		if (isGreen(c1) || isGreen(c2) || isGreen(c3)) {
    			if (isBlack(c1) || isBlack(c2) || isBlack(c3)) {
    				if (isBlue(c1) || isBlue(c2) || isBlue(c3)) {
    					this.surePlaceColor = Color.RED.darker();
    				} else {
    					this.surePlaceColor = Color.BLUE.darker();
    				}
    			} else {
    				this.surePlaceColor = Color.BLACK.darker();
    			}
    		} else {
    			this.surePlaceColor = Color.GREEN.darker().darker();
    		}
    	}
    }
    
    
    public void setColors(Color c1, Color c2, Color c3, Color c4) {
    	this.surePlaceColor = c1;
    	this.sureColor = c2;
    	this.unsureColor = c3;
    	this.LDColor = c4;
    }
    
    
    private boolean smallRange(Color c1, Color c2) {
    	float[] hsv1 = Color.RGBtoHSB(c1.getRed(), c1.getGreen(), c1.getBlue(), null);
    	float hue1 = hsv1[0];
    	float saturation1 = hsv1[1];
        float britness1 = hsv1[2];
         
        float[] hsv2 = Color.RGBtoHSB(c2.getRed(), c2.getGreen(), c2.getBlue(), null);
    	float hue2 = hsv2[0];
    	float saturation2 = hsv2[1];
        float britness2 = hsv2[2];
        
        if (Math.abs(saturation1 - saturation2) < 0.2f && Math.abs(britness1 - britness2) < 0.2f && Math.abs(hue1 - hue2) < 0.1f) {
      	  return true;
        } else {
        	return false;
        }
        
	}

	private boolean isGreen(Color c) {
      float[] hsv = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
      float saturation = hsv[1];
      float britness = hsv[2];
      if (saturation < 0.2f || britness < 0.2f) {
    	  return false;
      }
      float hue = hsv[0];
      if (hue > 0.222222f && hue < 0.43f){
        return true;
      } 
      return false;
    }
	
	private boolean isBlue(Color c) {
	      float[] hsv = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
	      float saturation = hsv[1];
	      float britness = hsv[2];
	      if (saturation < 0.2f || britness < 0.2f) {
	    	  return false;
	      }
	      float hue = hsv[0];
	      if (hue > 0.45f && hue < 0.73f){
	        return true;
	      } 
	      return false;
	    }
    
    
    private boolean isBlack(Color c) {
      float[] hsv = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
      float britness = hsv[2];
      if (britness < 0.2f) {
    	  return true;
      } 
      return false;
    }
    
    
    public synchronized Edge addTransitionsArcFromFCGEdge(HybridDirectedGraphEdge edge) {
        Transition source = this.addTransition(edge.getSource().getLabel());
        Transition target = this.addTransition(edge.getTarget().getLabel());

        if (edge instanceof HybridDirectedSureGraphEdge)
            return this.addSureTransitionsArcPrivate(source, target, 1);
        else if (edge instanceof HybridDirectedUncertainGraphEdge)
            return this.addUncertainTransitionsArcPrivate(source, target, 1);
        else
        	return this.addLongDepTransitionsArcPrivate(source, target, 1);
    }

    public synchronized Edge addTransitionsArcFromFCGEdge(HybridDirectedGraphEdge edge, int weight) {
        Transition source = this.addTransition(edge.getSource().getLabel());
        Transition target = this.addTransition(edge.getTarget().getLabel());

        if (edge instanceof HybridDirectedSureGraphEdge)
            return this.addSureTransitionsArcPrivate(source, target, weight);
        else
            return this.addUncertainTransitionsArcPrivate(source, target, weight);
    }

    /**
     * The semantics o the method is the following. It adds a SureTransitionsArc between transition source and target if there is no
     * place already connecting source and target. This because when building the fuzzyNet, we first add all the places and respective
     * transitions, and then add the (sure and uncertain) arcs connecting two transitions. Things are complicated by the presence of
     * the weight, which is not taken into consideration in this first version (all arcs have weight=1). However, to be on the safe side,
     * the method checks if there already is a sureArc connecting the same source and transition but with a different weight, and if
     * this is the case, it sets the new weight as the sum of the two.
     * @param source the source of the sureTransitionArc to be added
     * @param target the target of the sureTransitionArc to be added
     * @param weight the weight of the arc
     * @return the SureTransitionArc if not present or if already present with a different weight. Null if there is already a place
     * connecting source and target.
     */
    public synchronized SureEdge addSureTransitionsArcPrivate(Transition source, Transition target, int weight) {
        synchronized (arcs) {
            // Following check just makes sure that source and target already exist in the net
            checkAddEdge(source, target);

            // Check if there is already a place connecting source and target. If there is, return null
            boolean placesAlreadyAdded = this.checkIfPlacesArePresent(source, target);
            if (!placesAlreadyAdded) {
                SureEdge a = new SureEdge(source, target, weight);

                if (arcs.add(a)) {
                    graphElementAdded(a);
                    return a;
                } else {
                    for (Arc existing : arcs) {
                        if (existing.equals(a)) {
                            existing.setWeight(existing.getWeight() + weight);
                            return (SureEdge) existing;
                        }
                    }
                }
                assert (false);
                return null;
            }
            return null;
        }
    }
    
    public synchronized LongDepEdge addLongDepTransitionsArcPrivate(Transition source, Transition target, int weight) {
        synchronized (arcs) {
            // Following check just makes sure that source and target already exist in the net
            checkAddEdge(source, target);

            // Check if there is already a place connecting source and target. If there is, return null
            boolean placesAlreadyAdded = this.checkIfPlacesArePresent(source, target);
            if (!placesAlreadyAdded) {
                LongDepEdge a = new LongDepEdge(source, target, weight);

                if (arcs.add(a)) {
                    graphElementAdded(a);
                    return a;
                } else {
                    for (Arc existing : arcs) {
                        if (existing.equals(a)) {
                            existing.setWeight(existing.getWeight() + weight);
                            return (LongDepEdge) existing;
                        }
                    }
                }
                assert (false);
                return null;
            }
            return null;
        }
    }

    /**
     * The semantics o the method is the following. It adds a UncertainTransitionsArc between transition source and target if there is no
     * place already connecting source and target. This because when building the fuzzyNet, we first add all the places and respective
     * transitions, and then add the (sure and uncertain) arcs connecting two transitions. Things are complicated by the presence of
     * the weight, which is not taken into consideration in this first version (all arcs have weight=1). However, to be on the safe side,
     * the method checks if there already is a sureArc connecting the same source and transition but with a different weight, and if
     * this is the case, it sets the new weight as the sum of the two.
     * @param source the source of the uncertainTransitionArc to be added
     * @param target the target of the uncertainTransitionArc to be added
     * @param weight the weight of the arc
     * @return the uncertainTransitionArc if not present or if already present with a different weight. Null if there is already a place
     * connecting source and target.
     */
    private synchronized UncertainEdge addUncertainTransitionsArcPrivate(Transition source, Transition target, int weight) {
        synchronized (arcs) {
            // Following check just makes sure that source and target already exist in the net
            checkAddEdge(source, target);

            // Check if there is already a place connecting source and target. If there is, return null
            boolean placesAlreadyAdded = this.checkIfPlacesArePresent(source, target);
            if (!placesAlreadyAdded) {
                UncertainEdge a = new UncertainEdge(source, target, weight);
                if (arcs.add(a)) {
                    graphElementAdded(a);
                    return a;
                } else {
                    for (Arc existing : arcs) {
                        if (existing.equals(a)) {
                            existing.setWeight(existing.getWeight() + weight);
                            return (UncertainEdge) existing;
                        }
                    }
                }
                assert (false);
                return null;
            }
            return null;
        }
    }


    /*
    Given a source node s, it returns all nodes t such that there exists an edge (s, t)
     */
    public synchronized Set<PetrinetNode> getOutputNodes(PetrinetNode source) {
        Set<PetrinetNode> result = new HashSet<>();
        Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> outgoingEdgesFromSource = this.getOutEdges(source);
        for (PetrinetEdge edge : outgoingEdgesFromSource) {
            result.add((PetrinetNode) edge.getTarget());
        }
        return result;
    }

    /*
    Given a target node t, it returns all nodes s such that there exists an edge (t, s)
     */
    public synchronized Set<PetrinetNode> getInputNodes(PetrinetNode target) {
        Set<PetrinetNode> result = new HashSet<>();
        Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> ingoingEdgesToTarget = this.getInEdges(target);
        for (PetrinetEdge edge : ingoingEdgesToTarget) {
            result.add((PetrinetNode) edge.getSource());
        }
        return result;
    }


    private synchronized boolean checkIfPlacesArePresent(Transition source, Transition target) {
         
    	Set<PetrinetNode> sourceOutputNodes = this.getOutputNodes(source);
        Set<PetrinetNode> targetInputNodes = this.getInputNodes(target);
        sourceOutputNodes.retainAll(targetInputNodes);
        for (PetrinetNode node : sourceOutputNodes) {
            if (node instanceof Place)
                //result.add((Place) node);
            	return true;
        }
        return false;
    }


    /**
     * We build fuzzyPetrinets with a heavy restriction: we do not ever have two transition with the same label. For
     * such a reason, when adding a new transition, we have to check if a transition with the same label is
     * already present.
     * @param label the name of the transition
     * @return a new Transition if a transition with the same label DO NOT already exists, otherwise returns
     * the already present transition.
     */
    public synchronized Transition addTransition(String label) {
        Transition alreadyPresent = this.labelTransitionsMap.get(label);
        if (alreadyPresent == null) {
            Transition newTransition = super.addTransition(label);
            this.labelTransitionsMap.put(label, newTransition);
            return newTransition;
        }
        else {
            return alreadyPresent;
        }
    }


    /*public synchronized <N extends AbstractDirectedGraphNode> void addPlaceFromPlaceEvaluation(PlaceEvaluation<N> placeEval, Set<String> selfLoop, Set<String> optionalSelfLoop) {
        // todo: name of the places?
        Place p = this.addPlace(placeEval.toString());
        for (N node : placeEval.getPlaceOutputNodes()) {
            Transition t = this.addTransition(node.getLabel());
            this.addArc(t, p);
        }
        for (N node: placeEval.getPlaceInputNodes()) {
            Transition t = this.addTransition(node.getLabel());
            this.addArc(p, t);
        }
    }*/
    
    /*
     * Humam added this (to replace addPlaceFromPlaceEvaluation)
     */
	public synchronized <N extends AbstractDirectedGraphNode> void addPlaceFromPartialPlaceEvaluation(PartialPlaceEvaluation<N> placeEval) {
		Place p = this.addPlace(placeEval.toString());
		for (N node : placeEval.getPlaceOutputNodes()) {
        	String label = node.getLabel();
        	Transition t = this.addTransition(label);
            this.addArc(t, p);     
        }
        for (N node: placeEval.getPlaceInputNodes()) {
        	String label = node.getLabel();
        	Transition t = this.addTransition(label);
            this.addArc(p, t);  
        }
	}

    /**
     *Returns a place named label
     * @param label
     * @return
     */
    public synchronized Place getPlace(String label){
    	Place place = null;
    	Collection<Place> places = super.getPlaces();
    	for (Place itPlace : places) {
			if (itPlace.getLabel().equals(label))
				place = itPlace; 
		}
    	return place;
    }

    /**
     * Returns a transition named label
     * @param label
     * @return
     */
    public synchronized Transition getTransition(String label){
    	Transition transition = null;
    	Collection<Transition> transitions = super.getTransitions();
    	for (Transition itTransition : transitions) {
			if (itTransition.getLabel().equals(label))
				transition = itTransition; 
		}
    	return transition;
    }
    
    /** CHIARA
     * Returns the set of sure arcs
     * @return set of sure arcs
     */
    public synchronized Collection<SureEdge> getSureArcs(){
    	Set<SureEdge> directedSureGraphEdges = new HashSet<SureEdge>();
    	for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : getEdges()) {
            if (edge instanceof SureEdge)
            	directedSureGraphEdges.add((SureEdge) edge);
		}
    	return directedSureGraphEdges;
    }
    
    /** CHIARA
     * Returns the set of uncertain arcs
     * @return set of uncertain arcs
     */
    public synchronized Collection<UncertainEdge> getUncertainArcs(){
    	Set<UncertainEdge> directedUncertainGraphEdges = new HashSet<UncertainEdge>();
    	for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : getEdges()) {
            if (edge instanceof UncertainEdge)
            	directedUncertainGraphEdges.add((UncertainEdge) edge);
		}
    	return directedUncertainGraphEdges;
    }
    
    /*
    In the following the exact rewriting of methods of AbstractResetInhibitorNet
    JUST TO HAVE RIGHT IN THIS CLASS THE METHOD USEFUL FOR US (as in the superclass there are a plethora of methods)
    not useful.
     */
    public synchronized Place addPlace(String label) {
        return super.addPlace(label);
    }

    public synchronized Arc addArc(Place p, Transition t) {
        return super.addArc(p, t);
    }
    
    public synchronized Arc addSureArc(Transition s, Transition t) {
        return addSureTransitionsArcPrivate(s, t, 1);
    }
    
    public synchronized Arc addLongDepArc(Transition s, Transition t) {
        return addLongDepTransitionsArcPrivate(s, t, 1);
    }
    
    public synchronized Arc addUnsureArc(Transition s, Transition t) {
        return addUncertainTransitionsArcPrivate(s, t, 1);
    }

    public synchronized Arc addArc(Transition t, Place p) {
        return super.addArc(t, p);
    }

    @Override
    public String toString() {
    	String fuzzyPetriNetString = "*** FUZZY PETRI NET "+this.getLabel()+" *** \n";
    	for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : getEdges()) {
    		if (edge instanceof SureEdge)
    			fuzzyPetriNetString+=edge.getSource()+" ->- "+edge.getTarget()+"\n";
            if (edge instanceof UncertainEdge)
    			fuzzyPetriNetString+=edge.getSource()+" ->-? "+edge.getTarget()+"\n";
            else
                fuzzyPetriNetString+=edge.getSource()+" -> "+edge.getTarget()+"\n";
		}
    	return fuzzyPetriNetString;
    }

    
    
	/*
	 * Added by Humam
	 */
	public ExtendedHybridPetrinet cloneToPN() {
		ExtendedHybridPetrinet newPN = new ExtendedHybridPetrinet("copy of + " + this.getLabel());
		for (Transition t: this.getTransitions()) {
			String label = t.getLabel();
			newPN.addTransition(label);
		}
		for (Place p: this.getPlaces()) {
			String label = p.getLabel();
			newPN.addPlace(label);
		}
		for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> a: this.getEdges()) {
			PetrinetNode s = a.getSource();
			PetrinetNode t = a.getTarget();
			if (s instanceof Place && t instanceof Transition) {
				newPN.addArc (newPN.getPlace(s.getLabel()), newPN.getTransition(t.getLabel()));
			} else if (s instanceof Transition && t instanceof Place) {
				newPN.addArc(newPN.getTransition(s.getLabel()), newPN.getPlace(t.getLabel()));
			}

		}

		return newPN;
	}

	public Color getSurePlaceColor() {
		return this.surePlaceColor;
	}

	public Color getSureColor() {
		return this.sureColor;
	}

	public Color getUnsureColor() {
		return this.unsureColor;
	}

	public void updateSurePlaceColor(Color c) {
		this.surePlaceColor = c;
	}

	public void updateSureColor(Color c) {
	    this.sureColor = c;	
	}

	public void updateUnsureColor(Color c) {
		this.unsureColor = c;
	}

	public Marking setInitialMarking() {
		this.initialMarking = new Marking();
		this.initialMarking.add(getPlace("start"));
		return this.initialMarking;		
	}

	public Collection<Marking> setFinalMarkings() {
		Marking finalMarking = new Marking();
		finalMarking.add(getPlace("end"));
		this.finalMarkings = new ArrayList<Marking>();
		this.finalMarkings.add(finalMarking);
		return this.finalMarkings;
	}
	
	

	
	public void init() {
		//this.net = net;
		initialMarking = new Marking();
		finalMarkings = new HashSet<Marking>();
		for (Place place : getPlaces()) {
			if (getInEdges(place).isEmpty()) {
				initialMarking.add(place);
			}
			if (getOutEdges(place).isEmpty()) {
				Marking finalMarking = new Marking();
				finalMarking.add(place);
				finalMarkings.add(finalMarking);
			}
		}
		if (finalMarkings.isEmpty()) {
			finalMarkings.add(new Marking());
		}
	}
	
	public Color getLDColor() {
		return this.LDColor;
	}
	
	public void updateLDColor(Color c) {
		this.LDColor = c;
	}

}
