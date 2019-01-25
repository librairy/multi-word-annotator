package edu.mit.jmwe.util;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * A file chooser that confirms with the user before selecting a file for
 * overwriting.
 * 
 * @author M.A. Finlayson
 * @version $Id: ConfirmingJFileChooser.java 323 2011-05-07 01:00:47Z markaf $
 * @since jMWE 1.0.0
 */
@SuppressWarnings("serial")
public class ConfirmingJFileChooser extends JFileChooser {
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see javax.swing.JFileChooser#approveSelection()
	 */
	@Override
	public void approveSelection(){
	    File f = getSelectedFile();
	    if(getDialogType() == SAVE_DIALOG && !confirmFileReplace(this, f))
	    	return;
	    super.approveSelection();
	}
	
	/**
	 * Confirms with the user the replacement of a file
	 * 
	 * @param parent
	 *            the parent on whom the window should be opened
	 * @param f
	 *            the file to be replaced
	 * @return <code>true</code> if the file may be replaced; <code>false</code>
	 *         if the user vetos the replacement
	 * @since jMWE 1.0.0
	 */
	public static boolean confirmFileReplace(Component parent, File f){
		if(!f.exists())
			return true;
		
		// construct message
		StringBuilder msg = new StringBuilder();
		msg.append("The following file exists:\n");
		msg.append(f.getAbsolutePath());
		msg.append("\nDo you want to overwrite it?");
		
		// show option pane
		String title = "Confirm Replace";
	    int result = JOptionPane.showConfirmDialog(parent, msg.toString(), title, JOptionPane.YES_NO_OPTION);
	    return result == JOptionPane.YES_OPTION;
	}
	
}