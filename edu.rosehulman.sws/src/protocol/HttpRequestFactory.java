/*
 * HttpRequestFactory.java
 * Apr 24, 2015
 *
 * Simple Web Server (SWS) for EE407/507 and CS455/555
 * 
 * Copyright (C) 2011 Chandan Raj Rupakheti, Clarkson University
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
 * Contact Us:
 * Chandan Raj Rupakheti (rupakhcr@clarkson.edu)
 * Department of Electrical and Computer Engineering
 * Clarkson University
 * Potsdam
 * NY 13699-5722
 * http://clarkson.edu/~rupakhcr
 */
 
package protocol;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Not used for now
 * 
 * @author Chandan R. Rupakheti (rupakhcr@clarkson.edu)
 */
public class HttpRequestFactory {
	private Map<String, Class<? extends HttpRequest>> requestMap;
	
	public Map<String, Class<? extends HttpRequest>> getRequestMap() {
		return this.requestMap;
	}
	
	public HttpRequestFactory() {
		this.requestMap = new HashMap<String, Class<? extends HttpRequest>>();
		this.requestMap.put(Protocol.GET, GETRequest.class);
		this.requestMap.put(Protocol.POST, POSTRequest.class);
		this.requestMap.put(Protocol.PUT, PUTRequest.class);
		this.requestMap.put(Protocol.DELETE, DELETERequest.class);
	}
	
	public HttpRequest createRequest(File file, String requestType) {
		HttpRequest request = null;
		try {
			request = this.requestMap.get(requestType).getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException |
				InvocationTargetException | NoSuchMethodException | SecurityException exception) {
			exception.printStackTrace();
		}
		return request;
	}
}
