import java.io.File;
import java.io.FileNotFoundException;

import protocol.HttpRequest;
import protocol.Protocol;
import protocol.Servlet;
import protocol.ServletResponse;


/**
 * TODO Put here a description of what this class does.
 *
 * @author royappmj.
 *         Created May 6, 2015.
 */
public class NewServletDelete implements Servlet {
//	private HttpRequest request;
//	private ServletResponse response;
//	
//	public NewServletDelete(HttpRequest request, ServletResponse response) {
//		this.request = request;
//		this.response = response;
//	}
	
	@Override
	public ServletResponse service(HttpRequest request, ServletResponse response){
		// DELETE request
		String rootDirectory = response.getRootDirectory();
		String dir = rootDirectory + "/" + request.getFilename();
		// Combine them together to form absolute file path
		System.out.println("deleting file: " + dir);
		File file = new File(dir);
		if(!file.exists()) {
			// Can't delete a file that doesn't exist
			System.out.println("file doesn't exist");
			response.setStatus(Protocol.NOT_FOUND_CODE);
//			throw new FileNotFoundException();
			return response;
		}
		else {
			File f = new File(dir);
			boolean success = f.delete();
			
			if(success) {
				//file was deleted				
				System.out.println("file deleted");
				response.setStatus(Protocol.OK_CODE);
				return response;
			}
			else {
				//file couldn't be deleted
				System.out.println("file can't be deleted");
				response.setStatus(Protocol.BAD_REQUEST_CODE);
//				throw new Exception();
				return response;
			}
		}
	}
}
