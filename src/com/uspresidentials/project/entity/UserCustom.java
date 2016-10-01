package com.uspresidentials.project.entity;

import java.util.Arrays;

public class UserCustom {

	String idUser;
	String userName;
	
	String [] friends;
	double pageRank;

	public UserCustom(String userName, double pageRank){
		this.userName = userName;
		this.pageRank = pageRank;
	}
	
	public String getIdUser() {
		return idUser;
	}

	public void setIdUser(String idUser) {
		this.idUser = idUser;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String[] getFriends() {
		return friends;
	}

	public void setFriends(String[] friends) {
		this.friends = friends;
	}

	public double getPageRank() {
		return pageRank;
	}

	public void setPageRank(double pageRank) {
		this.pageRank = pageRank;
	}

	@Override
	public String toString() {
		return "User [idUser=" + idUser + ", userName=" + userName + ", friends=" + Arrays.toString(friends)
				+ ", pageRank=" + pageRank + "]";
	}
}
