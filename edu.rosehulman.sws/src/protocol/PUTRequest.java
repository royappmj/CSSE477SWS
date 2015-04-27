/*
 * PUTRequest.java
 * Apr 27, 2015
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import server.Server;

/**
 * 
 * @author Chandan R. Rupakheti (rupakhcr@clarkson.edu)
 */
public class PUTRequest extends HttpRequest {

	/**
	 * @param file
	 */
	public PUTRequest(File file) {
		super(file);
	}

	@Override
	public HttpResponse runRequest(HttpResponse response, Server server, File postFile,
			HttpResponseFactory hrf) {
		// Handling POST request here
		// Get root directory path from server
		String rootDirectory = server.getRootDirectory();
		String dir = rootDirectory + this.uri;
		// Combine them together to form absolute file path
		System.out.println("writing to " + dir);
		File file = new File(dir);
		if(!file.exists()) {
			try {
				PrintWriter writer = new PrintWriter(rootDirectory + this.uri, "UTF-8");
				writer.print(this.body);
				writer.close();
				
				response = hrf.createResponse(new File(rootDirectory + this.uri), Protocol.CLOSE,
						Protocol.OK_CODE);
			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				e.printStackTrace();
				response = hrf.createResponse(null, Protocol.CLOSE, Protocol.NOT_FOUND_CODE);
			}

		}
		else {
			try {
				PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("myfile.txt", true)));
				writer.print(this.body);
				writer.close();
				
				response = hrf.createResponse(new File(rootDirectory + this.uri), Protocol.CLOSE,
						Protocol.OK_CODE);
			} catch (IOException exception) {
				exception.printStackTrace();
				response = hrf.createResponse(file, Protocol.CLOSE, Protocol.BAD_REQUEST_CODE);
			}
		}
		return response;
	}

}
