package org.processmining.extendedhybridminer.models.hybridpetrinet;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.processmining.extendedhybridminer.models.causalgraph.HybridDirectedGraphEdge;
import org.processmining.extendedhybridminer.models.causalgraph.HybridDirectedSureGraphEdge;
import org.processmining.extendedhybridminer.models.causalgraph.HybridDirectedUncertainGraphEdge;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.connections.GraphLayoutConnection;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetImpl;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.pnml.base.FullPnmlElementFactory;
import org.processmining.plugins.pnml.base.Pnml;
import org.processmining.plugins.pnml.base.Pnml.PnmlType;
import org.processmining.plugins.pnml.base.PnmlElementFactory;
import org.processmining.plugins.pnml.importing.PnmlImportUtils;

/**
 * Created by demas on 27/07/16.
 */
public class HybridPetrinet extends PetrinetImpl {
    private Map<String, Transition> labelTransitionsMap;
	private Color surePlaceColor;
	private Color sureColor;
	private Color unsureColor;
	private Color LDColor;
	private Marking initialMarking;
	private Collection<Marking> finalMarkings;
	

    public HybridPetrinet(String label) {
        super(label);
        this.labelTransitionsMap = new HashMap<>();
        this.initialMarking = null;
        this.finalMarkings = null;
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
    
    
    public synchronized TransitionsArc addTransitionsArcFromFCGEdge(HybridDirectedGraphEdge edge) {
        Transition source = this.addTransition(edge.getSource().getLabel());
        Transition target = this.addTransition(edge.getTarget().getLabel());

        if (edge instanceof HybridDirectedSureGraphEdge)
            return this.addSureTransitionsArcPrivate(source, target, 1);
        else if (edge instanceof HybridDirectedUncertainGraphEdge)
            return this.addUncertainTransitionsArcPrivate(source, target, 1);
        else
        	return this.addLongDepTransitionsArcPrivate(source, target, 1);
    }

    public synchronized TransitionsArc addTransitionsArcFromFCGEdge(HybridDirectedGraphEdge edge, int weight) {
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
    public synchronized SureTransitionsArc addSureTransitionsArcPrivate(Transition source, Transition target, int weight) {
        synchronized (arcs) {
            // Following check just makes sure that source and target already exist in the net
            checkAddEdge(source, target);

            // Check if there is already a place connecting source and target. If there is, return null
            boolean placesAlreadyAdded = this.checkIfPlacesArePresent(source, target);
            if (!placesAlreadyAdded) {
                SureTransitionsArc a = new SureTransitionsArc(source, target, weight);

                if (arcs.add(a)) {
                    graphElementAdded(a);
                    return a;
                } else {
                    for (Arc existing : arcs) {
                        if (existing.equals(a)) {
                            existing.setWeight(existing.getWeight() + weight);
                            return (SureTransitionsArc) existing;
                        }
                    }
                }
                assert (false);
                return null;
            }
            return null;
        }
    }
    
    public synchronized LongDepTransitionsArc addLongDepTransitionsArcPrivate(Transition source, Transition target, int weight) {
        synchronized (arcs) {
            // Following check just makes sure that source and target already exist in the net
            checkAddEdge(source, target);

            // Check if there is already a place connecting source and target. If there is, return null
            boolean placesAlreadyAdded = this.checkIfPlacesArePresent(source, target);
            if (!placesAlreadyAdded) {
                LongDepTransitionsArc a = new LongDepTransitionsArc(source, target, weight);

                if (arcs.add(a)) {
                    graphElementAdded(a);
                    return a;
                } else {
                    for (Arc existing : arcs) {
                        if (existing.equals(a)) {
                            existing.setWeight(existing.getWeight() + weight);
                            return (LongDepTransitionsArc) existing;
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
    private synchronized UncertainTransitionsArc addUncertainTransitionsArcPrivate(Transition source, Transition target, int weight) {
        synchronized (arcs) {
            // Following check just makes sure that source and target already exist in the net
            checkAddEdge(source, target);

            // Check if there is already a place connecting source and target. If there is, return null
            boolean placesAlreadyAdded = this.checkIfPlacesArePresent(source, target);
            if (!placesAlreadyAdded) {
                UncertainTransitionsArc a = new UncertainTransitionsArc(source, target, weight);
                if (arcs.add(a)) {
                    graphElementAdded(a);
                    return a;
                } else {
                    for (Arc existing : arcs) {
                        if (existing.equals(a)) {
                            existing.setWeight(existing.getWeight() + weight);
                            return (UncertainTransitionsArc) existing;
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
    public synchronized Collection<SureTransitionsArc> getSureArcs(){
    	Set<SureTransitionsArc> directedSureGraphEdges = new HashSet<SureTransitionsArc>();
    	for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : getEdges()) {
            if (edge instanceof SureTransitionsArc)
            	directedSureGraphEdges.add((SureTransitionsArc) edge);
		}
    	return directedSureGraphEdges;
    }
    
    /** CHIARA
     * Returns the set of uncertain arcs
     * @return set of uncertain arcs
     */
    public synchronized Collection<UncertainTransitionsArc> getUncertainArcs(){
    	Set<UncertainTransitionsArc> directedUncertainGraphEdges = new HashSet<UncertainTransitionsArc>();
    	for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : getEdges()) {
            if (edge instanceof UncertainTransitionsArc)
            	directedUncertainGraphEdges.add((UncertainTransitionsArc) edge);
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
    		if (edge instanceof SureTransitionsArc)
    			fuzzyPetriNetString+=edge.getSource()+" ->- "+edge.getTarget()+"\n";
            if (edge instanceof UncertainTransitionsArc)
    			fuzzyPetriNetString+=edge.getSource()+" ->-? "+edge.getTarget()+"\n";
            else
                fuzzyPetriNetString+=edge.getSource()+" -> "+edge.getTarget()+"\n";
		}
    	return fuzzyPetriNetString;
    }

    
    
	/*
	 * Added by Humam
	 */
	public HybridPetrinet cloneToPN() {
		HybridPetrinet newPN = new HybridPetrinet("copy of + " + this.getLabel());
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

	public void importFromStream(PluginContext context, InputStream input) throws Exception {
		PnmlImportUtils utils = new PnmlImportUtils();
		Set<Point2D> positions = new HashSet<Point2D>();
		Pnml pnml = utils.importPnmlFromStream(context, input, "", 0);
		setColors();
		if (pnml == null) {
			return;
		}
		/*if (pnml.hasModule()) {
			OpenNet openNet = new OpenNet(pnml.getLabel());
			Marking openInitialMarking = new Marking();
			GraphLayoutConnection openLayout = new GraphLayoutConnection(openNet);
			pnml.convertToNet(openNet, openInitialMarking, openLayout);
			System.out.println("openNet: " + openNet.getNodes().toString());
			//HybridPetrinet net = new HybridPetrinet(pnml.getLabel());
			GraphLayoutConnection layout = new GraphLayoutConnection(this);
			layout.setLabel(openLayout.getLabel());
			layout.setLayedOut(openLayout.isLayedOut());
			Map<Transition, Transition> transitionMap = new HashMap<Transition, Transition>();
			Map<Place, Place> placeMap = new HashMap<Place, Place>();
			for (Transition openTransition : openNet.getTransitions()) {
				Transition transition = addTransition(openTransition.getLabel());
				transitionMap.put(openTransition, transition);
				transition.setInvisible(openTransition.isInvisible());
				if (openLayout.isLayedOut()) {
					layout.setPosition(transition, openLayout.getPosition(openTransition));
					positions.add(openLayout.getPosition(openTransition));
					layout.setSize(transition, openLayout.getSize(openTransition));
				}
			}
			for (Place openPlace : openNet.getPlaces()) {
				Place place = addPlace(openPlace.getLabel());
				placeMap.put(openPlace, place);
				if (openLayout.isLayedOut()) {
					layout.setPosition(place, openLayout.getPosition(openPlace));
					positions.add(openLayout.getPosition(openPlace));
					layout.setSize(place, openLayout.getSize(openPlace));
				}
			}
			if (positions.size() < getTransitions().size() + getPlaces().size()) {
				layout.setLayedOut(false);
			}
			for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> openEdge : openNet.getEdges()) {
				PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge = null;
				if (placeMap.containsKey(openEdge.getSource())) {
					addArc(placeMap.get(openEdge.getSource()), transitionMap.get(openEdge.getTarget()));
				} else if (placeMap.containsKey(openEdge.getTarget())) {
					addArc(transitionMap.get(openEdge.getSource()), placeMap.get(openEdge.getTarget()));
				} else {
					addArc(transitionMap.get(openEdge.getSource()), transitionMap.get(openEdge.getTarget()))	;	

					if (openLayout.isLayedOut()) {
						layout.setEdgePoints(edge, openLayout.getEdgePoints(openEdge));
					}
				}
				context.addConnection(layout);
				init();
				initialMarking = new Marking();
				for (Place place : openInitialMarking.baseSet()) {
					initialMarking.add(placeMap.get(place), openInitialMarking.occurrences(place));
				}
				context.addConnection(new InitialMarkingConnection(this, initialMarking));
				finalMarkings.clear();
				for (Marking openFinalMarking : openNet.getFinalMarkings()) {
					Marking finalMarking = new Marking();
					for (Place place : openFinalMarking.baseSet()) {
						finalMarking.add(placeMap.get(place), openFinalMarking.occurrences(place));
					}
					finalMarkings.add(finalMarking);
				}
			}
		} else {*/
			//	System.out.println("USED");
			//net = PetrinetFactory.newPetrinet(pnml.getLabel());
			GraphLayoutConnection layout = new GraphLayoutConnection(this);
			context.getConnectionManager().addConnection(layout);
			initialMarking = new Marking();
			finalMarkings = new HashSet<Marking>();
			pnml.convertToNet(this, initialMarking, finalMarkings, layout);
			if (finalMarkings.isEmpty()) {
				finalMarkings.add(new Marking());
			}
		//}
		System.out.println("this: " + this.getNodes().toString());

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

	public void exportToFile(PluginContext context, File file) throws IOException {
		GraphLayoutConnection layout;
		try {
			layout = context.getConnectionManager().getFirstConnection(GraphLayoutConnection.class,
					context, this);
		} catch (ConnectionCannotBeObtained e) {
			layout = new GraphLayoutConnection(this);
		}

		PnmlElementFactory factory = new FullPnmlElementFactory();
		Pnml pnml = new Pnml();
		synchronized (factory) {
			pnml.setFactory(factory);
			pnml = new Pnml().convertFromNet(this, setInitialMarking(), setFinalMarkings(), layout);
			pnml.setType(PnmlType.PNML);
		}
		String text = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" + pnml.exportElement(pnml);

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
		bw.write(text);
		bw.close();

	}


	public Color getLDColor() {
		return this.LDColor;
	}
	
	public void updateLDColor(Color c) {
		this.LDColor = c;
	}

}
