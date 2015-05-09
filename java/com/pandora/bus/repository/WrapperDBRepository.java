package com.pandora.bus.repository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Vector;

import com.pandora.DBQueryParam;
import com.pandora.ProjectTO;
import com.pandora.RepositoryFileTO;
import com.pandora.RepositoryLogTO;
import com.pandora.UserTO;
import com.pandora.delegate.DbQueryDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.helper.DateUtil;
import com.pandora.helper.LogUtil;

public class WrapperDBRepository extends Repository {
	
	@Override
	public String getUniqueName() {
		return "label.projRepository.db";
	}

	
	@Override
	public String getId() {
		return "DB_REPOSITORY";
	}

	
	@Override
	public boolean showUserPwdFields() {
		return false;
	}

	
	@Override
	public Vector<RepositoryFileTO> getFiles(ProjectTO pto, String url, String path, String rev, String user, String pass) throws Exception {
		Vector<RepositoryFileTO> response = new Vector<RepositoryFileTO>();
		DbQueryDelegate db = new DbQueryDelegate();
		
		String versionPath = "";
		long version = this.formatRevision(rev);
		if (version>0) {
			versionPath = "where v.version <= " + version + " "; 
		}
		
		if (path==null) {
			path = "";
		}

		String wherePath = this.getWherePath(path);
		String sql = "select sub.id, sub.version, v.content_type, v.creation_date, v.comment, u.username, " + 
				           "i.repository_file_name, i.repository_file_path, i.is_directory, v.file_size " +
				     "from db_repository_item i " +
				     	  "left outer join db_repository_version v on v.repository_item_id = i.id " +
			              "left outer join tool_user u on v.user_id = u.id " +
			              "left outer join (select v.repository_item_id as id, max(v.version) as version " +
			                                 "from db_repository_version v " + versionPath + " group by v.repository_item_id " +
			                               ") as sub on i.id = sub.id and v.version = sub.version " +
					 "where project_id = ? and ((sub.id is not null and i.is_directory = '0') or (i.is_directory = '1')) " + wherePath +
					 "order by i.repository_file_name";
		
		int[] types = new int[]{Types.VARCHAR};
		Vector<String> param = new Vector<String>();
		param.add(pto.getId());
		Vector list = db.performQuery(sql, types, param);
		
		if (list!=null && list.size()>1) {
			for (int i=1; i<list.size(); i++) {			
				Vector<Object> item = (Vector)list.elementAt(i);
				
				RepositoryFileTO rfto = new RepositoryFileTO();
				
	    		String isD = (String)item.elementAt(8);
	    		if (isD!=null) {
		    		rfto.setIsDirectory(new Boolean(isD.equals("1")));	    			
	    		}
				
	    		rfto.setName((String)item.elementAt(6));
	    		rfto.setComment((String)item.elementAt(4));
	    		rfto.setPath((String)item.elementAt(7));
	    		rfto.setAuthor((String)item.elementAt(5));
	    		
	    		Timestamp ts = (Timestamp)item.elementAt(3);
	    		if (ts!=null) {
	    			rfto.setCreationDate(new Timestamp(ts.getTime()));	
	    		}
	    			    		
	    		String vs = (String)item.elementAt(1);
	    		if (vs!=null) {
	    			rfto.setRevision(new Long(vs));	
	    		}
	    		
	    		String size = (String)item.elementAt(9);
	    		if (size!=null) {
	    			rfto.setFileSize(Long.parseLong(size));	
	    		}	    		
	    		
	    		response.addElement(rfto);
			}			
		}

		return response;		
	}

	
	@Override
	public RepositoryFileTO getFile(ProjectTO pto, String url, String pathName, String rev, String user, String pass) throws Exception {
		RepositoryFileTO response = null;		
		DbQueryDelegate db = new DbQueryDelegate();
		
		String versionPath = "";
		long version = this.formatRevision(rev);
		if (version>0) {
			versionPath = "where v.version <= " + version + " "; 
		}

		String sql = "select sub.id, sub.version, v.content_type, v.creation_date, v.comment, u.username, " + 
				           "i.repository_file_name, i.repository_file_path, i.is_directory, v.file_size, v.binary_file " +
				     "from db_repository_item i " +
				     	  "left outer join db_repository_version v on v.repository_item_id = i.id " +
			              "left outer join tool_user u on v.user_id = u.id " +
			              "left outer join (select v.repository_item_id as id, max(v.version) as version " +
			                                 "from db_repository_version v " + versionPath + " group by v.repository_item_id " +
			                               ") as sub on i.id = sub.id and v.version = sub.version " +
					 "where project_id = ? and i.repository_file_path = ? and sub.id is not null";

		int[] types = new int[]{Types.VARCHAR, Types.VARCHAR};
		Vector<String> param = new Vector<String>();
		param.add(pto.getId());
		param.add(pathName);
		Vector list = db.performQuery(sql, types, param);
		
		if (list!=null && list.size()>1) {
			Vector item = (Vector)list.elementAt(1);
			response = new RepositoryFileTO();
			
			response.setId((String)item.elementAt(0));
			
    		String isD = (String)item.elementAt(8);
    		if (isD!=null) {
    			response.setIsDirectory(new Boolean(isD.equals("1")));	    			
    		}

    		response.setContentType((String)item.elementAt(2));
    		response.setName((String)item.elementAt(6));
    		response.setComment((String)item.elementAt(4));
    		response.setPath((String)item.elementAt(7));
    		response.setAuthor((String)item.elementAt(5));
    		
    		Timestamp ts = (Timestamp)item.elementAt(3);
    		if (ts!=null) {
    			response.setCreationDate(new Timestamp(ts.getTime()));	
    		}
    			    		
    		String vs = (String)item.elementAt(1);
    		if (vs!=null) {
    			response.setRevision(new Long(vs));	
    		}
    		
    		String size = (String)item.elementAt(9);
    		if (size!=null) {
    			response.setFileSize(Long.parseLong(size));	
    		}	 
    		
    		ByteArrayInputStream bis = null;
    		ByteArrayOutputStream bos = null;
    		try {
        		bis = (ByteArrayInputStream)item.elementAt(10);
        		if (bis!=null) {
        		    response.setBinaryFile(bis , bis.available());

        		    int bytesRead = 0;
        		    byte[] buffer = new byte[bis.available()];  
        		    bos = new ByteArrayOutputStream();
        		    while ((bytesRead = bis.read(buffer)) != -1) {
        		        bos.write(buffer, 0, bytesRead);
        		    }
        		    response.setFileInBytes(buffer, buffer.length);
        		}    			
    		} catch (Exception e){
    			LogUtil.log(this, LogUtil.LOG_ERROR, "error getting blob from db repository", e);
    			
    		} finally {
    			if (bis!=null) bis.close();
			    if (bos!=null) bos.close();
    		}
		        		
		}
		return response;
	}
	

	public RepositoryFileTO getFileInfo(ProjectTO pto, String url, String pathName, String rev, String user, String pass) throws Exception {
		return getFile(pto, url, pathName, rev, user, pass);
	}
	
	
	@Override
	public void commitFile(ProjectTO pto, String url, String path, String fileName, String rev,	
			String user, String pass, String logMessage, String contentType, byte[] data) throws Exception {
		DbQueryDelegate db = new DbQueryDelegate();
		ArrayList<String> sqls = new ArrayList<String>();
		ArrayList<DBQueryParam> params = new ArrayList<DBQueryParam>();
		UserDelegate udel = new UserDelegate();
		String seq = null;
		
		UserTO uto = new UserTO();
		uto.setUsername(user);
		uto = udel.getObjectByUsername(uto);
		if (uto==null) {
			throw new Exception("The user [" + user + "] cannot be found.");
		}
		 
		long version = this.getNewVersionSeq(pto);
		if (path==null) {
			path = "";
		}
		
		String pathName = fileName;
		if (!path.trim().equals("")) {
			pathName = path + "/" + fileName;
		}
			
		RepositoryFileTO rfto = this.getFile(pto, url, pathName, rev, user, pass);
		if (rfto==null) {

			//generate sequence to the file...
			seq = db.getNewId();

			RepositoryFileTO parentFolder = this.getFile(pto, url, path, rev, user, pass);
			
			//Checks if parent folder is into DB. Otherwise, create each folder level and return the highest level...
			if (parentFolder==null) {
				parentFolder = this.createFolders(path, uto, pto, rev, logMessage);	
			}
			
			sqls.add("insert into db_repository_item (id, repository_file_path, repository_file_name, project_id, is_directory, parent_id) values (?, ?, ?, ?, ?, ?)");
			int[] tp2 = {Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.INTEGER, Types.VARCHAR};
			Vector<Object> pr2 = new Vector<Object>();
			pr2.add(seq);
			pr2.add(pathName);
			pr2.add(fileName);
			pr2.add(pto.getId());
			pr2.add(new Integer(0));
			if (parentFolder!=null) {
				pr2.add(parentFolder.getId());
			} else {
				pr2.add(null);	
			}
			params.add(new DBQueryParam(tp2, pr2));
			
		} else {
			seq = rfto.getId();
		}
		
		sqls.add("insert into db_repository_version (repository_item_id, version, content_type, creation_date, comment, user_id, file_size, binary_file) values (?, ?, ?, ?, ?, ?, ?, ?)");
		int[] tp3 = {Types.VARCHAR, Types.INTEGER, Types.VARCHAR, Types.TIMESTAMP, Types.VARCHAR, Types.VARCHAR, Types.INTEGER, Types.BLOB};
		Vector<Object> pr3 = new Vector<Object>();
		pr3.add(seq);
		pr3.add(new Integer(version+""));
		pr3.add("application/octet-stream");
		pr3.add(DateUtil.getNow());
		pr3.add(logMessage);
		pr3.add(uto.getId());
		pr3.add(new Integer(data.length));
		pr3.add(data);
		params.add(new DBQueryParam(tp3, pr3));
		
		db.executeQuery(sqls, params);
		
	}	

	@Override	
	public Vector<RepositoryLogTO> getLogs(ProjectTO pto, String url, String path, String rev, String user, String pass) throws Exception {
		Vector<RepositoryLogTO> response = new Vector<RepositoryLogTO>();
		DbQueryDelegate db = new DbQueryDelegate();
		UserDelegate udel = new UserDelegate();				
		String versionPath = "";
		String directoryWhere = "";
		
		long version = this.formatRevision(rev);
		if (version>0) {
			versionPath = " and v.version <= " + version; 
		}

		RepositoryFileTO rfto = this.getFile(pto, url, path, rev, user, pass);
		if (rfto!=null && rfto.getIsDirectory()!=null && rfto.getIsDirectory().booleanValue()) {
			directoryWhere = " or repository_file_path LIKE '" + path + "/%')";
		} else {
			directoryWhere = ")";
		}
		
		
		String sql = "select v.version, v.creation_date, v.comment, v.user_id, " +
							"i.repository_file_name, i.repository_file_path " +
					 "from db_repository_item i " +
					 		"left outer join db_repository_version v on v.repository_item_id = i.id " + 
					 		"left outer join tool_user u on v.user_id = u.id " +
					 "where project_id = ? and (i.repository_file_path = ? " + directoryWhere + versionPath;
		
		int[] types = new int[]{Types.VARCHAR, Types.VARCHAR};
		Vector<Object> param = new Vector<Object>();
		param.add(pto.getId());
		param.add(path);
		Vector list = db.performQuery(sql, types, param);
		
		if (list!=null && list.size()>1) {
			for (int i=1; i<list.size(); i++) {
				Vector item = (Vector)list.elementAt(i);
				RepositoryLogTO log = new RepositoryLogTO();
			
				UserTO uto = udel.getUser(new UserTO((String)item.elementAt(3)));
				if (uto!=null) {
					log.setAuthor(uto.getUsername());
				}			
				log.setPath((String)item.elementAt(5));
				log.setRevision(new Long((String)item.elementAt(0)));
				log.setDate((Timestamp)item.elementAt(1));
				String logMsg = (String)item.elementAt(2);
				if (logMsg!=null) {
					log.setMessage(logMsg);
				} else {
					log.setMessage("");
				}
				
				response.add(log);				
			}
		}
		
		return response;
	}
	

	@Override
	public void newFile(ProjectTO pto, String url, String path,	String fileName, String user, 
			String pass, String logMessage,	String contentType, byte[] data) throws Exception {
		this.commitFile(pto, url, path, fileName, null, user, pass, logMessage, contentType, data);
	}

	
	@Override
	public void createFolder(ProjectTO pto, String repositoryURL, String path,
			String username, String logMessage) throws Exception {
		UserDelegate udel = new UserDelegate();
		UserTO uto = new UserTO();
		uto.setUsername(username);
		uto = udel.getObjectByUsername(uto);
		if (uto==null) {
			throw new Exception("The user [" + username + "] cannot be found.");
		}

		this.createFolders(path, uto, pto, null, logMessage);
	}


	@Override
	public boolean canUploadFile() {
		return true;
	}

	
	private RepositoryFileTO createFolders(String path, UserTO uto, ProjectTO pto, String rev, String logMsg) throws Exception {
		DbQueryDelegate db = new DbQueryDelegate();
		
		RepositoryFileTO response = null;
		if (path!=null && !path.trim().equals("")) {
			
			String partialPath = "";
			String parentFolderId = null;
			String[] folders = path.split("\\/");
			for (int i=0; i<folders.length; i++) {
				String folderName = folders[i].trim();
				
				String currePath = (partialPath.equals("")?folderName:partialPath + "/" + folderName);
				RepositoryFileTO parentFolder = this.getFile(pto, null, currePath, rev, null, null);
				
				if (parentFolder==null) {
					String seq = db.getNewId();
					ArrayList<String> sqls = new ArrayList<String>();
					ArrayList<DBQueryParam> params = new ArrayList<DBQueryParam>();

					sqls.add("insert into db_repository_item (id, repository_file_path, repository_file_name, project_id, " +
										"is_directory, parent_id) values (?, ?, ?, ?, ?, ?)");
					int[] tp = {Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.INTEGER, Types.VARCHAR};
					Vector<Object> pr = new Vector<Object>();
					pr.add(seq); pr.add(currePath); pr.add(folderName); pr.add(pto.getId()); 	pr.add(new Integer(1)); pr.add(parentFolderId);
					params.add(new DBQueryParam(tp, pr));

					long version = this.getNewVersionSeq(pto);

					sqls.add("insert into db_repository_version (repository_item_id, version, content_type, creation_date, " +
							            "comment, user_id, file_size, binary_file) values (?, ?, 'application/octet-stream', ?, ?, ?, 0, null)");
					int[] tp2 = {Types.VARCHAR, Types.INTEGER, Types.TIMESTAMP, Types.VARCHAR, Types.VARCHAR};
					Vector<Object> pr2 = new Vector<Object>();
					pr2.add(seq); pr2.add(new Integer(version+""));	pr2.add(DateUtil.getNow());	pr2.add(logMsg); pr2.add(uto.getId());
					params.add(new DBQueryParam(tp2, pr2));
					
					db.executeQuery(sqls, params);
					
					response = this.getFile(pto, null, currePath, rev, null, null);
					if (response==null) {
						throw new Exception("The folder [" + path + "] cannot be created.");		
					}
					parentFolderId = response.getId();
					
				} else {
					parentFolderId = parentFolder.getId();
				}

				if (partialPath.equals("")) {
					partialPath = folderName;
				} else {
					partialPath = partialPath.concat("/" + folderName);	
				}
			}
		}
		return response;
	}
	
	
	private synchronized long getNewVersionSeq(ProjectTO pto) throws BusinessException {
		DbQueryDelegate del = new DbQueryDelegate();
		long currentValue = 0;
		
		try {
			
			int[] tp = {Types.VARCHAR};
			Vector<Object> pr = new Vector<Object>();
			pr.add(pto.getId());
			Vector vseq = del.performQuery("select id from db_repository_sequence where project_id=?", tp, pr);
			if (vseq!=null && vseq.size()>1) {
				Vector item = (Vector)vseq.get(1);
				currentValue = Long.parseLong((String)item.get(0));
			}
		} catch (NullPointerException e) {
		    LogUtil.log(this, LogUtil.LOG_ERROR, "current project sequence is null, assuming value=1", e);
		    currentValue = 0;
		}

	    currentValue++;
		if (currentValue>1) {
		    del.executeQuery("update db_repository_sequence set id=" + currentValue + " where project_id='" + pto.getId() + "'");			
		} else {
		    del.executeQuery("insert into db_repository_sequence (id, project_id) values (1, '" + pto.getId() + "')");
		}
	    
		return currentValue;	   
	}
	

	private String getWherePath(String path) {
		String reponse = "";
		
		if (path!=null) {
			String localPath = path; 
			if (!path.trim().equals("")){
				localPath = localPath + "/%";
			}
			
			if (path.equals("")) {
				reponse = "and repository_file_path NOT LIKE '%/%' ";
			} else {
				reponse = "and repository_file_path LIKE '" + localPath + "' and repository_file_path NOT LIKE '" + localPath + "/%' ";				
			}
		}		
		return reponse;
	}

}
