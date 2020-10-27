package board.board.controller;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import board.board.entity.BoardEntity;
import board.board.entity.BoardFileEntity;
import board.board.service.BoardServiceImpl;
 
@Controller
public class BoardController {
  
	protected final Logger logger = getLogger(getClass());

	@Autowired
	private BoardServiceImpl boardService;
  	
	@RequestMapping(value="/blocko/board", method=RequestMethod.GET)
	public ModelAndView openBoardList(ModelMap model) throws Exception{
		ModelAndView mv = new ModelAndView("/board/BoardList");
		
		List<BoardEntity> list = boardService.selectBoardList();
		mv.addObject("list", list);
		
		return mv;
	}
	
	@RequestMapping(value="/blocko/board/write", method=RequestMethod.GET)
	public String openBoardWrite() throws Exception{
		return "/board/BoardWrite";
	}
	
	@RequestMapping(value="/blocko/board/write", method=RequestMethod.POST)
	public String writeBoard(BoardEntity board, MultipartHttpServletRequest multipartHttpServletRequest) throws Exception{
		boardService.saveBoard(board, multipartHttpServletRequest);
		return "redirect:/blocko/board";
	}
	
	@RequestMapping(value="/blocko/board/{boardIdx}", method=RequestMethod.GET)
	public ModelAndView openBoardDetail(@PathVariable("boardIdx") int boardIdx) throws Exception{
		ModelAndView mv = new ModelAndView("/board/BoardDetail");
		
		BoardEntity board = boardService.selectBoardDetail(boardIdx);
		mv.addObject("board", board);
		
		return mv;
	}
	
	@RequestMapping(value="/blocko/board/{boardIdx}", method=RequestMethod.POST)
	public String updateBoard(BoardEntity board) throws Exception{
		boardService.updateBoard(board,null);
		return "redirect:/blocko/board";
	}
	
	@RequestMapping(value="/blocko/board/{boardIdx}", method=RequestMethod.DELETE)
	public String deleteBoard(@PathVariable("boardIdx") int boardIdx) throws Exception{
		boardService.deleteBoard(boardIdx);
		return "redirect:/blocko/board";
	}
	
	@RequestMapping(value="/blocko/board/file", method=RequestMethod.GET)
	public void downloadBoardFile(int boardIdx, int idx, HttpServletResponse response) throws Exception{
		BoardFileEntity file = boardService.selectBoardFileInformation(boardIdx, idx); 
		
		byte[] files = FileUtils.readFileToByteArray(new File(file.getStoredFilePath()));
		
		response.setContentType("application/octet-stream");
		response.setContentLength(files.length);
		response.setHeader("Content-Disposition", "attachment; fileName=\"" + URLEncoder.encode(file.getOriginalFileName(),"UTF-8")+"\";");
		response.setHeader("Content-Transfer-Encoding", "binary");
		
		response.getOutputStream().write(files);
		response.getOutputStream().flush();
		response.getOutputStream().close();
	}
}