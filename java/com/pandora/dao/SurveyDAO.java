package com.pandora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;

import com.pandora.ProjectTO;
import com.pandora.QuestionAlternativeTO;
import com.pandora.QuestionAnswerTO;
import com.pandora.SurveyQuestionTO;
import com.pandora.SurveyTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.exception.DataAccessException;
import com.pandora.helper.DateUtil;
import com.pandora.helper.LogUtil;

public class SurveyDAO extends DataAccess {

	public Vector<QuestionAnswerTO> getAnswerByQuestion(SurveyQuestionTO sqto) throws DataAccessException {
        Vector<QuestionAnswerTO> response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getAnswerByQuestion(sqto, null, c);
		} catch(Exception e) {
			throw new DataAccessException(e);
		} finally {
			this.closeConnection(c);
		}
        return response;
	}
	
	
    public boolean checkIfThereAreAnswers(SurveyQuestionTO qto) throws DataAccessException {
    	boolean response = false;
        Connection c = null;
		try {
			c = getConnection();
			response = this.checkIfThereAreAnswers(qto, c);
		} catch(Exception e) {
			throw new DataAccessException(e);
		} finally {
			this.closeConnection(c);
		}
        return response;
	}

	
	public Vector<SurveyTO> getSurveyListByUser(UserTO uto, boolean ignoreClosed) throws DataAccessException {
        Vector<SurveyTO> response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getSurveyListByUser(uto, ignoreClosed, c);
		} catch(Exception e) {
			throw new DataAccessException(e);
		} finally {
			this.closeConnection(c);
		}
        return response;
	}


	public Vector<SurveyTO> getSurveyListByProject(ProjectTO pto, boolean ignoreClosed) throws DataAccessException {
        Vector<SurveyTO> response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getSurveyListByProject(pto, ignoreClosed, c);
		} catch(Exception e) {
			throw new DataAccessException(e);
		} finally {
			this.closeConnection(c);
		}
        return response;
	}
	
	public void saveAnswer(Vector<QuestionAnswerTO> answerList) throws DataAccessException {
		Connection c = null;
		PreparedStatement pstmt = null;		
		try {
			
			if (answerList!=null) {
				c = getConnection(false);
								
				Iterator<QuestionAnswerTO> i = answerList.iterator();
				while(i.hasNext()) {
					QuestionAnswerTO qato = i.next();
					
					if (qato.getUser()!=null) {
						pstmt = c.prepareStatement("delete from question_answer " +
												   "where survey_id=? and question_id=? and user_id=?");
						pstmt.setString(1, qato.getQuestion().getSurvey().getId());
						pstmt.setString(2, qato.getQuestion().getId());
						pstmt.setString(3, qato.getUser().getId());
						pstmt.executeUpdate();						
					}
					
					this.insertAnswer(qato, c);
				}
				
				c.commit();				
			}

		} catch(Exception e){
			try {				
				c.rollback();
			} catch (SQLException er) {
				LogUtil.log(this, LogUtil.LOG_ERROR, "", er);
			}
			throw new DataAccessException(e);
			
		} finally{
			super.closeStatement(null, pstmt);
			this.closeConnection(c);
		}		
	}
	
	
	private Vector<SurveyTO> getSurveyListByUser(UserTO uto, boolean ignoreClosed, Connection c) throws DataAccessException{
		Vector<SurveyTO> response = new Vector<SurveyTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		String closedWhere = "";
		
		try {
			
			if (ignoreClosed) {
				closedWhere = " and (s.final_date is null or s.final_date >= ?)";
			}
			
			pstmt = c.prepareStatement("select s.id, s.name, s.description, s.is_template, s.is_anonymous, " +
					"s.project_id, s.creation_date, s.final_date, s.date_publishing, " +
					"s.anonymous_key, p.name as PROJECT_NAME " +
					"from survey s, project p " +
					"where s.project_id = p.id " +
					  "and s.project_id in (select distinct project_id from customer where id = ?) " +
					  "and s.creation_date <= ?" + closedWhere + " order by s.creation_date");	
			pstmt.setString(1, uto.getId());
			pstmt.setTimestamp(2, DateUtil.getNow());
			if (ignoreClosed) {
				pstmt.setTimestamp(3, DateUtil.getNow());	
			}
			rs = pstmt.executeQuery();
			while (rs.next()) {
				SurveyTO to = this.populateBeanByResultSet(rs);
				to.setGenericTag("[" + getString(rs, "PROJECT_NAME") + "] " + to.getName());
			    response.addElement(to);
			}
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		} finally {
			super.closeStatement(rs, pstmt);			
		}
		return response;		
	}
	
	
    private Vector<SurveyTO> getSurveyListByProject(ProjectTO pto, boolean ignoreClosed, Connection c) throws DataAccessException {
		Vector<SurveyTO> response= new Vector<SurveyTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		String closedWhere = "";
		try {

			if (ignoreClosed) {
				closedWhere = " and (final_date is null or final_date >= ?)";
			}
			
			pstmt = c.prepareStatement("select id, name, description, is_template, is_anonymous, " +
					"project_id, creation_date, final_date, date_publishing, anonymous_key " +
					"from survey where project_id=?" + closedWhere);			
			pstmt.setString(1, pto.getId());
			if (ignoreClosed) {
				pstmt.setTimestamp(2, DateUtil.getNow());	
			}			
			rs = pstmt.executeQuery();			
			while (rs.next()) {
			    response.addElement(this.populateBeanByResultSet(rs));
			}
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		} finally {
			super.closeStatement(rs, pstmt);			
		}
		return response;
    }

    
	public SurveyTO getSurveyByKey(String key)  throws DataAccessException {
		SurveyTO response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getSurveyByKey(key, c);
		} catch(Exception e) {
			throw new DataAccessException(e);
		} finally {
			this.closeConnection(c);
		}
        return response;		
	}
	
	
    public TransferObject getObject(TransferObject to, Connection c)  throws DataAccessException {
        SurveyTO response= null;
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		
		try {
			SurveyTO sto = (SurveyTO)to;
			pstmt = c.prepareStatement("select id, name, description, is_template, is_anonymous, " +
					"project_id, creation_date, final_date, date_publishing, anonymous_key " +
					"from survey where id=?");			
			pstmt.setString(1, sto.getId());
			rs = pstmt.executeQuery();
			if (rs.next()){
			    response = this.populateBeanByResultSet(rs);
			    response.setOwner(sto.getOwner());
			    response.setQuestionList(this.getSurveyQuestion(response, c));
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }

    
    private Vector<SurveyQuestionTO> getSurveyQuestion(SurveyTO to, Connection c)  throws DataAccessException {
        Vector<SurveyQuestionTO> response = new Vector<SurveyQuestionTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		
		try {
			pstmt = c.prepareStatement("select id, type, content, position, sub_title, is_mandatory " +
					"from survey_question where survey_id=? order by position, sub_title");			
			pstmt.setString(1, to.getId());
			rs = pstmt.executeQuery();
			while (rs.next()){
				SurveyQuestionTO question = this.populateQuestionByResultSet(to, rs);
		        if (question.getQuestionType().equals(SurveyQuestionTO.QUESTION_TYPE_MULTI)) {
		        	question.setAlterativesList(this.getAlternatives(question, c));
		        }
		        if (to.getOwner()!=null) {
			        Vector<QuestionAnswerTO> answers = this.getAnswerByQuestion(question, to.getOwner(), c);
			        if (answers!=null && answers.size()>0) {
			        	question.setRelatedAnswer((QuestionAnswerTO)answers.get(0));	
			        }		        	
		        }
				response.addElement(question);
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }

    
    private Vector<QuestionAnswerTO> getAnswerByQuestion(SurveyQuestionTO question, UserTO uto, Connection c)  throws DataAccessException {
    	Vector<QuestionAnswerTO> response = new Vector<QuestionAnswerTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null;

		try {
			String sql = "select q.user_id, q.value, q.answer_date, t.username " +
						 "from question_answer q LEFT OUTER JOIN tool_user t on (q.user_id = t.id) " +
						 "where q.survey_id=? and q.question_id=? ";
			if (uto!=null) {
				sql = sql + "and q.user_id=?";
			}
			pstmt = c.prepareStatement(sql);			
			pstmt.setString(1, question.getSurvey().getId());
			pstmt.setString(2, question.getId());
			if (uto!=null) {
				pstmt.setString(3, uto.getId());	
			}
			rs = pstmt.executeQuery();
			while (rs.next()){
			    response.addElement(this.populateAnswerByResultSet(question, rs));
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);			
		}	 
		return response;
	}

    
	private SurveyTO getSurveyByKey(String key, Connection c) throws DataAccessException {
    	SurveyTO response = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		try {
			pstmt = c.prepareStatement("select id, name, description, is_template, is_anonymous, " +
					"project_id, creation_date, final_date, date_publishing, anonymous_key " +
					"from survey where anonymous_key=?");			
			pstmt.setString(1, key);
			rs = pstmt.executeQuery();			
			if (rs.next()){
			    response = this.populateBeanByResultSet(rs);
			}
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);			
		}	 
		return response;
    }

	
    private Vector<QuestionAlternativeTO> getAlternatives(SurveyQuestionTO to, Connection c)  throws DataAccessException {
        Vector<QuestionAlternativeTO> response = new Vector<QuestionAlternativeTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		
		try {
			
			pstmt = c.prepareStatement("select survey_id, question_id, sequence, content " +
					"from question_alternative where question_id=? and survey_id=?");			
			pstmt.setString(1, to.getId());
			pstmt.setString(2, to.getSurvey().getId());
			rs = pstmt.executeQuery();
			while (rs.next()){
				QuestionAlternativeTO option = this.populateAlternativeByResultSet(to, rs);
				response.addElement(option);
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);			
		}	 
		return response;
    }
    
    
    private void insertAlternatives(SurveyQuestionTO q, Connection c) throws DataAccessException{
		PreparedStatement pstmt = null;
		try {
			Vector<QuestionAlternativeTO> list = q.getAlterativesList();
			if (list!=null) {
				Iterator<QuestionAlternativeTO> i = list.iterator();
				while (i.hasNext()) {
					QuestionAlternativeTO qa = (QuestionAlternativeTO)i.next();
					
					pstmt = c.prepareStatement("insert into question_alternative (survey_id, question_id, " +
					   "sequence, content) values (?,?,?,?)");
					pstmt.setString(1, q.getSurvey().getId());
					pstmt.setString(2, q.getId());
					pstmt.setInt(3, qa.getSequence().intValue());
					pstmt.setString(4, qa.getContent());
					pstmt.executeUpdate();				
				}
			}
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);			
		}                   	
    }
    
    
    private void insertAnswer(QuestionAnswerTO to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;
		try {
			pstmt = c.prepareStatement("insert into question_answer (survey_id, question_id, answer_date, " +
									   "user_id, value) values (?,?,?,?,?)");
			pstmt.setString(1, to.getQuestion().getSurvey().getId());
			pstmt.setString(2, to.getQuestion().getId());
			pstmt.setTimestamp(3, to.getAnswerDate());
			if (to.getUser()!=null) {
				pstmt.setString(4, to.getUser().getId());			    
			} else {
			    pstmt.setNull(4, java.sql.Types.VARCHAR);
			}
			pstmt.setString(5, to.getValue());
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);			
		}               
    }
    

    private boolean checkIfThereAreAnswers(SurveyQuestionTO qto, Connection c) throws DataAccessException {
    	boolean response = false;
    	ResultSet rs = null;
		PreparedStatement pstmt = null;
		try {
			pstmt = c.prepareStatement("select count(*) as answers from question_answer " +
									   "where question_id=? and survey_id=?");
			pstmt.setString(1, qto.getId());
			pstmt.setString(2, qto.getSurvey().getId());
			rs = pstmt.executeQuery();
			if (rs.next()){
				String answers = super.getString(rs, "answers");
				response = !answers.equals("0");
			} 
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);			
		}   
		return response;
    }
    
    public void insert(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;
		try {
		    
		    SurveyTO sto = (SurveyTO)to;
		    sto.setId(this.getNewId());
		    sto.setCreationDate(DateUtil.getNow());

		    pstmt = c.prepareStatement("insert into survey (id, name, description, is_template, is_anonymous, " +
		    		"project_id, creation_date, final_date, date_publishing, anonymous_key) " +
					"values (?,?,?,?,?,?,?,?,?,?)");
			pstmt.setString(1, sto.getId());
			pstmt.setString(2, sto.getName());
			pstmt.setString(3, sto.getDescription());
			if (sto.getIsTemplate()==null) {
				pstmt.setString(4, "0");	
			} else {
				pstmt.setString(4, (sto.getIsTemplate().booleanValue()?"1":"0"));
			}
			if (sto.getIsAnonymous()==null) {
				pstmt.setString(5, "0");	
			} else {
				pstmt.setString(5, (sto.getIsAnonymous().booleanValue()?"1":"0"));
			}
			pstmt.setString(6, sto.getProject().getId());
			pstmt.setTimestamp(7, sto.getCreationDate());
			pstmt.setTimestamp(8, sto.getFinalDate());
			pstmt.setTimestamp(9, sto.getPublishingDate());
			pstmt.setString(10, sto.getAnonymousKey());
			pstmt.executeUpdate();
			
			if (sto.getQuestionList()!=null) {
				Iterator<SurveyQuestionTO> i = sto.getQuestionList().iterator();
				while(i.hasNext()) {
					SurveyQuestionTO q = i.next();
					q.setSurvey(sto);				
					this.insertQuestion(q, c);
				}				
			}
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}               
    }
    

    public void update(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;
		try {
		    
		    SurveyTO sto = (SurveyTO)to;
		    pstmt = c.prepareStatement("update survey set name=?, description=?, is_template=?, is_anonymous=?, " +
		    						   "final_date=?, date_publishing=? where id=?");
			pstmt.setString(1, sto.getName());
			pstmt.setString(2, sto.getDescription());
			if (sto.getIsTemplate()==null) {
				pstmt.setString(3, "0");	
			} else {
				pstmt.setString(3, (sto.getIsTemplate().booleanValue()?"1":"0"));
			}
			if (sto.getIsAnonymous()==null) {
				pstmt.setString(4, "0");	
			} else {
				pstmt.setString(4, (sto.getIsAnonymous().booleanValue()?"1":"0"));
			}
			pstmt.setTimestamp(5, sto.getFinalDate());
			pstmt.setTimestamp(6, sto.getPublishingDate());
			pstmt.setString(7, sto.getId());
			pstmt.executeUpdate();
			
			//update question list if necessary
			if (sto.getQuestionsToBeUpdated()!=null && sto.getQuestionList()!=null) {
				Object[] list = sto.getQuestionsToBeUpdated().toArray();
				for (int i=0 ; i<list.length; i++) {
					Iterator<SurveyQuestionTO> j = sto.getQuestionList().iterator();
					while(j.hasNext()) {
						SurveyQuestionTO sq = j.next();
						if (list[i].equals(sq.getId())) {
							this.updateQuestion(sq, c);
						}						
					}
				}							
			}

			//remove questions if necessary
			if (sto.getQuestionsToBeRemoved()!=null && sto.getQuestionList()!=null) {
				Object[] list = sto.getQuestionsToBeRemoved().toArray();
				for (int i=0 ; i<list.length; i++) {
					this.removeQuestion(sto.getId(), (String)list[i], c);
				}							
			}

			//insert new questions if necessary
			Iterator<SurveyQuestionTO>i = sto.getQuestionList().iterator();
			while(i.hasNext()) {
				SurveyQuestionTO q = i.next();
				if (q.getId().startsWith("NEW_")) {
					q.setSurvey(sto);				
					this.insertQuestion(q, c);					
				}
			}
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		} finally {
			super.closeStatement(null, pstmt);
		}               
    }
    
    
    private void removeQuestion(String surveyId, String questionId, Connection c) throws DataAccessException{
		PreparedStatement pstmt = null;
		try {
			pstmt = c.prepareStatement("delete from question_alternative where survey_id=? and question_id=?");
			pstmt.setString(1, surveyId);
			pstmt.setString(2, questionId);
			pstmt.executeUpdate();
			
			pstmt = c.prepareStatement("delete from survey_question where survey_id=? and id=?");
			pstmt.setString(1, surveyId);
			pstmt.setString(2, questionId);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);			
		}                   	
    }
    
    
    private void updateQuestion(SurveyQuestionTO sqto, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;
		try {
			pstmt = c.prepareStatement("update survey_question set content=?, position=?, sub_title=?, " +
									   "is_mandatory=? where survey_id=? and id=?");
			pstmt.setString(1, sqto.getContent());
			pstmt.setInt(2, sqto.getPosition().intValue());
			pstmt.setString(3, sqto.getSubTitle());
			pstmt.setString(4, (sqto.getIsMandatory().booleanValue()?"1":"0"));
			pstmt.setString(5, sqto.getSurvey().getId());
			pstmt.setString(6, sqto.getId());
			pstmt.executeUpdate();

			boolean anyAnswers = this.checkIfThereAreAnswers(sqto);
			if (!anyAnswers) {
				pstmt = c.prepareStatement("delete from question_alternative where survey_id=? and question_id=?");
				pstmt.setString(1, sqto.getSurvey().getId());
				pstmt.setString(2, sqto.getId());
				pstmt.executeUpdate();
				this.insertAlternatives(sqto, c);
			}
			
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);			
		}               
    }

    
    private void insertQuestion(SurveyQuestionTO q, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;
		try {
			if (q.getId().startsWith("NEW_")) {
			    q.setId(this.getNewId());
			}
			
			pstmt = c.prepareStatement("insert into survey_question (survey_id, id, type, " +
									   "content, position, sub_title, is_mandatory) values (?,?,?,?,?,?,?)");
			pstmt.setString(1, q.getSurvey().getId());
			pstmt.setString(2, q.getId());
			pstmt.setString(3, q.getQuestionType());
			pstmt.setString(4, q.getContent());
			pstmt.setInt(5, q.getPosition().intValue());
			pstmt.setString(6, q.getSubTitle());
			pstmt.setString(7, (q.getIsMandatory().booleanValue()?"1":"0"));
			pstmt.executeUpdate();
			
	        if (q.getQuestionType().equals(SurveyQuestionTO.QUESTION_TYPE_MULTI)) {
	        	this.insertAlternatives(q, c);
	        }
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);			
		}               
    }
    
    public void remove(TransferObject to, Connection c)  throws DataAccessException {
		PreparedStatement pstmt = null;
		try {
			SurveyTO sto = (SurveyTO)to;

			pstmt = c.prepareStatement("delete from question_alternative where survey_id=?");
			pstmt.setString(1, sto.getId());
			pstmt.executeUpdate();

			pstmt = c.prepareStatement("delete from survey_question where survey_id=?");
			pstmt.setString(1, sto.getId());
			pstmt.executeUpdate();
			
			pstmt = c.prepareStatement("delete from survey where id = ?");
			pstmt.setString(1, sto.getId());			
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}        
    }
    

    
    /**
     * Create a new TO object based on data into result set.
     */
    protected SurveyTO populateBeanByResultSet(ResultSet rs) throws DataAccessException{
    	SurveyTO response = new SurveyTO();
        response.setId(getString(rs, "id"));
        response.setName(getString(rs, "name"));
        response.setDescription(getString(rs, "description"));
        response.setIsTemplate(new Boolean(getString(rs, "is_template").equals("1")));
        response.setIsAnonymous(new Boolean(getString(rs, "is_anonymous").equals("1")));
        response.setCreationDate(getTimestamp(rs, "creation_date"));
        response.setFinalDate(getTimestamp(rs, "final_date"));
        response.setPublishingDate(getTimestamp(rs, "date_publishing"));
        response.setAnonymousKey(getString(rs, "anonymous_key"));
        response.setProject(new ProjectTO(getString(rs, "project_id")));
        return response;
    }

    
    protected SurveyQuestionTO populateQuestionByResultSet(SurveyTO sto, ResultSet rs) throws DataAccessException{
    	SurveyQuestionTO response = new SurveyQuestionTO();
        response.setId(getString(rs, "id"));
        response.setContent(getString(rs, "content"));
        response.setSubTitle(getString(rs, "sub_title"));
        response.setPosition(getInteger(rs, "position"));
        response.setQuestionType(getString(rs, "type"));
        
        String isMandatory = getString(rs, "is_mandatory");
        if (isMandatory!=null) {
        	response.setIsMandatory(new Boolean(isMandatory.equals("1")));	
        } else {
        	response.setIsMandatory(new Boolean(false));
        }
        
        response.setSurvey(sto);        
        return response;
    }

    
    protected QuestionAlternativeTO populateAlternativeByResultSet(SurveyQuestionTO sqto, ResultSet rs) throws DataAccessException{
    	QuestionAlternativeTO response = new QuestionAlternativeTO();
        response.setQuestion(sqto);
        response.setSequence(getInteger(rs, "sequence"));
        response.setContent(getString(rs, "content"));
        return response;
    }

    protected QuestionAnswerTO populateAnswerByResultSet(SurveyQuestionTO question, ResultSet rs) throws DataAccessException{
    	QuestionAnswerTO response = new QuestionAnswerTO();
    	response.setQuestion(question);
        response.setAnswerDate(getTimestamp(rs, "answer_date"));
        String user = getString(rs, "user_id");
        if (user!=null && !user.trim().equals("")) {
        	UserTO u = new UserTO(user);
        	u.setUsername(getString(rs, "username"));
        	response.setUser(u);	
        }
        response.setValue(getString(rs, "value"));
        return response;
    }


}
