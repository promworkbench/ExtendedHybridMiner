package org.processmining.extendedhybridminer.models.causalgraph;

import java.awt.geom.Point2D;
import java.text.NumberFormat;

import org.jgraph.graph.GraphConstants;
import org.processmining.models.graphbased.AttributeMap;

/**
 * Created by demas on 27/07/16.
 */
public class HybridDirectedLongDepGraphEdge extends HybridDirectedGraphEdge {

	NumberFormat nf=null;
    
    public HybridDirectedLongDepGraphEdge(HybridDirectedGraphNode source, HybridDirectedGraphNode target) {
        super(source, target);
        String[] lab={"          ","        "};
        getAttributeMap().put(AttributeMap.EXTRALABELS,lab);
		getAttributeMap().put(AttributeMap.LABEL, " ");
		getAttributeMap().put(AttributeMap.LABELALONGEDGE, false);
		getAttributeMap().put(AttributeMap.SHOWLABEL, true);
		Point2D[] labelPositions = { 
				new Point2D.Double (50,-10),
				new Point2D.Double (GraphConstants.PERMILLE -50,-10)  }; 
		getAttributeMap().put(AttributeMap.EXTRALABELPOSITIONS,labelPositions);

    }

    public void setLabel(double value1)
    {
    	if(nf==null)
    	{
    		nf=NumberFormat.getInstance();
    		nf.setMaximumFractionDigits(2);
    		nf.setMinimumFractionDigits(0);
    	}
    	String[] lab={"     ","     "};
    	getAttributeMap().put(AttributeMap.LABELALONGEDGE,false);

    	getAttributeMap().put(AttributeMap.LABEL,nf.format(value1).replace(",",""));	
    	getAttributeMap().put(AttributeMap.EXTRALABELS,lab);
    	
    }

    public void setLabel(double value1,double value2)
    {
    	if(nf==null)
    	{
    		nf=NumberFormat.getInstance();
    		nf.setMaximumFractionDigits(2);
    		nf.setMinimumFractionDigits(0);
    	}
    	String[] lab={nf.format(value1).replace(",",""),nf.format(value2).replace(",","")};
    	getAttributeMap().put(AttributeMap.LABELALONGEDGE,false);
        getAttributeMap().put(AttributeMap.LABEL,"     ");
    	getAttributeMap().put(AttributeMap.EXTRALABELS,lab);
    }
}