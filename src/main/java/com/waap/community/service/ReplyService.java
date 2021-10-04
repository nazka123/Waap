package com.waap.community.service;

import java.util.List;

import com.waap.community.vo.ReplyVO;

public interface ReplyService {
	//댓글 조회
	public List<ReplyVO> readReply(int com_no) throws Exception;
	
	//댓글 작성
	public void writeReply(ReplyVO vo) throws Exception;
	
	//댓글 수정
	public void updateReply(ReplyVO vo) throws Exception;
	
	//댓글 삭제
	public void deleteReply(ReplyVO vo) throws Exception;
	
	//선택된 댓글 조회
	public ReplyVO selectReply(int reply_no) throws Exception;
}
