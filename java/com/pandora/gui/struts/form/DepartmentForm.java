package com.pandora.gui.struts.form;

/**
 * This class handle the data of Department Form
 */
public class DepartmentForm extends GeneralStrutsForm {

	private static final long serialVersionUID = 1L;
	
    /** Name of Department */
    private String name;

    /** Description of Department */
    private String description;


    public String getDescription() {
        return description;
    }
    public String getName() {
        return name;
    }
    public void setDescription(String newValue) {
        this.description = newValue;
    }
    public void setName(String newValue) {
        this.name = newValue;
    }
}