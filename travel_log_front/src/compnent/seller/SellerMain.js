import { Route, Routes } from "react-router-dom";
import SellerLodgmentList from "./SellerLodgmentList";
import InsertLodgment from "./InsertLodgment";
import SellerInfo from "./SellerInfo";
import InsertRoom from "./InsertRoom";
import LodgmentView from "./LodgmentView";
import Reserve from "./Reserve";
import StmSeller from "./StmSeller";
import RoomView from "./RoomView";
import InqList from "./InqList";
import InqView from "./InqView";
import ReserveList from "./ReserveList";
import UpdateRoom from "./UpdateRoom";
import UpdateLodgment from "./UpdateLodgment";
const SellerMain = () => {
  return (
    <Routes>
      {/* 판매자 메인 - 등록한 호텔 정보 출력 -> 토큰, post해서 조회 (일단 get으로 판매자 번호 1 부여함 수정해야함) */}
      <Route path="list" element={<SellerLodgmentList />} />

      {/* 호텔 등록하기 */}
      <Route path="insertLodgment" element={<InsertLodgment />} />

      {/* 판매자 개인정보 조회  -> 토큰, post해서 조회*/}
      <Route path="info" element={<SellerInfo />} />

      {/* 호텔 객실 등록하기 */}
      <Route path="insertRoom/:lodgmentNo" element={<InsertRoom />} />

      {/* 호텔 누르면 해당 호텔 상세로 이동 */}
      <Route path="lodgmentView/:lodgmentNo" element={<LodgmentView />} />

      {/* 예약 리스트 조회 */}
      <Route path="bookList" element={<ReserveList />} />

      {/* 예약 - 예약 번호 보내서 조회 */}
      <Route path="reserve/:bookNo" element={<Reserve />} />

      {/* 호텔 객실 정보 조회 */}
      <Route path="roomView/:lodgmentNo/:roomNo" element={<RoomView />} />

      {/* 매출 - 매출 조회 */}
      <Route path="stm" element={<StmSeller />} />

      {/* 문의 - 판매자 문의 조회 -> 토큰, post해서 조회****/}
      <Route path="inqList" element={<InqList />} />

      {/* 문의 - 문의 상세*/}
      <Route path="inqView/:inqNo" element={<InqView />} />

      {/* 객실 수정 */}
      <Route path="updateRoom/:lodgmentNo/:roomNo" element={<UpdateRoom />} />

      {/* 호텔 수정 */}
      <Route path="updateLodgment/:lodgmentNo" element={<UpdateLodgment />} />

      {/* 판매자 정보 조회 */}
      <Route path="sellerInfo" element={<SellerInfo />} />
    </Routes>
  );
};

export default SellerMain;
