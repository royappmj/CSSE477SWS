/*
 * HttpResponse.java
 * Oct 7, 2012
 *
 * Simple Web Server (SWS) for CSSE 477
 * 
 * Copyright (C) 2012 Chandan Raj Rupakheti
 * 
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License 
 * as published by the Free Software Foundation, either 
 * version 3 of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/lgpl.html>.
 * 
 */
 
package protocol;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

/**
 * Represents a response object for HTTP.
 * 
 * @author Chandan R. Rupakheti (rupakhet@rose-hulman.edu)
 */
public abstract class HttpResponse {
	protected String version;
	protected int status;
	protected String phrase;
	protected Map<String, String> header;
	protected File file;
	protected String body;
	
	/**
	 * Constructs a HttpResponse object using supplied parameter
	 * 
	 * @param version The http version.
	 * @param status The response status.
	 * @param phrase The response status phrase.
	 * @param header The header field map.
	 * @param file The file to be sent.
	 */
	public HttpResponse(String version, int status, String phrase, Map<String, String> header,
			File file) {
		this.version = version;
		this.status = status;
		this.phrase = phrase;
		this.header = header;
		this.file = file;
	}
	
	/**
	 * @param connection Supported values are {@link Protocol#OPEN} and {@link Protocol#CLOSE}.
	 */
	public abstract void populateFields(String connection);
	
	/**
	 * Convenience method for adding general header to the supplied response object.
	 * 
	 * @param connection Supported values are {@link Protocol#OPEN} and {@link Protocol#CLOSE}.
	 */
	protected void fillGeneralHeader(String connection) {
		// Let's add Connection header
		this.put(Protocol.CONNECTION, connection);

		// Let's add current date
		Date date = Calendar.getInstance().getTime();
		this.put(Protocol.DATE, date.toString());
		
		// Let's add server info
		this.put(Protocol.Server, Protocol.getServerInfo());

		// Let's add extra header with provider info
		this.put(Protocol.PROVIDER, Protocol.AUTHOR);
		this.put("Access-Control-Allow-Origin", "http://127.0.0.1:8080");
	}

	/**
	 * Gets the version of the HTTP.
	 * 
	 * @return the version
	 */
	public String getVersion() {
		return this.version;
	}

	/**
	 * Gets the status code of the response object.
	 * @return the status
	 */
	public int getStatus() {
		return this.status;
	}

	/**
	 * Gets the status phrase of the response object.
	 * 
	 * @return the phrase
	 */
	public String getPhrase() {
		return this.phrase;
	}
	
	/**
	 * The file to be sent.
	 * 
	 * @return the file
	 */
	public File getFile() {
		return this.file;
	}
	
	public String getBody() {
		return this.body;
	}
	
	public void setBody(String body) {
		this.body = body;
	}
	
	public void setText(String t) {
		this.phrase = t;
	}

	/**
	 * Returns the header fields associated with the response object.
	 * @return the header
	 */
	public Map<String, String> getHeader() {
		// Lets return the unmodifiable view of the header map
		return Collections.unmodifiableMap(this.header);
	}

	/**
	 * Maps a key to value in the header map.
	 * @param key A key, e.g. "Host"
	 * @param value A value, e.g. "www.rose-hulman.edu"
	 */
	public void put(String key, String value) {
		this.header.put(key, value);
	}
	
	/**
	 * Writes the data of the http response object to the output stream.
	 * 
	 * @param outStream The output stream
	 * @throws Exception
	 */
	public void write(OutputStream outStream) throws Exception {
		BufferedOutputStream out = new BufferedOutputStream(outStream, Protocol.CHUNK_LENGTH);

		// First status line
		String line = this.version + Protocol.SPACE + this.status + Protocol.SPACE +
				this.phrase + Protocol.CRLF;
		out.write(line.getBytes());
		
		// Write header fields if there is something to write in header field
		if(this.header != null && !this.header.isEmpty()) {
			for(Map.Entry<String, String> entry : this.header.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				
				// Write each header field line
				line = key + Protocol.SEPERATOR + Protocol.SPACE + value + Protocol.CRLF;
				out.write(line.getBytes());
			}
		}

		// Write a blank line
		out.write(Protocol.CRLF.getBytes());

		// We are reading a file
		if(this.getStatus() == Protocol.OK_CODE && this.file != null) {
			// Process text documents
			FileInputStream fileInStream = new FileInputStream(this.file);
			BufferedInputStream inStream = new BufferedInputStream(fileInStream,
					Protocol.CHUNK_LENGTH);
			
			byte[] buffer = new byte[Protocol.CHUNK_LENGTH];
			int bytesRead = 0;
			// While there is some bytes to read from file,
			// read each chunk and send to the socket out stream
			while((bytesRead = inStream.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
			}
			// Close the file input stream, we are done reading
			inStream.close();
		}
		
		if(this.getStatus() == Protocol.OK_CODE && this.body != null) {
			System.out.println("body is\n" + this.body);
			out.write(this.body.getBytes());
		}
		
		// Flush the data so that outStream sends everything through the socket 
		out.flush();
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("----------------------------------\n");
		buffer.append(this.version);
		buffer.append(Protocol.SPACE);
		buffer.append(this.status);
		buffer.append(Protocol.SPACE);
		buffer.append(this.phrase);
		buffer.append(Protocol.LF);
		
		for(Map.Entry<String, String> entry : this.header.entrySet()) {
			buffer.append(entry.getKey());
			buffer.append(Protocol.SEPERATOR);
			buffer.append(Protocol.SPACE);
			buffer.append(entry.getValue());
			buffer.append(Protocol.LF);
		}
		
		buffer.append(Protocol.LF);
		if(this.file != null) {
			buffer.append("Data: ");
			buffer.append(this.file.getAbsolutePath());
		}
		buffer.append("\n----------------------------------\n");
		return buffer.toString();
	}
	
}
