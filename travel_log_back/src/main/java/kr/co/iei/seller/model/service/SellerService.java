package kr.co.iei.seller.model.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import kr.co.iei.inquiry.model.dto.InquiryDTO;
import kr.co.iei.member.model.dto.MemberDTO;
import kr.co.iei.seller.model.dao.SellerDao;
import kr.co.iei.seller.model.dao.SellerLodgmentDao;
import kr.co.iei.seller.model.dto.BookingInfoDTO;
import kr.co.iei.seller.model.dto.InsertRoomDTO;
import kr.co.iei.seller.model.dto.LodgmentStorageDTO;
import kr.co.iei.seller.model.dto.LoginSellerDTO;
import kr.co.iei.seller.model.dto.RoomDTO;
import kr.co.iei.seller.model.dto.RoomFileDTO;
import kr.co.iei.seller.model.dto.RoomServiceTagDTO;
import kr.co.iei.seller.model.dto.SellerDTO;
import kr.co.iei.seller.model.dto.ServiceTagDTO;
import kr.co.iei.seller.model.dto.StmInfoDTO;
import kr.co.iei.util.FileUtils;
import kr.co.iei.util.JwtUtils;
import kr.co.iei.util.SellerJwtUtils;

@Service
public class SellerService {
	@Autowired
	private SellerDao sellerDao;
	@Autowired
	private SellerLodgmentDao sellerLodgmentDao;

	@Autowired
	private BCryptPasswordEncoder encoder;

	@Autowired
	private SellerJwtUtils sellerJwtUtils;

	@Autowired
	private FileUtils fileUtil;
	
	@Autowired
	private JwtUtils jwtUtil;

	@Value("${file.root}")
	public String root;

	// 메인(등록한 호텔 정보)
//	public List selectLodgmentList(int sellerNo) {
//		List list = sellerLodgmentDao.selectLodgmentList(sellerNo);
//		return list;
//	}
	public List selectLodgmentList(String token) {
		// 로그인시 받은 토큰을 검증한 후 회원아이디랑 등급을 추출해서 리턴받음
		// 토큰 체크
		LoginSellerDTO loginSeller = jwtUtil.sellerCheckToken(token);
		System.out.println(loginSeller);
		// 토큰 해석으로 받은 아이디를 통해서 DB에서 회원정보 조회
		List list = sellerLodgmentDao.selectLodgmentList(loginSeller.getSellerNo());
		System.out.println(list);
		return list;
//		return null;
	}
	
	
	// 기존 숙소 검색
	public List selectXlsxHotelInfo(String searchInfo) {
		List list = sellerLodgmentDao.selectXlsxHotelInfo(searchInfo);
		return list;
	}

	// 판매자 정보 조회
	public MemberDTO selectOneSeller(int sellerNo) {
		// SellerDTO로 바꿔야함
		return null;
	}

	// 호텔 정보 출력
	public LodgmentStorageDTO selectOneLodgment(int lodgmentNo) {
		LodgmentStorageDTO ls = sellerLodgmentDao.selectOneLodgment(lodgmentNo);
		return ls;
	}

	// 호텔 등록
	@Transactional
	public int insertLodgment(LodgmentStorageDTO ls) {
		LodgmentStorageDTO lg = sellerLodgmentDao.selectOneLodgment(ls.getLodgmentNo());
		if (lg == null) {
			int result = sellerLodgmentDao.insertLodgment(ls);
			int rs = sellerLodgmentDao.deleteLodgment(ls.getLodgmentNo());
			return result;
		}
		return 0;
	}

	// 호텔 상세 (호텔 + 객실 상세)
	public Map selectHotelInfo(int lodgmentNo) {
		Map<String, Object> map = new HashMap<String, Object>();

		LodgmentStorageDTO ls = sellerLodgmentDao.selectOneLodgment(lodgmentNo); // 호텔 정보 출력
		List<RoomDTO> list = sellerLodgmentDao.selectRoomInfo(lodgmentNo); // 객실 정보 출력
		System.out.println(list);

		map.put("lodgment", ls);
		map.put("list", list);

		return map;
	}

	// 객실 상세
	public Map selectRoomInfo(int lodgmentNo, int roomNo) {
		Map<String, Object> map = new HashMap<String, Object>();
		LodgmentStorageDTO ls = sellerLodgmentDao.selectOneLodgment(lodgmentNo); // 호텔 정보 출력 (호텔 이름, 체크인, 체크 아웃, 주소 들고
																					// 오기)
		RoomDTO rd = sellerLodgmentDao.selectRoomViewInfo(roomNo);
		map.put("lodgment", ls);
		map.put("room", rd);
		return map;
	}

	// 객실 등록(객실 정보, 파일, 해시태그 동시 처리)
	@Transactional
	public int insertRoom(InsertRoomDTO room, List<RoomFileDTO> roomFileList) {
		int result = sellerLodgmentDao.insertRoom(room);
		System.out.println(room);
		for (RoomFileDTO roomFile : roomFileList) {
			roomFile.setRoomNo(room.getRoomNo());
			result += sellerLodgmentDao.insertRoomFile(roomFile);
		}
		// 체크가 null 값이 아닐 때만 (해시태그가 비어있을 수도 있으니까...)
		if (room.getServiceTag() != null) {
			for (int serviceTagNo : room.getServiceTag()) {
				RoomServiceTagDTO rst = new RoomServiceTagDTO();
				rst.setRoomNo(room.getRoomNo());
				rst.setServiceTagNo(serviceTagNo); // 태그 값을 설정

				result += sellerLodgmentDao.insertServiceTag(rst); // 서비스 태그 DB에 삽입
			}
		}

		return result;
	}

	// 판매자 정산
	public List<StmInfoDTO> selectStmInfo(StmInfoDTO st) {
		List<StmInfoDTO> ls = sellerLodgmentDao.selectStmInfo(st);
		return ls;
	}

	// 판매자 문의 글 리스트 조회
	public List<StmInfoDTO> selectStmSearchInfo(StmInfoDTO st) {
		List<StmInfoDTO> ls = sellerLodgmentDao.selectStmInfo(st);
		return ls;
	}

	public List<InquiryDTO> selectInqList(String token) {
		LoginSellerDTO loginSeller = jwtUtil.sellerCheckToken(token);
		List<InquiryDTO> ls = sellerLodgmentDao.selectInqList(loginSeller.getSellerNo());
		return ls;
	}

	@Transactional
	// 형묵 seller-회원가입
	public int insertSeller(SellerDTO seller) {
		String encPw = encoder.encode(seller.getSellerPw());
		seller.setSellerPw(encPw);
		int result = sellerDao.insertSeller(seller);
		return result;
	}

	// 형묵 seller- id 중복체크
	public int checkSellerId(String businessNo) {
		int result = sellerDao.checkSellerId(businessNo);
		return result;
	}

	// 형묵 seller-login 완
	public LoginSellerDTO login(SellerDTO seller) {
		SellerDTO s = sellerDao.selectLoginSeller(seller.getBusinessNo());
		System.out.println(s);
		if (s != null && encoder.matches(seller.getSellerPw(), s.getSellerPw())) {
			String accessToken = sellerJwtUtils.createAccessToken(s.getSellerNo());
			String refreshToken = sellerJwtUtils.createRefreshToken(s.getSellerNo());
			LoginSellerDTO loginSeller = new LoginSellerDTO();
			loginSeller.setAccessToken(accessToken);
			loginSeller.setRefreshToken(refreshToken);
			loginSeller.setSellerNo(s.getSellerNo());
			loginSeller.setBusinessName(s.getBusinessName());
			return loginSeller;
		}
		return null;
	}

	// 판매자 문의 글 상세
	public InquiryDTO selectInqView(int inqNo) {
		InquiryDTO id = sellerLodgmentDao.selectInqView(inqNo);
		return id;
	}

	// seller refresh 형묵
	public LoginSellerDTO refresh(String token) {
		try {
			LoginSellerDTO loginSeller = sellerJwtUtils.checkToken(token);
			String accessToken = sellerJwtUtils.createAccessToken(loginSeller.getSellerNo());
			String refreshToken = sellerJwtUtils.createRefreshToken(loginSeller.getSellerNo());
			loginSeller.setAccessToken(accessToken);
			loginSeller.setRefreshToken(refreshToken);
			SellerDTO s = sellerDao.selectOneSeller(loginSeller.getSellerNo());
			loginSeller.setBusinessName(s.getBusinessName());
			return loginSeller;
		} catch (Exception e) {

		}
		return null;

	}

	// 예약 리스트 조회
	public List selectReserveList(String token) {
		LoginSellerDTO loginSeller = jwtUtil.sellerCheckToken(token);
		List list = sellerLodgmentDao.selectReserve(loginSeller.getSellerNo());
		System.out.println("list"+list);
		return list;
	}

	// 예약 상세 조회
	public BookingInfoDTO bookInfo(int bookNo) {
		BookingInfoDTO bid = sellerLodgmentDao.bookInfo(bookNo);
		return bid;
	}
}