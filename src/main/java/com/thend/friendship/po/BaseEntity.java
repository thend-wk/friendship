package com.thend.friendship.po;

import com.thend.friendship.utils.JsonSerializer;

public abstract class BaseEntity {
	
	public String toJson(){
        return JsonSerializer.toJson(this);
    }

}
