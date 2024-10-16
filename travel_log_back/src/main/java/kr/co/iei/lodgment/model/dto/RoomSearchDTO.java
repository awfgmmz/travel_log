package kr.co.iei.lodgment.model.dto;

import java.util.List;

import org.apache.ibatis.type.Alias;

import io.swagger.v3.oas.annotations.media.Schema;
import jdk.jfr.Description;
import kr.co.iei.seller.model.dto.LodgmentStorageDTO;
import kr.co.iei.seller.model.dto.RoomDTO;
import kr.co.iei.seller.model.dto.RoomFileDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Alias(value ="roomSearch")
@Schema(description = "숙박업체 객실 정보")
public class RoomSearchDTO {
	@Schema(description = "객실 번호",type="int")
	private int roomNo;
	@Schema(description = "숙소 번호",type="int")
	private int lodgmentNo;
	@Schema(description = "객실 이름",type="String")
	private String roomName;
	@Schema(description = "객실 인원",type="int")
	private int roomQua;
	@Schema(description = "객실 최대 인원",type="int")
	private int roomMaxCapacity;
	@Schema(description = "객실 1박 가격",type="int")
	private int roomPrice;
	@Schema(description = "객실 정보",type="String")
	private String roomInfo;
	@Schema(description = "예약가능여부",type="String")
	private String reservationstatus;
	@Schema(description = "객실 사진 리스트",type="List<RoomFileDTO>")
	private List<RoomFileDTO> fileList;
}
