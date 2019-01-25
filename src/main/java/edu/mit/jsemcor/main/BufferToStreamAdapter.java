/********************************************************************************
 * MIT JSemcor Library (JSemcor) v1.0.1
 * Copyright (c) 2008-2011 Massachusetts Institute of Technology
  * 
 * This program and the accompanying materials are made available by MIT under 
 * the terms of the MIT JSemcor License. Refer to the license document included 
 * with this distribution, or contact markaf@alum.mit.edu for further details.
 *******************************************************************************/

package edu.mit.jsemcor.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
 * Simple adapter that makes an java.nio.ByteBuffer look like an InputStream.
 * 
 * @author M.A. Finlayson
 * @version 1.502, 29 Jan 2011
 * @since JSemcor 1.0.0
 */
public class BufferToStreamAdapter extends InputStream {
	
	/**
	 * The string that indicates the file protocol.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static final String FILE_PROTOCOL = "file";
	
	/**
	 * The string that indicates the UTF8 character encoding.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static final String UTF8 = "UTF-8";
	
	private int mark = -1;
	private ByteBuffer buf;
	
	/**
	 * Creates a new instance of this class that wraps the specified buffer as
	 * an {@link InputStream}.
	 * 
	 * @throws NullPointerException
	 *             if the specified buffer is <code>null</code>
	 * @since JSemcor 1.0.0
	 */
	public BufferToStreamAdapter(ByteBuffer buffer){
		if(buffer == null) throw new NullPointerException();
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
		if(buf.position() == buf.limit()) return -1;
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
		if(mark == -1) throw new IOException();
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
	 * the <tt>file://<tt>protocol and is in
	 * UTF8 encoding. If the transformation cannot be done, this method returns
	 * <code>null</code>.
	 * 
	 * @throws NullPointerException
	 *             if the specified url is <code>null</code>
	 * @since JSemcor 1.0.0
	 */
	public static File toFile(URL url) {
		if(url.getProtocol().equals(FILE_PROTOCOL)){
			try{
				return new File(URLDecoder.decode(url.getPath(), UTF8));
			} catch(UnsupportedEncodingException e){
				return null;
			}
		} else {
			return null;
		}
	}
	
	/**
	 * TODO: Write comment
	 *
	 * @param file
	 * @return
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
	 * @throws NullPointerException
	 *             if the specified {@link URL} is <code>null</code>
	 * @since JSemcor 1.0.0
	 */
	public static InputStream makeStream(URL url) throws IOException {
		File file = toFile(url);
		if(file != null){
			return new BufferToStreamAdapter(makeByteBuffer(file));
		} else {
			return url.openConnection().getInputStream();
		}
	}
	
	/**
	 * Returns a mapped byte buffer for the specified {@link URL}, which must
	 * point to an file that exists on the local file system.
	 * 
	 * @throws FileNotFoundException
	 *             if the file does not exist, is a directory rather than a
	 *             regular file, or for some other reason cannot be opened for
	 *             reading.
	 * @throws NullPointerException
	 *             if the specified file is <code>null</code>
	 * @since JSemcor 1.0.0
	 */
	public static ByteBuffer makeByteBuffer(File file) throws IOException {
		FileInputStream is = new FileInputStream(file);
		return is.getChannel().map(MapMode.READ_ONLY, 0, file.length());
	}
	
}
