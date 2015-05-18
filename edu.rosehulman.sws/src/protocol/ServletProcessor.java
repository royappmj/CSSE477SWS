/*
 * ServletProcessor.java
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

package protocol;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;
import java.util.Arrays;
/**
 * 
 * @author Chandan R. Rupakheti (rupakhcr@clarkson.edu)
 */
public class ServletProcessor {
	public static ServletResponse process(HttpRequest request, ServletResponse response) {
		String uri = request.getUri();
		//Relative URI
		String locations[] = uri.split("/");
		String pluginName = locations[2];
		String servletName = locations[3];
		// M3
//		System.out.println("Locations length = " + locations.length);
		if(locations.length > 5) {
			
			response.setStatus(Protocol.BAD_REQUEST_CODE);
			response.setText(Protocol.BAD_REQUEST_TEXT + " Cannot determine filename.");
			return response;
		}
		// M3 (if + try/catch)
/*
		else if(!request.getMethod().equals(Protocol.GET)) {
			try {
				request.setFilename(locations[4].substring(0, locations[4].contains("?") ?
						locations[4].indexOf("?") : locations[4].length()));
			} catch (ArrayIndexOutOfBoundsException e) {
				request.setFilename(null);
				response.setStatus(Protocol.BAD_REQUEST_CODE);
				response.setText(Protocol.BAD_REQUEST_TEXT + " No filename specified.");
				return response;
			}
		}
		*/
		URLClassLoader loader = null;

		try {
			// create a URLClassLoader
			URLStreamHandler streamHandler = null;

			URL[] urls = new URL[1];
			File classPath = new File(System.getProperty("user.dir") + "/web/plugins/" + pluginName + ".jar");
			String repository = (new URL("file", null, classPath.getCanonicalPath())).toString();
			urls[0] = new URL(null, repository, streamHandler);
			loader = new URLClassLoader(urls);
//			System.out.println("URLS ARE: " + loader.getURLs()[0]);
		} catch (IOException e) {
			System.out.println(e.toString());
		}

		Class myClass = null;

		try {
			// System.out.println("Servlet Name is: " + servletName);
			myClass = loader.loadClass(servletName);
		} catch (ClassNotFoundException e) {
			// M3
			response.setStatus(Protocol.BAD_REQUEST_CODE);
			response.setText(Protocol.BAD_REQUEST_TEXT + " Class loader could not load class " + servletName);
			return response;
		}
		
		// M3 - to get rid of leakage warning that loader never gets closed
		try {
			loader.close();
		} catch (IOException e1) {
			response.setStatus(Protocol.INTERNAL_SERVER_ERR_CODE);
			response.setText(Protocol.INTERNAL_SERVER_ERR_TEXT + " Class loader could not close properly.");
			return response;
		}
		
		Servlet servlet = null;
		try {
			//Servlet Class

			if (Arrays.asList(myClass.getInterfaces()).contains(RunnableServlet.class)) {
			// M3
				servlet = (RunnableServlet) myClass.getDeclaredConstructor(Class.forName("protocol.HttpRequest"), 
						Class.forName("protocol.ServletResponse")).newInstance(request, response);

				
				// M3
				Thread t = new Thread((Runnable) servlet);
				
				long startTime = System.currentTimeMillis();
				long endTime = startTime + 4 * 1000L;
				
				t.start();
				while(System.currentTimeMillis() < endTime) {
					try {
						if(t.getState()!=Thread.State.TERMINATED)
							Thread.sleep(10L);
						else
							break;
					} catch (InterruptedException e) {
						// Just fine

					}
				}
				
				boolean interrupted = false;
				if(t.getState()!=Thread.State.TERMINATED) {
					t.interrupt();
					interrupted = true;
					System.out.println("Stopped thread early");
					response.setStatus(Protocol.TIMEOUT_CODE);
					response.setText(Protocol.TIMEOUT_TEXT);
				}
				
				t.join();
				
				if(interrupted == false) {
				// Request finished in plenty of time
					response.setStatus(Protocol.OK_CODE);
					response.setText(Protocol.OK_TEXT);
				}
			}
			else {
				System.out.println("myclass is: " + myClass);
				servlet = (Servlet) myClass.newInstance();
				response = servlet.service(request, response);
			}
			return response;
		} catch (InterruptedException e) {
			response.setStatus(Protocol.TIMEOUT_CODE);
			response.setText(Protocol.TIMEOUT_TEXT);
			return response;
		} catch (Throwable e) {
			e.printStackTrace();
			response.setStatus(Protocol.BAD_REQUEST_CODE);
			// M3
			response.setText(Protocol.BAD_REQUEST_TEXT + " Requested servlet does not exist.");
			return response;
		}
	}
}
