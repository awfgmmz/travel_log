package kr.co.iei.lodgment.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Request {
	private int start;
	private int end;
	private int lodgmentNo;
    private int reviewNo;
    private int loginNo;
    private int selectedReason;
}
