package usf.saav.alma.app;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import usf.saav.common.monitor.MonitoredTrigger;

public class AlmaStandardMenu extends JMenuBar implements ActionListener {

	private static final long serialVersionUID = -6905404187443383755L;

	private JMenu menuFile;
	private JMenuItem menuOpen;
	private JMenuItem menuClose;
	private JMenuItem menuExit;
	
	public  MonitoredTrigger monFileOpen  = new MonitoredTrigger("File Open");
	public  MonitoredTrigger monFileClose = new MonitoredTrigger("File Close");

	
	public AlmaStandardMenu( ){

		menuOpen = new JMenuItem("Open", KeyEvent.VK_O );
		menuOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		menuOpen.addActionListener( this );

		menuClose = new JMenuItem("Close", KeyEvent.VK_C );
		menuClose.addActionListener( this );
		menuClose.addActionListener( this );

		
		menuExit = new JMenuItem("Exit", KeyEvent.VK_Q );
		menuOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
		menuExit.addActionListener( this );

		menuFile = new JMenu("File");
		menuFile.add(menuOpen);
		menuFile.addSeparator();
		menuFile.add(menuClose);
		menuFile.addSeparator();
		menuFile.add(menuExit);

		this.add(menuFile);

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if( e.getSource() == menuExit  ){ System.exit(0);		  }
		if( e.getSource() == menuOpen  ){ monFileOpen.trigger();  }
		if( e.getSource() == menuClose ){ monFileClose.trigger(); }
		
	}
	
	
}
