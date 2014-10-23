package com.thend.friendship.solr;

public class FieldConstant {
	
	public static final String USER_USERID = "user.userId";//用户ID
	public static final String USER_USERNAME = "user.userName";//用户名称
	public static final String USER_USERAGE = "user.userAge";//用户年龄
	
	public static enum SearchOrder {
		userAge("user.userAge");
		private String field;
		private SearchOrder(String field) {
			this.field = field;
		}
		public String getField() {
			return field;
		}
	}
}
