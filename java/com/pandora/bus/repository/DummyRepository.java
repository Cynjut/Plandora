package com.pandora.bus.repository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import com.pandora.ProjectTO;
import com.pandora.RepositoryFileTO;
import com.pandora.helper.DateUtil;

public class DummyRepository extends Repository {
	
	public String getUniqueName() {
		return "Dummy";
	}

	public String getId() {
		return "DUMMY_REPOSITORY";
	}

	public Vector getFiles(ProjectTO pto, String url, String path, String rev, String user, String pass) throws Exception {
		Vector response = new Vector();
		
		if (path.equals("")) {
			response.addElement(this.getDummyFile("dev", "/dev", true));
			response.addElement(this.getDummyFile("docs", "/docs", true));
		} else if (path.equals("/dev")) {
			response.addElement(this.getDummyFile("trunk", "/dev/trunk", true));
		} else if (path.equals("/docs")) {
			response.addElement(this.getDummyFile("specification.odt", "/docs/specification.odt", false));
			response.addElement(this.getDummyFile("manual.pdf", "/docs/manual.pdf", false));
			response.addElement(this.getDummyFile("mindmap.mm", "/docs/mindmap.mm", false));
		} else if (path.equals("/dev/trunk")) {
			response.addElement(this.getDummyFile("MyClass.java", "/dev/trunk/MyClass.java", false));
			response.addElement(this.getDummyFile("logo.png", "/dev/trunk/logo.png", false));
		}
		
		return response;
	}

		
	public RepositoryFileTO getFile(ProjectTO pto, String url, String pathName, String rev, String user, String pass) throws Exception {
		RepositoryFileTO dummy = this.getFileInfo(pto, url, pathName, rev, user, pass);
		
		//TODO set a dummy file into transfer object...
		//
		
		return dummy;
	}

	public RepositoryFileTO getFileInfo(ProjectTO pto, String url, String pathName, String rev, String user, String pass) throws Exception {
		RepositoryFileTO dummy = null;
		
		if (pathName.endsWith("dev/trunk/MyClass.java")) {
			dummy = this.getDummyFile("MyClass.java", "/dev/trunk/MyClass.java", false);
			dummy.setContentType("text/plain");
		} else if (pathName.endsWith("dev/trunk/logo.png")) {
			dummy = this.getDummyFile("logo.png", "/dev/trunk/logo.png", false);
			dummy.setContentType("image/png");
		} else if (pathName.endsWith("docs/specification.odt")) {
			dummy = this.getDummyFile("specification.odt", "/docs/specification.odt", false);
			dummy.setContentType("application/octet-stream");
		} else if (pathName.endsWith("docs/manual.pdf")) {
			dummy = this.getDummyFile("manual.pdf", "/docs/manual.pdf", false);
			dummy.setContentType("application/pdf");
		} else if (pathName.endsWith("docs/mindmap.mm")) {
			dummy = this.getDummyFile("mindmap.mm", "/docs/mindmap.mm", false);
			dummy.setContentType("application/x-xmind");
		}
		
		return dummy;
	}
	

	private RepositoryFileTO getDummyFile(String name, String path, boolean isDir) throws Exception{
		RepositoryFileTO item = new RepositoryFileTO();
		item.setName(name);
		item.setCreationDate(DateUtil.getNow());
		item.setComment("bla bla bla");
		item.setPath(path);
		item.setIsDirectory(new Boolean(isDir));
		item.setRevision(new Long(123));
		item.setAuthor("franz");
		item.setFileSize(isDir?0:1544);
		item.setIsLocked(new Boolean(false));
		
		if (path.endsWith("docs/mindmap.mm")) {
			byte[] buf = this.getMMFile();
			ByteArrayInputStream io = new ByteArrayInputStream(buf);
			item.setBinaryFile(io, 596);
			item.setFileInBytes(buf, 596);
			
		} else if (path.endsWith("docs/manual.pdf")){
			FileInputStream is = null;
			ByteArrayInputStream bis = null;
			try {
				byte[] bytes = null;
				File f = new File("c:\\temp\\teste.pdf");
				is = new FileInputStream(f);
		        bytes = new byte[(int)f.length()];
	    
		        int offset = 0;
		        int numRead = 0;
		        while (offset < bytes.length
		               && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
		            offset += numRead;
		        }
				bis = new ByteArrayInputStream(bytes);
				item.setBinaryFile(bis, bytes.length);
				item.setFileInBytes(bytes, bytes.length);

			} finally {
				if (is!=null) {
					is.close();	
				}
				if (bis!=null) {
					bis.close();	
				}
			}
		}
		
		return item;
	}
	
	
	private byte[] getMMFile(){
		String content = "<map version=\"0.9.0_Beta_8\">" +
					"<node CREATED=\"1267279964156\" ID=\"Freemind_Link_1998633445\" MODIFIED=\"1267279969984\" TEXT=\"WBS\">" +
					"<node CREATED=\"1267279978890\" ID=\"Freemind_Link_548103896\" MODIFIED=\"1267279985937\" POSITION=\"right\" TEXT=\"Create Customer Form\">" +
					"<node CREATED=\"1267280015390\" ID=\"Freemind_Link_1928963940\" MODIFIED=\"1267280019468\" TEXT=\"Change DataBase\"/>" +
					"<node CREATED=\"1267280021968\" ID=\"Freemind_Link_18944929\" MODIFIED=\"1267280025937\" TEXT=\"Create GUI\"/>" +
					"<node CREATED=\"1267280027859\" ID=\"Freemind_Link_1954341457\" MODIFIED=\"1267280036687\" TEXT=\"Test Customer Form\"/>" +
					"</node></node></map>";
		return content.getBytes();
	}
	
}
