package com.thend.friendship.solr.vo;

import org.apache.commons.lang.StringUtils;

import com.thend.friendship.po.User;
import com.thend.friendship.solr.FieldConstant;

public class UserAdapter {
	
	private long userId;
	private String userName;
	private int userAge;
	
    private static final String UPDATE_FIELD_TEMPLATE = "<field name=\"%s\">%s</field>\n";
    
    private static final String DELETE_FIELD_TEMPLATE = "<%s>%s</%s>\n";
    
    public UserAdapter(long userId) {
    	this.userId = userId;
    }

	public UserAdapter(User user) {
		this.userId = user.getUserId();
		this.userName = user.getUserName();
		this.userAge = user.getUserAge();
	}
	
	public String getUpdateXML() {
        StringBuilder sb = new StringBuilder();
        sb.append("<add>\n<doc>\n");
        updateAppend(sb, FieldConstant.USER_USERID, String.valueOf(userId));
        updateAppend(sb, FieldConstant.USER_USERNAME, userName);
        updateAppend(sb, FieldConstant.USER_USERAGE, String.valueOf(userAge));
        sb.append("</doc>\n</add>");
        return sb.toString();
    }
    
    private void updateAppend(StringBuilder sb, String field, String value) {
        if(sb != null && StringUtils.isNotBlank(field) && StringUtils.isNotBlank(value)) {
            sb.append(String.format(UPDATE_FIELD_TEMPLATE, field, value.replaceAll("[\\s&]", "")));
        }
    }
    
    private void deleteAppend(StringBuilder sb, String field, String value) {
        if(sb != null && StringUtils.isNotBlank(field) && StringUtils.isNotBlank(value)) {
            sb.append(String.format(DELETE_FIELD_TEMPLATE, field, value, field));
        }
    }
    
    public String getDeleteXML() {
        StringBuilder sb = new StringBuilder();
        sb.append("<delete>\n");
        deleteAppend(sb, FieldConstant.USER_USERID, String.valueOf(userId));
        sb.append("</delete>");
        return sb.toString();
    }

}
