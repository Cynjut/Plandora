package com.pandora.bus.repository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLock;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNProperty;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.io.diff.SVNDeltaGenerator;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import com.pandora.ProjectTO;
import com.pandora.RepositoryFileTO;
import com.pandora.RepositoryLogTO;

public class WrapperSVNRepository extends Repository {

	public String getUniqueName() {
		return "label.projRepository.svn";
	}

	public String getId() {
		return "SVN_REPOSITORY";
	}

	public Vector getFiles(ProjectTO pto, String url, String path, String rev, String user, String pass) throws Exception {
		Vector response = new Vector();
		
		SVNRepository repository = this.connect(url, user, pass);
	    long revision = this.formatRevision(rev);

		try {
            SVNNodeKind nodeKind = repository.checkPath(path, revision);
            if (nodeKind == SVNNodeKind.NONE) {
            	throw new Exception("There is no entry at '" + url + "'.");
            } else if (nodeKind == SVNNodeKind.FILE) {
            	throw new Exception("The entry at '" + url + "' is a file while a directory was expected.");
            }
            response = this.listEntries(repository, path, revision);
            
        } catch (SVNException e) {
        	throw new Exception(e);
        }
		
		return response;		
	}


	public Vector getLogs(ProjectTO pto, String url, String path, String rev, String user, String pass) throws Exception {
		Vector response = new Vector();
		
		SVNRepository repository = this.connect(url, user, pass);
	    long revision = this.formatRevision(rev);
	    
		try {
			Collection logEntries = repository.log(
					new String[]{path}, null, 0, revision, false, false);
        	Iterator iterator = logEntries.iterator();
        	while (iterator.hasNext()) {
        		SVNLogEntry logEntry = ( SVNLogEntry ) iterator.next( );
        		RepositoryLogTO log = new RepositoryLogTO();
        		log.setAuthor(logEntry.getAuthor());
        		log.setPath(path);
        		log.setRevision(logEntry.getRevision());
        		if (logEntry.getDate()!=null) {
        			log.setDate(new Timestamp(logEntry.getDate().getTime()));	
        		}
        		log.setMessage(logEntry.getMessage());
        		response.add(log);
        	}
		} catch (Exception e) {
			e.printStackTrace();
        	throw new Exception(e);
        }
		
		return response;		
	}

	
	public RepositoryFileTO getFile(ProjectTO pto, String url, String pathName, String rev, String user, String pass) throws Exception {
		RepositoryFileTO response = null;
		SVNRepository repository = this.connect(url, user, pass);
	    long revision = this.formatRevision(rev);

        ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
        try {
        	SVNProperties fileProperties = new SVNProperties();
            SVNNodeKind nodeKind = repository.checkPath(pathName, revision);

            if (nodeKind == SVNNodeKind.NONE) {
            	throw new Exception("There is no entry at '" + url + pathName + "'.");
            } else if (nodeKind == SVNNodeKind.DIR) {
            	throw new Exception("The entry at '" + url + pathName + "' is not a file.");
            } else {
                repository.getFile(pathName, revision, fileProperties, baos);
            }
            String fileName = getNameFromPath(pathName);

            String mimeType = fileProperties.getStringValue(SVNProperty.MIME_TYPE);
            response = new RepositoryFileTO();
            response.setContentType(mimeType);
            response.setName(fileName);
            response.setFileInBytes(baos.toByteArray(), baos.toByteArray().length);
            
        } catch (SVNException e) {
        	throw new Exception(e);
        }
		return response;
	}
	

	public RepositoryFileTO getFileInfo(ProjectTO pto, String url, String pathName, String rev, String user, String pass) throws Exception {
		RepositoryFileTO response = null;
		SVNRepository repository = this.connect(url, user, pass);
	    long revision = this.formatRevision(rev);

        ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
        try {
        	SVNProperties fileProperties = new SVNProperties();
        	       	
        	//pathName = this.ajustPath(url, pathName, repository);
            SVNNodeKind nodeKind = repository.checkPath(pathName, revision);
            
            if (nodeKind == SVNNodeKind.NONE) {
            	fileProperties = null;
            } else if (nodeKind == SVNNodeKind.DIR) {
            	fileProperties = null;
            } else {
                repository.getFile(pathName, revision, fileProperties, baos);
            }
            
            if (fileProperties!=null) {
                String fileName = this.getNameFromPath(pathName);
                String mimeType = fileProperties.getStringValue(SVNProperty.MIME_TYPE);
                response = new RepositoryFileTO();
                response.setContentType(mimeType);
                response.setName(fileName);
                response.setAuthor(fileProperties.getStringValue(SVNProperty.LAST_AUTHOR));
                response.setRevision(new Long(fileProperties.getStringValue(SVNProperty.COMMITTED_REVISION)));
                
                String lastCommit = fileProperties.getStringValue(SVNProperty.COMMITTED_DATE);
                //response.setCreationDate(DateUtil.getDateTime(lastCommit, "yyyy-MM-dd HH:mm:ss", super.handler.getLocale()));
                
                response.setIsDirectory(new Boolean(false));
                if (baos!=null) {
                	response.setFileSize(baos.size());	
                }            	
            }
                        
        } catch (SVNException e) {
        	throw new Exception(e);
        }
		return response;
	}

	
	public void commitFile(ProjectTO pto, String url, String path, String fileName, String rev, String user, String pass, String logMessage, String contentType, byte[] data) throws Exception {
		ISVNEditor editor = null;
		try {
			path = path.replaceAll(url + "/", "");
			
			SVNRepository repository = this.connect(url, user, pass);
			long revision = this.formatRevision(rev);
			
			editor = repository.getCommitEditor( logMessage , null, true, null);
			
			String filePath = path + "/" + fileName;
			
			editor.openRoot(revision);
			//editor.addDir(path, null, revision);
			editor.addFile(filePath, null, revision);
	        editor.applyTextDelta(filePath, null );

			SVNDeltaGenerator deltaGenerator = new SVNDeltaGenerator();
		    String checksum = deltaGenerator.sendDelta(filePath, new ByteArrayInputStream(data), editor, true);
		    editor.closeFile(filePath , checksum );
	        //editor.closeDir();
	        editor.closeDir();
		    
		    SVNCommitInfo info = editor.closeEdit();
		    System.out.println(info); 
		    
		} catch (Exception e){
			if (editor!=null) {
				editor.abortEdit();	
			}
			throw e;
		}
	}
	
	
	private Vector listEntries(SVNRepository repository, String path, long revision) throws SVNException {
    	Vector response = new Vector();
    	Collection entries = repository.getDir(path, revision, null, (Collection)null);
    	Iterator iterator = entries.iterator();
    	while (iterator.hasNext()) {
    		SVNDirEntry entry = (SVNDirEntry) iterator.next();
    		RepositoryFileTO item = new RepositoryFileTO();
    		item.setName(entry.getName());
    		item.setCreationDate(new Timestamp(entry.getDate().getTime()));
    		item.setComment(entry.getCommitMessage());
    		item.setPath(entry.getURL().toDecodedString());
    		item.setIsDirectory(new Boolean(entry.getKind() == SVNNodeKind.DIR));
    		item.setRevision(new Long(entry.getRevision()));
    		item.setAuthor(entry.getAuthor());
    		item.setFileSize(entry.getSize());
    		
    		SVNLock lock = entry.getLock();
    		if (lock!=null && lock.getOwner()!=null) {
    			item.setIsLocked(new Boolean(true));
        		item.setLockOwner(lock.getOwner());
        		if (lock.getCreationDate()!=null) {
        			item.setLockDate(new Timestamp(lock.getCreationDate().getTime()));	
        		}
        		if (lock.getExpirationDate()!=null) {
        			item.setLockExpirationDate(new Timestamp(lock.getExpirationDate().getTime()));	
        		}
        		item.setLockComment(lock.getComment());
    		} else {
    			item.setIsLocked(new Boolean(false));
    		}

    		/*
    		if (entry.getKind() != SVNNodeKind.DIR) {
        		SVNProperties fileProperties = new SVNProperties();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
        		repository.getFile(path + entry.getName(), -1, fileProperties, baos);
       		 	String mimeType = fileProperties.getStringValue(SVNProperty.MIME_TYPE);
       		 	item.setContentType(mimeType);
    		}
    		*/
    		
    		response.addElement(item);
    	}
    	return response;
    }
	
    
	private SVNRepository connect(String url, String user, String pass) throws Exception{
		this.setupLibrary();
		
		SVNRepository repository = null;
		try {
		    repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(url));
		} catch (SVNException e) {
			throw new Exception(e);
		}
		
		ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(user, pass);
		repository.setAuthenticationManager(authManager);	
		
		return repository;
	}
	
	
    private void setupLibrary() {
    	//For using over http:// and https://
        DAVRepositoryFactory.setup();

        //For using over svn:// and svn+xxx://
        SVNRepositoryFactoryImpl.setup();
        
        //For using over file:///
        FSRepositoryFactory.setup();
    }
    

	
	private String getNameFromPath(String pathName) {
		String fileName = pathName;
		int lastSlash = pathName.lastIndexOf("/");
		if (lastSlash>-1) {
			fileName = pathName.substring(lastSlash+1);
		}
		return fileName;
	}

	
	/*
	private String ajustPath(String url, String pathName, SVNRepository repository) throws SVNException {
		String reponse = pathName;
		try {
			String root = repository.getRepositoryRoot(true).toString();
			if (!url.equals(root)) {
				String sufix = url.replaceAll(root, "");	
				pathName = sufix + pathName;
			}
		} catch (Exception e) {
			reponse = pathName;
		}
		return reponse;
	}
*/
}
