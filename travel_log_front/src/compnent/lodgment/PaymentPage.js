import { useEffect, useState } from "react";
import "./css/paymentPage.css";
import LodgmentDetailMapPayment from "./LodgmentDetailMapPayment";
import PayMentUserInfo from "./PaymentUserInfo";
import { useLocation, useNavigate } from "react-router-dom";
import axios from "axios";
import { loginNoState } from "../utils/RecoilData";
import { useRecoilState } from "recoil";
import Swal from "sweetalert2";
import { guestState, startDateState, endDateState } from "../utils/RecoilData";

const PaymentPage = () => {
  const BackServer = process.env.REACT_APP_BACK_SERVER;
  const [loginNo] = useRecoilState(loginNoState);
  //console.log(loginNo);
  const { state } = useLocation();
  const navigate = useNavigate();
  //console.log(state);
  useEffect(() => {
    window.scrollTo(0, 0);
  }, []);
  const [guest] = useRecoilState(guestState);
  const [startDate] = useRecoilState(startDateState);
  const [endDate] = useRecoilState(endDateState);

  useEffect(() => {
    // 외부 스크립트 로드 함수
    const loadScript = (src, callback) => {
      const script = document.createElement("script");
      script.type = "text/javascript";
      script.src = src;
      script.onload = callback;
      document.head.appendChild(script);
    };
    // 스크립트 로드 후 실행
    loadScript("https://code.jquery.com/jquery-1.12.4.min.js", () => {
      loadScript("https://cdn.iamport.kr/js/iamport.payment-1.2.0.js", () => {
        const IMP = window.IMP;
        // 가맹점 식별코드
        IMP.init("imp17705812");
      });
    });
    // 컴포넌트가 언마운트될 때 스크립트를 제거하기 위한 정리 함수
    return () => {
      const scripts = document.querySelectorAll('script[src^="https://"]');
      scripts.forEach((script) => script.remove());
    };
  }, []);

  // 투숙객 정보
  const [bookingInfo, setBookingInfo] = useState({
    guestName: "",
    guestPhone: "",
    guestRequest: "",
    uestCount: guest,
    startDate: state.checkIn,
    endDate: state.checkOut,
    roomNo: state.room.roomNo,
    sellerNo: state.lodgmentInfo.sellerNo,
    memberNo: "",
    portoneimpuid: "",
  });
  // console.log("book : " + bookingInfo.memberNo);
  //console.log("room : " + bookingInfo.roomNo);

  //console.log(bookingInfo);
  // 투숙객 정보 입력 변환
  const valueChange = (e) => {
    const { name, value } = e.target;
    if (name === "guestName" && value.length > 5) {
      Swal.fire({
        title: "이름 오류",
        text: "이름은 5자 이내로 입력해주세요.",
        icon: "info",
        confirmButtonText: "확인",
      });
      return;
    }

    if (name === "guestPhone") {
      const phoneNumber = value
        .replace(/[^0-9]/g, "") // 숫자만 남기기
        .replace(/^(\d{3})(\d{3,4})(\d{4})$/, "$1-$2-$3"); // 형식 맞추기

      setBookingInfo((prev) => ({ ...prev, [name]: phoneNumber }));
    } else {
      setBookingInfo((prev) => ({ ...prev, [name]: value }));
    }
  };

  // 약관 동의
  const [agreePrivacy, setAgreePrivacy] = useState(false);
  const [agreeInfoSharing, setAgreeInfoSharing] = useState(false);

  // 체크인과 체크아웃 요일
  const checkInDay = new Date(state.checkIn).toLocaleString("ko-KR", {
    weekday: "long",
  });
  const checkOutDay = new Date(state.checkOut).toLocaleString("ko-KR", {
    weekday: "long",
  });

  //checkIn, checkOut 시간 계산
  const checkInClock =
    " " +
    (state.lodgmentInfo.lodgmentCheckIn
      ? state.lodgmentInfo.lodgmentCheckIn.slice(0, 2)
      : "00") +
    "시 " +
    (state.lodgmentInfo.lodgmentCheckIn
      ? state.lodgmentInfo.lodgmentCheckIn.slice(3, 5)
      : "00") +
    "분";

  const checkOutClock =
    " " +
    (state.lodgmentInfo.lodgmentCheckOut
      ? state.lodgmentInfo.lodgmentCheckOut.slice(0, 2)
      : "00") +
    "시 " +
    (state.lodgmentInfo.lodgmentCheckOut
      ? state.lodgmentInfo.lodgmentCheckOut.slice(3, 5)
      : "00") +
    "분";

  const nights = Math.ceil(
    (new Date(state.checkOut) - new Date(state.checkIn)) / (1000 * 60 * 60 * 24)
  ); // 숙박 일수 계산
  const totalPrice = nights * state.room.roomPrice;
  const priceTax = totalPrice * 0.1;
  const netAmount = totalPrice * 0.9;
  const productName =
    state.lodgmentInfo.lodgmentName +
    "/" +
    state.room.roomName +
    "/" +
    nights +
    "박/" +
    state.guest +
    "명";

  // 서버에 결제 정보를 전달
  const payBtn = async () => {
    // 투숙객 정보가 입력되었는지 확인
    if (bookingInfo.guestName === "" || bookingInfo.guestPhone === "") {
      Swal.fire({
        title: "투수객 정보를 입력해주세요.",
        text: "이름과 전화번호를 확인해주세요.",
        icon: "warning",
        confirmButtonText: "확인",
      });
      return;
    }

    try {
      // 예약 가능한 방이 있는지 확인
      const res = await axios.post(
        `${BackServer}/booking/comfirm`,
        bookingInfo
      );
      //console.log(res);
      // 예약 가능한 방이 없을 경우
      if (res.data === -1) {
        Swal.fire({
          title: "예약 가능한 방이 없습니다.",
          text: "다시 검색해주세요.",
          icon: "info",
          confirmButtonText: "확인",
        });
        navigate(`/lodgment/lodgmentList`);
        return;
      }

      // 결제 요청
      window.IMP.request_pay(
        {
          pg: "html5_inicis.INIpayTest", // 테스트 시 html5_inicis.INIpayTest 기재
          pay_method: "card",
          merchant_uid: "order_no_" + crypto.randomUUID(), // 상점에서 생성한 고유 주문번호
          name: productName,
          amount: totalPrice,
        },
        async (rsp) => {
          // 결제 성공 시 처리
          if (rsp.success) {
            const updatedBookingInfo = {
              ...bookingInfo,
              portoneimpuid: rsp.merchant_uid,
              memberNo: loginNo,
            };

            try {
              // 서버에 예약 정보 전달
              const bookingRes = await axios.post(
                `${BackServer}/booking`,
                updatedBookingInfo
              );
              // console.log(bookingRes);
              // 서버 오류 처리
              if (bookingRes.data === -1) {
                Swal.fire({
                  title: "서버 오류.",
                  text: "관리자에게 문의해주세요.",
                  icon: "info",
                  confirmButtonText: "확인",
                });
                navigate(`/lodgment/lodgmentList`);
              } else if (bookingRes.data !== null) {
                // 예약 정보 페이지로 이동
                navigate(`/lodgment/bookingInfo`, {
                  state: { bookNo: bookingRes.data },
                });
              }
            } catch (error) {
              //console.log(error);
              // 예약 오류 처리
              Swal.fire({
                title: "예약 오류.",
                text: "관리자에게 문의해주세요.",
                icon: "error",
                confirmButtonText: "확인",
              });
            }
          } else {
            // 결제 실패 처리
            // console.error("Payment failed:", rsp);
            Swal.fire({
              title: "결제 실패",
              text: "결제에 실패했습니다. 다시 시도해주세요.",
              icon: "error",
              confirmButtonText: "확인",
            });
          }
        }
      );
    } catch (error) {
      console.error("Error during payment process:", error);
      // 예약 확인 오류 처리
      Swal.fire({
        title: "예약 확인 오류",
        text: "예약 확인 중 오류가 발생했습니다. 다시 시도해주세요.",
        icon: "error",
        confirmButtonText: "확인",
      });
    }
  };

  return (
    <section className="section">
      <div className="lodgment-payment-info-wrap">
        <h1>예약하기</h1>
        <div className="paymentPage-title"></div>
        <div className="lodgment-payment-img">
          <img
            src={
              state.lodgmentInfo.lodgmentImgPath
                ? `${BackServer}/seller/lodgment/${state.lodgmentInfo.lodgmentImgPath}`
                : "/image/default_img.png"
            }
            alt={state.lodgmentInfo.lodgmentName}
          />
        </div>
        <div className="lodgment-payment-detail-wrap">
          <table className="lodgment-payment-detail">
            <tbody>
              <tr style={{ height: "50px" }}>
                <td width={"100%"}>
                  <h1>{state.lodgmentInfo.lodgmentName}</h1>
                  <div className="booking-roomName">
                    <h2>{state.room.roomName}</h2>
                  </div>
                </td>
              </tr>
              <tr style={{ height: "50px" }}>
                <td>
                  <span>
                    체크인 : {state.checkIn} ({checkInDay}){checkInClock}
                  </span>
                  <h6>
                    체크아웃 : {state.checkOut} ({checkOutDay}){checkOutClock}
                  </h6>
                </td>
              </tr>
              <tr>
                <td></td>
              </tr>
            </tbody>
          </table>
          <div>
            <table className="lodgment-payment-detail">
              <tbody>
                <tr>
                  <th width={"80%"}>
                    <h5>
                      {state.guest}명 / {nights}박
                    </h5>
                  </th>
                  <td width={"20%"}>{netAmount} 원</td>
                </tr>
                <tr>
                  <th width={"80%"}>
                    <h5>세금 및 수수료</h5>
                  </th>
                  <td width={"20%"}>{priceTax} 원</td>
                </tr>
                <tr>
                  <th width={"80%"}>
                    <h5>총 결제 금액</h5>
                  </th>
                  <td width={"20%"}>{totalPrice} 원</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
      <div className="lodgment-payment-info">
        <BookingLoom />
      </div>
      <div className="payment-user-info-wrap">
        <PayMentUserInfo valueChange={valueChange} bookingInfo={bookingInfo} />
      </div>

      <div className="terms-and-conditions">
        <h3>개인정보 수집 및 이용 동의 (필수)</h3>
        <label>
          <input
            type="checkbox"
            checked={agreePrivacy}
            onChange={() => setAgreePrivacy(!agreePrivacy)}
          />
          약관에 동의합니다.
        </label>
        <textarea className="terms-content">
          1.개인정보 수집 및 이용목적 상품 제공 계약의 성립, 계약 관련 동의 또는
          철회 본인 의사 확인, 대금환불, 고객상담 2.개인정보 수집 항목 예약자 및
          여행자 정보 : 이름, 이메일, 휴대폰번호, 영문이름(일부상품) 예약내역 :
          예약일자, 결제금액 추가 예약 정보 : 성별 (일부 상품), 생년월일(일부
          상품) 3. 보유 및 이용기간 회원 탈퇴시 까지(단, 관계 법령에 의해 보존할
          경우 그 의무기간 동안 별도 보관) ※ 동의를 거부할 권리 및 동의 거부에
          따른 불이익 개인정보 수집 및 이용에 대해 거부할 권리가 있으며, 동의를
          거부할 경우 상품 예약 및 서비스 이용이 불가함을 알려 드립니다.
        </textarea>
      </div>
      <div className="terms-and-conditions" readOnly>
        <h3>개인정보 제공 동의 (필수)</h3>
        <label>
          <input
            type="checkbox"
            checked={agreeInfoSharing}
            onChange={() => setAgreeInfoSharing(!agreeInfoSharing)}
          />
          약관에 동의합니다.
        </label>
        <textarea className="terms-content" readOnly>
          1.개인 정보를 제공받는자 2.제공하는 개인정보 항목 *예약자 및 여행자
          정보 : 이름, 이메일, 휴대폰번호, 영문이름(일부상품) 예약내역 :
          예약일자, 결제금액 추가 예약 정보 : 성별 (일부 상품), 생년월일(일부
          상품) 3.개인정보를 제공받는 자의 이용 목적 * 판매자와 구매자 거래,
          고객관리(고객상담/불만처리) 4. 보유 및 이용기간 * 여행 완료 후 5일까지
          ※ 동의를 거부할 권리 및 동의 거부에 따른 불이익 제3자제공 동의에 대해
          거부할 권리가 있으며, 동의를 거부할 경우 상품 예약 및 서비스 이용이
          불가함을 알려 드립니다..
        </textarea>
      </div>
      <div className="payment-action">
        <button disabled={!agreePrivacy || !agreeInfoSharing} onClick={payBtn}>
          결제하기
        </button>
      </div>
    </section>
  );
};

const BookingLoom = () => {
  // 필요한 내용 추가
};

export default PaymentPage;
