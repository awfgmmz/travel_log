import { Route, Routes } from "react-router-dom";
import SellerLodgmentList from "./SellerLodgmentList";
import InsertLodgment from "./InsertLodgment";
import SellerInfo from "./SellerInfo";
import InsertRoom from "./InsertRoom";
import LodgmentView from "./LodgmentView";
const SellerMain = () => {
  return (
    <Routes>
      {/* 판매자 메인 - 등록한 호텔 정보 출력 */}
      <Route path="list" element={<SellerLodgmentList />} />
      {/* 호텔 등록하기 */}
      <Route path="insertLodgment" element={<InsertLodgment />} />
      {/* 판매자 개인정보 조회 */}
      <Route path="info" element={<SellerInfo />} />
      {/* 호텔 객실 등록하기 */}
      <Route path="insertRoom" element={<InsertRoom />} />
      {/* 호텔 누르면 해당 호텔 상세로 이동 */}
      <Route path="lodgmentView/:lodgmentNo" element={<LodgmentView />} />
    </Routes>
  );
};

export default SellerMain;
