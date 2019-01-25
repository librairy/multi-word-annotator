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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel.MapMode;

/**
 * An adapter that makes a {@link ByteBuffer} look like an {@link InputStream}.
 * 
 * @author M.A. Finlayson
 * @version $Id: StreamAdapter.java 356 2015-11-25 22:36:46Z markaf $
 * @since jMWE 1.0.0
 */
public class StreamAdapter extends InputStream {
	
	/**
	 * The string that indicates the file protocol.
	 * 
	 * @since jMWE 1.0.0
	 */
	public static final String FILE_PROTOCOL = "file";

	/**
	 * The string that indicates the UTF8 character encoding.
	 * 
	 * @since jMWE 1.0.0
	 */
	public static final String UTF8 = "UTF-8";
	
	// dynamic instance fields
	private int mark = -1;
	private ByteBuffer buf;

	/**
	 * Creates a new instance of this class that wraps the specified buffer as
	 * an {@link InputStream}.
	 * 
	 * @param buffer the buffer to be wrapped; may not be <code>null</code>
	 * @throws NullPointerException
	 *             if the specified buffer is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public StreamAdapter(ByteBuffer buffer){
		if(buffer == null)
			throw new NullPointerException();
		this.buf = buffer;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see java.io.InputStream#available()
	 */
	@Override
	public int available() throws IOException {
		return buf.limit()-buf.position();
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see java.io.InputStream#read()
	 */
	@Override
	public int read() throws IOException {
		if(buf.position() == buf.limit()) 
			return -1;
		return buf.get();
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see java.io.InputStream#close()
	 */
	@Override
	public void close() throws IOException {
		buf = null;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see java.io.InputStream#mark(int)
	 */
	@Override
	public synchronized void mark(int readlimit) {
		mark = buf.position();
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see java.io.InputStream#reset()
	 */
	@Override
	public synchronized void reset() throws IOException {
		if(mark == -1) 
			throw new IOException();
		buf.position(mark);
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see java.io.InputStream#markSupported()
	 */
	@Override
	public boolean markSupported() {
		return true;
	}

	/**
	 * Transforms the specified {@link URL} into a {@link File} object, if in
	 * fact the {@link URL} points to a file. That is, if the {@link URL} uses
	 * the <tt>file://</tt> protocol and is in UTF8 encoding. If the
	 * transformation cannot be done, this method returns <code>null</code>.
	 * 
	 * @param url
	 *            the url to be converted to a file, may not be
	 *            <code>null</code>
	 * @throws NullPointerException
	 *             if the specified URL is <code>null</code>
	 * @return the file corresponding to the url, or <code>null</code> if the
	 *         url does not correspond to a file.
	 * @since jMWE 1.0.0
	 */
	public static File toFile(URL url) {
		if(url.getProtocol().equals(FILE_PROTOCOL)){
			try{
				return new File(URLDecoder.decode(url.getPath(), UTF8));
			} catch(UnsupportedEncodingException e){
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * Transforms a file into a URL
	 * 
	 * @param file
	 *            the file to be transformed
	 * @return a URL representing the file
	 * @throws NullPointerException
	 *             if the specified file is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public static URL toURL(File file) {
		if(file == null)
			throw new NullPointerException();
		try{
			URI uri = new URI("file", "//", file.toURL().getPath() , null);
			return new URL("file", null, uri.getRawPath());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Returns an input stream for the specified {@link URL}. This
	 * implementation produces a mapped byte-buffer wrapped as a stream if the
	 * specified {@link URL} points to a local file system resource.
	 * 
	 * @param url
	 *            the url to make into an input stream
	 * @return an input stream that reads from the specified url
	 * @throws NullPointerException
	 *             if the specified {@link URL} is <code>null</code>
	 * @throws IOException
	 *             if there is an IO exception when creating the stream
	 * @since jMWE 1.0.0
	 */
	public static InputStream make(URL url) throws IOException {
		File file = toFile(url);
		if(file != null){
			return make(file);
		} else {
			return url.openConnection().getInputStream();
		}
	}

	/**
	 * Returns an input stream on the specified file that is backed by a mapped
	 * byte buffer, making it much faster than a normal {@link FileInputStream}.
	 * 
	 * @param file
	 *            the file on which to open the stream
	 * @return the mapped-byte-buffer-backed input stream for reading the file
	 * @throws IOException
	 *             if there was a io error accessing the file
	 * @since jMWE 1.0.0
	 */
	public static InputStream make(File file) throws IOException{
		@SuppressWarnings("resource")
		FileInputStream is = new FileInputStream(file);
		ByteBuffer buffer = is.getChannel().map(MapMode.READ_ONLY, 0, file.length());
		return new StreamAdapter(buffer);
	}
		
}
