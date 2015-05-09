package com.pandora.gui.struts.action;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.AttachmentHistoryTO;
import com.pandora.delegate.AttachmentDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.gui.struts.form.HistAttachForm;
import com.pandora.helper.LogUtil;
import com.pandora.helper.StringUtil;

/**
 */
public class HistAttachAction extends GeneralStrutsAction {

    public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) {
        String forward = "showHistory";

        try {
            HistAttachForm frm = (HistAttachForm) form;
            this.clearMessages(request);

            AttachmentDelegate del = new AttachmentDelegate();
            Vector list = del.getHistory(frm.getAttachmentId());
            request.getSession().setAttribute("attachHistoryList", list);

        } catch (BusinessException e) {
            LogUtil.log(this, LogUtil.LOG_ERROR, "Show AttachmentHistory error", e);
        }

        return mapping.findForward(forward);
    }

    public ActionForward viewContent(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) {

        String forward = "showAttachContent";
        try {

            HistAttachForm frm = (HistAttachForm) form;
            Vector list = (Vector) request.getSession().getAttribute(
                    "attachHistoryList");

            String selectedItem = frm.getSelectedIndex();
            if (selectedItem != null) {
                int index = Integer.parseInt(selectedItem);
                AttachmentHistoryTO hto = (AttachmentHistoryTO) list.get(index);
                frm.setHistoryContent(this.getContent(hto));
            }

        } catch (BusinessException e) {
            LogUtil.log(this, LogUtil.LOG_ERROR, "Show AttachmentHistory error", e);
        }

        return mapping.findForward(forward);
    }

    private String getContent(AttachmentHistoryTO hto) throws BusinessException {
        String response = "";
        String content = hto.getHistory();
        if (content != null && !content.trim().equals("")) {
            response = StringUtil.formatWordToJScript(content);
        }

        return response;
    }

}