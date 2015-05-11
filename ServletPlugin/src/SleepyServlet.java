/*
 * SleepyServlet.java
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

import protocol.HttpRequest;
import protocol.Protocol;
import protocol.RunnableServlet;
import protocol.Servlet;
import protocol.ServletResponse;

/**
 * Class for M3 Performance Test 1
 * @author Carly Heibel
 */
public class SleepyServlet implements RunnableServlet {
	private HttpRequest request;
	private ServletResponse response;
	
	public SleepyServlet(HttpRequest request, ServletResponse response) {
		this.request = request;
		this.response = response;
	}
	
	
	public void service() {
			try {
				for(int i = 1; i <= 5; i++) {
					System.out.print(i + ", ");
					Thread.sleep(1000);
				}
				response.setStatus(Protocol.OK_CODE);
			} catch (InterruptedException e) {
//				e.printStackTrace();
				response.setStatus(Protocol.TIMEOUT_CODE);
				response.setText(Protocol.TIMEOUT_TEXT);
			}
			
//			return response;
		
	}

	@Override
	public void run() {
		service();
	}


	@Override
	public ServletResponse service(HttpRequest request, ServletResponse response) {
		// TODO Auto-generated method stub
		return null;
	}

}
