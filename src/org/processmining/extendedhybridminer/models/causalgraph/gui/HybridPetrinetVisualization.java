package org.processmining.extendedhybridminer.models.causalgraph.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
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
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.BasicMarqueeHandler;
import org.processmining.extendedhybridminer.models.hybridpetrinet.ExtendedHybridPetrinet;
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

public class HybridPetrinetVisualization extends JLayeredPane implements Cleanable, ChangeListener, MouseMotionListener,
UpdateListener, HybridGraphVisualization {


	private static final long serialVersionUID = -7826982030435169712L;

	/**
	 * The maximal zoom factor for the primary view on the transition system.
	 */
	public static final int MAX_ZOOM = 800;

	/**
	 * The access to scalable methods of primary view
	 */
	protected final ScalableComponent scalable;

	/**
	 * The primary view
	 */
	private JComponent component;

	/**
	 * The scroll pane containing the primary view on the transition system.
	 */
	protected JScrollPane scroll;
	private ViewInteractionPanel visiblePanel = null;
	private Map<ViewInteractionPanel, Pair<JPanel, JPanel>> panels = new HashMap<ViewInteractionPanel, Pair<JPanel, JPanel>>();
	private Map<ViewInteractionPanel, Integer> locations = new HashMap<ViewInteractionPanel, Integer>();
	//private JButton[] buttons = new JButton[4];
	public final static int TAB_HEIGHT = 30;
	public final static int TAB_WIDTH = 120;

	/**
	 * The bounds for the primary view on the transition system.
	 */
	private Rectangle normalBounds, zoomBounds;	
	protected SlickerFactory factory;
	protected SlickerDecorator decorator;
	protected ProMJGraph graph;
	//private double normalScale;
	private boolean hasNodeSelected;
    private List<SelectionListener<?, ?>> selectionListeners = new ArrayList<SelectionListener<?, ?>>(0);
	private ContextMenuCreator creator = null;
	//private ColorPanel surePlaceColorPanel;
	//private JPanel surePlaceColorPanel, sureColorPanel, unsureColorPanel;
	private ExtendedHybridPetrinet hPN;
	private PIPInteractionPanel pip;
	private ZoomInteractionPanel zoomIP;
	private ExportInteractionPanel export;

	private ColorPanel colorPanel;
	private JPanel colorPanelOn, colorPanelOff ,zoomPanelOn, zoomPanelOff, pipPanelOn, 
	pipPanelOff, exportPanelOn, exportPanelOff;
	//private double normalScale;
	
	
	public void updateColor() {
		try {
			this.graph = HybridPetrinetVisualizer.createJGraph(this.hPN, new ViewSpecificAttributeMap(), new ProgressBarImpl(null));
		} catch (Exception e) {
			e.printStackTrace();
		}
		scalable.setScale(scalable.getScale());
		this.pip.initializeImage();
	}
	
	
	public HybridPetrinetVisualization(final ProMJGraph jgraph, ExtendedHybridPetrinet hPN) {
		// TODO Auto-generated constructor stub
		this.scalable = jgraph;
		this.component = jgraph.getComponent();
		scalablePanel(jgraph);
		this.setLayout(null);
		this.graph = jgraph;
		this.hPN = hPN;
		factory = SlickerFactory.instance();
		//SlickerDecorator decorator = SlickerDecorator.instance();
		this.initGraph();
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

		
		/*this.surePlaceColorPanel=factory.createRoundedPanel(15,Color.LIGHT_GRAY);
		this.surePlaceColorPanel=factory.createRoundedPanel(15,Color.DARK_GRAY);
		this.surePlaceColorPanel.setLayout(null);
		this.surePlaceColorPanel.setLayout(null);
		this.surePlaceColorPanel.setVisible(true);
		this.surePlaceColorPanel.setEnabled(true);
		this.surePlaceColorPanel.setBounds(105,0,80,30);
		JLabel surePlacePanel = factory.createLabel("Places");
		surePlacePanel.setForeground(Color.WHITE);
		surePlacePanel.setFont(new java.awt.Font("Dialog",java.awt.Font.BOLD,18));
//		colorPanel.setUI(new VerticalLabelUI(false));
		this.surePlaceColorPanel.add(surePlacePanel);
		surePlacePanel.setBounds(10,0,80,30);
		this.surePlaceColorPanel.addMouseListener(new MouseListener()
		{
			public void mouseClicked(MouseEvent e)
			{
				Color c=JColorChooser.showDialog(new Frame(),"Pick a Color for Places", hPN.getSurePlaceColor());
				System.out.println("Sure "+c.toString());
				hPN.updateSurePlaceColor(c);
				updateColor();	
			}
			public void mousePressed(MouseEvent e)
			{
				// TODO Auto-generated method stub	
			}
			public void mouseReleased(MouseEvent e)
			{
				// TODO Auto-generated method stub	
			}
			public void mouseEntered(MouseEvent e)
			{
				// TODO Auto-generated method stub	
			}
			public void mouseExited(MouseEvent e)
			{
				// TODO Auto-generated method stub	
			}
		});*/
		
		/*this.sureColorPanel=factory.createRoundedPanel(15,Color.LIGHT_GRAY);
		this.sureColorPanel=factory.createRoundedPanel(15,Color.DARK_GRAY);
		this.sureColorPanel.setLayout(null);
		this.sureColorPanel.setLayout(null);
		this.sureColorPanel.setVisible(true);
		this.sureColorPanel.setEnabled(true);
		this.sureColorPanel.setBounds(200,0,200,30);
		JLabel surePanel=factory.createLabel("Informal Sure Edges");
		surePanel.setForeground(Color.WHITE);
		surePanel.setFont(new java.awt.Font("Dialog",java.awt.Font.BOLD,18));
//		colorPanel.setUI(new VerticalLabelUI(false));
		this.sureColorPanel.add(surePanel);
		surePanel.setBounds(10,0,200,30);
		this.sureColorPanel.addMouseListener(new MouseListener()
		{
			public void mouseClicked(MouseEvent e)
			{
				// TODO Auto-generated method stub
				Color c=JColorChooser.showDialog(new Frame(),"Pick a Color for Informal Sure Edges", hPN.getSureColor());
				System.out.println("Sure "+c.toString());
				hPN.updateSureColor(c);
				updateColor();
				
				//redraw();
				//scroll.getHorizontalScrollBar().setValue(scroll.getHorizontalScrollBar().getValue()+1);
				//scroll.getHorizontalScrollBar().setValue(scroll.getHorizontalScrollBar().getValue()-1);
			}
			public void mousePressed(MouseEvent e)
			{
				// TODO Auto-generated method stub	
			}
			public void mouseReleased(MouseEvent e)
			{
				// TODO Auto-generated method stub	
			}
			public void mouseEntered(MouseEvent e)
			{
				// TODO Auto-generated method stub	
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
		this.unsureColorPanel.setBounds(415,0,145,30);
		
		JLabel unsurePanel=factory.createLabel("Unsure Edges");
		unsurePanel.setForeground(Color.WHITE);
		unsurePanel.setFont(new java.awt.Font("Dialog",java.awt.Font.BOLD,18));
//		colorPanel.setUI(new VerticalLabelUI(false));
		this.unsureColorPanel.add(unsurePanel);
		unsurePanel.setBounds(10,0,140,30);

		this.unsureColorPanel.addMouseListener(new MouseListener()
		{
			public void mouseClicked(MouseEvent e)
			{
				// TODO Auto-generated method stub
				Color c=JColorChooser.showDialog(new Frame(),"Pick a Color for Unsure Edges", hPN.getUnsureColor());
				System.out.println("Unsure "+c.toString());
				hPN.updateUnsureColor(c);
				updateColor();
				//redraw();
				//scroll.getHorizontalScrollBar().setValue(scroll.getHorizontalScrollBar().getValue()+1);
				//scroll.getHorizontalScrollBar().setValue(scroll.getHorizontalScrollBar().getValue()-1);
			}
			public void mousePressed(MouseEvent e)
			{
				// TODO Auto-generated method stub	
			}
			public void mouseReleased(MouseEvent e)
			{
				// TODO Auto-generated method stub	
			}
			public void mouseEntered(MouseEvent e)
			{
				// TODO Auto-generated method stub	
			}
			public void mouseExited(MouseEvent e)
			{
				// TODO Auto-generated method stub	
			}
		});*/

		//this.surePlaceColorPanel = new ColorPanel(this, hPN);
		//this.addColorPanel(this.surePlaceColorPanel, "place");
		/*this.add(this.surePlaceColorPanel);
		this.add(this.sureColorPanel);
		this.add(this.unsureColorPanel);*/
		//this.pip = new PIPInteractionPanel(this);
		//this.addViewInteractionPanel(this.pip, SwingConstants.NORTH);
		this.addPIPPanel();
		this.addColorPanel();
		this.addZoomPanel();
		this.addExportPanel();;
		//this.zoom = new ZoomInteractionPanel(this, ScalableViewPanel.MAX_ZOOM);
		//this.addViewInteractionPanel(this.zoom, SwingConstants.WEST);
		//this.addViewInteractionPanel(new ExportInteractionPanel(this), SwingConstants.SOUTH);
	
		this.setBackground(Color.WHITE);
		this.validate();
		this.repaint();
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
				showColor(false);
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
		
		exportPanelOff.setBounds(40, getHeight() - TAB_HEIGHT, 75, 40);
		
		this.exportPanelOff.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent e) {
				
			}

			public void mouseEntered(MouseEvent e) {
				
			}

			public void mouseExited(MouseEvent e) {
				
			}

			public void mousePressed(MouseEvent e) {
				showColor(false);
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
		this.colorPanel = new ColorPanel(this, this.hPN);
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
		add(scroll, JLayeredPane.DEFAULT_LAYER);

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

		validate();
		repaint();
		
	}	/*private void resize() {

		/*int width = this.getSize().width;
		int height = this.getSize().height;

		//int pipHeight = 250;
		//int pipWidth = (int) ((float) width / (float) height * pipHeight);
		//this.pip.setBounds(10, 20, pipWidth, pipHeight);

		//this.zoom.setHeight((int) (height * 0.66));

		//int zoomWidth = this.zoom.getSize().width;
		//int zoomHeight = this.zoom.getSize().height;

		//this.pipRatio = (float) (height - pipHeight - 50)
		//		/ (float) (height - 60);
		this.zoomRatio = (float) (width - zoomWidth - 40)
				/ (float) (width - 60);
		this.normalBounds = new Rectangle(30, 30, width - 60, height - 60);
		this.zoomBounds = new Rectangle(10 + zoomWidth,
				30 + (int) ((1f - this.zoomRatio) * (height - 60)), width
						- zoomWidth - 40,
				(int) (this.zoomRatio * (height - 60)));
		/*this.pipBounds = new Rectangle(
				30 + ((int) ((1f - this.pipRatio) * (width - 60))),
				20 + pipHeight, (int) (this.pipRatio * (width - 60)), height
						- pipHeight - 50);

		this.normalScale = graph.getScale();

		/*this.scroll.setBounds(this.normalBounds);
		this.pipPanelON.setBounds(40, -10, pipWidth + 20, pipHeight + 30);
		this.pipPanelOFF.setBounds(40, -10, 50, 40);*/
		//this.zoomPanelON.setBounds(0, 40, zoomWidth + 10, zoomHeight);
		//this.zoomPanelOFF.setBounds(-10, 40, 40, 72);

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
		//this.zoom
		//		.setFitValue((int) Math.floor(fitRatio * this.zoomRatio * 100));
		//this.scalePIP();

		/*this.joinsPanel.setBounds((int) (width / 2f) - 305, height - 300, 300,
				310);
		this.joins.setSize(300 - 20, 300 - 20);
		this.joins.setBounds(10, 10, 300 - 20, 300 - 20);

		this.splitsPanel.setBounds((int) (width / 2f) + 5, height - 300, 300,
				310);
		this.splits.setSize(300 - 20, 300 - 20);
		this.splits.setBounds(10, 10, 300 - 20, 300 - 20);
	}*/

	/*private void showZoom(boolean status) {

		zoomPanelOFF.setVisible(!status);
		zoomPanelOFF.setEnabled(!status);
		zoomPanelON.setVisible(status);
		zoomPanelON.setEnabled(status);

		if (status) {

			//this.scroll.setBounds(this.zoomBounds);
			graph.setScale(this.normalScale * this.zoomRatio);
		} else {

			//this.scroll.setBounds(this.normalBounds);
			graph.setScale(this.normalScale);
		}

	}*/

	/*private void showPIP(boolean status) {

		/*pipPanelOFF.setVisible(!status);
		pipPanelOFF.setEnabled(!status);
		pipPanelON.setVisible(status);
		pipPanelON.setEnabled(status);

		if (status) {

			this.scroll.setBounds(this.pipBounds);
			graph.setScale(this.normalScale * this.pipRatio);
		} else {

			this.scroll.setBounds(this.normalBounds);
			graph.setScale(this.normalScale);
		}
	}*/

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

	/*private void redraw() {

		int scrollPositionX = this.scroll.getHorizontalScrollBar().getValue();
		int scrollPositionY = this.scroll.getVerticalScrollBar().getValue();

		//AnnotatedVisualizationGenerator generator = new AnnotatedVisualizationGenerator();
		//HeuristicsNetGraph hng = generator.generate(this.net, this.setup
		//		.getSettings());

		//this.graph = FuzzyCausalGraphVisualizer.createJGraph(hng, new ViewSpecificAttributeMap(), null);

		//this.initGraph();

		this.remove(this.scroll);

		AdjustmentListener aListener = new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {

				repaintPIP(graph.getVisibleRect());
			}
		};
		MouseListener mListener = new MouseListener() {

			public void mouseClicked(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {

				if (hasNodeSelected) {

				}
			}

			public void mouseExited(MouseEvent e) {

				if (hasNodeSelected) {

				}
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
			}
		};

		this.scroll = new JScrollPane(graph);
		//SlickerDecorator.instance().decorate(this.scroll, Color.WHITE,
		//		Color.GRAY, Color.DARK_GRAY);
		this.scroll.getHorizontalScrollBar().addAdjustmentListener(aListener);
		this.scroll.getVerticalScrollBar().addAdjustmentListener(aListener);
		this.scroll.getHorizontalScrollBar().addMouseListener(mListener);
		this.scroll.getVerticalScrollBar().addMouseListener(mListener);
		this.scroll.setBorder(new DashedBorder(Color.LIGHT_GRAY));
		this.add(this.scroll);
		this.scroll.setBounds(this.normalBounds);

		//this.pip.setScalableComponent(this.graph);
		//this.pip.setParentScroll(this.scroll);
		this.pip.setScalableComponent(graph);

		this.scalePIP();
		this.initGraph();
		//this.graph.setScale(this.normalScale);

		this.scroll.getHorizontalScrollBar().setValue(scrollPositionX);
		this.scroll.getVerticalScrollBar().setValue(scrollPositionY);

	}*/

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
							JPopupMenu menu = creator.createMenuFor(graph.getProMGraph(), sel);
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

				if (hasNodeSelected) {


				}
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
		/*this.pipGraph = new ProMJGraph(model, true, graph.getViewSpecificAttributes(), con) {

			private static final long serialVersionUID = -4671278744184554287L;

			@Override
			protected void changeHandled() {

				scalePIP(); 
				repaintPIP(graph.getVisibleRect());
			}
		};*/

		this.hasNodeSelected = false;
	}

	/*public double getScale() {
		return graph.getScale();
	}*/

	/*public void setScale(double d) {

		int b = (int) (100.0 * d);
		b = Math.max(b, 1);
		b = Math.min(b, MAX_ZOOM);
		this.zoom.setValue(b);
	}*/

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

	/*public double factorMultiplyGraphToPIP() {
		return graph.getScale() / graph.getScale();
	}*/

	/*protected void scalePIP() {
		this.graph.setScale(scaleToFit(this.graph, this.pip, false));
	}*/

	protected double scaleToFit(ProMJGraph pipGraph2, Container container,
			boolean reposition) {

		Rectangle2D bounds = pipGraph2.getBounds();
		double x = bounds.getX();
		double y = bounds.getY();
		if (reposition) {

			pipGraph2.repositionToOrigin();
			x = 0;
			y = 0;
		}

		Dimension size = container.getSize();

		double ratio = Math.min(size.getWidth() / (bounds.getWidth() + x), size
				.getHeight()
				/ (bounds.getHeight() + y));

		return ratio;
	}

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

			scalable.setScale(((JSlider) source).getValue() / 100.0);
			getComponent().repaint();
			repaintPIP(component.getVisibleRect());
			//repaintPIP(graph.getVisibleRect());

			
			//this.normalScale = graph.getScale();
		}
	}

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void finalize() throws Throwable {

		try {
			cleanUp();
		} finally {
			super.finalize();
		}
	}
	
	/*public JScrollBar getHorizontalScrollBar() {
		return scroll.getHorizontalScrollBar();
	}

	
	public JScrollBar getVerticalScrollBar() {
		return scroll.getVerticalScrollBar();
	}*/
	
	
	public JViewport getViewport() {
		return scroll.getViewport();
	}
	
	
	public double factorMultiplyGraphToPIP() {
		// TODO Auto-generated method stub
		return 0;
	}
    
	/*public synchronized void addViewInteractionPanel(ViewInteractionPanel panel, int location) {
		panel.setScalableComponent(scalable);
		//panel.setParent(this);
        System.out.println("Override");
		JPanel panelOn = factory.createRoundedPanel(15, Color.LIGHT_GRAY);
		JPanel panelOff = factory.createRoundedPanel(15, Color.DARK_GRAY);
		panelOn.setLayout(null);
		panelOff.setLayout(null);

		panelOn.add(panel.getComponent());
		panelOn.setVisible(false);
		panelOn.setEnabled(false);
		panelOff.setVisible(true);
		panelOff.setEnabled(true);
		JLabel panelTitle = factory.createLabel(panel.getPanelName());
		panelTitle.setHorizontalTextPosition(SwingConstants.CENTER);
		panelTitle.setVerticalTextPosition(SwingConstants.CENTER);
		panelTitle.setForeground(Color.WHITE);
		panelTitle.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 16));
		panelOff.add(panelTitle);

		panels.put(panel, new Pair<JPanel, JPanel>(panelOn, panelOff));
		locations.put(panel, location);

		switch (location) {
			case SwingConstants.NORTH : {
				panelTitle.setBounds(10, 10, 30, 30);
				panelOn.setLocation(40, -10);
				panel.getComponent().setLocation(10, 20);
				panelOff.setBounds(40, -10, 50, 40);
				break;
			}
			/*case SwingConstants.EAST : {
				panelTitle.setBounds(0, 5, TAB_HEIGHT, TAB_WIDTH - 15);
				panelTitle.setUI(new VerticalLabelUI(true));
				panelOn.setLocation(getWidth() - TAB_HEIGHT, TAB_HEIGHT + 10 + east * TAB_WIDTH);
				panelOff.setBounds(getWidth() - TAB_HEIGHT, TAB_HEIGHT + 10 + east * TAB_WIDTH, TAB_HEIGHT + 10,
						TAB_WIDTH - 5);
				panel.getComponent().setLocation(10, 10);
				break;
			}
			case SwingConstants.WEST : {
				panelTitle.setBounds(10, 10, 30, 55);
				panelTitle.setUI(new VerticalLabelUI(true));
				panelOn.setLocation(-10, 40);
				panelOff.setBounds(-10, 40, 40, 65);
				panel.getComponent().setLocation(20, 10);
				break;
			}
			default : {
				//SOUTH
				panelTitle.setBounds(10, 0, 55, 30);
				panelOn.setLocation(40, getHeight() - TAB_HEIGHT);
				panelOff.setBounds(40, getHeight() - TAB_HEIGHT, 75, 40);
				panel.getComponent().setLocation(10, 10);
			}
		}
		setSize(panel, panelOff, panelOn);
		setLocation(panel, panelOff, panelOn);

		add(panelOn, JLayeredPane.PALETTE_LAYER);
		add(panelOff, JLayeredPane.PALETTE_LAYER);
		panel.updated();
	}*/
	
	
	/*public synchronized void addColorPanel(ViewInteractionPanel panel, String location) {
		panel.setScalableComponent(scalable);
		//panel.setParent(this);
        System.out.println("Override");
		JPanel panelOn = factory.createRoundedPanel(15, Color.LIGHT_GRAY);
		JPanel panelOff = factory.createRoundedPanel(15, Color.DARK_GRAY);
		panelOn.setLayout(null);
		panelOff.setLayout(null);

		panelOn.add(panel.getComponent());
		panelOn.setVisible(false);
		panelOn.setEnabled(false);
		panelOff.setVisible(true);
		panelOff.setEnabled(true);
		JLabel panelTitle; 
		switch (location) {
			case "place" : {
				panelTitle = factory.createLabel("Place");
			    break;
			} default : {
				panelTitle = factory.createLabel("Default");
			}
		}
		panelTitle.setHorizontalTextPosition(SwingConstants.CENTER);
		panelTitle.setVerticalTextPosition(SwingConstants.CENTER);
		panelTitle.setForeground(Color.WHITE);
		panelTitle.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 18));
		panelOff.add(panelTitle);

		panels.put(panel, new Pair<JPanel, JPanel>(panelOn, panelOff));
		locations.put(panel, new Integer(58));

		switch (location) {
			case "place" : {
				panelTitle.setBounds(10,0,80,30);
				panelOn.setLocation(40, -10);
				panel.getComponent().setLocation(10, 20);
				panelOff.setBounds(105,0,80,30);
				break;
			} default : {
				//SOUTH
				panelTitle.setBounds(10, 0, 55, 30);
				panelOn.setLocation(40, getHeight() - TAB_HEIGHT);
				panelOff.setBounds(40, getHeight() - TAB_HEIGHT, 40, 40);
				panel.getComponent().setLocation(10, 10);
				south++;

			}
		}
		setSize(panel, panelOff, panelOn);
		setLocation(panel, panelOff, panelOn);

		add(panelOn, JLayeredPane.PALETTE_LAYER);
		add(panelOff, JLayeredPane.PALETTE_LAYER);
		panel.updated();
	}*/

	/**
	 * Remove a previously added interaction panel from the ScalableViewPanel.
	 * 
	 * This can be used to remove an interaction panel from the
	 * ScalableViewPanel. If the interaction panel does not exist, nothing will
	 * be removed.
	 * 
	 * @param panel
	 *            The panel that should be removed.
	 */
	public synchronized void removeViewInteractionPanel(ViewInteractionPanel panel) {

		//Remove the panelOn and panelOff panels from the pane.
		Pair<JPanel, JPanel> pair = panels.remove(panel);
		if (pair != null) {
			remove(pair.getFirst());
			remove(pair.getSecond());
		}

		//Modify the position counters to account for the removed interaction panels.
		Integer location = locations.remove(panel);
		if (location != null) {

			switch (location) {
				case SwingConstants.NORTH :
					break;
				case SwingConstants.EAST :
					break;
				case SwingConstants.SOUTH :
					break;
				case SwingConstants.WEST :
					break;
				default :
					System.err.println("Unknown interaction panel location. No position counters have been updated.");
					break;
			}
		}

		//Repaint to get rid of the old panel tab pictures.
		repaint();
	}

	/**
	 * List all registered interaction panels and their locations.
	 * 
	 * @return Map of interaction panels and their locations.
	 */
	

	private boolean isChild(Component c, final Component parent) {
		if (c == parent) {
			return true;
		} else if (c.getParent() == null) {
			return false;
		} else {
			return (c.getParent() == parent) || isChild(c.getParent(), parent);
		}
	}

	public synchronized void mouseMoved(MouseEvent e) {
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

	public void mouseDragged(MouseEvent e) {
		// ignore!

	}

	/**
	 * Adds a button to one of the positions on the screen indicated by the
	 * location parameter. Should be SwingConstants.NORTH_EAST,
	 * SwingConstants.SOUTH_EAST SwingConstants.NORTH_WEST,
	 * SwingConstants.SOUTH_WEST
	 * 
	 * @param label
	 * @param listener
	 * @param location
	 */
	/*public void addButton(JLabel label, ActionListener listener, int location) {
		JButton button = factory.createButton("");
		label.setHorizontalTextPosition(SwingConstants.CENTER);
		label.setVerticalTextPosition(SwingConstants.CENTER);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setVerticalAlignment(SwingConstants.CENTER);
		label.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 18));
		label.setForeground(Color.WHITE);
		label.setBorder(BorderFactory.createEmptyBorder());
		label.setOpaque(false);

		button.setLayout(null);
		button.setBorder(BorderFactory.createEmptyBorder());
		button.add(label);
		button.setToolTipText("Reposition the graph to the origin");
		button.addActionListener(listener);

		label.setBounds(0, 0, TAB_HEIGHT - 5, TAB_HEIGHT - 5);

		switch (location) {
			case SwingConstants.NORTH_WEST :
				button.setBounds(0, 0, TAB_HEIGHT, TAB_HEIGHT);
				buttons[0] = button;
				break;
			case SwingConstants.NORTH_EAST :
				button.setBounds(0, getWidth() - TAB_HEIGHT, TAB_HEIGHT, TAB_HEIGHT);
				buttons[1] = button;
				break;
			case SwingConstants.SOUTH_EAST :
				button.setBounds(getHeight() - TAB_HEIGHT, getWidth() - TAB_HEIGHT, TAB_HEIGHT, TAB_HEIGHT);
				buttons[2] = button;
				break;
			default :
				// SOUTH_WEST
				button.setBounds(getHeight() - TAB_HEIGHT, 0, TAB_HEIGHT, TAB_HEIGHT);
				buttons[3] = button;
		}

		this.add(button, JLayeredPane.PALETTE_LAYER);

	}*/


	public void setSize(ViewInteractionPanel panel, JPanel panelOff, JPanel panelOn) {
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

	public void setLocation(ViewInteractionPanel panel, JPanel panelOff, JPanel panelOn) {
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
	}

	/**
	 * Resizes the panels base don the current size of the layered pane.
	 */
	private void resize() {
		/*
		 * Get the size of the layered pane.
		 */
		/*for (Entry<ViewInteractionPanel, Pair<JPanel, JPanel>> entry : panels.entrySet()) {
			JPanel panelOn = entry.getValue().getFirst();
			JPanel panelOff = entry.getValue().getSecond();
			ViewInteractionPanel panel = entry.getKey();

			if (locations.get(panel) == SwingConstants.SOUTH) {
				// south
				panelOn.setLocation(panelOn.getLocation().x, getHeight() - TAB_HEIGHT);
				panelOff.setBounds(panelOff.getLocation().x, getHeight() - TAB_HEIGHT, 75, 40);
			}
			setSize(panel, panelOff, panelOn);
			setLocation(panel, panelOff, panelOn);
		}*/

		/*for (int i = 1; i < 3; i++) {
			if (buttons[i] == null) {
				continue;
			}
			buttons[i].setLocation(i > 1 ? getHeight() - TAB_HEIGHT : 0, i < 3 ? getWidth() - TAB_HEIGHT : 0);
		}*/
		//this.normalScale = graph.getScale();

			int width = this.getSize().width;
			int height = this.getSize().height;

			//int pipHeight = this.pip.getHeight();
			//int pipWidth = this.pip.getWidth();
			//this.pip.setBounds(10, 20, pipWidth, pipHeight);

			this.zoomIP.setHeight((int) (height * 0.66));
			
			int zoomWidth = this.zoomPanelOn.getWidth();


		    //this.zoomRatio = (float) (width - zoomWidth - 40)	/ (float) (width - 60);
		
			this.normalBounds = new Rectangle(30, 30, width - 60, height - 60);
			this.zoomBounds = new Rectangle(20 + zoomWidth, 30, width - 50 - zoomWidth, height - 60);
			//this.normalScale = graph.getScale();

			this.scroll.setBounds(this.normalBounds);
			setSizeZoom();
			this.zoomIP.setValue(100);

			setSizePIP();
			setSizeExport();
			updated();
			this.zoomIP.computeFitScale();
			this.zoomIP.fit();
		//		invalidate();

	}

	private void setSizeExport() {
		exportPanelOff.setBounds(40, getHeight() - TAB_HEIGHT, 75, 40);
	}


	public JScrollBar getHorizontalScrollBar() {
		return scroll.getHorizontalScrollBar();
	}

	public JScrollBar getVerticalScrollBar() {
		return scroll.getVerticalScrollBar();
	}

	/**
	 * Returns the zoom factor of the primary view.
	 * 
	 * @return The zoom factor of the primary view.
	 */
	public double getScale() {
		return scalable.getScale();
	}

	/**
	 * Sets the zoom factor of the primary view to the given factor.
	 * 
	 * @param d
	 *            The given factor.
	 */
	public void setScale(double d) {
		double b = Math.max(d, 0.01);
		b = Math.min(b, MAX_ZOOM / 100.);
		scalable.setScale(b);
	}

	/**
	 * Clean up.
	 */
	/*public void cleanUp() {
		
		if (getComponent() instanceof Cleanable) {
			((Cleanable) getComponent()).cleanUp();
		}
		scalable.removeUpdateListener(this);
		getComponent().removeMouseMotionListener(this);
	}*/

	/**
	 * Deals with change events.
	 */
	/*public void stateChanged(ChangeEvent e) {
		Object source = e.getSource();
		if (source instanceof JSlider) {
			
			scalable.setScale(((JSlider) source).getValue() / 100.0);
			getComponent().repaint();
			//repaintPIP(component.getVisibleRect());
			
		}
	}*/

	

	public void updated() {
		JComponent newComponent = scalable.getComponent();
		if (newComponent != getComponent()) {
			scroll.setViewportView(newComponent);
			if (getComponent() instanceof Cleanable) {
				((Cleanable) getComponent()).cleanUp();
			}
			getComponent().removeMouseMotionListener(this);

			component = newComponent;
			getComponent().addMouseMotionListener(this);
			invalidate();
		}
		for (ViewInteractionPanel panel : panels.keySet()) {
			// HV: Do not call setScalableComponent now, as it changes the originalAttributeMap of the scalable.
			//			panel.setScalableComponent(scalable);
			panel.updated();
		}
	}


	public void scaleToFit() {
		scalable.setScale(1);
		double rx = scroll.getViewport().getExtentSize().getWidth()
				/ scalable.getComponent().getPreferredSize().getWidth();
		double ry = scroll.getViewport().getExtentSize().getHeight()
				/ scalable.getComponent().getPreferredSize().getHeight();
		scalable.setScale(Math.min(rx, ry));
	}

	
	public JComponent getComponent() {
		return component;
	}


	public PIPInteractionPanel getpip() {
		return this.pip;
	}


	public void showLegend(boolean b) {
		// TODO Auto-generated method stub
		
	}


	public void showConfig(boolean b) {
		// TODO Auto-generated method stub
		
	}

}




