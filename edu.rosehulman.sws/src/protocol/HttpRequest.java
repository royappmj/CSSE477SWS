/*
 * HttpRequest.java
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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import server.Server;

/**
 * Represents a request object for HTTP.
 * 
 * @author Chandan R. Rupakheti (rupakhet@rose-hulman.edu)
 */
public abstract class HttpRequest {
	protected String method;
	protected String uri;
	protected String version;
	protected String filename;
	protected Map<String, String> header;
	/**
	 * map containing the different kinds of requests
	 */
	protected static Map<String, HttpRequest> methodsMap = new HashMap<String, HttpRequest>() {{
		put(Protocol.GET, new GETRequest());
		put(Protocol.POST, new POSTRequest());
		put(Protocol.PUT, new PUTRequest());
		put(Protocol.DELETE, new DELETERequest());
	}};
	protected char[] body;
	protected File file;
	
	public HttpRequest() {
		this.header = new HashMap<String, String>();
		this.body = new char[0];
	}
	
	/**
	 * @param response
	 * @param server
	 * @param postFile
	 * @param hrf
	 * @return response generated from executing the request
	 */
	public abstract HttpResponse runRequest(HttpResponse response, Server server,
			File postFile, HttpResponseFactory hrf);
	
	/**
	 * The request method.
	 * 
	 * @return the method
	 */
	public String getMethod() {
		return this.method;
	}

	/**
	 * The URI of the request object.
	 * 
	 * @return the uri
	 */
	public String getUri() {
		return this.uri;
	}
	
	public String getFilename() {
		return this.filename;
	}
	
	public void setFilename(String name) {
		this.filename = name;
	}

	/**
	 * The version of the http request.
	 * @return the version
	 */
	public String getVersion() {
		return this.version;
	}
	
	public char[] getBody() {
		return this.body;
	}

	/**
	 * The key to value mapping in the request header fields.
	 * 
	 * @return the header
	 */
	public Map<String, String> getHeader() {
		// Let's return the unmodifiable view of the header map
		return Collections.unmodifiableMap(this.header);
	}

	/**
	 * Reads raw data from the supplied input stream and constructs a 
	 * <tt>HttpRequest</tt> object out of the raw data.
	 * 
	 * @param inputStream The input stream to read from.
	 * @return A <tt>HttpRequest</tt> object.
	 * @throws Exception Throws either {@link ProtocolException} for bad request or 
	 * {@link IOException} for socket input stream read errors.
	 */
	public static HttpRequest read(InputStream inputStream) throws Exception {
		// We will fill this object with the data from input stream and return it
//		HttpRequest request = new HttpRequest();
		
		InputStreamReader inStreamReader = new InputStreamReader(inputStream);
		BufferedReader reader = new BufferedReader(inStreamReader);
		
		//First Request Line: GET /somedir/page.html HTTP/1.1
		String line = reader.readLine(); // A line ends with either a \r, or a \n, or both
		
		if(line == null) {
			throw new ProtocolException(Protocol.BAD_REQUEST_CODE, Protocol.BAD_REQUEST_TEXT);
		}
		
		// We will break this line using space as delimiter into three parts
		StringTokenizer tokenizer = new StringTokenizer(line, " ");
		
		// Error checking the first line must have exactly three elements
		if(tokenizer.countTokens() != 3) {
			throw new ProtocolException(Protocol.BAD_REQUEST_CODE, Protocol.BAD_REQUEST_TEXT);
		}
		
		System.out.println(line);
		
		String tMethod = tokenizer.nextToken();
		HttpRequest request = methodsMap.get(tMethod);
		
		request.method = tMethod;					// request method
		request.uri = tokenizer.nextToken();		// /somedir/page.html
		request.uri = request.uri.substring(0, request.uri.contains("?") ?
				request.uri.indexOf("?") : request.uri.length());
		request.version = tokenizer.nextToken();	// HTTP/1.1
		
		System.out.println("URI in read: " + request.uri);
		
		// Rest of the request is a header that maps keys to values
		// e.g. Host: www.rose-hulman.edu
		// We will convert both the strings to lower case to be able to search later
		line = reader.readLine().trim();
		
		while(!line.equals("")) {
			// THIS IS A PATCH 
			// Instead of a string tokenizer, we are using string split
			// Let's break the line into two part with first space as a separator 
			
			// First let's trim the line to remove escape characters
			line = line.trim();
			
			// Now, get index of the first occurrence of space
			int index = line.indexOf(' ');
			
			if(index > 0 && index < line.length()-1) {
				// Now let's break the string in two parts
				String key = line.substring(0, index); // Get first part, e.g. "Host:"
				String value = line.substring(index+1); // Get the rest, e.g. "www.rose-hulman.edu"
				
				// Let's strip off the white spaces from key if any and change it to lower case
				key = key.trim().toLowerCase();
				
				// Let's also remove ":" from the key
				key = key.substring(0, key.length() - 1);
				
				// Let's strip white spaces if any from value as well
				value = value.trim();
				
				// Now let's put the key=>value mapping to the header map
				request.header.put(key, value);
			}
//			request.header.put("Access-Control-Allow-Origin", s.getInetAddress().getHostAddress());
			
			// Processed one more line, now let's read another header line and loop
			line = reader.readLine().trim();
		}
		
		int contentLength = 0;
		try {
			contentLength = Integer.parseInt(request.header.get(Protocol.CONTENT_LENGTH.toLowerCase()));
		}
		catch(Exception e){/**/}
		
		if(contentLength > 0) {
			request.body = new char[contentLength];
			reader.read(request.body);
		}
		
		return request;
	}
	
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("----------- Header ----------------\n");
		buffer.append(this.method);
		buffer.append(Protocol.SPACE);
		buffer.append(this.uri);
		buffer.append(Protocol.SPACE);
		buffer.append(this.version);
		buffer.append(Protocol.LF);
		
		for(Map.Entry<String, String> entry : this.header.entrySet()) {
			buffer.append(entry.getKey());
			buffer.append(Protocol.SEPERATOR);
			buffer.append(Protocol.SPACE);
			buffer.append(entry.getValue());
			buffer.append(Protocol.LF);
		}
		buffer.append("------------- Body ---------------\n");
		buffer.append(this.body);
		buffer.append("----------------------------------\n");
		return buffer.toString();
	}
}
