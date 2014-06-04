package org.mymmsc.api.http.samples;

import java.util.ArrayList;
import java.util.List;

public class FengBagMenuMemberCardList {
	private List<AreaBean> arealist=new ArrayList<AreaBean>();

	private List<FengBagMenuMemberCardListData> datalist=new ArrayList<FengBagMenuMemberCardListData>();
	public List<FengBagMenuMemberCardListData> getDatalist() {
		return datalist;
	}

	public void setDatalist(List<FengBagMenuMemberCardListData> datalist) {
		this.datalist = datalist;
	}

	public List<AreaBean> getArealist() {
		return arealist;
	}

	public void setArealist(List<AreaBean> arealist) {
		this.arealist = arealist;
	}

	 
}
