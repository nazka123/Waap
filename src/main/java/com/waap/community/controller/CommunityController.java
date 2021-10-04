package com.waap.community.controller;

import java.io.File;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.waap.common.base.PageMaker;
import com.waap.common.base.SearchCriteria;
import com.waap.community.service.CommunityService;
import com.waap.community.service.ReplyService;
import com.waap.community.vo.CommunityVO;
import com.waap.community.vo.ReplyVO;


@Controller("communityController")
@RequestMapping(value="/community")
public class CommunityController {

	
	@Autowired
	private CommunityService service;

	@Autowired
	private ReplyService replyService;
	
	
	
	
	// 커뮤니티 글 작성 화면
	@RequestMapping(value="/writeView.do",method={RequestMethod.POST,RequestMethod.GET})
	public ModelAndView writeView(HttpServletRequest request) throws Exception{
		
		String viewName = (String)request.getAttribute("viewName");
		ModelAndView mav = new ModelAndView();
		mav.setViewName(viewName);
		
		return mav;
	}
	
	// 커뮤니티 글 작성
	@RequestMapping(value="/write.do",method={RequestMethod.POST,RequestMethod.GET})
	public String write(CommunityVO communityVO, MultipartHttpServletRequest mpRequest) throws Exception{
		
		service.write(communityVO, mpRequest);
		return "redirect:/community/list.do";
	}
	
	// 커뮤니티 목록 조회
	@RequestMapping(value="/list.do",method={RequestMethod.POST,RequestMethod.GET})
	public ModelAndView list(SearchCriteria scri, HttpServletRequest request, HttpServletResponse response) throws Exception{
		String viewName = (String)request.getAttribute("viewName");
		ModelAndView mav = new ModelAndView();
		mav.setViewName(viewName);
		mav.addObject("list", service.list(scri));
		
		PageMaker pageMaker = new PageMaker();
		pageMaker.setCri(scri);
		pageMaker.setTotalCount(service.listCount(scri));
		mav.addObject("pageMaker", pageMaker);
		
		return mav;
	}
	
	// 커뮤니티 최신글 목록 조회
	@RequestMapping(value="/list_last.do",method={RequestMethod.POST,RequestMethod.GET})
	public ModelAndView list_last(SearchCriteria scri, HttpServletRequest request, HttpServletResponse response) throws Exception{
		String viewName = (String)request.getAttribute("viewName");
		ModelAndView mav = new ModelAndView();
		mav.setViewName(viewName);
		mav.addObject("list", service.list_last(scri));
		
		PageMaker pageMaker = new PageMaker();
		pageMaker.setCri(scri);
		pageMaker.setTotalCount(service.listCount(scri));
		mav.addObject("pageMaker", pageMaker);
		
		return mav;
	}
	
	// 커뮤니티 인기글 목록 조회
	@RequestMapping(value="/list_count.do",method={RequestMethod.POST,RequestMethod.GET})
	public ModelAndView list_count(SearchCriteria scri, HttpServletRequest request, HttpServletResponse response) throws Exception{
		String viewName = (String)request.getAttribute("viewName");
		ModelAndView mav = new ModelAndView();
		mav.setViewName(viewName);
		mav.addObject("list", service.list_count(scri));
		
		PageMaker pageMaker = new PageMaker();
		pageMaker.setCri(scri);
		pageMaker.setTotalCount(service.listCount(scri));
		mav.addObject("pageMaker", pageMaker);
		
		return mav;
	}
	
	
	// 커뮤니티 조회
	@RequestMapping(value="/readView.do", method={RequestMethod.POST,RequestMethod.GET})
	public ModelAndView read(@RequestParam("com_no") int com_no, CommunityVO communityVO, SearchCriteria scri, HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		//조회수 증가
		service.viewsCount(com_no);
		
		//댓글 개수 증가
		service.updateReplyCount(com_no);
		
		String viewName = (String)request.getAttribute("viewName");
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName(viewName);
		mav.addObject("read", service.read(communityVO.getCom_no()));
		mav.addObject("scri", scri);
		
		List<ReplyVO> replyList = replyService.readReply(communityVO.getCom_no());
		mav.addObject("replyList", replyList);
		
		
		List<Map<String, Object>> fileList = service.selectFileList(communityVO.getCom_no());
		mav.addObject("file", fileList);
		
		
		return mav;
		
	}
	
	// 커뮤니티 수정뷰
	@RequestMapping(value="/updateView.do", method={RequestMethod.POST,RequestMethod.GET})
	public ModelAndView updateView(@ModelAttribute("scri") CommunityVO communityVO, SearchCriteria scri,
			     HttpServletRequest request, HttpServletResponse response) throws Exception {
			
		String viewName = (String)request.getAttribute("viewName");
			
		ModelAndView mav = new ModelAndView();
		mav.setViewName(viewName);
			
		mav.addObject("update", service.read(communityVO.getCom_no()));
		mav.addObject("scri", scri);
			
		List<Map<String, Object>> fileList = service.selectFileList(communityVO.getCom_no());
		mav.addObject("file", fileList);
			
		return mav;
	}
		
	// 커뮤니티 수정
	@RequestMapping(value="/update.do", method={RequestMethod.POST,RequestMethod.GET})
	public String update(@ModelAttribute("scri") CommunityVO communityVO, SearchCriteria scri, RedirectAttributes rttr,
						 @RequestParam(value="fileNoDel[]") String[] files,
						 @RequestParam(value="fileNameDel[]") String[] fileNames,
						 MultipartHttpServletRequest mpRequest) throws Exception {
		service.update(communityVO, files, fileNames, mpRequest);
			
		rttr.addAttribute("com_no", communityVO.getCom_no());
		rttr.addAttribute("page", scri.getPage());
		rttr.addAttribute("perPageNum", scri.getPerPageNum());
		rttr.addAttribute("searchType", scri.getSearchType());
		rttr.addAttribute("keyword", scri.getKeyword());
			
		return "redirect:/community/readView.do";
			
	}
	
	//커뮤니티 삭제
	@RequestMapping(value="/delete.do", method={RequestMethod.POST,RequestMethod.GET})
	public String delete(CommunityVO communityVO) throws Exception {
		service.delete(communityVO.getCom_no());
		
		return "redirect:/community/list.do";
		
	}
	
	//댓글 작성
	@RequestMapping(value="/replyWrite.do", method={RequestMethod.POST,RequestMethod.GET})
	public String replyWrite(ReplyVO vo, SearchCriteria scri, RedirectAttributes rttr) throws Exception {
		replyService.writeReply(vo);
		
		rttr.addAttribute("com_no", vo.getCom_no());
		rttr.addAttribute("page", scri.getPage());
		rttr.addAttribute("perPageNum", scri.getPerPageNum());
		rttr.addAttribute("searchType", scri.getSearchType());
		rttr.addAttribute("keyword", scri.getKeyword());
		
		return "redirect:/community/readView.do";
	}

	//댓글 삭제 
	@RequestMapping(value="/replyDelete.do", method={RequestMethod.POST,RequestMethod.GET})
	public String replyDelete(Model model, ReplyVO vo, SearchCriteria scri, RedirectAttributes rttr) throws Exception {
		
		replyService.deleteReply(vo);
		
		model.addAttribute("replyDelete", replyService.selectReply(vo.getReply_no()));
		model.addAttribute("scri", scri);

		rttr.addAttribute("com_no", vo.getCom_no());
		rttr.addAttribute("page", scri.getPage());
		rttr.addAttribute("perPageNum", scri.getPerPageNum());
		rttr.addAttribute("searchType", scri.getSearchType());
		rttr.addAttribute("keyword", scri.getKeyword());
		
		return "redirect:/community/readView.do";
	}
	
	//첨부파일 다운로드
	@RequestMapping(value="/fileDown.do")
	public void fileDown(@RequestParam Map<String, Object> map, HttpServletResponse response) throws Exception{
		Map<String, Object> resultMap = service.selectFileInfo(map);
		String storedFileName = (String) resultMap.get("STORED_FILE_NAME");
		String originalFileName = (String) resultMap.get("ORG_FILE_NAME");
		
		// 파일을 저장했던 위치에서 첨부파일을 읽어 byte[]형식으로 변환한다.
		byte fileByte[] = org.apache.commons.io.FileUtils.readFileToByteArray(new File("D:\\waap_project_file\\community\\file\\"+storedFileName));
		
		response.setContentType("application/octet-stream");
		response.setContentLength(fileByte.length);
		response.setHeader("Content-Disposition",  "attachment; fileName=\""+URLEncoder.encode(originalFileName, "UTF-8")+"\";");
		response.getOutputStream().write(fileByte);
		response.getOutputStream().flush();
		response.getOutputStream().close();
	}
}
