package kr.co.iei.admin.controller;


import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.iei.admin.model.servcie.AdminService;
import kr.co.iei.faq.model.dto.FaqDTO;
import kr.co.iei.member.model.dto.MemberDTO;
import kr.co.iei.seller.model.dto.SellerDTO;


@RestController
@CrossOrigin("*")
@RequestMapping(value="/admin")
@Tag(name = "ADMIN", description = "ADMIN API")
public class AdminController {
	@Autowired
	private AdminService adminService;
	
	@GetMapping(value="/inquiry/list/{reqPage}/{type}/{state}")
	@Operation(summary = "1:1문의 리스트 조회",description = "페이지번호,타입,처리상태를 받아서 1:1문의 리스트 조회")
	public ResponseEntity<Map> selectInquiryList(@PathVariable int reqPage,@PathVariable String type,@PathVariable int state){
		Map map = adminService.selectInquiryList(reqPage,type,state);
		return ResponseEntity.ok(map);
	}
	@GetMapping(value="/faq/{faqNo}")
	@Operation(summary = "faq 조회",description = "faq 번호를 받아서 faq 수정용 정보 조회")
	public ResponseEntity<FaqDTO> selectAdminFaq(@PathVariable int faqNo){
		FaqDTO faq = adminService.selectAdminFaq(faqNo);
		return ResponseEntity.ok(faq);
	}

	@PostMapping(value="/faq")
	@Operation(summary = "faq 등록",description = "faq 타입,제목,내용을 받아서 faq 정보 등록")
	public ResponseEntity<Integer> insertFaq(@RequestBody FaqDTO faq){
		int result = adminService.insertFaq(faq);
		return ResponseEntity.ok(result);
	}
	
	@PatchMapping(value="/faq")
	@Operation(summary = "faq 수정",description = "faq 번호,타입,제목,내용을 받아서 faq 정보 수정")
	public ResponseEntity<Integer> updateFaq(@RequestBody FaqDTO faq){
		int result = adminService.updateFaq(faq);
		return ResponseEntity.ok(result);
	}
	
	@DeleteMapping(value="/faq/{faqNo}")
	@Operation(summary = "faq 정보 삭제",description = "faq 번호를 받아서 faq 정보 삭제")
	public ResponseEntity<Integer> deleteFaq(@PathVariable int faqNo){
		int result = adminService.deleteFaq(faqNo);
		return ResponseEntity.ok(result);
	}
	@GetMapping(value="/seller/list/{reqPage}/{sellerApp}")
	@Operation(summary = "판매자 리스트 조회",description = "페이지 번호,판매자 승인여부를 받아서 판매자 리스트 조회")
	public ResponseEntity<Map> selectSellerList(@PathVariable int reqPage,@PathVariable int sellerApp){
		Map map = adminService.selectSellerList(reqPage,sellerApp);
		return ResponseEntity.ok(map);
	}
	@PatchMapping(value="/seller")
	@Operation(summary = "판매자 승인 정보 수정",description = "판매자 번호 배열을 받아서 가입승인 정보 수정")
	public ResponseEntity<Boolean> updateSellerApp(@RequestBody int[] sellerNo){
		int result = adminService.updateSellerApp(sellerNo);
		return ResponseEntity.ok(result == sellerNo.length);
	}
	@GetMapping(value="/member/enroll")
	@Operation(summary = "회원 가입수 정보 조회",description = "올해와 작년의 월별기준으로 회원 가입수 리스트 조회")
	public ResponseEntity<List> getMemberEnrollData(){
		List list = adminService.getMemberEnrollData();
		return ResponseEntity.ok(list);
	}
	@GetMapping(value="/member/age/gender")
	@Operation(summary = "회원의 나이,성별별 인원 정보 조회",description = "회원의 나이별,성별 총 회원수 리스트 조회")
	public ResponseEntity<List> getMemberData(){
		List list = adminService.getMemberData();
		return ResponseEntity.ok(list);
	}
	@GetMapping(value="/lodgment/region")
	@Operation(summary = "숙소 지역 리스트 조회",description = "db에 저장된 숙소들의 지역 리스트 조회")
	public ResponseEntity<List> getLodgmentResionData(){
		List list = adminService.getLodgmentResionData();
		return ResponseEntity.ok(list);
	}
	@GetMapping(value="/lodgment/region/member/{region}")
	@Operation(summary = "지역 회원 이용자 정보 조회",description = "지역을 받아서 해당 지역의 숙소를 이용하는 나이별,성별 회원수 리스트 조회")
	public ResponseEntity<List> getLodgmentResionSearchMemberData(@PathVariable String region){
		List list = adminService.getLodgmentResionSearchMemberData(region);
		return ResponseEntity.ok(list);
	}
	@GetMapping(value="/lodgment/region/member")
	@Operation(summary = "지역별 이용자수 조회",description = "지역별 이용자수 리스트 조회")
	public ResponseEntity<List> getLodmentResionMemberData(){
		List list = adminService.getLodgmentResionMemberData();
		return ResponseEntity.ok(list);
	}
	@GetMapping(value="/seller/list")
	@Operation(summary = "판매자 리스트 조회",description = "판매자 번호, 사업자명 리스트 조회")
	public ResponseEntity<List> getSellerList(){
		List list = adminService.getSellerList();
		return ResponseEntity.ok(list);
	}
	@GetMapping(value="/seller/sales/{type}/{date}")
	@Operation(summary = "연도별, 월별 판매자 매출  리스트 조회",description = "타입과 날짜를 받아서 연도별 월별 판매자 매출 리스트 조회")
	public ResponseEntity<List> getSellerListSales(@PathVariable String type,@PathVariable String date){
		List list = adminService.getSellerListSales(type,date);
		return ResponseEntity.ok(list);
	}
	@GetMapping(value="/seller/sales/{sellerNo}")
	@Operation(summary = "판매자 매출 정보 조회",description = "판매자 번호를 받아서 올해와 작년 월별 매출 정보 조회")
	public ResponseEntity<List> getSellerSales(@PathVariable int sellerNo){
		List list = adminService.getSellerSales(sellerNo);
		return ResponseEntity.ok(list);
	}
	@GetMapping(value="/seller/sales/gender/{sellerNo}")
	@Operation(summary = "판매자의 상품을 구매한 회원 성별 정보 조회 ",description = "판매자 번호를 받아서 상품을 구매한 회원들의 성별 조회")
	public ResponseEntity<List> getSellerSalesGender(@PathVariable int sellerNo){
		List list = adminService.getSellerSalesGender(sellerNo);
		return ResponseEntity.ok(list);
	}
	@GetMapping(value="/seller/sales/age/{sellerNo}")
	@Operation(summary = "판매자의 상품을 구매한 회원 나이 정보 조회 ",description = "판매자 번호를 받아서 상품을 구매한 회원들의 나이 조회")
	public ResponseEntity<List> getSellerSalesAge(@PathVariable int sellerNo){
		List list = adminService.getSellerSalesAge(sellerNo);
		return ResponseEntity.ok(list);
	}
	@GetMapping(value="/seller/stmList/{reqPage}/{state}")
	@Operation(summary = "판매자 정산 리스트 조회",description = "페이지 번호와 정산 상태를 받아서 판매자 정산 리스트 조회")
	public ResponseEntity<Map> getSellerStmList(@PathVariable int reqPage,@PathVariable int state){
		Map map = adminService.getSellerStmList(reqPage,state);
		return ResponseEntity.ok(map);
	}
	@PatchMapping(value="/seller/stm")
	@Operation(summary = "정산 정보 수정",description = "정산 번호 배열을 받아서 정산완료로 업데이트")
	public ResponseEntity<Boolean> updateStm(@RequestBody int[] stmNum){
		int result = adminService.updateStm(stmNum);
		return ResponseEntity.ok(result == stmNum.length);
	}
	@GetMapping(value="/lodgment/list/{reqPage}/{lodgmentDelete}")
	@Operation(summary = "상품 게시글 리스트 조회",description = "페이지 번호와 상품 게시글 등록 상태를 받아서 게시글 리스트 조회")
	public ResponseEntity<Map> getAdminLodgmentList(@PathVariable int reqPage,@PathVariable int lodgmentDelete){
		Map map = adminService.getAdminLodgmentList(reqPage,lodgmentDelete);
		return ResponseEntity.ok(map);
	}
	@PatchMapping(value="/lodgment")
	@Operation(summary = "상품 게시글 정보 수정",description = "상품 게시글 번호 배열을 받아서 등록완료 상태로 정보 수정")
	public ResponseEntity<Boolean> updateLodgmentDelete(@RequestBody int[] lodgmentNo){
		int result = adminService.updateLodgmentDelete(lodgmentNo);
		return ResponseEntity.ok(result == lodgmentNo.length);
	}

	@GetMapping(value="/board/report/list/{reqPage}")
	@Operation(summary = "신고 게시글 리스트 조회",description = "페이지 번호를 받아서 신고가 존재하는 게시글 리스트 조회")
	public ResponseEntity<Map> getBoardReportList(@PathVariable int reqPage){
		Map map = adminService.getBoardReportList(reqPage);
		return ResponseEntity.ok(map);
	}
	@GetMapping(value="/board/report/{boardNo}")
	@Operation(summary = "게시글 신고 리스트 조회",description = "게시글 번호를 받아서 게시글 신고 정보 리스트 조회")
	public ResponseEntity<List> getBoardReport(@PathVariable int boardNo){
		List list = adminService.getBoardReport(boardNo);
		return ResponseEntity.ok(list);
	}
	@DeleteMapping(value="/board/report/{reportNo}")
	@Operation(summary = "게시글 신고 삭제",description = "신고 번호를 받아서 게시글 신고 삭제")
	public ResponseEntity<Integer> deleteBoardReport(@PathVariable int reportNo){
		int result = adminService.deleteBoardReport(reportNo);
		return ResponseEntity.ok(result);
	}
	
	@GetMapping(value="/comment/report/list/{reqPage}")
	@Operation(summary = "신고 댓글 리스트 조회",description = "페이지 번호를 받아서 신고가 존재하는 댓글 리스트 조회")
	public ResponseEntity<Map> getCommentReportList(@PathVariable int reqPage){
		Map map = adminService.getCommentReportList(reqPage);
		return ResponseEntity.ok(map);
	}
	@GetMapping(value="/comment/report/{commentNo}")
	@Operation(summary = "댓글 신고 리스트 조회",description = "댓글 번호를 받아서 댓글 신고 정보 리스트 조회")
	public ResponseEntity<List> getCommentReport(@PathVariable int commentNo){
		List list = adminService.getCommentReport(commentNo);
		return ResponseEntity.ok(list);
	}
	@DeleteMapping(value="/comment/report/{reportNo}")
	@Operation(summary = "댓글 신고 삭제",description = "신고 번호를 받아서 댓글 신고 삭제")
	public ResponseEntity<Integer> deleteCommentReport(@PathVariable int reportNo){
		int result = adminService.deleteCommentReport(reportNo);
		return ResponseEntity.ok(result);
	}
	@GetMapping(value="/review/report/list/{reqPage}")
	@Operation(summary = "신고 리뷰 리스트 조회",description = "페이지 번호를 받아서 신고가 존재하는 리뷰 리스트 조회")
	public ResponseEntity<Map> getReviewReportList(@PathVariable int reqPage){
		Map map = adminService.getReviewReportList(reqPage);
		return ResponseEntity.ok(map);
	}
	@DeleteMapping(value="/review/report/{reviewNo}")
	@Operation(summary = "리뷰 신고 삭제",description = "리뷰 번호를 받아서 리뷰 신고 전체 삭제")
	public ResponseEntity<Integer> deleteReviewReport(@PathVariable int reviewNo){
		int result = adminService.deleteReviewReport(reviewNo);
		return ResponseEntity.ok(result);
	}
	@GetMapping(value="/member/list/{reqPage}/{type}")
	@Operation(summary = "회원 리스트 조회",description = "페이지 번호, 타입을 받아서 type이 1일경우 신고많은순 외에는 번호순 리스트 조회")
	public ResponseEntity<Map> getAdminMemberList(@PathVariable int reqPage, @PathVariable int type){
		Map map = adminService.getAdminMemberList(reqPage,type);
		return ResponseEntity.ok(map);
	}
	@PostMapping(value="/member/report")
	@Operation(summary = "회원 정지 등록",description = "회원 번호를 받아서 정지 정보 등록")
	public ResponseEntity<Integer> insertMemberReport(@RequestBody MemberDTO member){
		int result = adminService.insertMemberReport(member);
		return ResponseEntity.ok(result);
	}
	@PatchMapping(value="/member")
	@Operation(summary = "회원 등급 수정",description = "회원 번호와 등급 번호를 받아서 회원 등급 정보 수정")
	public ResponseEntity<Integer> updateMemberLevel(@RequestBody MemberDTO member){
		int result = adminService.updateMemberLevel(member);
		return ResponseEntity.ok(result);
	}
}
