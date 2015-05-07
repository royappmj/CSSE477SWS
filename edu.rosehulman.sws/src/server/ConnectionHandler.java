/*
 * ConnectionHandler.java
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

package server;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.HttpResponseFactory;
import protocol.Protocol;
import protocol.ProtocolException;
import protocol.ServletProcessor;
import protocol.ServletResponse;

/**
 * This class is responsible for handling a incoming request by creating a {@link HttpRequest}
 * object and sending the appropriate response be creating a {@link HttpResponse} object. It
 * implements {@link Runnable} to be used in multi-threaded environment.
 * 
 * @author Chandan R. Rupakheti (rupakhet@rose-hulman.edu)
 */
public class ConnectionHandler implements Runnable {
	private Server server;
	private Socket socket;
	private Map<Integer, String> textMap;

	public ConnectionHandler(Server server, Socket socket) {
		this.server = server;
		this.socket = socket;
		this.textMap = new HashMap<Integer, String>();
		this.textMap.put(Protocol.OK_CODE, Protocol.OK_TEXT);
		this.textMap.put(Protocol.BAD_REQUEST_CODE, Protocol.BAD_REQUEST_TEXT);
		this.textMap.put(Protocol.NOT_FOUND_CODE, Protocol.NOT_FOUND_TEXT);
//		this.textMap.put(Protocol.OK_CODE, Protocol.OK_TEXT);
//		this.textMap.put(Protocol.OK_CODE, Protocol.OK_TEXT);

	}

	/**
	 * @return the socket
	 */
	public Socket getSocket() {
		return this.socket;
	}

	/**
	 * The entry point for connection handler. It first parses incoming request and creates a
	 * {@link HttpRequest} object, then it creates an appropriate {@link HttpResponse} object and
	 * sends the response back to the client (web browser).
	 */
	public void run() {
		// Get the start time
		long start = System.currentTimeMillis();

		InputStream inStream = null;
		OutputStream outStream = null;
		File file = null;
		
		HttpResponseFactory responseFact = new HttpResponseFactory();

		try {
			inStream = this.socket.getInputStream();
			outStream = this.socket.getOutputStream();
		} catch (Exception e) {
			// Cannot do anything if we have exception reading input or output stream
			// May be have text to log this for further analysis?
			e.printStackTrace();

			// Increment number of connections by 1
			this.server.incrementConnections(1);
			// Get the end time
			long end = System.currentTimeMillis();
			this.server.incrementServiceTime(end - start);
			return;
		}

		// At this point we have the input and output stream of the socket
		// Now let's create a HttpRequest object
		HttpRequest request = null;
		HttpResponse response = null;
		try {
			if(inStream == null) System.out.println("null");
			request = HttpRequest.read(inStream);
			
//			if(request.getUri().startsWith("/plugins/"))
			System.out.println(request.getUri());
		} catch (ProtocolException pe) {
			// We have some sort of protocol exception. Get its status code and create response
			// We know only two kind of exception is possible inside fromInputStream
			// Protocol.BAD_REQUEST_CODE and Protocol.NOT_SUPPORTED_CODE
			int status = pe.getStatus();
			if (status == Protocol.BAD_REQUEST_CODE) {
				response = responseFact.createResponse(null, Protocol.CLOSE,
						Protocol.BAD_REQUEST_CODE);
			}
			// TODO: Handle version not supported code as well
		} catch (Exception e) {
			e.printStackTrace();
			// For any other error, we will create bad request response as well
			response = responseFact.createResponse(null, Protocol.CLOSE,
					Protocol.BAD_REQUEST_CODE);
		}

		if (response != null) {
			// Means there was an error, now write the response object to the socket
			try {
				response.write(outStream);
				// System.out.println(response);
			} catch (Exception e) {
				// We will ignore this exception
				e.printStackTrace();
			}

			// Increment number of connections by 1
			this.server.incrementConnections(1);
			// Get the end time
			long end = System.currentTimeMillis();
			this.server.incrementServiceTime(end - start);
			return;
		}

		// We reached here means no error so far, so let's process further
		ServletResponse sResponse = null;
		try {
			// Fill in the code to create a response for version mismatch.
			// You may want to use constants such as Protocol.VERSION, Protocol.NOT_SUPPORTED_CODE,
			// and more. You can check if the version matches as follows
			if (!request.getVersion().equalsIgnoreCase(Protocol.VERSION)) {
				// Here you checked that the "Protocol.VERSION" string is not equal to the
				// "request.version" string ignoring the case of the letters in both strings
				// TODO: Fill in the rest of the code here
			} else {
				//get appropriate response from running the given request
				if(request.getUri().startsWith("/plugins/")) {
					sResponse = new ServletResponse(Protocol.VERSION,
							Protocol.OK_CODE, Protocol.OK_TEXT, new HashMap<String, String>(),
							file, outStream, this.server);
					sResponse.populateFields(Protocol.CLOSE);
//					sResponse.write(outStream);
					System.out.println("Request body is:\n" + new String(request.getBody()));
					sResponse = ServletProcessor.process(request, sResponse);
					sResponse.setText(this.textMap.get(sResponse.getStatus()));
					sResponse.write(outStream);
				}
				else response = request.runRequest(response, this.server, file, responseFact);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// TODO: So far response could be null for protocol version mismatch.
		// So this is a temporary patch for that problem and should be removed
		// after a response object is created for protocol version mismatch.
		if (response == null) {
			response = responseFact.createResponse(null, Protocol.CLOSE,
					Protocol.BAD_REQUEST_CODE);
		}

		try {
			// Write response and we are all done so close the socket
			if(sResponse == null) response.write(outStream);
			// System.out.println(response);
			this.socket.close();
		} catch (Exception e) {
			// We will ignore this exception
			e.printStackTrace();
		}

		// Increment number of connections by 1
		this.server.incrementConnections(1);
		// Get the end time
		long end = System.currentTimeMillis();
		this.server.incrementServiceTime(end - start);
	}
}
