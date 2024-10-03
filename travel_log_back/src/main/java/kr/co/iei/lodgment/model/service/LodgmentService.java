package kr.co.iei.lodgment.model.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import kr.co.iei.lodgment.model.dao.LodgmentDao;
import kr.co.iei.lodgment.model.dto.LodgmentDTO;
import kr.co.iei.lodgment.model.dto.LodgmentReviewDTO;
import kr.co.iei.lodgment.model.dto.LodgmentReviewFileDTO;
import kr.co.iei.lodgment.model.dto.RoomSearchDTO;
import kr.co.iei.lodgment.model.dto.SearchLodgmentDTO;
import kr.co.iei.seller.model.dto.LodgmentStorageDTO;
import kr.co.iei.seller.model.dto.RoomDTO;

@Service
public class LodgmentService {

	@Autowired
	private LodgmentDao lodgmentDao;
	
	//서비스 태그 가져오기
	public List serviceList() {
		List list = lodgmentDao.serviceList();
		return list;
	}
	//지역,호텔 검색창 관련 검색어 저장 
	public Map search(String value) {
		List list = lodgmentDao.search(value);
		List name = lodgmentDao.searchLodgment(value);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", list);
		map.put("name", name);
		return map;
	}


	//reqPage 작업도 같이 해서 보냄 
	public List<SearchLodgmentDTO> getLodgmentList(int reqPage, String lodgment, String startDate, String endDate,
		 int guest, int minPrice, int maxPrice, int[] selectedServiceTagsArry, int starValue, int order, int lodgmentType) {
		 int limit = 10;  //한페이지당 열개 
	     int start = (reqPage - 1) * limit +1; // 1~      11    21
	     int end = reqPage*limit; //                ~10    ~20    ~30           
	     List<SearchLodgmentDTO> list = lodgmentDao.getLodgmentList
	    		 (start, end, lodgment, startDate.substring(0, 10), endDate.substring(0, 10), guest,
	    				 minPrice, maxPrice, selectedServiceTagsArry, starValue, order, lodgmentType);

		return list;
	}
	
	//상세페이지 방 정보 불러오기 
	public Map getRoomInfo(int lodgmentNo, String startDate, String endDate, int loginNo) {
		//List<RoomSearchDTO> roomSearchList = lodgmentDao.getRoomInfo(lodgmentNo, startDate.substring(0, 10), endDate.substring(0, 10) );
		LodgmentDTO lodgmentInfo = lodgmentDao.getLodgmentInfo(lodgmentNo);
		
		//보관함 여부 조회  -1 은 로그인이 안되어있을때의 디폴트 값 
		int lodgmentCollection = -1;
		//System.out.println(loginNo);
		if(loginNo != -1) {			
			//0(보관함 x) 이거나 1(보관함 O) 
			lodgmentCollection = lodgmentDao.lodgmentCollection(loginNo, lodgmentNo);
			//System.out.println("lodgmentCollection"+lodgmentCollection);
		}
		//숙박업체에 해당되는 룸번호 배열 
		List<Integer> roomNoList = lodgmentDao.getRoomNo(lodgmentNo); 
		
		//배열 번호로 foreach 문 통해서 룸 배열 저장 
		List<RoomSearchDTO> roomSearchList = new ArrayList();
	   
		for (Integer roomNo : roomNoList) { // roomNo의 타입을 Integer로 지정
		    //System.out.println(roomNo); // 각 방 번호를 출력
		    RoomSearchDTO roomList = lodgmentDao.getRoomList(roomNo, lodgmentNo, startDate, endDate);
			roomSearchList.add(roomList);
		}
		lodgmentInfo.setRoomSearchList(roomSearchList);
		
		Map<String, Object> map = new HashMap<String, Object>();
	    map.put("lodgmentInfo", lodgmentInfo);
	    map.put("lodgmentCollection", lodgmentCollection);
		return map;
	}
	
	//숙박 업체 보관함 저장 
	@Transactional
	public int insertCollect(int lodgmentNo, int loginNo) {
		int result = lodgmentDao.insertCollect(lodgmentNo, loginNo);
		return result;
	}
	
	//숙박 업체 보관한 저장 취소 
	@Transactional
	public int deleteCollect(int lodgmentNo, int loginNo) {
		int result = lodgmentDao.deleteCollect(lodgmentNo, loginNo);
		return result;
	}
	
	//리뷰 등록 
	@Transactional
	public int insertReview(LodgmentReviewDTO lodgmentReview, List<LodgmentReviewFileDTO> fileSave) {
		int result = lodgmentDao.insertReview(lodgmentReview);
		if(!fileSave.isEmpty()) {
			for (LodgmentReviewFileDTO file : fileSave) {
				file.setReviewNo(lodgmentReview.getReviewNo());
				result += lodgmentDao.insertReviewFile(file);
			}
		}
		return result;
	}
}
