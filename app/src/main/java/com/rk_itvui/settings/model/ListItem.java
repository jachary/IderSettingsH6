/*******************************************************************
* Company:     Fuzhou Rockchip Electronics Co., Ltd
* Description:   
* @author:     fxw@rock-chips.com
* 
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* 2014      fxw         1.0         create
*******************************************************************/   

package com.rk_itvui.settings.model;

public class ListItem {

	private int itemId;
	private int iconRes;
	private String title;
	private String detail;
	private String cls;
	
	public ListItem() {
	}
	
	public ListItem(int iconRes, String title, String detail) {
		this.iconRes = iconRes;
		this.title = title;
		this.detail = detail;
	}
	
	/**
	 * @return the itemId
	 */
	public int getItemId() {
		return itemId;
	}

	/**
	 * @param itemId the itemId to set
	 */
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	/**
	 * @return the iconRes
	 */
	public int getIconRes() {
		return iconRes;
	}
	/**
	 * @param iconRes the iconRes to set
	 */
	public void setIconRes(int iconRes) {
		this.iconRes = iconRes;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the detail
	 */
	public String getDetail() {
		return detail;
	}
	/**
	 * @param detail the detail to set
	 */
	public void setDetail(String detail) {
		this.detail = detail;
	}

	/**
	 * @return the cls
	 */
	public String getCls() {
		return cls;
	}

	/**
	 * @param cls the cls to set
	 */
	public void setCls(String cls) {
		this.cls = cls;
	}
	
}
