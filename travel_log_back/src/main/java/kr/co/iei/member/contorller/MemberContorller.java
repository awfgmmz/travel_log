package kr.co.iei.member.contorller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.iei.member.model.dto.MemberDTO;
import kr.co.iei.member.model.service.MemberService;
import kr.co.iei.util.EmailSender;

@CrossOrigin("*")
@RestController
@RequestMapping(value="member")
@Tag(name="member",description = "MEMBER API")
public class MemberContorller {
	
	@Autowired
	private MemberService memberService;
	@Autowired
	private EmailSender emailSender;
	
	@GetMapping(value="/memberId/{memberId}/check-id")
	public ResponseEntity<Integer> checkId(@PathVariable String memberId){
		int result = memberService.checkId(memberId);
		
		return ResponseEntity.ok(result);
	}
	@PostMapping
	public ResponseEntity<Integer> join(@RequestBody MemberDTO member){
		int result = memberService.insertMember(member);
		if(result > 0 ) {
			return ResponseEntity.ok(result);
		}else {
			return ResponseEntity.status(500).build();
		}
	}
	@GetMapping(value="/email")
	public String email() {
		return "etc/email";
	}
	
	@GetMapping(value="/sendEmail/{memberEmail}")
	public String sendEamil(@PathVariable String memberEmail) {
		//인증메일 인증코드 생성
		Random r = new Random();
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<6;i++) {
			//0 ~ 9 : r.nextInt(10); 
			//A ~ Z : r.nextInt(26) +65 ;
			//a ~ z : r.nextInt(26) +97 ;
			
			int flag = r.nextInt(3); //0,1,2 -> 숫자쓸지, 대문자쓸지, 소문자쓸지 결정
			if(flag == 0) {
				int randomCode = r.nextInt(10);
				sb.append(randomCode);
			}else if(flag == 1) {
				char randomCode = (char)(r.nextInt(26)+65);
				sb.append(randomCode);
			}else if(flag == 2 ) {
				char randomCode = (char)(r.nextInt(26)+97);
				sb.append(randomCode);
			}
		}
		String emailContent = "<h1>안녕하세요 .트레블로그입니다. </h1>"
							  +"<h3>인증번호는 [<span style='color:red;'>"
							  + sb.toString()
							  +"</span>]입니다.</h3>";
		emailSender.sendMail("트레블로그 이메일 인증번호", memberEmail, emailContent);
		return sb.toString();
	}
}
