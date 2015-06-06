package com.pandora.imp;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Vector;

import au.com.bytecode.opencsv.CSVReader;

import com.pandora.CategoryTO;
import com.pandora.CustomerTO;
import com.pandora.FieldValueTO;
import com.pandora.ProjectTO;
import com.pandora.RequirementTO;
import com.pandora.TransferObject;
import com.pandora.delegate.CategoryDelegate;
import com.pandora.delegate.RequirementDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.helper.DateUtil;

public class RequestCSVImportBUS extends ImportBUS {

	@Override
	public void validate(InputStream is, ProjectTO pto, Vector fields) throws BusinessException {
    	UserDelegate udel = new UserDelegate();
		CategoryDelegate cdel = new CategoryDelegate();
		
    	if (!handler.isLeader(pto)) {
    		throw new BusinessException("Sorry. To perform this feature it is mandatory to be the leader of the project [ " + pto.getName() + "]");
    	}
    	
    	if (!pto.getBollCanAlloc()) {
    		throw new BusinessException("This project [" + pto.getName() + "] is not able to be allocated. Check the allocation status at 'Project Form'.");	
    	}
    	
    	try {
    		boolean first = true;
    		long row = 0;
			String pri = null;
			String req = null;
			String own = null;
			String cat = null;

    		List<String[]> content = this.getCSVContent(is, fields);			
		    for (String[] nextLine : content) {
        		row++;
        		try {
        			pri = nextLine[0]; req = nextLine[1]; own = nextLine[2]; cat = nextLine[3];
        		} catch(Exception e){
        			throw new BusinessException("The CSV file must contain at least 4 columns.");
        		}
				
    			if (first) {
    				if (pri.equalsIgnoreCase("priority") && req.equalsIgnoreCase("request") && own.equalsIgnoreCase("owner") && cat.equalsIgnoreCase("category")) {
    					first = false;
    				} else {
    					throw new BusinessException("The title of columns must be: 'priority', 'request', 'owner' and 'category'.");		
    				}
    			} else {
    				if (!pri.equals("0") && !pri.equals("1") && !pri.equals("2") && !pri.equals("3") && !pri.equals("4") && !pri.equals("5")) {
    					throw new BusinessException("Error at row [" + row + "]. The 'priority' values must contain values between 0 to 5 (0=to be defined, 1=lowest, 5=highest).");
    				} else if (own.equals("")) {
    					throw new BusinessException("Error at row [" + row + "]. The 'owner' cannot be empty.");
    				} else if (cat.equals("")) {
    					throw new BusinessException("Error at row [" + row + "]. The 'category' cannot be empty.");
    				} else {
    					boolean found = false;
    					Vector<CustomerTO> list = udel.getCustomerByProject(pto, false);
    					if (list!=null) {
        					for (CustomerTO cto : list) {
    							if (cto.getUsername().equals(own)) {
    								found = true;
    								break;
    							}
    						}    						
    					} else{
    						throw new BusinessException("Error at row [" + row + "]. The 'project' [" + pto.getName() + "] does not contain customers.");	
    					}
    					if (!found) {
    						throw new BusinessException("Error at row [" + row + "]. The 'owner' [" + own + "] was not found at project [" + pto.getName() + "].");
    					}
    				}
    				
    				CategoryTO cto = cdel.getCategoryByName(cat, CategoryTO.TYPE_REQUIREMENT, pto);
    				if (cto==null) {
    					if (cat.equalsIgnoreCase("NONE")) {
    						cto = cdel.getCategory(new CategoryTO("0"));
    					}
    					if (cto==null) {
    						throw new BusinessException("Error at row [" + row + "]. The 'category' [" + cat + "] was not found at project [" + pto.getName() + "]. Use the key-word 'NONE' in case of empty category.");	
    					}
    				}
    			}
    		}
    	} catch(BusinessException e){
    		throw e;
    	} catch(Exception e){
    		throw new BusinessException("The CSV file cannot be read and parsed.");
    	}
	}

	
	@Override
	public void importFile(InputStream is, ProjectTO pto, Vector fields) throws BusinessException {
		UserDelegate udel = new UserDelegate();
		CategoryDelegate cdel = new CategoryDelegate();
		RequirementDelegate rdel = new RequirementDelegate();
    	try {
    		boolean first = true;
			String pri = null;
			String req = null;
			String own = null;
			String cat = null;

			Vector<RequirementTO> rlist  = new Vector<RequirementTO>();
    		List<String[]> content = this.getCSVContent(is, fields);			
		    for (String[] nextLine : content) {
        		try {
        			pri = nextLine[0]; req = nextLine[1]; own = nextLine[2]; cat = nextLine[3];
        		} catch(Exception e){
        			throw new BusinessException("The CSV file must contain at least 4 columns.");
        		}
				
    			if (!first) {
    				RequirementTO rto = this.getStandardReq();
    				rto.setId(null);
    				rto.setCreationDate(DateUtil.getNow());
    				rto.setDescription(req);
    				rto.setProject(pto);
    				rto.setType(RequirementTO.PLANNING_REQUIREMENT);
    				
    				CategoryTO cto = cdel.getCategoryByName(cat, CategoryTO.TYPE_REQUIREMENT, pto);
    				if (cto==null) {
    					cto = cdel.getCategory(new CategoryTO("0"));
    				}
    				rto.setCategory(cto);
    				
    				if (pri.equals("1") || pri.equals("2") || pri.equals("3") || pri.equals("4") || pri.equals("5") || pri.equals("0")) {
    					rto.setPriority(new Integer(pri));	
    				} else {
    					rto.setPriority(new Integer("0"));
    				}
    				
					Vector<CustomerTO> list = udel.getCustomerByProject(pto, false);
					if (list!=null) {
    					for (CustomerTO cuto : list) {
							if (cuto.getUsername().equals(own)) {
								rto.setRequester(cuto); 
								break;
							}
						}    						
					}
    				rlist.add(rto);
    				
    			} else {
    				first = false;
    			}
    		}
		    
			rdel.insertRequirement(rlist);
			
    	} catch(BusinessException e){
    		throw e;
    	} catch(Exception e){
    		throw new BusinessException("The CSV file cannot be read and parsed.");
    	}
	}


	@Override
	public String getLabel() throws BusinessException {
		return "label.importExport.reqCSVImport";
	}

	@Override
	public Vector<FieldValueTO> getFields() throws BusinessException {
    	Vector<FieldValueTO> list = new Vector<FieldValueTO>();
    	
    	String lbl0 = super.handler.getBundle().getMessage(super.handler.getLocale(), "label.importExport.reqCSVImport.delim.1");
    	String lbl1 = super.handler.getBundle().getMessage(super.handler.getLocale(), "label.importExport.reqCSVImport.delim.2");
    	String lbl2 = super.handler.getBundle().getMessage(super.handler.getLocale(), "label.importExport.reqCSVImport.delim.3");

    	Vector<TransferObject> options = new Vector<TransferObject>();
    	options.add(new TransferObject("1", lbl0));
    	options.add(new TransferObject("2", lbl1));
    	options.add(new TransferObject("3", lbl2));

    	FieldValueTO delimiter = new FieldValueTO("DELIMITER", "label.importExport.reqCSVImport.delim", options);
    	list.add(delimiter);
    	
    	return list;
	}

	
	private List<String[]> getCSVContent(InputStream is, Vector<FieldValueTO> fields) throws BusinessException{
    	Reader reader = null;
    	DataInputStream in = null;
    	List<String[]> response = null;
    	
    	char delimiter = ';';
        FieldValueTO fvto = (FieldValueTO)fields.elementAt(0);
        if (fvto!=null && fvto.getCurrentValue().equals("1")) {
        	delimiter = ',';
        } else if (fvto!=null && fvto.getCurrentValue().equals("2")) {
        	delimiter = ';';
        } else if (fvto!=null && fvto.getCurrentValue().equals("3")) {
        	delimiter = '\t';
        }
    	
		try {
			in = new DataInputStream(is);
			reader = new BufferedReader(new InputStreamReader(in));
		} catch (Exception e) {
			throw new BusinessException(e);
		} finally {
			try {
				if (in!=null) {
					in.close();	
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		try {
	    	if (reader==null || !reader.ready()) {
	    		throw new BusinessException("The CSV file cannot be opened.");	
	    	}
			CSVReader parser = new CSVReader(reader, delimiter);
			response = parser.readAll();
	    	
		} catch (Exception e) {
			throw new BusinessException(e);
		}
		
		return response;
	}
	
	private RequirementTO getStandardReq() {
		RequirementTO rto = new RequirementTO();
		rto.setAdditionalComment(null);
		rto.setAdditionalFields(null);
		rto.setAttachments(null);
		rto.setDeadlineDate(null);
		rto.setDiscussionTopics(null);
		rto.setEstimatedTime(null);
		rto.setFinalDate(null);
		rto.setIsAdjustment(false);
		rto.setIteration(null);
		rto.setParentRequirementId(null);
		rto.setPreApprovedReqResource(null);
		rto.setRelationList(null);
		rto.setReopening(false);
		rto.setReopeningOccurrences(null);
		//rto.setRequirementHistory(null);
		rto.setResourceTaskList(null);
		rto.setSuggestedDate(null);
		rto.setVisible(true);
		rto.setRequirementStatus(null);		
		return rto;
	}	
	
}