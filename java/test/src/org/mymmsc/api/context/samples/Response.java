package org.mymmsc.api.context.samples;


public class Response<T>{
	private int status=0;
	private String message=null;
	private T data=null;
	/**当前页 */
	private int page=0;
	/**总记录数 */
	private int totalcount=0;
	/**分页大小 */
	private int pagesize=0;
	/**多少页 */
	private int pagecount=0;
	public Response(){
		//data=new ;
		//Class<?> c =Class.forName(data);
	}
	
	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getTotalcount() {
		return totalcount;
	}

	public void setTotalcount(int totalcount) {
		this.totalcount = totalcount;
	}

	public int getPagesize() {
		return pagesize;
	}

	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}

	public int getPagecount() {
		return pagecount;
	}

	public void setPagecount(int pagecount) {
		this.pagecount = pagecount;
	}

	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	
}
