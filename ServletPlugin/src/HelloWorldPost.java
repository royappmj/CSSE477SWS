/*
 * HelloWorldServlet.java
 * May 3, 2015
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

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import protocol.HttpRequest;
import protocol.Protocol;
import protocol.Servlet;
import protocol.ServletResponse;

/**
 * 
 * @author Chandan R. Rupakheti (rupakhcr@clarkson.edu)
 */
public class HelloWorldPost implements Servlet {

	@Override
	public ServletResponse service(HttpRequest request, ServletResponse response) {
		// This is a POST request
		// Get root directory path from server
		String rootDirectory = response.getRootDirectory();
		
		try {
			//create new file or overwrite existing file with contents of request body 
			PrintWriter writer = new PrintWriter(rootDirectory + "/" + request.getFilename(), "UTF-8");
			writer.write(request.getBody());
			writer.close();
			
			response.setBody(new String(request.getBody()));
			System.out.println("body in servlet\n" + response.getBody());
			response.setStatus(Protocol.OK_CODE);
			return response;
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
			response.setStatus(Protocol.NOT_FOUND_CODE);
			return response;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			response.setStatus(Protocol.BAD_REQUEST_CODE);
			return response;
		}
	}

}
