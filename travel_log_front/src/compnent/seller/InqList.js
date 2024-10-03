import { SellerInqList } from "./sellerUtil/RowList";
import "./css/seller_inqList.css";
import { Link, useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import axios from "axios";
import { useRecoilState } from "recoil";
import { sellerLoginNoState } from "../utils/RecoilData";

const InqList = () => {
  const backServer = process.env.REACT_APP_BACK_SERVER;
  const [inqList, setInqList] = useState([]);
  console.log(inqList.length === 0);
  const [loginNo, setLoginNo] = useRecoilState(sellerLoginNoState);
  useEffect(() => {
    axios
      .post(`${backServer}/seller/inqList`, null, {
        params: { loginNo: loginNo },
      })
      .then((res) => {
        console.log(res);
        setInqList(res.data);
      })
      .catch((err) => {
        console.log(err);
      });
  }, [loginNo]);
  return (
    <>
      <div className="seller-inq-box-wrap">
        <h3>내가 쓴 문의 조회</h3>
        <div className="inq-btn-wrap">
          <Link to={`/seller/insertInq`} className="inq-btn">
            문의 하기
          </Link>
        </div>
        <SellerInqList inqList={inqList} />
      </div>
    </>
  );
};

export default InqList;
