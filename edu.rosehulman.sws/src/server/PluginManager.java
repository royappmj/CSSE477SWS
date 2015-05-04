/*
 * PluginManager.java
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
 
package server;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
//import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

//import com.chandansminions.CoreEngine.AListPlugin;
//import com.chandansminions.CoreEngine.APlugin;
//import com.chandansminions.CoreEngine.ILogger;
//import com.chandansminions.CoreEngine.ILogger.LogCode;

import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.*;
import static java.nio.file.LinkOption.*;

import java.nio.file.attribute.*;
import java.io.*;
import java.util.*;

public class PluginManager {

//	private AListPlugin listPlugin;
//	private ILogger log;
	
	public PluginManager(/*ILogger log, AListPlugin listPlugin*/){
//		this.listPlugin = listPlugin;
//		this.log = log;
		
		
		File dir = FileSystems.getDefault().getPath("plugins").toFile();
		System.out.println(dir);
		for (File f: dir.listFiles()){
			System.out.println(f);
			if (f.toString().endsWith(".jar")){
				this.loadPlugins(f.toString());
			}
			
		}
		
		this.monitorFolder("plugins");
	}
	
	public void monitorFolder(String pathToFolder){
		
		Path dir = FileSystems.getDefault().getPath(pathToFolder);
		WatchDir watcher;
		try {
			watcher = new WatchDir(dir, false);
			watcher.processEvents();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void loadPlugins(String pathToJar){
		try {
			JarFile jarFile = new JarFile(pathToJar);
			Enumeration<JarEntry> e = jarFile.entries();

			URL[] urls = { new URL("jar:file:" + pathToJar + "!/") };
			URLClassLoader cl = URLClassLoader.newInstance(urls);
//			InputStream is = cl.getResourceAsStream("Blocks");
//			System.out.println(is);
		    while (e.hasMoreElements()) {
		        JarEntry je = e.nextElement();
//		        if(je.getName().equals("config.txt")) System.out.println(je.getName());
		        if(je.isDirectory() || !je.getName().endsWith(".class")){
		            continue;
		        }
			    // -6 because of .class
			    String className = je.getName().substring(0, je.getName().length() - 6);
			    className = className.replace('/', '.');
			    Class c = cl.loadClass(className);
			    
//			    System.out.println(className);
//			    if (APlugin.class.isAssignableFrom(c)){
//			    	APlugin plugin = (APlugin)c.newInstance();
//
//				    plugin.setILoggable(log);
//				    
//				    this.listPlugin.addPlugin(plugin);
//			    }
			}
		    jarFile.close();
			    
		} catch (Exception e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	/*
	 * Copyright (c) 2008, 2010, Oracle and/or its affiliates. All rights reserved.
	 *
	 * Redistribution and use in source and binary forms, with or without
	 * modification, are permitted provided that the following conditions
	 * are met:
	 *
	 *   - Redistributions of source code must retain the above copyright
	 *     notice, this list of conditions and the following disclaimer.
	 *
	 *   - Redistributions in binary form must reproduce the above copyright
	 *     notice, this list of conditions and the following disclaimer in the
	 *     documentation and/or other materials provided with the distribution.
	 *
	 *   - Neither the name of Oracle nor the names of its
	 *     contributors may be used to endorse or promote products derived
	 *     from this software without specific prior written permission.
	 *
	 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
	 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
	 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
	 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
	 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
	 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
	 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
	 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
	 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
	 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
	 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
	 */
	 

	 
	/**
	 * Example to watch a directory (or tree) for changes to files.
	 */
	 
	private class WatchDir {
	 
	    private final WatchService watcher;
	    private final Map<WatchKey,Path> keys;
	    private final boolean recursive;
	    private boolean trace = false;
	 
	    @SuppressWarnings("unchecked")
	    <T> WatchEvent<T> cast(WatchEvent<?> event) {
	        return (WatchEvent<T>)event;
	    }
	 
	    /**
	     * Register the given directory with the WatchService
	     */
	    private void register(Path dir) throws IOException {
	        WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
	        if (trace) {
	            Path prev = keys.get(key);
	            if (prev == null) {
	                System.out.format("register: %s\n", dir);
	            } else {
	                if (!dir.equals(prev)) {
	                    System.out.format("update: %s -> %s\n", prev, dir);
	                }
	            }
	        }
	        keys.put(key, dir);
	    }
	 
	    /**
	     * Register the given directory, and all its sub-directories, with the
	     * WatchService.
	     */
	    private void registerAll(final Path start) throws IOException {
	        // register directory and sub-directories
	        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
	            @Override
	            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
	                throws IOException {
	                register(dir);
	                return FileVisitResult.CONTINUE;
	            }
	        });
	    }
	 
	    /**
	     * Creates a WatchService and registers the given directory
	     */
	    WatchDir(Path dir, boolean recursive) throws IOException {
	        this.watcher = FileSystems.getDefault().newWatchService();
	        this.keys = new HashMap<WatchKey, Path>();
	        this.recursive = recursive;
	 
//	        if (recursive) {
//	            log.log(LogCode.MESSAGE, String.format("Scanning %s ...\n", dir));
//	            registerAll(dir);
//	            log.log(LogCode.MESSAGE,"Done.");
//	        } else {
            register(dir);
//	        }
	 
	        // enable trace after initial registration
	        this.trace = true;
	    }
	 
	    /**
	     * Process all events for keys queued to the watcher
	     */
	    void processEvents() {
	        for (;;) {
	 
	            // wait for key to be signaled
	            WatchKey key;
	            try {
	                key = watcher.take();
	            } catch (InterruptedException x) {
	                return;
	            }
	 
	            Path dir = keys.get(key);
	            if (dir == null) {
	                System.err.println("WatchKey not recognized!!");
	                continue;
	            }
	 
	            for (WatchEvent<?> event: key.pollEvents()) {
	                WatchEvent.Kind kind = event.kind();
	 
	                // TBD - provide example of how OVERFLOW event is handled
	                if (kind == OVERFLOW) {
	                    continue;
	                }
	 
	                // Context for directory entry event is the file name of entry
	                WatchEvent<Path> ev = cast(event);
	                Path name = ev.context();
	                Path child = dir.resolve(name);
	 
	                // print out event
	                //System.out.format("%s: %s\n", event.kind().name(), child);
	                
	                // If the a new JAR is created load it with PluginManager
	                if (kind == ENTRY_CREATE && name.toString().endsWith(".jar")){
	                	System.out.println("Jar Found");
	                	System.out.println(child.toString());
	                	PluginManager.this.loadPlugins(child.toString());
	                }
	 
	                // if directory is created, and watching recursively, then
	                // register it and its sub-directories
	                if (recursive && (kind == ENTRY_CREATE)) {
	                    try {
	                        if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
	                            registerAll(child);
	                        }
	                    } catch (IOException x) {
	                        // ignore to keep sample readable
	                    }
	                }
	            }
	 
	            // reset key and remove from set if directory no longer accessible
	            boolean valid = key.reset();
	            if (!valid) {
	                keys.remove(key);
	 
	                // all directories are inaccessible
	                if (keys.isEmpty()) {
	                    break;
	                }
	            }
	        }
	    }
	}
	
//	public static void main(String args[]) {
//		new PluginManager();
//	}
}