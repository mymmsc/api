package org.mymmsc.api.context.samples;

import java.util.List;
import org.mymmsc.api.assembly.BeanAlias;

public class TObject {
	private List<Bill> bills;
	@BeanAlias("errorid,errno")
	private int status;
	private Object message;
	private TOrder order;
	
	public List<Bill> getBills() {
		return bills;
	}

	public void setBills(List<Bill> bills) {
		this.bills = bills;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Object getMessage() {
		return message;
	}

	public void setMessage(Object message) {
		this.message = message;
	}

	/**
	 * @return the order
	 */
	public TOrder getOrder() {
		return order;
	}

	/**
	 * @param order the order to set
	 */
	public void setOrder(TOrder order) {
		this.order = order;
	}
}
