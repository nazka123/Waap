package com.waap.admin.management.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.waap.common.base.SearchCriteria;
import com.waap.community.vo.CommunityVO;
import com.waap.member.vo.MemberVO;

@Repository("managementDAO")
public class ManagementDAOImpl implements ManagementDAO{
	@Autowired
	private SqlSession sqlSession;
	
	//회원 관리 메인
	@Override
	public List<MemberVO> list(SearchCriteria scri) throws Exception {
		return sqlSession.selectList("mapper.member.management_list", scri);
	}
	
	//회원 총 몇명
	@Override
	public int listCount(SearchCriteria scri) throws Exception {
		return sqlSession.selectOne("mapper.member.listCount", scri);
	}
	
	//회원 상세 정보
	@Override
	public MemberVO info(String member_id) throws Exception {
		return sqlSession.selectOne("mapper.member.member_info", member_id);
	}
	
	//회원 추방
	@Override
	public void deport(MemberVO vo) throws Exception{
		sqlSession.delete("mapper.member.deport", vo);
	}
	
	//관리자 작성 글 목록 조회
	@Override
	public List<CommunityVO> mylist(SearchCriteria scri) throws Exception{
		return sqlSession.selectList("communityMapper.mylist", scri);
	}
		
	//관리자 작성 글 총 개수
	@Override
	public int mylistCount(SearchCriteria scri) throws Exception {
		return sqlSession.selectOne("communityMapper.mylistCount", scri);
	}
}
