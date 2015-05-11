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

import java.io.IOException;
import java.io.OutputStream;

import protocol.HttpRequest;
import protocol.Protocol;
import protocol.Servlet;
import protocol.ServletResponse;

/**
 * 
 * @author Chandan R. Rupakheti (rupakhcr@clarkson.edu)
 */
public class HelloWorldServlet implements Servlet{
//	private HttpRequest request;
//	private ServletResponse response;
//	
//	public HelloWorldServlet(HttpRequest request, ServletResponse response) {
//		this.request = request;
//		this.response = response;
//	}
	
	@Override
	public ServletResponse service(HttpRequest request, ServletResponse response){
		// GET request servlet
		try (OutputStream out = response.getWriter()) {
//		OutputStream out = response.getWriter();
			String content = "<html><head><title>TEST PAGE LOL</title></head><body>";
			String content2 = "a line is here<br>now another <br></body></html>";
			out.write(content.getBytes());
			out.write(content2.getBytes());
			
			response.setBody(content + content2);
			response.setStatus(Protocol.OK_CODE);
			return response;
		} catch (IOException exception) {
			exception.printStackTrace();
			response.setStatus(Protocol.BAD_REQUEST_CODE);
			return response;
		}
		
	}

//	@Override
//	public void run() {
//		// TODO Auto-generated method stub
//	}

}
