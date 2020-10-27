package board.board.service;

import static org.slf4j.LoggerFactory.getLogger;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import board.board.entity.BoardEntity;
import board.board.entity.BoardFileEntity;
import board.board.repository.BoardRepository;
import board.common.FileUtils;

@Service
public class BoardServiceImpl implements BoardService {
 
 	protected final Logger logger = getLogger(getClass());
 
	@Autowired
	FileUtils fileUtils;
  	
	@Autowired
	BoardRepository boardRepository;
  
	@Override
	public List<BoardEntity> selectBoardList() throws Exception {
		return boardRepository.findAllByOrderByBoardIdxDesc();
	}

	@Override
	public void saveBoard(BoardEntity board, MultipartHttpServletRequest multipartHttpServletRequest) throws Exception {
 		LocalDate currentDate = LocalDate.now();
		board.setCreatedDatetime(currentDate.toString());
//		List<BoardFileEntity> list = fileUtils.parseFileInfo(multipartHttpServletRequest);
//		if(CollectionUtils.isEmpty(list) == false){
//			board.setFileList(list);
//		}
		boardRepository.save(board);
	}
	
	@Override
	public void updateBoard(BoardEntity board, MultipartHttpServletRequest multipartHttpServletRequest) throws Exception{
		Optional<BoardEntity> optional = boardRepository.findById(board.getBoardIdx());
		if(optional.isPresent()) {
			BoardEntity boards = optional.get();
	 		LocalDate currentDate = LocalDate.now();
			boards.update(board.getTitle(), board.getContents(), board.getCreatorId(), currentDate.toString());
		}
		else {
			throw new NullPointerException();
		}	
	}
	
	@Override
	public BoardEntity selectBoardDetail(int boardIdx) throws Exception{
		Optional<BoardEntity> optional = boardRepository.findById(boardIdx);
		if(optional.isPresent()){
			BoardEntity board = optional.get();
			board.setHitCnt(board.getHitCnt() + 1);
			boardRepository.save(board);
			
			return board;
		}
		else {
			throw new NullPointerException();
		}
	}

	@Override
	public void deleteBoard(int boardIdx) {
		boardRepository.deleteById(boardIdx);
	}

	@Override
	public BoardFileEntity selectBoardFileInformation(int boardIdx, int idx) throws Exception {
		BoardFileEntity boardFile = boardRepository.findBoardFile(boardIdx, idx);
		return boardFile;
	}
	
	 
}