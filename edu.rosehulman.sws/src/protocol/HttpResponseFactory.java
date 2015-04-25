/*
 * HttpResponseFactory.java
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

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * This is a factory to produce various kind of HTTP responses.
 * 
 * @author Chandan R. Rupakheti (rupakhet@rose-hulman.edu)
 */
public class HttpResponseFactory {
	
	private Map<Integer, Class<? extends HttpResponse>> responseMap;
	
	public HttpResponseFactory() {
		this.responseMap = new HashMap<Integer, Class<? extends HttpResponse>>();
		this.responseMap.put(Protocol.OK_CODE, OK200Response.class);
		this.responseMap.put(Protocol.BAD_REQUEST_CODE, BadRequest400Response.class);
		this.responseMap.put(Protocol.NOT_FOUND_CODE, FileNotFound404Response.class);
//		responseMap.put(Protocol., 304Response.class);
//		responseMap.put(Protocol., 505Response.class);
	}
	
	public HttpResponse createResponse(File file, String connection, int code) {
		HttpResponse response = null;
		try {
			response = this.responseMap.get(code).getDeclaredConstructor(Class.forName("java.io.File")).
					newInstance(file);
			response.populateFields(connection);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException |
				InvocationTargetException | NoSuchMethodException | SecurityException |
				ClassNotFoundException exception) {
			exception.printStackTrace();
		}
		return response;
	}	
	
	/**
	 * Creates a {@link HttpResponse} object for sending version not supported response.
	 * 
	 * @param connection Supported values are {@link Protocol#OPEN} and {@link Protocol#CLOSE}.
	 * @return A {@link HttpResponse} object represent 505 status.
	 */
	public HttpResponse create505NotSupported(String connection) {
		// TODO fill in this method
		return null;
	}
	
	/**
	 * Creates a {@link HttpResponse} object for sending file not modified response.
	 * 
	 * @param connection Supported values are {@link Protocol#OPEN} and {@link Protocol#CLOSE}.
	 * @return A {@link HttpResponse} object represent 304 status.
	 */
	public HttpResponse create304NotModified(String connection) {
		// TODO fill in this method
		return null;
	}
}
