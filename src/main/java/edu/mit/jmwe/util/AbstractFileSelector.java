/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.util;

import java.io.File;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;

/**
 * Abstract base class that allows interaction with the user, saving and
 * selecting files and directories.
 * 
 * @author M.A. Finlayson
 * @version $Id: AbstractFileSelector.java 323 2011-05-07 01:00:47Z markaf $
 * @since jMWE 1.0.0
 */
public abstract class AbstractFileSelector {

	/**
	 * Gets a location indexed by the specified class as a key.
	 * 
	 * @param key
	 *            the class used as a key for the location
	 * @return the location associated with the class, or <code>null</code> if
	 *         none
	 * @throws NullPointerException
	 *             if the specified key is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	protected File getLocation(Class<?> key) {
		Preferences node = Preferences.userNodeForPackage(this.getClass());
		String path = node.get(key.getCanonicalName(), null);
		return (path == null) ? null : new File(path);
	}

	/**
	 * Sets a location indexed by the specified class as a key.
	 * 
	 * @param key
	 *            the class used as a key for the location
	 * @param loc
	 *            the location
	 * @throws NullPointerException
	 *             if the specified key or location is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	protected void setLocation(Class<?> key, File loc) {
		Preferences node = Preferences.userNodeForPackage(this.getClass());
		node.put(key.getCanonicalName(), loc.getAbsolutePath());
	}

	/**
	 * Chooses a file for opening, showing the specified message, associated
	 * with the specified key.
	 * 
	 * @param msg
	 *            the message to show to the user
	 * @param key
	 *            the class with which to associate the location
	 * @return the chosen file
	 * @since jMWE 1.0.0
	 */
	protected File chooseFile(String msg, Class<?> key) {
		return choose(msg, key, JFileChooser.OPEN_DIALOG, JFileChooser.FILES_ONLY);
	}
	
	/**
	 * Chooses a file for writing, showing the specified message, associated
	 * with the specified key.
	 * 
	 * @param msg
	 *            the message to show to the user
	 * @param key
	 *            the class with which to associate the location
	 * @return the chosen file
	 * @since jMWE 1.0.0
	 */
	protected File chooseFileForWriting(String msg, Class<?> key) {
		return choose(msg, key, JFileChooser.SAVE_DIALOG, JFileChooser.FILES_ONLY);
	}
	
	/**
	 * Chooses a directory, showing the specified message, associated
	 * with the specified key.
	 * 
	 * @param msg
	 *            the message to show to the user
	 * @param key
	 *            the class with which to associate the location
	 * @return the chosen file
	 * @since jMWE 1.0.0
	 */
	protected File chooseDirectory(String msg, Class<?> key){
		return choose(msg, key, JFileChooser.OPEN_DIALOG, JFileChooser.DIRECTORIES_ONLY);
	}

	/**
	 * Choose a file, opening a file chooser at the location already associated
	 * with the class (if any), and saving the selected location with the class.
	 * 
	 * @param msg
	 *            the message to display
	 * @param key
	 *            the class with which the location selected should be
	 *            associated
	 * @param dialogType
	 *            the type of dialog, see
	 *            {@link JFileChooser#setDialogType(int)}
	 * @param selMode
	 *            file selection mode, see
	 *            {@link JFileChooser#setFileSelectionMode(int)}
	 * @return the file chosen
	 * @since jMWE 1.0.0
	 */
	protected File choose(String msg, Class<?> key, int dialogType, int selMode){
		File location = getLocation(key);
		JFileChooser chooser = getFileChooser();
		chooser.setDialogTitle(msg);
		chooser.setDialogType(dialogType);
		chooser.setFileSelectionMode(selMode);
		chooser.setSelectedFile(location);
		if(chooser.showDialog(null, null) != JFileChooser.APPROVE_OPTION)
			return null;
		File result = chooser.getSelectedFile();
		setLocation(key, result);
		return result;
	}

	private JFileChooser chooser;

	/**
	 * Returns the file chooser for this instance.
	 * 
	 * @return the file chooser for this instance
	 * @since jMWE 1.0.0
	 */
	protected JFileChooser getFileChooser(){
		if(chooser == null)
			chooser = new ConfirmingJFileChooser();
		return chooser;
	}



}
