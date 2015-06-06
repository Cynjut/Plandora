package com.pandora.gui.struts.action.projectpanel;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collections;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import com.pandora.OccurrenceFieldTO;
import com.pandora.OccurrenceTO;
import com.pandora.ProjectTO;
import com.pandora.UserTO;
import com.pandora.bus.occurrence.EventOccurrence;
import com.pandora.bus.occurrence.IterationOccurrence;
import com.pandora.bus.occurrence.MilestoneOccurrence;
import com.pandora.delegate.OccurrenceDelegate;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.gui.struts.action.ProjectPanelAction;
import com.pandora.gui.struts.form.ProjectPanelForm;
import com.pandora.helper.DateUtil;
import com.pandora.helper.SessionUtil;

public class MilestonesAndInterationProjectPanel extends ProjectPanelAction {
	
	private static final int SLOT_WIDTH = 70;
	
	@Override
	public String getPanelId() {
		return "MILE_ITER_INFO";
	}

	@Override
	public String getPanelTitle() {
		return "title.projectPanelForm.mileiter";
	}
	
	@Override
	public String renderPanel(HttpServletRequest request,
			ProjectPanelForm frm) throws BusinessException {
		StringBuffer sb = new StringBuffer("");
		ProjectDelegate pdel = new ProjectDelegate();
		OccurrenceDelegate odel = new OccurrenceDelegate();

		if (frm.getProjectId()!=null && !frm.getProjectId().trim().equals("")) {
			ProjectTO pto = new ProjectTO(frm.getProjectId());
			pto = pdel.getProjectObject(pto, true);
			UserTO uto = SessionUtil.getCurrentUser(request);
			
			Vector<OccurrenceTO> milestones = odel.getOccurenceListByType(pto.getId(), MilestoneOccurrence.class.getName(), true);
			Vector<OccurrenceTO> iterations = odel.getOccurenceListByType(pto.getId(), IterationOccurrence.class.getName(), true);
			Vector<OccurrenceTO> events = odel.getOccurenceListByType(pto.getId(), EventOccurrence.class.getName(), true);
			Vector<OccurrenceTO> orderedData = this.processData(milestones, iterations, events, pto, uto);

			sb.append("<table width=\"985px\" border=\"0\" cellspacing=\"2\" cellpadding=\"0\">\n");
			if (orderedData!=null) {
				sb.append(this.getFilterComboBox(request, this, uto, 4));
				String ganttBody = this.getGanttBody(pto, uto, orderedData);
				
				sb.append("<tr  height=\"15px\" class=\"pagingFormBody\"><td class=\"formTitle\" width=\"300\">&nbsp;</td><td class=\"formTitle\" width=\"80\">&nbsp;</td>" +
							"<td class=\"formTitle\" width=\"80\">&nbsp;</td><td rowspan=\"" + (orderedData.size()+2) + "\" class=\"formBody\"><div style=\"width:500px; overflow-x: scroll;\">" + 
							ganttBody + "</div></td></tr>\n");
				
				for (OccurrenceTO oto : orderedData) {
					Timestamp dtRef = DateUtil.getDateTime(oto.getGenericTag(), "yyyyMMdd", uto.getLocale());

					String finalDt = "-"; 
					if (oto.getSource()!=null && oto.getSource().equals(IterationOccurrence.class.getName())) {
						OccurrenceFieldTO fdt = oto.getField(IterationOccurrence.ITERATION_FINAL_DATE);
						if (fdt!=null && fdt.getDateValue()!=null) {
							finalDt = DateUtil.getDate(fdt.getDateValue(), uto.getCalendarMask(), uto.getLocale());
						}
					}

					sb.append("<tr class=\"pagingFormBody\" height=\"20px\"><td style=\"border: solid 1px #c0c0c0;\" class=\"formTitle\">" + oto.getName() + 
							  "&nbsp;&nbsp;</td><td style=\"border: solid 1px #c0c0c0;\" class=\"formTitle\"><center>" + DateUtil.getDateTime(dtRef, uto.getCalendarMask(), uto.getLocale()) +
							  "</center></td><td style=\"border: solid 1px #c0c0c0;\" class=\"formTitle\"><center>" + finalDt + "</center></td></tr>\n");					
				}
				
				sb.append("<tr class=\"pagingFormBody\"><td class=\"formTitle\" colspan=\"3\">&nbsp;</td></tr>\n");
				
			}
			sb.append("</table>\n");  	
		}
		
		return sb.toString();

	}

	private Vector<OccurrenceTO> processData(Vector<OccurrenceTO> milestones, Vector<OccurrenceTO> iterations, Vector<OccurrenceTO> events, ProjectTO pto, UserTO uto) throws BusinessException {
		OccurrenceDelegate odel = new OccurrenceDelegate();
		Vector<OccurrenceTO> response = new Vector<OccurrenceTO>();
		
		Vector<OccurrenceTO> allObjects = new Vector<OccurrenceTO>();
		allObjects.addAll(milestones);
		allObjects.addAll(iterations);
		allObjects.addAll(events);
		
		OccurrenceTO startProj = new OccurrenceTO();
		startProj.setCreationDate(pto.getCreationDate());
		startProj.setName(uto.getBundle().getMessage("label.projectPanelForm.projStart", uto.getLocale()));
		allObjects.add(startProj);

		OccurrenceTO endProj = new OccurrenceTO();
		endProj.setCreationDate(this.getEstimatedProjectEnding(pto));
		endProj.setName(uto.getBundle().getMessage("label.projEstimClosure", uto.getLocale()));
		allObjects.add(endProj);
		
		for (OccurrenceTO oto : allObjects) {
			OccurrenceTO dbOcc = odel.getOccurrenceObject(oto);
			if (dbOcc!=null) {
				oto = dbOcc;	
			}
			Timestamp refDate = this.getRefDate(oto);
			if (refDate!=null) {
				oto.setGenericTag(DateUtil.getDateTime(refDate, "yyyyMMdd"));
				response.add(oto);
			}
		}

		Collections.sort(response);
		
		return response;
	}

	private Timestamp getRefDate(OccurrenceTO oto) {
		OccurrenceFieldTO refDate = null;
		Timestamp response = null;
		
		if (oto!=null && oto.getSource()!=null) {
			if (oto.getSource().equals(MilestoneOccurrence.class.getName())) {
				refDate = oto.getField(MilestoneOccurrence.MILE_DATE);
			} else if (oto.getSource().equals(IterationOccurrence.class.getName())) {
				refDate = oto.getField(IterationOccurrence.ITERATION_INI_DATE);
			} else if (oto.getSource().equals(EventOccurrence.class.getName())) {
				refDate = oto.getField(EventOccurrence.EVENT_DATE);
			}			
		}
		
		if (refDate!=null && refDate.getDateValue()!=null) {
			response = refDate.getDateValue();
		} else {
			response = oto.getCreationDate();
		}

		return response;
	}

	private String getGanttBody(ProjectTO pto, UserTO uto, Vector<OccurrenceTO> orderedData) {
		String response = "";
		int slots = 0;
		
		Timestamp estimatedEnd = getEstimatedProjectEnding(pto);
		int diff = DateUtil.getSlotBetweenDates(pto.getCreationDate(), estimatedEnd);
		
		String pattern = uto.getCalendarMask();
		int increment = Calendar.WEEK_OF_YEAR;		
		if (diff>30) {
			pattern = "MMM-yyyy";
			increment = Calendar.MONTH;
		}
		
		if (diff>0) {
			response = response + "<tr class=\"pagingFormBody\">";
			Timestamp cursor = pto.getCreationDate();
			while(cursor.before(estimatedEnd)) {
				
				int width = SLOT_WIDTH;
				String label = DateUtil.getDate(cursor, pattern, uto.getLocale());
				Timestamp nextCursor = DateUtil.getChangedDate(cursor, increment, 1);
				
				if (slots==0 && DateUtil.get(cursor, Calendar.DATE)!=1 && increment==Calendar.MONTH) {
					nextCursor = DateUtil.getDate("01", (DateUtil.get(nextCursor, Calendar.MONTH)+1)+"", DateUtil.get(nextCursor, Calendar.YEAR)+"");
					float fillerPerc = (float)((float)DateUtil.getSlotBetweenDates(cursor, nextCursor) / 30f);
					width = (int) (SLOT_WIDTH * fillerPerc);
					if (fillerPerc<.75) {
						label = "";
					}
				}
				
				response = response + "<td height=\"15px\" width=\"" + width + "px\" class=\"formTitle\"><center>" + label + "</center></td>";
				cursor = nextCursor;
				slots++;
			}
			response = response + "</tr>";
			response = "<table width=\"" + (SLOT_WIDTH * slots) + "px\" border=\"0\" cellspacing=\"1\" cellpadding=\"0\">\n" + response;
			
			for (OccurrenceTO oto : orderedData) {
				String body = "&nbsp;";
				Timestamp refDate = this.getRefDate(oto);
				if (refDate!=null) {
					int positionDays = DateUtil.getSlotBetweenDates(pto.getCreationDate(), refDate);
					float perc = (float)(((float)positionDays /(float)diff) * 100);
					float perc2 = 0;
					if (oto!=null && oto.getSource()!=null) {
						if (oto.getSource().equals(IterationOccurrence.class.getName())) {
							Timestamp otherDate = null;							
							OccurrenceFieldTO ofto = oto.getField(IterationOccurrence.ITERATION_FINAL_DATE);
							if (ofto!=null)  {
								otherDate = ofto.getDateValue();
								if (otherDate.after(refDate))  {
									int otherPosDays = DateUtil.getSlotBetweenDates(refDate, otherDate);
									perc2 = (float)(((float)otherPosDays /(float)diff) * 100);									
								}
							}
						}
					}
					
					body = "<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr><td width=\"" + (new Float(perc)).intValue() + "%\">&nbsp;</td>";
					if (perc2>0) {
						body = body + "<td width=\"" + (new Float(perc2)).intValue() + "%\"><img src=\"../images/fill.gif\" height=\"14px\" width=\"100%\" /></td>";	
					} else {
						body = body + "<td><img src=\"../images/flow-decision.gif\" height=\"14px\"/></td>";
					}
								
					body = body + "<td width=\"" + (100 - (new Float(perc2).intValue()) + (new Float(perc).intValue())) + "%\">&nbsp;</td></tr></table>";			
					
				}
				response = response + "<tr class=\"pagingFormBody\"><td colspan=\"" + slots + 
						"\" style=\"border: solid 1px #c0c0c0;\" class=\"formTitle\"><center>" + body + 
						"</center></td></tr>";
			}
			
		}		
		response = response + "</tr></table>";

		return response;
	}

	private Timestamp getEstimatedProjectEnding(ProjectTO pto) {
		Timestamp estimatedEnd;
		if (pto.getEstimatedClosureDate()!=null) {
			if (pto.getEstimatedClosureDate().after(DateUtil.getNow())) {
				estimatedEnd = pto.getEstimatedClosureDate();	
			} else {
				estimatedEnd = DateUtil.getNow();	
			}
		} else {
			estimatedEnd = DateUtil.getNow();
		}
		return estimatedEnd;
	}

	/*
	@Override
	public Vector<FieldValueTO> getPanelFields(HttpServletRequest request){
		Vector<FieldValueTO> list = new Vector<FieldValueTO>();
		
		System.out.println("parametro_2: " + (request.getParameter(this.getPanelId() + "_" + "CAMPO_2" )) + " parametro_1:" + (request.getParameter(this.getPanelId() + "_" + "CAMPO_1" )));
		
		String fval1 = request.getParameter(this.getPanelId() + "_" + "CAMPO_1" );
		Vector<TransferObject> d = new Vector<TransferObject>();
		d.add(new TransferObject("1", "BANANA"));
		d.add(new TransferObject("2", "MAÇA"));
		d.add(new TransferObject("3", "LARANJA"));
		FieldValueTO fv = new FieldValueTO("CAMPO_1", "", d);
		fv.setCurrentValue(fval1);
		list.add(fv);

		String fval2 = request.getParameter(this.getPanelId() + "_" + "CAMPO_2" );
		Vector<TransferObject> d2 = new Vector<TransferObject>();
		d2.add(new TransferObject("H", "Huguinho"));
		d2.add(new TransferObject("L", "Luizinho"));
		d2.add(new TransferObject("J", "Joaozinho"));
		FieldValueTO fv2 = new FieldValueTO("CAMPO_2", "", d2);
		fv2.setCurrentValue(fval2);
		list.add(fv2);
		
		return list;
	}	
	*/

}
