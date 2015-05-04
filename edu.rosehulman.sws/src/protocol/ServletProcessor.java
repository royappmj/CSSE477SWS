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

//import javax.ser

/**
 * 
 * @author Chandan R. Rupakheti (rupakhcr@clarkson.edu)
 */
public class ServletProcessor {
	public static void process(HttpRequest request, HttpResponse response) {
		String uri = request.getUri();
		String servletName = uri.substring(uri.lastIndexOf("/") + 1);
		String pluginName = uri.substring(uri.substring(1).indexOf('/') + 1, uri.lastIndexOf("/"));
		URLClassLoader loader = null;

		try {
			// create a URLClassLoader
			URLStreamHandler streamHandler = null;

			URL[] urls = new URL[1];
			File classPath = new File(System.getProperty("user.dir") + "/web/plugins" + pluginName + ".jar");
			String repository = (new URL("file", null, classPath.getCanonicalPath())).toString();
			urls[0] = new URL(null, repository, streamHandler);
			loader = new URLClassLoader(urls);
			System.out.println(loader.getURLs()[0]);
		} catch (IOException e) {
			System.out.println(e.toString());
		}

		Class myClass = null;

		try {
			// System.out.println("Servlet Name is: " + servletName);
			myClass = loader.loadClass(servletName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println(e.toString());
		}

		Servlet servlet = null;
		try {
			servlet = (Servlet) myClass.newInstance();
			servlet.service(request, response);
		} catch (Throwable e) {
			System.out.println(e.toString());
		}
	}
}
