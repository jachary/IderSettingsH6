/*******************************************************************
* Company:     Fuzhou Rockchip Electronics Co., Ltd
* Description:   
* @author:     fxw@rock-chips.com
* Create at:   2014��4��29�� ����6:18:22  
* 
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* 2014��4��29��      fxw         1.0         create
*******************************************************************/   

package com.rk_itvui.settings.language;

import java.text.Collator;
import java.util.Locale;

public class Language implements Comparable<Language>{
	
	static Collator sCollator = Collator.getInstance();
	private int iconRes;
    private String label;
    private Locale locale;

    public Language(String label, Locale locale) {
        this.label = label;
        this.locale = locale;
    }

    @Override
    public String toString() {
        return this.label;
    }

    public int compareTo(Language lang) {
        return sCollator.compare(this.label, lang.label);
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
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the locale
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 * @param locale the locale to set
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}
    
}
