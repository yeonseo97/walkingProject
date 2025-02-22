package com.walk.aroundyou.dto;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

// 게시물 상세를 보여주는 DTO
public interface BoardDetailResponse {
	Long getBoardId();
	String getBoardType();
	String getBoardTitle();
	String getBoardContent();
	String getUserNickname();
	String getUserId();
	Integer getBoardViewCount();
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
	Timestamp getBoardCreatedDate();
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
	Timestamp getBoardUpdatedDate();
	Integer getCommentCnt();
	Integer getLikeCnt();
//	boolean getIsBoardLike();
}
