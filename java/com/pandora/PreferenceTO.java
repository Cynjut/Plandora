package com.pandora;

import java.util.HashMap;

/**
 * This object it is a bean that represents a Preference entity. <br>
 * 
 * <i>Developer note</i>: To create a new preference, is necessary to create a new constant
 * to store the preference id and a new default value into getDefaultValue method. After
 * that, use the new preference accessing the id through the static attribute and change the
 * option form if necessary.
 */
public class PreferenceTO extends TransferObject {

	private static final long serialVersionUID = 1L;

    public static final String HOME_TASKLIST_NUMLINE        = "home.taskList.numLine";
    public static final String HOME_PENDLIST_NUMLINE        = "home.pendList.numLine";
    public static final String HOME_REQULIST_NUMLINE        = "home.requList.numLine";
    public static final String HOME_REQULIST_PRIORITY_COLOR = "home.requList.showPriorityColor";
    public static final String HOME_PROJLIST_NUMLINE        = "home.projList.numLine";
    public static final String HOME_TASKLIST_DAYSTOHIDE     = "home.taskList.dayToHide";
    public static final String HOME_TASKLIST_ORDER          = "home.taskList.order";
    
    public static final String LIST_ALL_SHOW_PAGING       = "listAll.showPagingMode";    
    public static final String LIST_ALL_REQ_SW_CR_DATE    = "listAll.myReq.creationDate.show";
    public static final String LIST_ALL_REQ_SW_DESC       = "listAll.myReq.description.show";
    public static final String LIST_ALL_REQ_SW_NAME       = "listAll.myReq.name.show";
    public static final String LIST_ALL_REQ_SW_CATEGORY   = "listAll.myReq.category.show";
    public static final String LIST_ALL_REQ_SW_STATUS     = "listAll.myReq.status.show";
    public static final String LIST_ALL_REQ_SW_REQUESTER  = "listAll.myReq.requester.show";
    public static final String LIST_ALL_REQ_SW_SUG_DATE   = "listAll.myReq.suggDate.show";
    public static final String LIST_ALL_REQ_SW_DEADL_DATE = "listAll.myReq.deadlineDate.show";
    public static final String LIST_ALL_REQ_SW_PRIORITY   = "listAll.myReq.priority.show";
    public static final String LIST_ALL_REQ_SW_ITERATION  = "listAll.myReq.iteration.show";
    public static final String LIST_ALL_REQ_SW_REL_RES    = "listAll.myReq.relatedRes.show";
    public static final String LIST_ALL_REQ_SW_PRG_TSK    = "listAll.myReq.taskprogress.show";
    public static final String LIST_ALL_REQ_SW_META_FIELD = "listAll.myReq.metaField.show";
    public static final String LIST_ALL_REQ_SW_PARENT_REQ = "listAll.myReq.parentReq.show";
    
    public static final String AGILE_PANEL_TODO            = "listAll.agilePanel.todo.show";
    public static final String AGILE_PANEL_PROG            = "listAll.agilePanel.prog.show";
    public static final String AGILE_PANEL_DONE            = "listAll.agilePanel.done.show";
    public static final String AGILE_PANEL_RANGE1          = "listAll.agilePanel.range1.show";
    public static final String AGILE_PANEL_RANGE2          = "listAll.agilePanel.range2.show";
    public static final String AGILE_PANEL_RANGE3          = "listAll.agilePanel.range3.show";
    public static final String AGILE_PANEL_ASSIG1          = "listAll.agilePanel.assig1.show";
    public static final String AGILE_PANEL_ASSIG2          = "listAll.agilePanel.assig2.show";

    public static final String RES_CAP_MAXLIMIT            = "listAll.rescap.maxlimit.status";
    public static final String RES_CAP_UNIT                = "listAll.rescap.unit.status";
    public static final String RES_CAP_VIEWMODE            = "listAll.rescap.viewmode.status";
    public static final String RES_CAP_GRANULARITY         = "listAll.rescap.granularity.status";
    public static final String RES_CAP_HIDE_DSBLE_USERS    = "listAll.rescap.hidedisable.status";

    public static final String COST_FILTER_SHOW_CHART      = "listAll.costPanel.flt.chart.show";
    public static final String COST_FILTER_GRANULARY       = "listAll.costPanel.flt.granularity";
    public static final String COST_FILTER_TYPE            = "listAll.costPanel.flt.type";
    public static final String COST_FILTER_VIEWMODE        = "listAll.costPanel.flt.viewmode";
    public static final String COST_FILTER_HIDE_OUT_RANGE  = "listAll.costPanel.flt.hideoutrange";

    public static final String AGILE_FILTER_ITERATION      = "listAll.agilePanel.flt.iter.status";
    public static final String AGILE_FILTER_GROUPING       = "listAll.agilePanel.flt.grop.status";    
    public static final String AGILE_FILTER_HIDE_REQ       = "listAll.agilePanel.flt.hide1.status";
    public static final String AGILE_FILTER_HIDE_TSK       = "listAll.agilePanel.flt.hide2.status";
    public static final String AGILE_FILTER_HIDE_TSKREQ    = "listAll.agilePanel.flt.hide3.status";
    public static final String AGILE_FILTER_SHOW_CHART     = "listAll.agilePanel.flt.show1.status";
    public static final String AGILE_FILTER_HIDE_OLD_ITER  = "listAll.agilePanel.flt.hide4.status";
    
    public static final String LIST_ALL_TSK_PROJECT        = "listAll.myTask.project.show";
    public static final String LIST_ALL_TSK_RESOURCE       = "listAll.myTask.resource.show";
    public static final String LIST_ALL_TSK_EST_DATE       = "listAll.myTask.estimdate.show";
    public static final String LIST_ALL_TSK_EST_TIME       = "listAll.myTask.estimtime.show";
    public static final String LIST_ALL_TSK_ACT_DATE       = "listAll.myTask.actualdate.show";
    public static final String LIST_ALL_TSK_ACT_TIME       = "listAll.myTask.actualtime.show";
    public static final String LIST_ALL_TSK_STATUS         = "listAll.myTask.status.show";
    public static final String LIST_ALL_TSK_SW_META_FIELD  = "listAll.myTask.metaField.show";
    public static final String LIST_ALL_TSK_BILLABLE       = "listAll.myTask.billable.show";
    
    public static final String HOME_TASKLIST_SW_CLASSIF    = "home.taskList.classif.show";
    public static final String HOME_TASKLIST_SW_PROJECT    = "home.taskList.project.show";
    public static final String HOME_TASKLIST_SW_ACT_DATE   = "home.taskList.actualdate.show";
    public static final String HOME_TASKLIST_SW_ACT_TIME   = "home.taskList.actualtime.show";
    public static final String HOME_TASKLIST_SW_EST_DATE   = "home.taskList.estimdate.show";
    public static final String HOME_TASKLIST_SW_EST_TIME   = "home.taskList.estimtime.show";
    public static final String HOME_TASKLIST_SW_TSK_STATUS = "home.taskList.taskstatus.show";
    public static final String HOME_TASKLIST_SW_TSK_CATEG  = "home.taskList.taskcateg.show";
    public static final String HOME_TASKLIST_SW_REL_REQ    = "home.taskList.relatereq.show";
    public static final String HOME_TASKLIST_SW_TSK_ITERAT = "home.taskList.iteration.show";
    public static final String HOME_TASKLIST_SW_REQ_ITERAT = "home.taskList.iteration.req.show";
    public static final String HOME_TASKLIST_SW_TSK_PARENT = "home.taskList.parent.show";
    public static final String HOME_TASKLIST_SW_BLOCKERS   = "home.taskList.blockers.show";
    public static final String HOME_TASKLIST_SW_BILLABLE   = "home.taskList.billable.show";

    public static final String HOME_PENDCOST_SW_PROJ  = "home.pendcost.project.show";
    public static final String HOME_PENDCOST_SW_CREAT = "home.pendcost.creatdate.show";
    public static final String HOME_PENDCOST_SW_USER  = "home.pendcost.username.show";
    public static final String HOME_PENDCOST_SW_NAME  = "home.pendcost.name.show";
    
    public static final String HOME_PENDLIST_SW_DESC  = "home.pendList.desc.show";
    public static final String HOME_PENDLIST_SW_PROJ  = "home.pendList.project.show";
    public static final String HOME_PENDLIST_SW_CATEG = "home.pendList.category.show";
    public static final String HOME_PENDLIST_SW_SUGG  = "home.pendList.suggdate.show";
    public static final String HOME_PENDLIST_SW_CREAT = "home.pendList.creatdate.show";
    public static final String HOME_PENDLIST_SW_REQUE = "home.pendList.requester.show";
    public static final String HOME_PENDLIST_SW_PRIOR = "home.pendList.priority.show";

    public static final String HOME_REQLIST_SW_DESC   = "home.reqList.desc.show";
    public static final String HOME_REQLIST_SW_PROJ   = "home.reqList.project.show";
    public static final String HOME_REQLIST_SW_CATEG  = "home.reqList.category.show";
    public static final String HOME_REQLIST_SW_PRIOR  = "home.reqList.priority.show";
    public static final String HOME_REQLIST_SW_ITERA  = "home.reqList.iteration.show";
    public static final String HOME_REQLIST_SW_STAT   = "home.reqList.status.show";

    public static final String INV_INVLIST_SW_NAME    = "invoice.invList.name.show";
    public static final String INV_INVLIST_SW_NUMBER  = "invoice.invList.number.show";
    public static final String INV_INVLIST_SW_PO      = "invoice.invList.po.show";
    public static final String INV_INVLIST_SW_PRJ     = "invoice.invList.project.show";
    public static final String INV_INVLIST_SW_CATEG   = "invoice.invList.category.show";
    public static final String INV_INVLIST_SW_DUEDT   = "invoice.invList.duedate.show";
    public static final String INV_INVLIST_SW_INVDT   = "invoice.invList.invoicedate.show";
    public static final String INV_INVLIST_SW_CONTACT = "invoice.invList.contact.show";
    public static final String INV_INVLIST_SW_TOTAL   = "invoice.invList.total.show";
    
    public static final String HOME_PROJLIST_SW_CREAT = "home.projList.creatdate.show";
    public static final String HOME_PROJLIST_SW_LIFE  = "home.projList.lifetime.show";
    public static final String HOME_PROJLIST_SW_ID    = "home.projList.id.show";
    public static final String HOME_PROJLIST_SW_STAT  = "home.projList.status.show";
    public static final String HOME_PROJLIST_SW_ROLE  = "home.projList.role.show";
    
    public static final String HOME_FORUMLIST_SW_CREAT  = "home.forumList.creatdate.show";
    public static final String HOME_FORUMLIST_SW_LSTUPD = "home.forumList.lastupd.show";
    public static final String HOME_FORUMLIST_SW_REPLYN = "home.forumList.replynum.show";
    
    public static final String RSK_SW_PROJECT     = "risk.project.show";
    public static final String RSK_SW_CATEGORY    = "risk.category.show";
    public static final String RSK_SW_RESPONSIBLE = "risk.responsible.show";
    public static final String RSK_SW_STATUS      = "risk.status.show";

    public static final String RSK_SW_PROB      = "risk.prob.show";
    public static final String RSK_SW_IMPACT    = "risk.impact.show";
    public static final String RSK_SW_TENDENCY  = "risk.tendency.show";
    public static final String RSK_SW_IMP_COST  = "risk.impact.cost.show";
    public static final String RSK_SW_IMP_QUAL  = "risk.impact.qual.show";
    public static final String RSK_SW_IMP_SCOP  = "risk.impact.scop.show";
    public static final String RSK_SW_IMP_TIME  = "risk.impact.time.show";
    public static final String RSK_SW_TYPE      = "risk.type.show";

    public static final String OCC_SW_PROJECT  = "occurrence.project.show";
    public static final String OCC_SW_STATUS   = "occurrence.status.show";
    public static final String OCC_SW_SOURCE   = "occurrence.source.show";
    public static final String OCC_HIDE_CLOSED = "occurrence.hideClosed";
    
    public static final String LIST_NUMWORDS      = "list.numWords";
    public static final String MY_REQU_DAYS_AGO   = "list.myRequirement.maxDaysAgo";
    public static final String MY_TASK_DAYS_AGO   = "list.myTasks.maxDaysAgo";    
    public static final String CRITICAL_DAY_TASK  = "list.task.delay.critical";
    public static final String WARNING_DAY_TASK   = "list.task.delay.warning";
    public static final String HIDE_PROJECT       = "list.task.hideproject";
    
    public static final String CUSTOM_FORM_HIDE_DAYS = "list.customform.hide.days";
    
    public static final String SHORTCUT_NAME      = "list.task.shortcut.title.";
    public static final String SHORTCUT_URL       = "list.task.shortcut.url.";
    public static final String SHORTCUT_ICON      = "list.task.shortcut.icon.";
    
    public static final String LDAP_FILTER       = "authentication.ldap.filter";
    public static final String LDAP_HOST         = "authentication.ldap.host";
    public static final String LDAP_PORT         = "authentication.ldap.port";
    public static final String LDAP_SEARCH_BASE  = "authentication.ldap.searchBase";
    public static final String LDAP_SEARCH_PASS  = "authentication.ldap.searchPass";
    public static final String LDAP_UID_REGISTER = "authentication.ldap.uidRegister";
    public static final String LDAP_UID_SEARCH   = "authentication.ldap.uidSearch";
        
    public static final String KB_CURSOR_PREFIX = "kb.cursor.";
    public static final String KB_MAX_PREFIX    = "kb.max.";
    public static final String KB_BUS_CLASS     = "kb.bus.class";
    public static final String KB_INDEX_FOLDER  = "kb.index.folder";

    public static final String SNIP_ARTIFACT_BUS_CLASS = "snip.artifact.bus.class";
    
    public static final String GADGET_BUS_CLASS   = "gadget.bus.class";
    public static final String GADGET_WIDTH       = "gadget.width";

    public static final String AUTH_BUS_CLASS        = "authentication.bus.class";
    public static final String ARTIFACT_EXPORT_CLASS = "artifact.bus.class";
    
    public static final String REPOSITORY_BUS_CLASS     = "repository.bus.class";
    public static final String REPOSITORY_GRID_REVISION = "repository.grid.revision.show";
    public static final String REPOSITORY_GRID_AUTHOR   = "repository.grid.author.show";
    public static final String REPOSITORY_GRID_SIZE     = "repository.grid.size.show";
    public static final String REPOSITORY_GRID_VIEWER   = "repository.grid.viewer.show";
    public static final String REPOSITORY_GRID_LOCK     = "repository.grid.lock.show";
    public static final String REPOSITORY_GRID_DATE     = "repository.grid.date.show";
    public static final String REPOSITORY_GRID_FULLPATH = "repository.grid.fullpath.show";
    public static final String REPOSITORY_GRID_GRANT    = "repository.grid.grant.show";
    public static final String REPOSITORY_GRID_LOG      = "repository.grid.log.show";
    
    public static final String GENERAL_SKIN           = "general.skin";
    public static final String GENERAL_CURRENCY       = "general.currency";
    
    public static final String CUSTHOME_DEF_PROJ      = "custHome.defaultrProject";
    public static final String NOTIFICATION_BUS_CLASS = "notification.bus.class";
    public static final String CALEND_SYNC_BUS_CLASS  = "calendar.sync.bus.class";
    public static final String CONVERTER_BUS_CLASS    = "converter.bus.class";
    public static final String OCCURRENCE_BUS_CLASS   = "occurrence.bus.class";
    public static final String IMP_EXP_BUS_CLASS      = "imp-exp.bus.class";
    public static final String UPLOAD_MAX_SIZE        = "upload.maxsize";
    public static final String ARTIFACT_MAX_SIZE      = "artifact.maxsize";
    public static final String NEW_VERSION_URL        = "newversion.url";
 
    public static final String PIN_TASK_LIST          = "task.pin.list";
    public static final String INPUT_TASK_FORMAT      = "task.input.format";
    public static final String TASK_REPORT_URL        = "task.report.url";
    public static final String SURVEY_REPORT_URL      = "survey.report.url";
    public static final String EXPENSE_REPORT_URL     = "expense.report.url";
    
    public static final String PANEL_HIDDEN           = "home.panel.hidden";
    
    public static final String KPI_SHOW_ONLY_CURPROJ  = "kpi.show.onlyCurrentProject";
    public static final String KPI_SHOW_ONLY_OPENED   = "kpi.show.onlyOpened";
    
    
    /** List of preferences of current user */
    private HashMap<String, PreferenceTO> preferences;
    
    /** User related with the preference */
    private UserTO user;
    
    /** The Value of preference */
    private String value;
    
    
    /**
     * Constructor 
     */
    public PreferenceTO(){
    }

    /**
     * Constructor 
     */    
    public PreferenceTO(String id, String value, UserTO uto){
        this.setId(id);
        this.setValue(value);
        this.setUser(uto);
    }
    
    
    
    /**
     *  Find a preference value based on a preference key
     * @param key
     * @return
     */
    public String getPreference(String key){
        String response = "";
        
        if (this.preferences!=null){
            PreferenceTO to = (PreferenceTO)this.preferences.get(key);
            if (to==null){
                response = this.getDefaultValue(key);
            } else {
                response = to.getValue();    
            }
        } else {
            response = this.getDefaultValue(key);
        }
        
        return response;
    }
    
    /**
     * Get a default value for a specific preferente (based on preference key)
     * @param key
     * @return
     */
    private String getDefaultValue(String key){
        String response = "";
        
        if (key.equals(GENERAL_SKIN)){
            response = "styleDefault.css";
        } if (key.equals(GENERAL_CURRENCY)){            
        	response = "en_US";
        } else if (key.equals(HOME_TASKLIST_NUMLINE)){
            response = "6";                
        } else if (key.equals(HOME_PENDLIST_NUMLINE)){
            response = "6";                
        } else if (key.equals(HOME_REQULIST_NUMLINE)){
            response = "4";
        } else if (key.equals(HOME_REQULIST_PRIORITY_COLOR)){
        	response = "FALSE";
        } else if (key.equals(HOME_PROJLIST_NUMLINE)){
            response = "3";                
        } else if (key.equals(HOME_PROJLIST_SW_ID)) {	
        	response = "off";            
        } else if (key.equals(HOME_TASKLIST_DAYSTOHIDE)){
            response = "7";
        } else if (key.equals(LIST_ALL_SHOW_PAGING)){
            response = "0";
        } else if (key.equals(CUSTHOME_DEF_PROJ)){
            response = "0";
        } else if (key.startsWith(HOME_TASKLIST_ORDER)){
            response = "0";
        } else if (key.startsWith(OCC_HIDE_CLOSED)){            
        	response = "off";
        } else if (key.equals(LIST_NUMWORDS)) {
            response = "20";
        } else if (key.equals(MY_REQU_DAYS_AGO)) {
            response = "90";
        } else if (key.equals(MY_TASK_DAYS_AGO)) {
            response = "90";
        } else if (key.equals(AGILE_PANEL_TODO)) {
        	response = "on";
        } else if (key.equals(AGILE_PANEL_PROG)) {
        	response = "on";
        } else if (key.equals(AGILE_PANEL_DONE)) {
        	response = "on";        	
        } else if (key.equals(AGILE_PANEL_RANGE1)) {
        	response = "on";
        } else if (key.equals(AGILE_PANEL_RANGE2)) {
        	response = "on";
        } else if (key.equals(AGILE_PANEL_RANGE3)) {
        	response = "on";    
        } else if (key.equals(AGILE_PANEL_ASSIG1)) {
        	response = "on";    
        } else if (key.equals(AGILE_PANEL_ASSIG2)) {
        	response = "on";   
        } else if (key.equals(RES_CAP_MAXLIMIT)) {
        	response = "480";
        } else if (key.equals(RES_CAP_UNIT)) {
        	response = "1";
        } else if (key.equals(RES_CAP_VIEWMODE)) {
        	response = "ALL";
        } else if (key.equals(RES_CAP_GRANULARITY)) {
        	response = "14";
        } else if (key.equals(RES_CAP_HIDE_DSBLE_USERS)) {        	
        	response = "on";
        } else if (key.equals(COST_FILTER_GRANULARY)) {        	
        	response = "3";
        } else if (key.equals(COST_FILTER_TYPE)) {        	
        	response = "PRJ";
        } else if (key.equals(COST_FILTER_VIEWMODE)) {        	
        	response = "ALL";
        } else if (key.equals(COST_FILTER_HIDE_OUT_RANGE)) {        	
        	response = "off";
        } else if (key.equals(AGILE_FILTER_ITERATION)) {
        	response = "-1";    
        } else if (key.equals(AGILE_FILTER_GROUPING)) {
        	response = "1";    
        } else if (key.equals(AGILE_FILTER_HIDE_REQ)) {
        	response = "off";    
        } else if (key.equals(AGILE_FILTER_HIDE_TSK)) {
        	response = "on";
        } else if (key.equals(AGILE_FILTER_HIDE_TSKREQ)) {
        	response = "on";
        } else if (key.equals(AGILE_FILTER_HIDE_OLD_ITER)) {
        	response = "on";
        } else if (key.equals(AGILE_FILTER_SHOW_CHART)) {
        	response = "off";
        } else if (key.equals(LIST_ALL_REQ_SW_SUG_DATE)) {
            response = "off";
        } else if (key.equals(LIST_ALL_REQ_SW_DEADL_DATE)) {
            response = "off";
        } else if (key.equals(LIST_ALL_REQ_SW_PRIORITY)) {
        	response = "off";
        } else if (key.equals(LIST_ALL_REQ_SW_PARENT_REQ)) {
        	response = "off";
        } else if (key.equals(LIST_ALL_REQ_SW_ITERATION)) {
        	response = "off";        	
        } else if (key.equals(LIST_ALL_REQ_SW_DESC)) {        	
        	response = "on";
        } else if (key.equals(LIST_ALL_REQ_SW_STATUS)) {        	
        	response = "on";
        } else if (key.equals(LIST_ALL_REQ_SW_REQUESTER)) {        	
        	response = "on";
        } else if (key.equals(LIST_ALL_REQ_SW_REL_RES)) {            
            response = "off";
        } else if (key.equals(LIST_ALL_REQ_SW_PRG_TSK)) {            
            response = "on";
        } else if (key.equals(LIST_ALL_REQ_SW_META_FIELD)) {    
            response = "on";
        } else if (key.equals(HOME_TASKLIST_SW_TSK_PARENT)) {            
        	response = "off";
        } else if (key.equals(HOME_TASKLIST_SW_BLOCKERS)) {        	
        	response = "off";
        } else if (key.equals(HOME_TASKLIST_SW_BILLABLE)) {        	
        	response = "off";
        } else if (key.equals(RSK_SW_PROJECT)) {            
            response = "off";
        } else if (key.equals(RSK_SW_STATUS)) {            
        	response = "on";
        } else if (key.equals(RSK_SW_PROB)) {            
        	response = "off";
        } else if (key.equals(RSK_SW_IMPACT)) {            
        	response = "off";
        } else if (key.equals(RSK_SW_TENDENCY)) {            
        	response = "off";
        } else if (key.equals(RSK_SW_IMP_COST)) {            
        	response = "on";
        } else if (key.equals(CUSTOM_FORM_HIDE_DAYS)) {
        	response = "30";
        } else if (key.equals(RSK_SW_IMP_QUAL)) {            
        	response = "on";
        } else if (key.equals(RSK_SW_IMP_SCOP)) {            
        	response = "on";
        } else if (key.equals(RSK_SW_IMP_TIME)) {            
        	response = "on";
        } else if (key.equals(RSK_SW_TYPE)) {            
        	response = "off";
        } else if (key.equals(OCC_SW_PROJECT)) {            
            response = "off";            
        } else if (key.equals(REPOSITORY_GRID_FULLPATH)) {            
        	response = "off";
        } else if (key.equals(REPOSITORY_GRID_GRANT)) {
        	response = "on";
        } else if (key.equals(REPOSITORY_GRID_LOG)) {
        	response = "on";
        } else if (key.equals(REPOSITORY_GRID_VIEWER)) {
        	response = "on";        	
        } else if (key.equals(INV_INVLIST_SW_NAME)) {
        	response = "on";
        } else if (key.equals(INV_INVLIST_SW_NUMBER)) {
        	response = "on";
        } else if (key.equals(INV_INVLIST_SW_PO)) {
        	response = "on";
        } else if (key.equals(INV_INVLIST_SW_PRJ)) {
        	response = "off";
        } else if (key.equals(INV_INVLIST_SW_CATEG)) {
        	response = "off";
        } else if (key.equals(INV_INVLIST_SW_DUEDT)) {
        	response = "on";
        } else if (key.equals(INV_INVLIST_SW_INVDT)) {
        	response = "on";
        } else if (key.equals(INV_INVLIST_SW_CONTACT)) {
        	response = "off";
        } else if (key.equals(INV_INVLIST_SW_TOTAL)) {
        	response = "on";
        } else if (key.equals(HOME_TASKLIST_SW_REQ_ITERAT)) {        	
        	response = "off";
        } else if (key.equals(HOME_REQLIST_SW_ITERA)) {
        	response = "off";
        } else if (key.equals(LIST_ALL_TSK_BILLABLE)) {
        	response = "off";
        } else if (key.equals(CRITICAL_DAY_TASK)) {            
            response = "7";            
        } else if (key.equals(WARNING_DAY_TASK)) {            
            response = "1";
        } else if (key.equals(HIDE_PROJECT)) {
        	response = "";
        } else if (key.equals(KPI_SHOW_ONLY_CURPROJ)) {
        	response = "off";
        } else if (key.equals(KPI_SHOW_ONLY_OPENED)) {
        	response = "on";
        } else if (key.equals(CALEND_SYNC_BUS_CLASS)) {
            response = "com.pandora.bus.occurrence.EventOccurrence; com.pandora.bus.occurrence.MilestoneOccurrence";
        } else if (key.equals(CONVERTER_BUS_CLASS)) {
            //response = "com.pandora.bus.convert.MM2SWFConverter; com.pandora.bus.convert.PDF2SWFConverter";
        	response = "";
        } else if (key.equals(NOTIFICATION_BUS_CLASS)) {
            response = "com.pandora.bus.alert.EmailNotification; com.pandora.bus.alert.DbLogNotification; com.pandora.bus.alert.HttpNotification; com.pandora.bus.alert.CheckFileNotification; com.pandora.bus.alert.ExecuteNotification; com.pandora.bus.alert.NewTaskNotification; com.pandora.bus.alert.ReplicateTableNotification";
        } else if (key.equals(OCCURRENCE_BUS_CLASS)) {
            response = "com.pandora.bus.occurrence.IssueOccurrence; com.pandora.bus.occurrence.AdHocOccurrence; com.pandora.bus.occurrence.EventOccurrence; com.pandora.bus.occurrence.LessonLearnedOccurrence; com.pandora.bus.occurrence.MilestoneOccurrence; com.pandora.bus.occurrence.IterationOccurrence; com.pandora.bus.occurrence.StrategicObjectivesOccurrence";
        } else if (key.equals(KB_BUS_CLASS)) {
            response = "com.pandora.bus.kb.RequirementKbIndex; com.pandora.bus.kb.TaskKbIndex; com.pandora.bus.kb.OccurrenceKbIndex; com.pandora.bus.kb.RiskKbIndex;";
        } else if (key.equals(SNIP_ARTIFACT_BUS_CLASS)) {            
            response = "com.pandora.bus.snip.ImageSnipArtifact; com.pandora.bus.snip.TableSnipArtifact";            
        } else if (key.equals(GADGET_BUS_CLASS)) {
            response = "com.pandora.bus.gadget.BillableTaskChartGadget; com.pandora.bus.gadget.DefectTaskChartGadget; com.pandora.bus.gadget.PredictableTaskChartGadget; com.pandora.bus.gadget.ResourceAllocationChartGadget; com.pandora.bus.gadget.TaskStreamHtmlGadget; com.pandora.bus.gadget.WorkflowTimeGadget; com.pandora.bus.gadget.KpiChartGadget; com.pandora.bus.gadget.ResourceCategoryCapacityChartGadget; com.pandora.bus.gadget.ResourceProjectCapacityChartGadget; com.pandora.bus.gadget.ProjectRiskChartGadget; " +
            		   "com.pandora.bus.gadget.ProjectRiskInPeriodGadget; com.pandora.bus.gadget.IterationBurndownChartGadget; com.pandora.bus.gadget.KanbanLimitWIPChartGadget; com.pandora.bus.gadget.CalendarHtmlGadget; com.pandora.bus.gadget.InvoiceIncomeChartGadget; com.pandora.bus.gadget.CostAndBudgetGadget; com.pandora.bus.gadget.RequirementSummaryChartGadget; com.pandora.bus.gadget.KPIHistogramGadgetChart; com.pandora.bus.gadget.GoogleSlot1HtmlGadget; com.pandora.bus.gadget.GoogleSlot2HtmlGadget; com.pandora.bus.gadget.GoogleSlot3HtmlGadget";
        } else if (key.equals(IMP_EXP_BUS_CLASS)) {
            response = "com.pandora.imp.GanttProject20XmlExportBUS; com.pandora.imp.GoogleAgenda20ExportBUS; com.pandora.imp.MsProjectXmlImportBUS; com.pandora.imp.KPICsvExportBUS";
        } else if (key.equals(REPOSITORY_BUS_CLASS)) {
        	response = "com.pandora.bus.repository.WrapperSVNRepository; com.pandora.bus.repository.WrapperDBRepository";
        } else if (key.equals(AUTH_BUS_CLASS)) {
        	response = "com.pandora.bus.auth.SystemAuthentication; com.pandora.bus.auth.LdapAuthentication";
        } else if (key.equals(ARTIFACT_EXPORT_CLASS)) {
        	response = "com.pandora.bus.artifact.HtmlArtifactExport; com.pandora.bus.artifact.PDFArtifactExport";
        } else if (key.equals(KB_INDEX_FOLDER)) {
            response = "c:\\index\\";
        } else if (key.equals(UPLOAD_MAX_SIZE)) {
            response = "307200";
        } else if (key.equals(ARTIFACT_MAX_SIZE)) {
            response = "307200";
        } else if (key.equals(NEW_VERSION_URL)) {
            response = "http://plandora.sourceforge.net/version.php";
        } else if (key.equals(LDAP_FILTER)) {            
            response = "";
        } else if (key.equals(LDAP_HOST)) {            
            response = "";
        } else if (key.equals(LDAP_PORT)) {            
            response = "";
        } else if (key.equals(LDAP_SEARCH_BASE)) {            
            response = "";
        } else if (key.equals(LDAP_SEARCH_PASS)) {            
            response = "";
        } else if (key.equals(LDAP_UID_REGISTER)) {            
            response = "";
        } else if (key.equals(LDAP_UID_SEARCH)) {            
            response = "";
        } else if (key.equals(PIN_TASK_LIST)) {
        	response = "";
        } else if (key.equals(INPUT_TASK_FORMAT)) {        	
        	response = "1";
        } else if (key.equals(TASK_REPORT_URL)) {
        	response = "../do/viewReport?operation=generate&reportId=7&Initial_Date_7=#INITIAL_RANGE#&Final_Date_7=#FINAL_RANGE#&reportOutput=PDF";
        } else if (key.equals(SURVEY_REPORT_URL)) {
        	response = "../do/viewReport?operation=generate&reportId=8&Survey_8=#SURVEY_ID#&reportOutput=PDF";
        } else if (key.equals(EXPENSE_REPORT_URL)) {
        	response = "../do/viewReport?operation=generate&reportId=9&Expense_9=#EXPENSE_ID#&reportOutput=PDF";
        } else if (key.equals(GADGET_WIDTH)) {        	
        	response = "220";
//the options bellow must be at the end...            

        } else if (key.startsWith(KB_CURSOR_PREFIX)) {
            response = "0";
        } else if (key.startsWith(KB_MAX_PREFIX)) { 
            response = "0";
        }

        return response;
    }

    
    /**
     * Add a new preference/value into hashmap of preferences
     * @param newValue
     */
    public void addPreferences(PreferenceTO newValue) {
        if (this.preferences==null) {
            this.preferences = new HashMap<String, PreferenceTO>();
        }
        this.preferences.remove(newValue.getId());
        this.preferences.put(newValue.getId(), newValue);
    }
    
    
    //////////////////////////////////////////
    public HashMap<String, PreferenceTO> getPreferences() {
        return preferences;
    }
    public void setPreferences(HashMap<String, PreferenceTO> newValue) {
        this.preferences = newValue;
    }
    
    //////////////////////////////////////////    
    public UserTO getUser() {
        return user;
    }
    public void setUser(UserTO newValue) {
        this.user = newValue;
    }
    
    //////////////////////////////////////////    
    public String getValue() {
        return value;
    }
    public void setValue(String newValue) {
        this.value = newValue;
    }
}
