/*
 * GETRequest.java
 * Apr 25, 2015
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

import server.Server;

/**
 * 
 * @author Chandan R. Rupakheti (rupakhcr@clarkson.edu)
 */
public class GETRequest extends HttpRequest {

	public GETRequest(File file) {
		super(file);
	}
	
	@Override
	public HttpResponse runRequest(HttpResponse response, Server server, File postFile, HttpResponseFactory hrf) {
		// Map<String, String> header = request.getHeader();
		// String date = header.get("if-modified-since");
		// String hostName = header.get("host");

		// Handling GET request here
		// Get root directory path from server
		String rootDirectory = server.getRootDirectory();
		// Combine them together to form absolute file path
		File file = new File(rootDirectory + this.uri);
		// Check if the file exists
		// TODO: improve if else flow by calling createResponse once at the end
		if (file.exists()) {
			if (file.isDirectory()) {
				// Look for default index.html file in a directory
				String location = rootDirectory + this.uri
						+ System.getProperty("file.separator") + Protocol.DEFAULT_FILE;
				file = new File(location);
				if (file.exists()) {
					// Let's create 200 OK response
					response = hrf.createResponse(file, Protocol.CLOSE,
							Protocol.OK_CODE);
				} else {
					// File does not exist so lets create 404 file not found code
					response = hrf.createResponse(null, Protocol.CLOSE,
							Protocol.NOT_FOUND_CODE);
				}
			} else { // It's a file
						// Let's create 200 OK response
				response = hrf.createResponse(file, Protocol.CLOSE,
						Protocol.OK_CODE);
			}
		} else {
			// File does not exist so let's create 404 file not found code
			
			response = hrf.createResponse(null, Protocol.CLOSE,
					Protocol.NOT_FOUND_CODE);
		}
		
		return response;
		
	}

}
