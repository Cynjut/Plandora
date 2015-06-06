package com.pandora;

import java.util.Vector;

public class ExpenseTO extends PlanningTO {

	private static final long serialVersionUID = 1L;

	private UserTO user;
	
	private ProjectTO project;

	private Vector<CostTO> expensesItems;
	
	//this attribute is transient
	private String refuserAprroverId; 
	
	
	public ExpenseTO() {
	}
	
	public ExpenseTO(String newId) {
		this.setId(newId);
	}

	
	////////////////////////////////////	
	public Long getTotal() {
		long acc = 0;
		if (expensesItems!=null) {
			for (CostTO cto : expensesItems) {
				Vector<CostInstallmentTO> instList = cto.getInstallments();
				if (instList!=null) {
					for (CostInstallmentTO cito : instList) {
						if (cito!=null && cito.getValue()!=null) {
							acc+= cito.getValue().longValue();
						}
					}
				}				
			}
		}
		return new Long(acc);
	}

	
	public boolean isWaiting() {
		boolean response = true;
		if (expensesItems!=null) {
			for (CostTO cto : expensesItems) {
				Vector<CostInstallmentTO> instList = cto.getInstallments();
				if (instList!=null) {
					for (CostInstallmentTO cito : instList) {
						if (cito!=null && cito.getCostStatus()!=null && cito.getCostStatus().getStateMachineOrder()!=null) {
							if (!cito.getCostStatus().getStateMachineOrder().equals(CostStatusTO.STATE_MACHINE_WAITING)) {
								response = false;
								break;
							}
						}
					}
				}				
			}
		}
		return response;
	}
	

	public void setApprover(LeaderTO led) {
		if (expensesItems!=null) {
			for (CostTO cto : expensesItems) {
				Vector<CostInstallmentTO> instList = cto.getInstallments();
				if (instList!=null) {
					for (CostInstallmentTO cito : instList) {
						cito.setApprover(led);
					}
				}				
			}
		}
	}
	
	
	////////////////////////////////////
	public UserTO getUser() {
		return user;
	}
	public void setUser(UserTO newValue) {
		this.user = newValue;
	}

	
	////////////////////////////////////
	public ProjectTO getProject() {
		return project;
	}
	public void setProject(ProjectTO newValue) {
		this.project = newValue;
	}
	
	
	////////////////////////////////////	
	public Vector<CostTO> getExpensesItems() {
		return expensesItems;
	}
	public void setExpensesItems(Vector<CostTO> newValue) {
		this.expensesItems = newValue;
	}

	
	////////////////////////////////////	
	public String getRefuserAprroverId() {
		return refuserAprroverId;
	}
	public void setRefuserAprroverId(String newValue) {
		this.refuserAprroverId = newValue;
	}
	
	
}
