package com.yuntongxun.ecdemo.ui.group;

import java.util.Comparator;

import com.yuntongxun.ecsdk.im.ECGroupMember;

public class ECGroupMemberComparator implements Comparator<ECGroupMember> {

	@Override
	public int compare(ECGroupMember lhs, ECGroupMember rhs) {

		if(lhs.getRole()>rhs.getRole()){
			
			return 1;
		}
		return 0;
	}

}
