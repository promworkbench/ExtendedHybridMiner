package org.processmining.extendedhybridminer.models.causalgraph.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.BasicMarqueeHandler;
import org.processmining.extendedhybridminer.algorithms.HybridCGMiner;
import org.processmining.extendedhybridminer.algorithms.preprocessing.LogFilterer;
import org.processmining.extendedhybridminer.algorithms.preprocessing.TraceVariantsLog;
import org.processmining.extendedhybridminer.models.causalgraph.ExtendedCausalGraph;
import org.processmining.extendedhybridminer.models.causalgraph.HybridDirectedGraphNode;
import org.processmining.extendedhybridminer.models.causalgraph.HybridDirectedLongDepGraphEdge;
import org.processmining.extendedhybridminer.models.causalgraph.HybridDirectedSureGraphEdge;
import org.processmining.extendedhybridminer.models.causalgraph.HybridDirectedUncertainGraphEdge;
import org.processmining.extendedhybridminer.plugins.HybridCGMinerSettings;
import org.processmining.framework.plugin.impl.ProgressBarImpl;
import org.processmining.framework.util.Cleanable;
import org.processmining.framework.util.Pair;
import org.processmining.framework.util.ui.scalableview.ScalableComponent;
import org.processmining.framework.util.ui.scalableview.ScalableComponent.UpdateListener;
import org.processmining.framework.util.ui.scalableview.VerticalLabelUI;
import org.processmining.framework.util.ui.scalableview.interaction.ViewInteractionPanel;
import org.processmining.models.connections.GraphLayoutConnection;
import org.processmining.models.graphbased.ViewSpecificAttributeMap;
import org.processmining.models.graphbased.directed.DirectedGraphEdge;
import org.processmining.models.graphbased.directed.DirectedGraphElement;
import org.processmining.models.graphbased.directed.DirectedGraphNode;
import org.processmining.models.jgraph.ContextMenuCreator;
import org.processmining.models.jgraph.ProMGraphModel;
import org.processmining.models.jgraph.ProMJGraph;
import org.processmining.models.jgraph.elements.ProMGraphCell;
import org.processmining.models.jgraph.elements.ProMGraphEdge;
import org.processmining.models.jgraph.listeners.SelectionListener;

import com.fluxicon.slickerbox.factory.SlickerDecorator;
import com.fluxicon.slickerbox.factory.SlickerFactory;

public class HybridCausalGraphVisualization extends JLayeredPane implements HybridGraphVisualization, MouseMotionListener, Cleanable,
		ChangeListener, UpdateListener, ActionListener
		{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1444524291294067108L;

	protected static final int MAX_ZOOM = 800;
	private JComponent component;
	private ViewInteractionPanel visiblePanel = null;
	// -------------------------------------------

	protected ProMJGraph graph;
	protected ProMJGraph pipGraph;

	// -------------------------------------------

	protected JScrollPane scroll;
	private JPanel configPanelON, configPanelOFF;
	private ConfigurationPanel config;
	private JPanel filterPanelON, filterPanelOFF;
	private FilterPanel filter;
	private ZoomInteractionPanel zoomIP;
	
	// Updates by sabi
	private JPanel legendPanelON,legendPanelOFF;
	private legendPanel legend;
	
	private ExtendedCausalGraph fCG;
	//private float zoomRatio, configRatio;
	//private double normalScale;
	private Rectangle normalBounds, zoomBounds, configBounds, filterBounds;

	private boolean hasNodeSelected;

	private List<SelectionListener<?, ?>> selectionListeners = new ArrayList<SelectionListener<?, ?>>(
			0);
	private Map<ViewInteractionPanel, Pair<JPanel, JPanel>> panels = new HashMap<ViewInteractionPanel, Pair<JPanel, JPanel>>();
	//private Map<ViewInteractionPanel, Integer> locations = new HashMap<ViewInteractionPanel, Integer>();
	
	private ContextMenuCreator creator = null;
	
	// Update by sabi
	//private float legendRatio;
	private Rectangle legendBounds;
	private String ArcLabel="Input & Output Direction";
	//private Rectangle colorBounds;
	//private JPanel sureColorPanel, unsureColorPanel;
	protected ColorPanel colorPanel;
	protected ScalableComponent scalable;
	protected SlickerFactory factory;
	protected SlickerDecorator decorator;
	protected PIPInteractionPanel pip;

	private ExportInteractionPanel export;

	private JPanel zoomPanelOn;

	private JPanel zoomPanelOff;

	private JPanel pipPanelOn;

	private JPanel pipPanelOff;

	private JPanel exportPanelOn;

	private JPanel exportPanelOff;

	private JPanel colorPanelOn;

	private JPanel colorPanelOff;
	//private Color sureColor=Color.BLACK,unsureColor=Color.GRAY;
	public final static int TAB_HEIGHT = 30;
	public final static int TAB_WIDTH = 120;
	
	public void updateColor()
	{	
		this.graph=HybridCausalGraphVisualizer.createJGraph(fCG,new ViewSpecificAttributeMap(),new ProgressBarImpl(null),ArcLabel);
		//scalable.setScale(scalable.getScale());
		this.pip.initializeImage();
	}
	

	public HybridCausalGraphVisualization(final ProMJGraph graph, ExtendedCausalGraph CG) {
		this.scalable = graph;
		this.component = graph.getComponent();
		scalablePanel(graph);
		factory = SlickerFactory.instance();
		this.setLayout(null);
		this.graph = graph;
		this.fCG = CG;
		SlickerFactory factory = SlickerFactory.instance();
		SlickerDecorator decorator = SlickerDecorator.instance();

		this.addComponentListener(new java.awt.event.ComponentListener() {

			public void componentHidden(ComponentEvent e) {
			}

			public void componentMoved(ComponentEvent e) {
			}

			public void componentShown(ComponentEvent e) {
			}

			public void componentResized(ComponentEvent e) {
				resize();
			}
		});

		this.initGraph();

		AdjustmentListener aListener = new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {
			}
		};
		MouseListener mListener = new MouseListener() {

			public void mouseClicked(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {

				if (hasNodeSelected) {

					scroll.repaint();

				}

			}

			public void mousePressed(MouseEvent e) {

			}

			public void mouseReleased(MouseEvent e) {

			}
		};
		
		this.scroll = new JScrollPane(graph);
		this.scroll.getHorizontalScrollBar().addAdjustmentListener(aListener);
		this.scroll.getVerticalScrollBar().addAdjustmentListener(aListener);
		this.scroll.getHorizontalScrollBar().addMouseListener(mListener);
		this.scroll.getVerticalScrollBar().addMouseListener(mListener);
		decorator.decorate(this.scroll, Color.WHITE, Color.GRAY,
				Color.DARK_GRAY);
		this.scroll.setBorder(new DashedBorder(Color.LIGHT_GRAY));

		
		// Updates by sabi
		this.legendPanelON=factory.createRoundedPanel(15,Color.LIGHT_GRAY);
		this.legendPanelOFF=factory.createRoundedPanel(15,Color.DARK_GRAY);
		this.legendPanelON.setLayout(null);
		this.legendPanelOFF.setLayout(null);
		this.legendPanelOFF.setVisible(true);
		this.legendPanelOFF.setEnabled(true);

		JLabel legendPanel=factory.createLabel("Legend");
		legendPanel.setForeground(Color.WHITE);
		legendPanel.setFont(new java.awt.Font("Dialog",java.awt.Font.BOLD,18));
		legendPanel.setUI(new VerticalLabelUI(true));
		legendPanel.setBounds(10,10,30,85);
		this.legendPanelOFF.add(legendPanel);

		this.legend=new legendPanel(factory,decorator);
		this.legend.setForeground(Color.WHITE);
		this.legend.setVisible(true);
		this.legend.setEnabled(true);
	    this.legend.setListener(this);
		this.legendPanelON.add(this.legend);
		this.legendPanelON.setVisible(false);
		this.legendPanelON.setEnabled(false);
		this.legendPanelON.addMouseListener(new MouseListener()
		{
			public void mouseClicked(MouseEvent e)
			{
			}

			public void mousePressed(MouseEvent e)
			{
			}

			public void mouseReleased(MouseEvent e)
			{
			}

			public void mouseEntered(MouseEvent e)
			{
			}

			public void mouseExited(MouseEvent e)
			{
				showLegend(false);
			}
		});

		this.legendPanelOFF.addMouseListener(new MouseListener()
		{
			public void mouseClicked(MouseEvent e)
			{
			}

			public void mousePressed(MouseEvent e)
			{
				showConfig(false);
				showFilter(false);
				showColor(false);
				showZoom(false);
				showPIP(false);
				showLegend(true);
			}

			public void mouseReleased(MouseEvent e)
			{
				// TODO Auto-generated method stub	
			}

			public void mouseEntered(MouseEvent e)
			{
				
			}

			public void mouseExited(MouseEvent e)
			{
			}
		});






		/*this.sureColorPanel=factory.createRoundedPanel(15,Color.LIGHT_GRAY);
		this.sureColorPanel=factory.createRoundedPanel(15,Color.DARK_GRAY);
		this.sureColorPanel.setLayout(null);
		this.sureColorPanel.setLayout(null);
		this.sureColorPanel.setVisible(true);
		this.sureColorPanel.setEnabled(true);
		this.sureColorPanel.setBounds(105,0,120,30);
		JLabel surePanel=factory.createLabel("Sure Edges");
		surePanel.setForeground(Color.WHITE);
		surePanel.setFont(new java.awt.Font("Dialog",java.awt.Font.BOLD,18));
//		colorPanel.setUI(new VerticalLabelUI(false));
		this.sureColorPanel.add(surePanel);
		surePanel.setBounds(10,0,120,30);
		this.sureColorPanel.addMouseListener(new MouseListener()
		{
			public void mouseClicked(MouseEvent e)
			{
				
			}
			public void mousePressed(MouseEvent e)
			{
				Color c = JColorChooser.showDialog(new Frame(),"Pick a Color for Sure Edges", fCG.getSureColor());
				if (c != null) {
					System.out.println("Sure "+c.toString());
					fCG.updateSureColor(c);
				    updateColor();
				}
			}
			public void mouseReleased(MouseEvent e)
			{
				// TODO Auto-generated method stub	
			}
			public void mouseEntered(MouseEvent e)
			{
				showConfig(false);
				showFilter(false);
				showLegend(false);	
				showZoom(false);
				showPIP(false);
			}
			public void mouseExited(MouseEvent e)
			{
				// TODO Auto-generated method stub	
			}
		});*/
		/*this.unsureColorPanel=factory.createRoundedPanel(15,Color.LIGHT_GRAY);
		this.unsureColorPanel=factory.createRoundedPanel(15,Color.DARK_GRAY);
		this.unsureColorPanel.setLayout(null);
		this.unsureColorPanel.setLayout(null);
		this.unsureColorPanel.setVisible(true);
		this.unsureColorPanel.setEnabled(true);
		this.unsureColorPanel.setBounds(240,0,140,30);
		
		JLabel unsurePanel=factory.createLabel("Unsure Edges");
		unsurePanel.setForeground(Color.WHITE);
		unsurePanel.setFont(new java.awt.Font("Dialog",java.awt.Font.BOLD,18));
//		colorPanel.setUI(new VerticalLabelUI(false));
		this.unsureColorPanel.add(unsurePanel);
		unsurePanel.setBounds(10,0,150,30);

		this.unsureColorPanel.addMouseListener(new MouseListener()
		{
			public void mouseClicked(MouseEvent e)
			{
				
			}
			public void mousePressed(MouseEvent e)
			{
				Color c=JColorChooser.showDialog(new Frame(),"Pick a Color for Unsure Edges", fCG.getUnsureColor());

				if (c != null) {
					System.out.println("Unsure "+c.toString());
				    fCG.updateUnsureColor(c);
				    updateColor();
				}
			}
			public void mouseReleased(MouseEvent e)
			{
				// TODO Auto-generated method stub	
			}
			public void mouseEntered(MouseEvent e)
			{
				showConfig(false);
				showFilter(false);
				showLegend(false);	
				showZoom(false);
				showPIP(false);	
			}
			public void mouseExited(MouseEvent e)
			{
				
			}
		});*/
		
	    int sureThresholdValue = (int) (fCG.getSettings().getSureThreshold()*100);
		int unsureThresholdValue = (int) (fCG.getSettings().getQuestionMarkThreshold()*100);
		int longDepThresholdValue = (int) (fCG.getSettings().getLongDepThreshold()*100);
		int causalityWeightValue = (int) (fCG.getSettings().getCausalityWeight()*100);

		//this.configPanelON = factory.createRoundedPanel(200, Color.LIGHT_GRAY);
		this.configPanelON = factory.createRoundedPanel(15, Color.LIGHT_GRAY);
		this.configPanelOFF = factory.createRoundedPanel(15, Color.DARK_GRAY);
		this.configPanelON.setLayout(null);
		this.configPanelOFF.setLayout(null);
		this.config = new ConfigurationPanel(factory, decorator, sureThresholdValue, unsureThresholdValue, longDepThresholdValue, causalityWeightValue);
		this.config.addSliderChangeListener(this);
		this.configPanelON.add(this.config);
		this.configPanelON.setVisible(false);
		this.configPanelON.setEnabled(false);
		JLabel configPanelTitle = factory.createLabel("Parameters");
		configPanelTitle.setForeground(Color.WHITE);
		configPanelTitle.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD,
				18));
		configPanelTitle.setUI(new VerticalLabelUI(true));
		this.configPanelOFF.add(configPanelTitle);
		configPanelTitle.setBounds(10, 10, 30, 120);

		this.configPanelOFF.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent e) {
				
			}

			public void mouseEntered(MouseEvent e) {
			    
			}

			public void mouseExited(MouseEvent e) {
				
			}

			public void mousePressed(MouseEvent e) {
				showLegend(false);
			    showZoom(false);
			    showColor(false);
			    showPIP(false);
			    showFilter(false);
				showConfig(true);
     		}

			public void mouseReleased(MouseEvent e) {
			}
		});

		/*this.configPanelON.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}
		
			public void mouseExited(MouseEvent e) {
				showConfig(false);
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
			}
		});	*/
		
	    int filterActivityValue = (int) (fCG.getSettings().getFilterAcivityThreshold()*100);
		int filterTraceValue = (int) (fCG.getSettings().getTraceVariantsThreshold()*100);

		this.filterPanelON = factory.createRoundedPanel(15, Color.LIGHT_GRAY);
		this.filterPanelOFF = factory.createRoundedPanel(15, Color.DARK_GRAY);
		this.filterPanelON.setLayout(null);
		this.filterPanelOFF.setLayout(null);
		this.filter = new FilterPanel(factory, decorator, filterActivityValue, filterTraceValue);
		this.filter.addSliderChangeListener(this);
		this.filterPanelON.add(this.filter);
		this.filterPanelON.setVisible(false);
		this.filterPanelON.setEnabled(false);
		JLabel filterPanelTitle = factory.createLabel("Filter");
		filterPanelTitle.setForeground(Color.WHITE);
		filterPanelTitle.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD,
				18));
		filterPanelTitle.setUI(new VerticalLabelUI(true));
		this.filterPanelOFF.add(filterPanelTitle);
		filterPanelTitle.setBounds(10, 10, 30, 65);

		this.filterPanelOFF.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent e) {
				
			}

			public void mouseEntered(MouseEvent e) {
			    
			}

			public void mouseExited(MouseEvent e) {
				
			}

			public void mousePressed(MouseEvent e) {
				showLegend(false);
			    showZoom(false);
			    showColor(false);
			    showPIP(false);
				showConfig(false);
				showFilter(true);
     		}

			public void mouseReleased(MouseEvent e) {
			}
		});

		/*this.filterPanelON.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}
		
			public void mouseExited(MouseEvent e) {
				showFilter(false);
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
			}
		});	*/
		
		this.add(this.scroll);
		this.addZoomPanel();
		this.zoomIP.computeFitScale();
		this.zoomIP.fit();
		//this.zoomIP.update();
		
		this.add(this.filterPanelON);
		this.add(this.filterPanelOFF);
		this.add(this.configPanelON);
		this.add(this.configPanelOFF);	
		this.add(this.legendPanelON);
		this.add(this.legendPanelOFF);
		//this.add(this.sureColorPanel);
		//this.add(this.unsureColorPanel);
		

		this.addPIPPanel();
		this.addExportPanel();
		this.addColorPanel();
		
		
		this.setBackground(Color.WHITE);

		this.validate();
		this.repaint();
	}

	private void scalablePanel(ProMJGraph scalableComponent) {
		setLayout(null);
		factory = SlickerFactory.instance();
		decorator = SlickerDecorator.instance();

		/*
		 * Create the scroll panel containing the primary view, and register the
		 * created adjustment and mouse listener.
		 */
		scroll = new JScrollPane(getComponent());
		/*
		 * Adjust Look+Feel of scrollbar to Slicker.
		 */
		decorator.decorate(scroll, Color.WHITE, Color.GRAY, Color.DARK_GRAY);
		/*
		 * Create a dashed border for the primary view.
		 */
		scroll.setBorder(new DashedBorder(Color.LIGHT_GRAY));

		/*
		 * Add primary view to the layered pane. The special panels are added to
		 * the drag layer, which keeps them on top even when the underlying
		 * primary view gets updated.
		 */
		//add(scroll, JLayeredPane.DEFAULT_LAYER);

		this.addMouseMotionListener(this);
		getComponent().addMouseMotionListener(this);
		scalable.addUpdateListener(this);

		/*
		 * Register a component listener to handle resize events, as the bounds
		 * of many panels depend on the size of this panel.
		 */
		this.addComponentListener(new java.awt.event.ComponentListener() {
			public void componentHidden(ComponentEvent e) {
			}

			public void componentMoved(ComponentEvent e) {
			}

			public void componentShown(ComponentEvent e) {
			}

			public void componentResized(ComponentEvent e) {
				resize();
			}
		});

		this.scroll.addComponentListener(new ComponentListener() {

			public void componentShown(ComponentEvent e) {

			}

			public void componentResized(ComponentEvent e) {
				scroll.removeComponentListener(this);
				scalable.setScale(1);
				double rx = (scroll.getWidth() - scroll.getVerticalScrollBar().getWidth())
						/ scalable.getComponent().getPreferredSize().getWidth();
				double ry = (scroll.getHeight() - scroll.getHorizontalScrollBar().getHeight())
						/ scalable.getComponent().getPreferredSize().getHeight();
				scalable.setScale(Math.min(rx, ry));
			}

			public void componentMoved(ComponentEvent e) {

			}

			public void componentHidden(ComponentEvent e) {

			}
		});

		/*validate();
		repaint();*/
		
	}
	
	
	public synchronized void addPIPPanel() {
		this.pip = new PIPInteractionPanel(this);
		this.pip.setScalableComponent(scalable);
		//this.pip.setOriginalGraph(this.scalable);
		this.pipPanelOn = factory.createRoundedPanel(15, Color.LIGHT_GRAY);
		this.pipPanelOff = factory.createRoundedPanel(15, Color.DARK_GRAY);
		this.pipPanelOn.setLayout(null);
		this.pipPanelOff.setLayout(null);

		this.pipPanelOn.add(this.pip.getComponent());
		this.pipPanelOn.setVisible(false);
		this.pipPanelOn.setEnabled(false);
		this.pipPanelOff.setVisible(true);
		this.pipPanelOff.setEnabled(true);
		JLabel panelTitle = factory.createLabel(this.pip.getPanelName());
		panelTitle.setHorizontalTextPosition(SwingConstants.CENTER);
		panelTitle.setVerticalTextPosition(SwingConstants.CENTER);
		panelTitle.setForeground(Color.WHITE);
		panelTitle.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 16));
		this.pipPanelOff.add(panelTitle);

		panels.put(this.pip, new Pair<JPanel, JPanel>(this.pipPanelOn, this.pipPanelOff));
		//locations.put(this.pip, SwingConstants.NORTH);

		panelTitle.setBounds(10, 0, 30, 30);
		setSizePIP();
		
		this.pipPanelOff.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent e) {
				
			}

			public void mouseEntered(MouseEvent e) {
			    
			}

			public void mouseExited(MouseEvent e) {
				
			}

			public void mousePressed(MouseEvent e) {
				showLegend(false);
			    showConfig(false);
			    showFilter(false);
			    showColor(false);
			    showZoom(false);
			    setSizePIP();
			    pip.initializeImage();
			    setSizePIP();
			    showPIP(true);
     		}

			public void mouseReleased(MouseEvent e) {
			}
		});
		
		this.pipPanelOn.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent e) {
				
			}

			public void mouseEntered(MouseEvent e) {
			    
			}

			public void mouseExited(MouseEvent e) {
				showPIP(false);
			}

			public void mousePressed(MouseEvent e) {
	
     		}

			public void mouseReleased(MouseEvent e) {
			}
		});

		pipPanelOn.invalidate();

     	add(this.pipPanelOn, JLayeredPane.PALETTE_LAYER);
		add(this.pipPanelOff, JLayeredPane.PALETTE_LAYER);
		this.pip.updated();
	}
	
	
	private void setSizePIP() {
		//pipPanelOn.setLocation(40, 10);
		this.pip.getComponent().setLocation(10, 10);
		//pipPanelOn.setSize(this.pip.getComponent().getWidth(), this.pip.getComponent().getHeight());
		pipPanelOff.setBounds(40, 0, 50, 30);
		double w = pip.getWidthInView();
		double h = pip.getHeightInView();
		if (w > 1) {
			// fixed width
			w += 20;
		} else {
			// relative width
			w *= scalable.getComponent().getPreferredSize().getWidth();
			//w = this.pip.getVisHeight();
		}
		if (h > 1) {
			// fixed height
			h += 40;
		} else {
			// relative height
		    h *= scalable.getComponent().getPreferredSize().getHeight();
		    //w = this.pip.getVisHeight();
		}
		//w = Math.min(w, scroll.getWidth() - 4 * TAB_HEIGHT);
		//h = Math.min(h, scroll.getHeight() - 2 * TAB_HEIGHT);

		pip.getComponent().setSize((int) w, (int) h);

		pipPanelOn.setSize(pip.getComponent().getWidth() + 20, pip.getComponent().getHeight() + 20);

		int x = pipPanelOff.getLocation().x;
		int y = pipPanelOff.getLocation().y;	
		if (x + pipPanelOn.getWidth() > getWidth()) {
			x = Math.max(TAB_HEIGHT, getWidth() - pipPanelOn.getWidth());
		}
		if (y + pipPanelOn.getHeight() > getHeight()) {
			y = Math.max(TAB_HEIGHT, getHeight() - pipPanelOn.getHeight());
		}
		pipPanelOn.setLocation(x, y);
		pipPanelOn.invalidate();
	}


	public synchronized void addZoomPanel() {
		this.zoomIP = new ZoomInteractionPanel(this, this.MAX_ZOOM);
		this.zoomIP.setScalableComponent(scalable);
		/*this.zoomIP.computeFitScale();
		this.zoomIP.fit();
		this.zoomIP.updated();*/
		this.zoomPanelOn = factory.createRoundedPanel(15, Color.LIGHT_GRAY);
		this.zoomPanelOff = factory.createRoundedPanel(15, Color.DARK_GRAY);
		this.zoomPanelOn.setLayout(null);
		this.zoomPanelOff.setLayout(null);

		this.zoomPanelOn.add(this.zoomIP.getComponent());
		this.zoomPanelOn.setVisible(false);
		this.zoomPanelOn.setEnabled(false);
		this.zoomPanelOff.setVisible(true);
		this.zoomPanelOff.setEnabled(true);
		JLabel panelTitle = factory.createLabel(this.zoomIP.getPanelName());
		panelTitle.setHorizontalTextPosition(SwingConstants.CENTER);
		panelTitle.setVerticalTextPosition(SwingConstants.CENTER);
		panelTitle.setForeground(Color.WHITE);
		panelTitle.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 16));
		this.zoomPanelOff.add(panelTitle);

		panels.put(this.zoomIP, new Pair<JPanel, JPanel>(this.zoomPanelOn, this.zoomPanelOff));
		//locations.put(this.zoomIP, SwingConstants.WEST);

		panelTitle.setBounds(10, 10, 30, 55);
		panelTitle.setUI(new VerticalLabelUI(true));  
		
		setSizeZoom();
		this.zoomPanelOff.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent e) {
				
			}

			public void mouseEntered(MouseEvent e) {
			    
			}

			public void mouseExited(MouseEvent e) {
				
			}

			public void mousePressed(MouseEvent e) {
				showLegend(false);
			    showConfig(false);
			    showColor(false);
			    showFilter(false);
			    showPIP(false);
			    zoomIP.computeFitScale();
				showZoom(true);
     		}

			public void mouseReleased(MouseEvent e) {
			}
		});
		
		/*this.zoomPanelOn.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent e) {
				
			}

			public void mouseEntered(MouseEvent e) {
			    
			}

			public void mouseExited(MouseEvent e) {
				showZoom(false);
			}

			public void mousePressed(MouseEvent e) {
	
     		}

			public void mouseReleased(MouseEvent e) {
			}
		});*/

		add(this.zoomPanelOn);
		add(this.zoomPanelOff);
		
	}
	
	
	private void setSizeZoom() {
		//this.zoomPanelOn.setLocation(-10, 40);
		this.zoomPanelOff.setBounds(-10, 40, 40, 65);
		double w = 120;
		double h = 0.66 * scroll.getHeight();
		
		w = Math.min(w, scroll.getWidth() - 4 * TAB_HEIGHT);
		h = Math.min(h, scroll.getHeight() - 2 * TAB_HEIGHT);
        zoomIP.getComponent().setSize((int) w, (int) h);

        zoomPanelOn.setSize(zoomIP.getComponent().getWidth() + 20, zoomIP.getComponent().getHeight() + 20);
        
        this.zoomIP.getComponent().setLocation(10, 10);
        
        int x = zoomPanelOff.getLocation().x;
		int y = zoomPanelOff.getLocation().y;
		if (x + zoomPanelOn.getWidth() > getWidth()) {
			x = Math.max(TAB_HEIGHT, getWidth() - zoomPanelOn.getWidth());
		}
		if (y + zoomPanelOn.getHeight() > getHeight()) {
			y = Math.max(TAB_HEIGHT, getHeight() - zoomPanelOn.getHeight());
		}
		zoomPanelOn.setLocation(x, y);
		
		zoomPanelOn.invalidate();
	}


	public synchronized void addExportPanel() {
		this.export = new ExportInteractionPanel(this);
		this.export.setScalableComponent(scalable);
		this.exportPanelOn = factory.createRoundedPanel(15, Color.LIGHT_GRAY);
		this.exportPanelOff = factory.createRoundedPanel(15, Color.DARK_GRAY);
		this.exportPanelOn.setLayout(null);
		this.exportPanelOff.setLayout(null);

		this.exportPanelOn.add(this.export.getComponent());
		this.exportPanelOn.setVisible(false);
		this.exportPanelOn.setEnabled(false);
		this.exportPanelOff.setVisible(true);
		this.exportPanelOff.setEnabled(true);
		JLabel panelTitle = factory.createLabel(this.export.getPanelName());
		panelTitle.setHorizontalTextPosition(SwingConstants.CENTER);
		panelTitle.setVerticalTextPosition(SwingConstants.CENTER);
		panelTitle.setForeground(Color.WHITE);
		panelTitle.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 16));
		this.exportPanelOff.add(panelTitle);

		panels.put(this.export, new Pair<JPanel, JPanel>(this.exportPanelOn, this.exportPanelOff));
		//locations.put(this.export, SwingConstants.SOUTH);

		panelTitle.setBounds(10, 0, 55, 30);
		
		setSizeExport();
		
		this.exportPanelOff.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent e) {
				
			}

			public void mouseEntered(MouseEvent e) {
				
			}

			public void mouseExited(MouseEvent e) {
				
			}

			public void mousePressed(MouseEvent e) {
				showLegend(false);
			    showConfig(false);
			    showColor(false);
			    showFilter(false);
			    showPIP(false);
				showZoom(false);
				export.export();
     		}

			public void mouseReleased(MouseEvent e) {
			}
		});

		add(this.exportPanelOn);
		add(this.exportPanelOff);
		this.export.updated();
	}
	
	
	public synchronized void addColorPanel() {
		this.colorPanel = new ColorPanel(this, this.fCG);
		//this.export.setScalableComponent(scalable);
		this.colorPanelOn = factory.createRoundedPanel(15, Color.LIGHT_GRAY);
		this.colorPanelOff = factory.createRoundedPanel(15, Color.DARK_GRAY);
		this.colorPanelOn.setLayout(null);
		this.colorPanelOff.setLayout(null);

		this.colorPanelOn.add(this.colorPanel.getComponent());
		this.colorPanelOn.setVisible(false);
		this.colorPanelOn.setEnabled(false);
		this.colorPanelOff.setVisible(true);
		this.colorPanelOff.setEnabled(true);
		JLabel panelTitle = factory.createLabel(this.colorPanel.getPanelName());
		panelTitle.setHorizontalTextPosition(SwingConstants.CENTER);
		panelTitle.setVerticalTextPosition(SwingConstants.CENTER);
		panelTitle.setForeground(Color.WHITE);
		panelTitle.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 16));
		this.colorPanelOff.add(panelTitle);

		panels.put(this.colorPanel, new Pair<JPanel, JPanel>(this.colorPanelOn, this.colorPanelOff));
		//locations.put(this.export, SwingConstants.SOUTH);

		panelTitle.setBounds(10, 0, 120, 30);
		
		setColorSize();
		//colorPanelOff.setBounds(105,0,120,30);
		
		this.colorPanelOff.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent e) {
				
			}

			public void mouseEntered(MouseEvent e) {
			    
			}

			public void mouseExited(MouseEvent e) {
				
			}

			public void mousePressed(MouseEvent e) {
				showLegend(false);
			    showConfig(false);
			    showFilter(false);
			    showZoom(false);
			    showPIP(false);
			    showColor(true);
     		}

			public void mouseReleased(MouseEvent e) {
			}
		});
		
		/*this.colorPanelOn.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent e) {
				
			}

			public void mouseEntered(MouseEvent e) {
			    
			}

			public void mouseExited(MouseEvent e) {
				//showColor(false);
			}

			public void mousePressed(MouseEvent e) {
	
     		}

			public void mouseReleased(MouseEvent e) {
			}
		});*/

		colorPanelOn.invalidate();

     	add(this.colorPanelOn, JLayeredPane.PALETTE_LAYER);
		add(this.colorPanelOff, JLayeredPane.PALETTE_LAYER);
	}

	
	private void setSizeExport() {
		//exportPanelOn.setLocation(40, getHeight() - TAB_HEIGHT);
		exportPanelOff.setBounds(40, getHeight() - TAB_HEIGHT, 75, 40);
		/*export.getComponent().setLocation(10, 10);
		this.export.getComponent().setLocation(20, 10);
		
		double w = export.getWidthInView() + 20;
		double h = export.getHeightInView() + 20;
		
		w = Math.min(w, scroll.getWidth() - 4 * TAB_HEIGHT);
		h = Math.min(h, scroll.getHeight() - 2 * TAB_HEIGHT);

		export.getComponent().setSize((int) w, (int) h);*/
		
		//int x = exportPanelOff.getLocation().x;
		//int y = exportPanelOff.getLocation().y;
		//y = exportPanelOff.getLocation().y - export.getComponent().getHeight() + 10;
		//exportPanelOn.setSize(export.getComponent().getWidth() + TAB_HEIGHT, export.getComponent().getHeight() + 20);
		
		/*if (x + exportPanelOn.getWidth() > getWidth()) {
			x = Math.max(TAB_HEIGHT, getWidth() - exportPanelOn.getWidth());
		}
		if (y + exportPanelOn.getHeight() > getHeight()) {
			y = Math.max(TAB_HEIGHT, getHeight() - exportPanelOn.getHeight());
		}*/
		//exportPanelOn.setLocation(x, y);
		//exportPanelOn.invalidate();
	}
	
	private void setColorSize() {
		colorPanelOn.setLocation(105, 0);
		this.colorPanelOff.setBounds(105,0,120,30);
		colorPanel.getComponent().setLocation(10, 10);
		
		double w = colorPanel.getWidthInView();
		double h = colorPanel.getHeightInView();
		
		//w = Math.min(w, scroll.getWidth() - 4 * TAB_HEIGHT);
		//h = Math.min(h, scroll.getHeight() - 2 * TAB_HEIGHT);

		colorPanel.getComponent().setSize((int) w, (int) h);
		
		/*int x = colorPanelOff.getLocation().x;
		int y = colorPanelOff.getLocation().y;
		y = colorPanelOff.getLocation().y - colorPanel.getComponent().getHeight() + 10;
		*/
		colorPanelOn.setSize(colorPanel.getComponent().getWidth() + 20, colorPanel.getComponent().getHeight() + 20);
		
		/*if (x + colorPanelOn.getWidth() > getWidth()) {
			x = Math.max(TAB_HEIGHT, getWidth() - colorPanelOn.getWidth());
		}
		if (y + colorPanelOn.getHeight() > getHeight()) {
			y = Math.max(TAB_HEIGHT, getHeight() - colorPanelOn.getHeight());
		}
		colorPanelOn.setLocation(x, y);*/
		colorPanelOn.invalidate();
	}


	public void setSize_DDD(ViewInteractionPanel panel, JPanel panelOff, JPanel panelOn) {
		double w = panel.getWidthInView();
		double h = panel.getHeightInView();
		if (w > 1) {
			// fixed width
			w += 20;
		} else {
			// relative width
			w *= scroll.getWidth();
		}
		if (h > 1) {
			// fixed height
			h += 20;
		} else {
			// relative height
			h *= scroll.getHeight();
		}
		w = Math.min(w, scroll.getWidth() - 2 * TAB_HEIGHT);
		h = Math.min(h, scroll.getHeight() - 2 * TAB_HEIGHT);

		panel.getComponent().setSize((int) w, (int) h);
		panelOn.invalidate();
	}

	/*public void setLocation_DDD(ViewInteractionPanel panel, JPanel panelOff, JPanel panelOn) {
		int x = panelOff.getLocation().x;
		int y = panelOff.getLocation().y;
		switch (locations.get(panel)) {
			case SwingConstants.SOUTH : {
				y = panelOff.getLocation().y - panel.getComponent().getHeight() + 10;
			}
				//$FALL-THROUGH$
			case SwingConstants.NORTH : {
				panelOn.setSize(panel.getComponent().getWidth() + 20, panel.getComponent().getHeight() + TAB_HEIGHT);
				break;
			}
			case SwingConstants.EAST : {
				x = panelOff.getLocation().x - panel.getComponent().getWidth() + 10;
			}
				//$FALL-THROUGH$
			default : {
				panelOn.setSize(panel.getComponent().getWidth() + TAB_HEIGHT, panel.getComponent().getHeight() + 20);
			}
		}
		if (x + panelOn.getWidth() > getWidth()) {
			x = Math.max(TAB_HEIGHT, getWidth() - panelOn.getWidth());
		}
		if (y + panelOn.getHeight() > getHeight()) {
			y = Math.max(TAB_HEIGHT, getHeight() - panelOn.getHeight());
		}
		panelOn.setLocation(x, y);
		panelOn.invalidate();
	}*/



	private void resize() {

		int width = this.getSize().width;
		int height = this.getSize().height;

		//int pipHeight = this.pip.getHeight();
		//int pipWidth = this.pip.getWidth();
		//this.pip.setBounds(10, 20, pipWidth, pipHeight);

		this.zoomIP.setHeight((int) (height * 0.66));
		
		//this.config.setHeight((int) (height * 0.66));
		this.config.setHeight((int) (height * 0.66));
		this.filter.setHeight((int) (height * 0.66));

		int zoomWidth = this.zoomPanelOn.getWidth();


		int configWidth = this.config.getSize().width;
		int configHeight = this.config.getSize().height;
		int filterWidth = this.filter.getSize().width;
		int filterHeight = this.filter.getSize().height;

		// Update by sabi
		this.legend.setHeight((int)(height*0.66));
		this.legend.setTotalHeight(height);
		int legendWidth=this.legend.getSize().width;
		int legendHeight=this.legend.getSize().height;

		//this.pipRatio = (float) (height - pipHeight - 50)
		//		/ (float) (height - 60);
		//this.zoomRatio = (float) (width - zoomWidth - 40)
			//	/ (float) (width - 60);
		/*this.configRatio = (float) (width - configWidth - 40)
				/ (float) (width - 60);
		this.filterRatio = (float) (width - filterWidth - 40)
				/ (float) (width - 60);*/

		// Update by sabi
		//this.legendRatio=(float)(width-legendWidth-40)/(float)(width-60);


		this.normalBounds = new Rectangle(30, 30, width - 60, height - 60);
		/*this.zoomBounds = new Rectangle(10 + zoomWidth,
				30 + (int) ((1f - this.zoomRatio) * (height - 60)), width
						- zoomWidth - 40,
				(int) (this.zoomRatio * (height - 60)));*/
		this.configBounds = new Rectangle(20 + configWidth, 30, width - 50 - configWidth, height - 60);
		this.filterBounds = new Rectangle(20 + filterWidth, 30, width - 50 - filterWidth, height - 60);
		this.legendBounds = new Rectangle(20 + legendWidth, 30, width - 50 - legendWidth, height - 60);
		this.zoomBounds = new Rectangle(20 + zoomWidth, 30, width - 50 - zoomWidth, height - 60);
		//this.pipBounds = new Rectangle(60 + pipWidth, 20 + pipHeight, width - pipWidth - 90 , height - pipHeight - 50);
//		
		//this.legendBounds=new Rectangle(10+legendWidth,30+(int)((1f-this.legendRatio)*(height-60)),(width-legendWidth-40)*2,(int)(this.legendRatio*(height-60)*2));
//		System.out.println(this.legend.getSize().getHeight());

		//this.normalScale = graph.getScale();

		this.scroll.setBounds(this.normalBounds);
		//this.pipPanelON.setBounds(40, -10, pipWidth + 20, pipHeight + 30);
		//this.pipPanelOFF.setBounds(40, -10, 50, 40);
		//this.zoomPanelON.setBounds(0, 40, zoomWidth + 10, zoomHeight);
		//this.zoomPanelOFF.setBounds(-10, 40, 40, 70);
		this.filterPanelON.setBounds(0, 120, filterWidth, filterHeight + 20);
		this.filterPanelOFF.setBounds(-10, 120, 40, 65);
		this.configPanelON.setBounds(0, 200, configWidth, configHeight + 20);
		this.configPanelOFF.setBounds(-10, 200, 40, 120);
		this.legendPanelOFF.setBounds(-10,335,40,90);
		this.legendPanelON.setBounds(0, 335, legendWidth, legendHeight);
//		this.legend.setBounds(0,50,legendWidth,legendHeight);
//		this.legend.setSize(legendWidth,legendHeight);
//		this.legend.setBounds(legendBounds);

		/*int parametersHeight = this.parameters.getHeight();
		this.parametersPanelON.setBounds(width - 580,
				height - parametersHeight, 540, parametersHeight + 10);
		this.parametersPanelOFF.setBounds(width - 165, height - 30, 125, 40);*/

		// this.optionsPanelOFF.setBounds(width - 130, -10, 90, 40);

		//this.setup.setBounds(10, 10, 330, 420);
		/*this.setupPanelON.setBounds(width - 335, height - 370, 345, 330);
		this.setupPanelOFF.setBounds(width - 30, height - 115, 40, 75);

		this.fitnessPanel.setBounds(-10, height - 30, 160, 40);*/

		//double fitRatio = scaleToFit(this.graph, this.scroll, false);
		//this.zoomIP
			//	.setFitValue((int) Math.floor(fitRatio * this.zoomRatio * 100));
		/*this.config
			.setSureCurrValue((int) Math.floor(fitRatio * this.configRatio * 100));	*/	
		
		/*for (Entry<ViewInteractionPanel, Pair<JPanel, JPanel>> entry : panels.entrySet()) {
			JPanel panelOn = entry.getValue().getFirst();
			JPanel panelOff = entry.getValue().getSecond();
			ViewInteractionPanel panel = entry.getKey();

			if (locations.get(panel) == SwingConstants.SOUTH) {
				// south
				panelOn.setLocation(panelOn.getLocation().x, getHeight() - TAB_HEIGHT);
				panelOff.setBounds(panelOff.getLocation().x, getHeight() - TAB_HEIGHT, 75, 40);
			}
			//setSize(panel, panelOff, panelOn);
			//setLocation(panel, panelOff, panelOn);
		}*/
		setSizeZoom();
		this.zoomIP.setValue(100);

		setSizePIP();
		setSizeExport();
		updated();
		this.zoomIP.computeFitScale();
		this.zoomIP.fit();
	}
	
	
	public void updated() {
		JComponent newComponent = scalable.getComponent();
		if (newComponent != getComponent()) {
			scroll.setViewportView(newComponent);
			if (getComponent() instanceof Cleanable) {
				((Cleanable) getComponent()).cleanUp();
			}
			getComponent().removeMouseMotionListener(this);

			this.component = newComponent;
			getComponent().addMouseMotionListener(this);
			invalidate();
		}
		/*for (ViewInteractionPanel panel : panels.keySet()) {
			// HV: Do not call setScalableComponent now, as it changes the originalAttributeMap of the scalable.
			//			panel.setScalableComponent(scalable);
			panel.updated();
		}*/
	}

	
	private JComponent getComponent() {
		// 
		return this.component;
	}


	private boolean legendOn = false;
	public void showLegend(boolean status)
	{
		legendOn = status;
		this.legendPanelOFF.setVisible(!status);
		this.legendPanelOFF.setEnabled(!status);
		this.legendPanelON.setVisible(status);
		this.legendPanelON.setEnabled(status);
		if(status) {
			this.scroll.setBounds(this.legendBounds);
		} else {
			this.scroll.setBounds(this.normalBounds);
		}
		
		/*if(status)
		{
			this.scroll.setBounds(this.legendBounds);
			//graph.setScale(this.normalScale*this.zoomIP.getZoomValue());
		}
		else
		{
			this.scroll.setBounds(this.normalBounds);
			//graph.setScale(this.normalScale*this.zoomIP.getZoomValue());
		}*/
	}

	private boolean zoomOn = false;
	public void showZoom(boolean status) {

		zoomOn = status;
		this.zoomPanelOff.setVisible(!status);
		this.zoomPanelOff.setEnabled(!status);
		this.zoomPanelOn.setVisible(status);
		this.zoomPanelOn.setEnabled(status);
		if(status) {
			this.scroll.setBounds(this.zoomBounds);
		} else {
			this.scroll.setBounds(this.normalBounds);
		}
		
	}


	private ProMJGraph recomputeJGraph()
	{

        int eventsNumber=fCG.getEventsNumber();
        fCG.emptyGraph();
        double w = fCG.getSettings().getCausalityWeight();
        
        for(int i=0;i<eventsNumber;i++)
        {
            String nodeILabel=fCG.getActivitiesMappingStructures().get(i);
            HybridDirectedGraphNode nodeI=null,nodeJ=null;
            nodeI=fCG.addNode(nodeILabel);
            for(int j=0;j<eventsNumber;j++)
            {
                String nodeJLabel=fCG.getActivitiesMappingStructures().get(j);
                nodeJ=fCG.addNode(nodeJLabel);

				//double directSuccession=fCG.getDirectSuccessionCount(i,j);

				//double outputDirectSuccessionDependency=(fCG.getRowSumDirectDependency(i)>0) ? directSuccession/fCG.getRowSumDirectDependency(i):0.0;
				//double inputDirectSuccessionDependency=(fCG.getColumnSumDirectDependency(j)>0) ? directSuccession/fCG.getColumnSumDirectDependency(j):0.0;

				//double directSuccessionDependency = (0.5*outputDirectSuccessionDependency)+(0.5*inputDirectSuccessionDependency);
				//double directSuccessionDependency=(2*(directSuccession))/(fCG.getColumnSumDirectDependency(j)+fCG.getRowSumDirectDependency(i));
                double rel1 = fCG.getRel1(i,j);
                double rel2 = fCG.getRel2(i,j);
				//double causalityMetric=(fCG.getSettings().getCausalityWeight()*directSuccessionDependency)+((1-fCG.getSettings().getCausalityWeight())*abdependencyMetric);
               double causalityMetric=(w*fCG.getRel1(i,j))+((1-w)*rel2);

				// the sure/unsure edge can be added if and only if the
				// abdependency metrics is higher than the parallelism threshold, 
				// as otherwise it would mean that they are candidate for parallelism
				//BigDecimal bD = new BigDecimal(abdependencyMetric);
				double ODSD=fCG.getOutputDirectSuccessionDependency(i,j);
				double IDSD=fCG.getInputDirectSuccessionDependency(i,j);

				//NumberFormat nf=NumberFormat.getInstance();
				//nf.setMaximumFractionDigits(2);
				//nf.setMinimumFractionDigits(0);

				if(causalityMetric>=fCG.getSettings().getSureThreshold())
				{
					HybridDirectedSureGraphEdge se=fCG.addSureEdge(nodeI,nodeJ);
//					System.out.println("STRONG "+nodeI.getLabel()+" -> "+nodeJ.getLabel()+" "+directSuccessionDependency+" "+abdependencyMetric);
					se.setAbDependencyMetric(rel2);
					se.setCausalityMetric(causalityMetric);
					se.setDirectSuccession(fCG.getDirectSuccessionCount(i,j));
					se.setDirectSuccessionDependency(rel1);
					se.setIDSD_ODSD(ODSD, IDSD);
				}
				else if(causalityMetric>=fCG.getSettings().getQuestionMarkThreshold())
					{
						HybridDirectedUncertainGraphEdge ue=fCG.addUncertainEdge(nodeI,nodeJ);
//						System.out.println("WEAK"+nodeI.getLabel()+" -> "+nodeJ.getLabel()+" "+directSuccessionDependency+" "+abdependencyMetric);
						ue.setAbDependencyMetric(rel2);
						ue.setCausalityMetric(causalityMetric);
						ue.setDirectSuccession(fCG.getDirectSuccessionCount(i,j));
						ue.setDirectSuccessionDependency(rel1);
						ue.setIDSD_ODSD(ODSD, IDSD);
					}

               /* double abdependency = fCG.getMetrics().getABdependencyMeasuresAll(i, j);
                double dependencyAccepted = fCG.getMetrics().getDependencyMeasuresAccepted(i, j);
                //double longDependencyAccepted = fCG.getMetrics().getLongRangeDependencyMeasures(i, j);


                if (abdependency>=fCG.getSettings().getSureThreshold()){
                	fCG.addSureEdge(nodeI, nodeJ);
                    //System.out.println("SURE "+nodeI.getLabel()+" -> "+nodeJ.getLabel()+" "+abdependency+" "+dependencyAccepted+" "+longDependencyAccepted);
                } else if (abdependency>=fCG.getSettings().getQuestionMarkThreshold()){
                	fCG.addUncertainEdge(nodeI, nodeJ);
                    //System.out.println("UNCERTAIN"+nodeI.getLabel()+" -> "+nodeJ.getLabel()+" "+abdependency+" "+dependencyAccepted+" "+longDependencyAccepted);
                }*/ 
            }
        }
        
        for(int i=eventsNumber-1; i>=0; i--) {
        	String nodeILabel = fCG.getActivitiesMappingStructures().get(i);
			for (int j=eventsNumber-1; j>=0; j--) {
				String nodeJLabel = fCG.getActivitiesMappingStructures().get(j);
				double rel1 = this.fCG.getrel1LD().get(i, j);
				double rel2 = this.fCG.getrel2LD().get(i, j);
				
				double LD = (w*rel1) + ((1 - w)*rel2);
			    
				if (LD >= fCG.getSettings().getLongDepThreshold()) {
					HybridDirectedLongDepGraphEdge edge = fCG.addLongDepEdge(fCG.getNode(nodeILabel), fCG.getNode(nodeJLabel));
					edge.setAbDependencyMetric(rel2);
					edge.setCausalityMetric(LD);
					edge.setDirectSuccession(this.fCG.getEF().get(i, j));
					edge.setDirectSuccessionDependency(rel1);
					edge.setIDSD_ODSD(this.fCG.getOLD().get(i, j), this.fCG.getILD().get(i, j));
	
				}
			}
		}

        // Update by sabi
        // Now method takes also a string as parameter with the name of the required value on the arcs
        ProMJGraph jGraph=HybridCausalGraphVisualizer.createJGraph(fCG,new ViewSpecificAttributeMap(),new ProgressBarImpl(null),ArcLabel);
		return jGraph;
	}	
	
    private boolean configOn = false;
	public void showConfig(boolean status) {

		configOn = status;
		configPanelOFF.setVisible(!status);
		configPanelOFF.setEnabled(!status);
		configPanelON.setVisible(status);
		configPanelON.setEnabled(status);
		
		if(status) {
			this.scroll.setBounds(this.configBounds);
		} else {
			this.scroll.setBounds(this.normalBounds);
		}
		
	}
	
	
	private boolean filterOn = false;
	public void showFilter(boolean status) {

		filterOn = status;
		filterPanelOFF.setVisible(!status);
		filterPanelOFF.setEnabled(!status);
		filterPanelON.setVisible(status);
		filterPanelON.setEnabled(status);
		
		if(status) {
			this.scroll.setBounds(this.filterBounds);
		} else {
			this.scroll.setBounds(this.normalBounds);
		}
		
	
	}

	
	private boolean pipOn = false;
	public void showPIP(boolean status) {
		pipOn = status;
		pipPanelOff.setVisible(!status);
		pipPanelOff.setEnabled(!status);
		pipPanelOn.setVisible(status);
		pipPanelOn.setEnabled(status);
		pip.setVisible(status);
		pip.setEnabled(status);
		
		/*if(status) {
			this.scroll.setBounds(this.pipBounds);
		} else {
			this.scroll.setBounds(this.normalBounds);
		}*/
	}
	
	
	private boolean colorOn = false;
	public void showColor(boolean status) {
		colorOn = status;
		colorPanelOff.setVisible(!status);
		colorPanelOff.setEnabled(!status);
		colorPanelOn.setVisible(status);
		colorPanelOn.setEnabled(status);
		colorPanel.setVisible(status);
		colorPanel.setEnabled(status);
		
		/*if(status) {
			this.scroll.setBounds(this.pipBounds);
		} else {
			this.scroll.setBounds(this.normalBounds);
		}*/
	}
	

/*	private void showParameters(boolean status) {

		parametersPanelOFF.setVisible(!status);
		parametersPanelOFF.setEnabled(!status);
		parametersPanelON.setVisible(status);
		parametersPanelON.setEnabled(status);
	}*/

	// private void showOptions(boolean status){
	//
	// optionsPanelOFF.setVisible(!status);
	// optionsPanelOFF.setEnabled(!status);
	// optionsPanelON.setVisible(status);
	// optionsPanelON.setEnabled(status);
	// }
	/*private void showSetup(boolean status) {

		setupPanelOFF.setVisible(!status);
		setupPanelOFF.setEnabled(!status);
		setupPanelON.setVisible(status);
		setupPanelON.setEnabled(status);
	}*/

	private void redraw() {

		//int scrollPositionX = this.scroll.getHorizontalScrollBar().getValue();
		//int scrollPositionY = this.scroll.getVerticalScrollBar().getValue();
		this.scalable = graph;
		this.initGraph();
		/*this.remove(this.scroll);

		AdjustmentListener aListener = new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {

				repaintPIP(graph.getVisibleRect());
			}
		};
		MouseListener mListener = new MouseListener() {

			public void mouseClicked(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {

			}

			public void mouseExited(MouseEvent e) {

			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
			}
		};

		this.scroll = new JScrollPane(graph);
		SlickerDecorator.instance().decorate(this.scroll, Color.WHITE,
				Color.GRAY, Color.DARK_GRAY);
		this.scroll.getHorizontalScrollBar().addAdjustmentListener(aListener);
		this.scroll.getVerticalScrollBar().addAdjustmentListener(aListener);
		this.scroll.getHorizontalScrollBar().addMouseListener(mListener);
		this.scroll.getVerticalScrollBar().addMouseListener(mListener);
		this.scroll.setBorder(new DashedBorder(Color.LIGHT_GRAY));
		this.add(this.scroll);*/
		//int width = this.getSize().width;
		//int height = this.getSize().height;
     	//int zoomWidth = this.zoomIP.getSize().width;
		/*this.zoomRatio = (float) (width - zoomWidth - 40)
				/ (float) (width - 60);*/
		//this.normalBounds = new Rectangle(30, 30, width - 60, height - 60);
		/*this.zoomBounds = new Rectangle(10 + zoomWidth,
				30 + (int) ((1f - this.zoomRatio) * (height - 60)), width
						- zoomWidth - 40,
				(int) (this.zoomRatio * (height - 60)));*/
		this.remove(this.scroll);
		this.scroll.setViewportView(this.graph);
		//this.normalScale = graph.getScale();
		this.scroll.setBounds(this.normalBounds);
		this.graph.addMouseMotionListener(this);
		this.scroll.addMouseMotionListener(this);
		this.add(this.scroll);

		//double fitRatio = scaleToFit(this.graph, this.scroll, false);
		this.zoomIP.reset(scalable);
		//this.zoomIP.setValue((int) Math.floor(fitRatio * this.zoomRatio * 100));
		this.zoomIP.computeFitScale();
		this.zoomIP.fit();
		
		//this.scroll.getHorizontalScrollBar().setValue(scrollPositionX);
		//this.scroll.getVerticalScrollBar().setValue(scrollPositionY);
		this.removeViewInteractionPanel(this.pip);
		
		this.addPIPPanel();
		this.removeViewInteractionPanel(this.export);
		this.addExportPanel();
		this.removeViewInteractionPanel(this.colorPanel);
		this.addColorPanel();
	}
	

	
	public synchronized void removeViewInteractionPanel(ViewInteractionPanel panel) {

		//Remove the panelOn and panelOff panels from the pane.
		Pair<JPanel, JPanel> pair = panels.remove(panel);
		if (pair != null) {
			remove(pair.getFirst());
			remove(pair.getSecond());
		}

		//Modify the position counters to account for the removed interaction panels.
		//Integer location = locations.remove(panel);
	}
	
	
	private void initGraph() {

		this.graph.addGraphSelectionListener(new GraphSelectionListener() {

			@SuppressWarnings("unchecked")
			public void valueChanged(GraphSelectionEvent e) {

				DirectedGraphNode selectedCell = null;

				Object[] cells = e.getCells();
				Collection nodesAdded = new ArrayList<ProMGraphCell>();
				Collection edgesAdded = new ArrayList<ProMGraphEdge>();
				Collection nodesRemoved = new ArrayList<ProMGraphCell>();
				Collection edgesRemoved = new ArrayList<ProMGraphEdge>();
				Collection<?> nodes = graph.getProMGraph().getNodes();
				Collection<?> edges = graph.getProMGraph().getEdges();
				for (int i = 0; i < cells.length; i++) {
					Collection nodeList;
					Collection edgeList;

					boolean isCell = cells[i] instanceof ProMGraphCell;
					boolean isEdge = cells[i] instanceof ProMGraphEdge;

					if (e.isAddedCell(i)) {
						nodeList = nodesAdded;
						edgeList = edgesAdded;

						if (isCell && (selectedCell == null))
							selectedCell = ((ProMGraphCell) cells[i]).getNode();

					} else {
						nodeList = nodesRemoved;
						edgeList = edgesRemoved;
					}
					if (isCell) {
						DirectedGraphNode node = ((ProMGraphCell) cells[i])
								.getNode();
						if (nodes.contains(node)) {
							nodeList.add(node);
						}
					} else if (isEdge) {
						DirectedGraphEdge<?, ?> edge = ((ProMGraphEdge) cells[i])
								.getEdge();
						if (edges.contains(edge)) {
							edgeList.add(((ProMGraphEdge) cells[i]).getEdge());
						}
					}
				}
				SelectionListener.SelectionChangeEvent event = new SelectionListener.SelectionChangeEvent(
						nodesAdded, edgesAdded, nodesRemoved, edgesRemoved);
				for (SelectionListener listener : selectionListeners) {
					listener.SelectionChanged(event);
				}

			}

		});
		this.graph.setTolerance(4);

		this.graph.setMarqueeHandler(new BasicMarqueeHandler() {
			private boolean test(MouseEvent e) {
				return SwingUtilities.isRightMouseButton(e)
						&& (e.getModifiers() & InputEvent.ALT_MASK) == 0;

			}

			public boolean isForceMarqueeEvent(MouseEvent event) {
				if (test(event)) {
					return true;
				} else {
					return false;
				}
			}

			@Override
			public void mouseReleased(final MouseEvent e) {
				if (test(e)) {
					e.consume();
				} else {
					super.mouseReleased(e);
				}
			}

			@Override
			public void mousePressed(final MouseEvent e) {
				if (test(e)) {
					synchronized (graph.getProMGraph()) {
						// Check for selection.
						// If the cell that is being clicked is part of the
						// selection,
						// we use the current selection.
						// otherwise, we use a new selection
						Object cell = graph.getFirstCellForLocation(e.getX(), e
								.getY());

						Collection<DirectedGraphElement> sel;
						if (cell == null) {
							// Nothing selected
							graph.clearSelection();
							sel = new ArrayList<DirectedGraphElement>(0);
						} else if (graph.getSelectionModel().isCellSelected(
								cell)) {
							// the current selection contains cell
							// use that selection
							sel = getSelectedElements();
						} else {
							// the current selection does not contain cell.
							// reset the selection to [cell]
							sel = new ArrayList<DirectedGraphElement>(1);
							sel.add(getElementForLocation(e.getX(), e.getY()));
							graph.setSelectionCell(cell);
						}
						if (creator != null) {
							JPopupMenu menu = creator.createMenuFor(graph
									.getProMGraph(), sel);
							if (menu != null) {
								menu.show(graph, e.getX(), e.getY());
							}
						}
					}
				} else {
					super.mousePressed(e);
				}
			}
		});

		this.graph.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {

				/*if (hasNodeSelected) {

					joinsPanel.repaint();
					splitsPanel.repaint();
				}*/
			}
		});

		// Collapse any expandable nodes that claim they are collapsed
		// This is not handled previously.
//		for (Object n : graph.getGraphLayoutCache().getCells(true, false,
//				false, false)) {
//			if (((ProMGraphCell) n).getNode() instanceof Expandable) {
//				Expandable ex = (Expandable) ((ProMGraphCell) n).getNode();
//				
//				if (ex.isCollapsed()) { ex.collapse(); }
//			}
//		}
		
		GraphLayoutConnection con = new GraphLayoutConnection(this.graph.getProMGraph());

		ProMGraphModel model = new ProMGraphModel(graph.getProMGraph());
		this.pipGraph = new ProMJGraph(model, true, graph.getViewSpecificAttributes(), con) {

			private static final long serialVersionUID = -4671278744184554287L;

			/*@Override
			protected void changeHandled() {
				this.pip
				//scalePIP();
				//repaintPIP(graph.getVisibleRect());
			}*/
		};

		this.hasNodeSelected = false;
	}

	public double getScale() {
		return graph.getScale();
	}

	public void setScale(double d) {

		int b = (int) (100.0 * d);
		b = Math.max(b, 1);
		b = Math.min(b, MAX_ZOOM);
		this.zoomIP.setValue(b);
	}

	protected void repaintPIP(Rectangle2D rect) {

		double s = factorMultiplyGraphToPIP();
		double x = Math.max(1, s * rect.getX());
		double y = Math.max(1, s * rect.getY());
		double w = Math.min(s * rect.getWidth(), this.pip.getVisWidth() - 1);
		double h = Math.min(s * rect.getHeight(), this.pip.getVisHeight() - 1);
		rect = new Rectangle2D.Double(x, y, w, h);
		this.pip.setRect();
		this.pip.repaint();
	}

	public double factorMultiplyGraphToPIP() {
		return 0;// pipGraph.getScale() / graph.getScale();
	}

	protected void scalePIP() {
		//this.pipGraph.setScale(scaleToFit(this.pipGraph, this.pip, false));
	}

	/*protected double scaleToFit(ProMJGraph graph, Container container,
			boolean reposition) {

		Rectangle2D bounds = graph.getBounds();
		double x = bounds.getX();
		double y = bounds.getY();
		if (reposition) {

			graph.repositionToOrigin();
			x = 0;
			y = 0;
		}

		Dimension size = container.getSize();

		double ratio = Math.min(size.getWidth() / (bounds.getWidth() + x), size
				.getHeight()
				/ (bounds.getHeight() + y));

		return ratio;
	}*/

	public void paint(Graphics g) {
		super.paint(g);
	}

	public DirectedGraphElement getElementForLocation(double x, double y) {
		Object cell = graph.getFirstCellForLocation(x, y);
		if (cell instanceof ProMGraphCell) {
			return ((ProMGraphCell) cell).getNode();
		}
		if (cell instanceof ProMGraphEdge) {
			return ((ProMGraphEdge) cell).getEdge();
		}
		return null;
	}

	public Collection<DirectedGraphNode> getSelectedNodes() {
		List<DirectedGraphNode> nodes = new ArrayList<DirectedGraphNode>();
		for (Object o : graph.getSelectionCells()) {
			if (o instanceof ProMGraphCell) {
				nodes.add(((ProMGraphCell) o).getNode());
			}
		}
		return nodes;
	}

	public Collection<DirectedGraphEdge<?, ?>> getSelectedEdges() {
		List<DirectedGraphEdge<?, ?>> edges = new ArrayList<DirectedGraphEdge<?, ?>>();
		for (Object o : graph.getSelectionCells()) {
			if (o instanceof ProMGraphEdge) {
				edges.add(((ProMGraphEdge) o).getEdge());
			}
		}
		return edges;
	}

	public Collection<DirectedGraphElement> getSelectedElements() {
		List<DirectedGraphElement> elements = new ArrayList<DirectedGraphElement>();
		for (Object o : graph.getSelectionCells()) {
			if (o instanceof ProMGraphCell) {
				elements.add(((ProMGraphCell) o).getNode());
			} else if (o instanceof ProMGraphEdge) {
				elements.add(((ProMGraphEdge) o).getEdge());
			}
		}
		return elements;
	}

	public void cleanUp() {

		graph.cleanUp();
		//pipGraph.cleanUp();
	}

	public void stateChanged(ChangeEvent e) {

		Object source = e.getSource();

		if (source instanceof JSlider) {
			JSlider slider = ((JSlider) source);
			if (slider.getName()!=null && slider.getName().equalsIgnoreCase("SureSlider")){
				HybridCGMinerSettings settings = fCG.getSettings();
				settings.setSureThreshold(((JSlider) source).getValue() / 100.0);
				fCG.setSettings(settings);
				this.graph = recomputeJGraph();
				//this.normalScale = graph.getScale() / this.zoomRatio;
				//this.initGraph();
				redraw();
				/*scalable.setScale(this.zoomIP.fitZoom);
				getComponent().repaint();*/
				
				//showConfig(false);
				showConfig(true);				
				//graph.clearOffscreen();
				//graph.setScale(this.normalScale);
				//graph.refresh();
				//repaintPIP(graph.getVisibleRect());
				//this.normalScale = graph.getScale() / this.zoomRatio;
				
			}
			else if (slider.getName()!=null && slider.getName().equalsIgnoreCase("UnsureSlider")){
				HybridCGMinerSettings settings = fCG.getSettings();
				settings.setQuestionMarkThreshold(((JSlider) source).getValue() / 100.0);
				fCG.setSettings(settings);
				this.graph = recomputeJGraph();
				//this.normalScale = graph.getScale() / this.zoomRatio;
				//this.initGraph();
				redraw();
				/*scalable.setScale(this.zoomIP.fitZoom);
				getComponent().repaint();*/
				
				//showConfig(false);
				showConfig(true);
			}
			else if (slider.getName()!=null && slider.getName().equalsIgnoreCase("LongDepSlider")){
				HybridCGMinerSettings settings = fCG.getSettings();
				settings.setLongDepThreshold(((JSlider) source).getValue() / 100.0);
				fCG.setSettings(settings);
				this.graph = recomputeJGraph();
				//this.normalScale = graph.getScale() / this.zoomRatio;
				//this.initGraph();
				redraw();
				/*scalable.setScale(this.zoomIP.fitZoom);
				getComponent().repaint();*/
				
				//showConfig(false);
				showConfig(true);
				
			}
			
			else if (slider.getName()!=null && slider.getName().equalsIgnoreCase("CausalityWeightSlider")){
				HybridCGMinerSettings settings = fCG.getSettings();
				settings.setCausalityWeight(((JSlider) source).getValue() / 100.0);
				fCG.setSettings(settings);
				this.graph = recomputeJGraph();
				//this.normalScale = graph.getScale() / this.zoomRatio;
				//this.initGraph();
				redraw();
				/*scalable.setScale(this.zoomIP.fitZoom);
				getComponent().repaint();*/
				
				//showConfig(false);
				showConfig(true);
				
			}
			
			else if (slider.getName()!=null && slider.getName().equalsIgnoreCase("filterActivitySlider")){
				HybridCGMinerSettings settings = fCG.getSettings();
				settings.setFilterAcivityThreshold(((JSlider) source).getValue() / 100.0);

				XLog log = fCG.getUnfilteredLog();
				Color sureColor = fCG.getSureColor();
				Color unsureColor = fCG.getUnsureColor();
				Color longDepColor = fCG.getLongDepColor();
				XLogInfo logInfo = XLogInfoFactory.createLogInfo(log, settings.getClassifier());
				XLog filteredLog = LogFilterer.filterLogByActivityFrequency(log, logInfo, settings);
		        
		        TraceVariantsLog variants = new TraceVariantsLog(filteredLog, settings, settings.getTraceVariantsThreshold());
				fCG.emptyGraph();
		        HybridCGMiner miner = new HybridCGMiner(filteredLog, filteredLog.getInfo(settings.getClassifier()), variants, settings);
				miner.updateCG(fCG);
				fCG.setUnfilteredLog(log);
				fCG.updateSureColor(sureColor);
				fCG.updateUnsureColor(unsureColor);
				fCG.updateLongDepColor(longDepColor);
		        this.graph = HybridCausalGraphVisualizer.createJGraph(fCG,new ViewSpecificAttributeMap(),new ProgressBarImpl(null),ArcLabel);
				//this.graph = this.recomputeJGraph(newCG);
				redraw();
				/*scalable.setScale(this.zoomIP.fitZoom);
				getComponent().repaint();*/
				
				//showConfig(false);
				showFilter(true);
				
			}
			
			else if (slider.getName()!=null && slider.getName().equalsIgnoreCase("filterTraceSlider")){
				HybridCGMinerSettings settings = fCG.getSettings();
				settings.setTraceVariantsThreshold(((JSlider) source).getValue() / 100.0);


				XLog log = fCG.getUnfilteredLog();
				XLog filteredLog = fCG.getLog();
				Color sureColor = fCG.getSureColor();
				Color unsureColor = fCG.getUnsureColor();
				Color longDepColor = fCG.getLongDepColor();
		        TraceVariantsLog variants = new TraceVariantsLog(filteredLog, settings, settings.getTraceVariantsThreshold());
				HybridCGMiner miner = new HybridCGMiner(filteredLog, filteredLog.getInfo(settings.getClassifier()), variants, settings);
				fCG.emptyGraph();
				miner.updateCG(fCG);
				this.fCG.setUnfilteredLog(log);
				this.fCG.updateSureColor(sureColor);
				this.fCG.updateUnsureColor(unsureColor);
				this.fCG.updateLongDepColor(longDepColor);
		        this.graph = HybridCausalGraphVisualizer.createJGraph(fCG,new ViewSpecificAttributeMap(),new ProgressBarImpl(null),ArcLabel);
				
				
				redraw();
				/*scalable.setScale(this.zoomIP.fitZoom);
				getComponent().repaint();*/
				
				//showConfig(false);
				showFilter(true);
				
			}
			
			/*else {
				//scalable.setScale(((JSlider) source).getValue() / 100.0);
				//getComponent().repaint();
				repaintPIP(component.getVisibleRect());
				
				
				/*graph.setScale(((JSlider) source).getValue() / 100.0);
				repaintPIP(graph.getVisibleRect());
				this.normalScale = graph.getScale() / this.zoomRatio;

			}*/
		}
	}

	boolean lastArcLabelTwoLabels = false;
	public void actionPerformed(ActionEvent e)
	{
		// TODO Auto-generated method stub
		if(e.getSource() instanceof JRadioButton)
		{
			ArcLabel = ((JRadioButton)e.getSource()).getText();
			this.graph = HybridCausalGraphVisualizer.createJGraph(fCG, new ViewSpecificAttributeMap(), new ProgressBarImpl(null), ArcLabel);
			if (ArcLabel.equals("Split and join fractions")) {
				if (!lastArcLabelTwoLabels) {
					redraw();
					showLegend(true);
				}
				lastArcLabelTwoLabels = true;
			} else {
				if (lastArcLabelTwoLabels) {
					redraw();
					showLegend(true);
				} else {
					this.pip.initializeImage();
				}
				lastArcLabelTwoLabels = false;
			}
			//this.pip.initializeImage();
		}
	}

	@Override
	public void finalize() throws Throwable {

		try {
			cleanUp();
		} finally {
			super.finalize();
		}
	}

	public JViewport getViewport() {
		return scroll.getViewport();
	}
	
	public JScrollBar getHorizontalScrollBar() {
		return scroll.getHorizontalScrollBar();
	}

	public JScrollBar getVerticalScrollBar() {
		return scroll.getVerticalScrollBar();
	}


	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

     
	public void mouseMoved(MouseEvent e) {
		/*Point p = e.getPoint();
		if (e.getComponent() == getComponent()) {
			Point p2 = scroll.getViewport().getViewPosition();
			p.setLocation(p.getX() - p2.getX() + TAB_HEIGHT, p.getY() - p2.getY() + TAB_HEIGHT);
		}
		Component c = findComponentAt(p.x, p.y);
		if (c == null) {
			return;
		}
		if (c == this || isChild(c, getComponent())) {
			turnPanelOff();
		} else {
			// walk through the off panels
			for (Entry<ViewInteractionPanel, Pair<JPanel, JPanel>> entry : panels.entrySet()) {
				JPanel panelOn = entry.getValue().getFirst();
				JPanel panelOff = entry.getValue().getSecond();
				ViewInteractionPanel panel = entry.getKey();
				if (panelOff.getBounds().contains(p)) {
					if (panelOff == c || isParentPanel(c, panelOff)) {

						setSize(entry.getKey(), panelOff, panelOn);
						setLocation(entry.getKey(), panelOff, panelOn);
						turnPanelOff();
						panel.willChangeVisibility(true);
						panelOn.setVisible(true);
						panelOn.setEnabled(true);
						panelOff.setVisible(false);
						panelOff.setEnabled(false);
						visiblePanel = entry.getKey();
					}
				}
			}
		}*/
		double x = e.getLocationOnScreen().x;
		double y = e.getLocationOnScreen().y;
		
		if(filterOn) {
			int locX = this.filterPanelON.getLocationOnScreen().x;
			int locY = this.filterPanelON.getLocationOnScreen().y;
			if (x < locX || x > this.filterPanelON.getWidth() + locX || y < locY || y > locY + this.filterPanelON.getHeight()) {
				showFilter(false);
			}
		}
		
		if(configOn) {
			int locX = this.configPanelON.getLocationOnScreen().x;
			int locY = this.configPanelON.getLocationOnScreen().y;
			if (x < locX || x > this.configPanelON.getWidth() + locX || y < locY || y > locY + this.configPanelON.getHeight()) {
			//if (x > this.configPanelON.getWidth() || y < 122 || y > 122 + configPanelON.getHeight()) {
				//System.out.println(this.configPanelON.getWidth() + ", 122,  " + (int) (122 + configPanelON.getHeight()));
				showConfig(false);
				//System.out.println("Config Closed at x = " + x + ", y= " + y);
			}
		}

		if(legendOn) {
			int locX = this.legendPanelON.getLocationOnScreen().x;
			int locY = this.legendPanelON.getLocationOnScreen().y;
			if (x < locX || x > this.legendPanelON.getWidth() + locX || y < locY || y > locY + this.legendPanelON.getHeight()) {
			//if (x > this.legendPanelON.getWidth() || y < 260 || y > 260 + legendPanelON.getHeight()) {
				//System.out.println(this.legendPanelON.getWidth() + ", 260,   " + (int) (260 + legendPanelON.getHeight()));
				showLegend(false);
				//System.out.println("Legend Closed at x = " + x + ", y= " + y);
			}
		}
	
		if (zoomOn) {
			int locX = this.zoomPanelOn.getLocationOnScreen().x;
			int locY = this.zoomPanelOn.getLocationOnScreen().y;
			if (x < locX || x > this.zoomPanelOn.getWidth() + locX || y < locY || y > locY + this.zoomPanelOn.getHeight()) {
				//System.out.println(this.zoomPanelOn.getWidth() + ", 40,  " + (int) (40 + zoomPanelOn.getHeight()));
				showZoom(false);
				//System.out.println("Zoom Closed at x = " + x + ", y= " + y);
			}
		}
		
		if (pipOn) {
			int locX = this.pipPanelOn.getLocationOnScreen().x;
			int locY = this.pipPanelOn.getLocationOnScreen().y;
			if (x < locX || x > locX + this.pipPanelOn.getWidth() || y < locY || y > this.pipPanelOn.getHeight() + locY) {
				//System.out.println("40, " + this.pipPanelOn.getWidth() + ", -10,  " + (int) (pipPanelOn.getHeight() - 10));
				showPIP(false);
				//System.out.println("pip Closed at x = " + x + ", y= " + y);
			}
		}
		
		if (colorOn) {
			int locX = this.colorPanelOn.getLocationOnScreen().x;
			int locY = this.colorPanelOn.getLocationOnScreen().y;
			if (x < locX || x > locX + this.colorPanelOn.getWidth() || y < locY || y > this.colorPanelOn.getHeight() + locY) {
				//System.out.println("40, " + this.pipPanelOn.getWidth() + ", -10,  " + (int) (pipPanelOn.getHeight() - 10));
				showColor(false);
				//System.out.println("pip Closed at x = " + x + ", y= " + y);
			}
		}
		
		//this.configPanelON.setBounds(0, 122, configWidth + 10, configHeight+30);
        //this.legendPanelON.setBounds(0,260,legendWidth,legendHeight);
		//this.zoomPanelOn.setLocation(-10, 40);
		//pipPanelOff.setBounds(40, -10, 50, 40);
	}
	
	private boolean isParentPanel(Component topmost, JPanel panel) {

		Container c = topmost.getParent();
		while (c != null) {

			if (c == panel) {
				return true;
			}

			c = c.getParent();
		}

		return false;
	}
	
	
	private boolean isChild(Component c, final Component parent) {
		if (c == parent) {
			return true;
		} else if (c.getParent() == null) {
			return false;
		} else {
			return (c.getParent() == parent) || isChild(c.getParent(), parent);
		}
	}

	private void turnPanelOff() {
		if (visiblePanel != null) {
			JPanel panelOn = panels.get(visiblePanel).getFirst();
			JPanel panelOff = panels.get(visiblePanel).getSecond();
			visiblePanel.willChangeVisibility(false);
			panelOn.setVisible(false);
			panelOn.setEnabled(false);
			panelOff.setVisible(true);
			panelOff.setEnabled(true);
			visiblePanel = null;
		}	
	}

	
	public PIPInteractionPanel getpip() {
		return this.pip;
	}


}