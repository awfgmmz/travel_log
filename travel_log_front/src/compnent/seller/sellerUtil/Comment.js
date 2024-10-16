import * as React from "react";
import Box from "@mui/joy/Box";
import Card from "@mui/joy/Card";
import CardContent from "@mui/joy/CardContent";
import Typography from "@mui/joy/Typography";
import PageNavi from "../../utils/PageNavi";
import { Button, Stack } from "@mui/material";
import { ReviewSlideImg } from "./SlideImg";
import Rating from "@mui/material/Rating";
import StarIcon from "@mui/icons-material/Star";
import Textarea from "@mui/joy/Textarea";
import { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import Swal from "sweetalert2";
import dayjs from "dayjs";

// 리뷰
const Comment = (props) => {
  const backServer = process.env.REACT_APP_BACK_SERVER;
  const {
    reviewList,
    setReviewList,
    reqPage,
    setReqPage,
    pi,
    sellerText,
    setSellerText,
  } = props;
  const [textComment, setTextComment] = useState(false);
  const [viewTextComment, setViewTextComment] = useState(false);
  const [viewUpdateTextComment, setViewUpdateTextComment] = useState(false);
  const [view, setView] = useState(false);
  // 답글 달기 눌렀을 때!
  const addComment = (reviewNo) => {
    setTextComment((prev) => ({
      ...prev,
      [reviewNo]: !prev[reviewNo], // 현재 reviewNo에 해당하는 값만 토글
    }));
  };

  // 답글 보기 눌렀을 때!
  const viewComment = (reviewNo) => {
    setViewTextComment((prev) => ({
      ...prev,
      [reviewNo]: !prev[reviewNo], // 현재 reviewNo에 해당하는 값만 토글
    }));
  };

  // 수정 버튼 눌렀을 때 토글!
  const viewUpdateComment = (reviewNo) => {
    setView(false);
    setViewUpdateTextComment((prev) => ({
      ...prev,
      [reviewNo]: !prev[reviewNo], // 현재 reviewNo에 해당하는 값만 토글
    }));
  };

  // 삭제 버튼 눌렀을 때
  const deleteComment = (reviewNo) => {
    Swal.fire({
      icon: "warning",
      title: "답글을 삭제하시겠습니까?",
      showCancelButton: true,
      confirmButtonText: "삭제하기",
      cancelButtonText: "취소",
    }).then((r) => {
      if (r.isConfirmed) {
        const form = new FormData();
        form.append("reviewNo", reviewNo);
        axios
          .patch(`${backServer}/seller/deleteComment`, form, {
            headers: {
              contentType: "multipart/form-data",
              processData: false,
            },
          })
          .then((res) => {
            if (res.data !== 0) {
              setSellerText("ok");
              setView(true); // 작성 완료를 누르면 textarea사라지고 타이포 나오게
            }
          })
          .catch((err) => {});
      }
    });
  };

  return (
    <Box
      sx={{
        width: "100%",
        display: "grid",
        gap: 2,
      }}
    >
      {reviewList.map((review, i) => {
        return (
          <Card variant="outlined" key={i}>
            <CardContent>
              <ReviewSlideImg fileList={review.fileList} />
              <Typography component="div" level="title-md">
                {review.memberId}
              </Typography>
              <Typography component="div">
                <ReviewStar star={review.rating} />
              </Typography>

              <Typography component="div">
                <div
                  dangerouslySetInnerHTML={{ __html: review.reviewContent }}
                ></div>
              </Typography>
              <Typography component="div">
                {review.sellerComment === null ? (
                  <>
                    <Button onClick={() => addComment(review.reviewNo)}>
                      {textComment[review.reviewNo] ? "답글 취소" : "답글 달기"}
                    </Button>
                    {textComment[review.reviewNo] ? (
                      <TextForm
                        reviewNo={review.reviewNo}
                        lodgmentNo={review.lodgmentNo}
                        setReviewList={setReviewList}
                        sellerText={sellerText}
                        setSellerText={setSellerText}
                        reqPage={reqPage}
                      />
                    ) : (
                      ""
                    )}
                  </>
                ) : (
                  <>
                    <Button onClick={() => viewComment(review.reviewNo)}>
                      {viewTextComment[review.reviewNo]
                        ? "답글 닫기"
                        : "답글 보기"}
                    </Button>
                    {viewTextComment[review.reviewNo] ? (
                      <Card>
                        <CardContent>
                          <Typography component="div" color="primary">
                            [ 판매자 답변 ]
                            <Button
                              onClick={() => viewUpdateComment(review.reviewNo)}
                            >
                              수정
                            </Button>
                            <Button
                              onClick={() => deleteComment(review.reviewNo)}
                            >
                              삭제
                            </Button>
                          </Typography>
                          {viewUpdateTextComment[review.reviewNo] &&
                          view !== true ? ( // view가 false면 TextForm 아니면 타이포 나오게
                            <TextForm
                              reviewNo={review.reviewNo}
                              lodgmentNo={review.lodgmentNo}
                              setReviewList={setReviewList}
                              sellerText={sellerText}
                              setSellerText={setSellerText}
                              reqPage={reqPage}
                              textComment={review.sellerComment}
                              setView={setView}
                            />
                          ) : (
                            <Typography component="div">
                              {review.sellerComment}
                            </Typography>
                          )}
                        </CardContent>
                      </Card>
                    ) : (
                      ""
                    )}
                  </>
                )}
              </Typography>
            </CardContent>
          </Card>
        );
      })}
      <div className="seller-comment-list-r">
        <PageNavi pi={pi} reqPage={reqPage} setReqPage={setReqPage} />
      </div>
    </Box>
  );
};
// 답글 달기
const TextForm = (props) => {
  const textComment = props.textComment;
  const setView = props.setView;
  const [text, setText] = useState(textComment || "");
  const backServer = process.env.REACT_APP_BACK_SERVER;
  const navigate = useNavigate();
  const { lodgmentNo, setReviewList, reqPage, setSellerText } = props;
  const { reviewNo } = props;
  return (
    <Box
      sx={{
        py: 2,
        flexDirection: "column",
        gap: 2,
        alignItems: "center",
        flexWrap: "wrap",
        width: "100%",
      }}
    >
      <form
        onSubmit={(event) => {
          event.preventDefault();
          // 댓글 1자 이상 100자 이하로..!
          const commentRegex = /^.{1,100}$/;

          if (!commentRegex.test(text)) {
            Swal.fire({
              text: "1~100자 이하로 입력해주세요",
              icon: "error",
            });
            return;
          }
          const form = new FormData();
          form.append("reviewNo", reviewNo);
          form.append("sellerComment", text);
          axios
            .patch(`${backServer}/seller/addComment`, form, {
              headers: {
                contentType: "multipart/form-data",
                processData: false,
              },
            })
            .then((res) => {
              if (res.data !== 0) {
                setSellerText("ok");
                setView(true); // 작성 완료를 누르면 textarea사라지고 타이포 나오게
              }
            })
            .catch((err) => {});
        }}
        style={{ display: "flex", alignItems: "center", width: "100%" }}
      >
        <Textarea
          placeholder="100자 이내로 적어주세요!"
          sx={{
            mb: 1,
            flexGrow: 1,
            minHeight: "40px", // 최소 높이
            maxHeight: "150px", // 최대 높이 (원하는 만큼 조정 가능)
            overflow: "hidden", // 콘텐츠가 넘칠 경우 스크롤 표시
            resize: "vertical", // 수직으로 크기 조절 가능
          }}
          value={text}
          onChange={(e) => {
            setText(e.target.value);
          }}
          onKeyDown={(e) => {
            if (e.key === "Enter" && !e.shiftKey) {
              // 엔터
              e.preventDefault();
              e.target.form.requestSubmit();
            }
          }}
        />
        <Button
          type="submit"
          sx={{
            ml: 2,
            height: "40px",
            padding: "8px 16px",
            marginTop: "-10px",
          }}
          variant="contained"
          color="success"
        >
          작성 완료
        </Button>
      </form>
    </Box>
  );
};

// 별
const ReviewStar = (props) => {
  const star = Number(props.star);

  return (
    <Box sx={{ width: 200, display: "flex", alignItems: "center" }}>
      <Rating
        name="text-feedback"
        value={star}
        readOnly
        precision={0.5}
        emptyIcon={<StarIcon style={{ opacity: 0.55 }} fontSize="inherit" />}
      />
    </Box>
  );
};

/* 문의 */
const QnaComment = (props) => {
  const {
    qnaList,
    setQnaList,
    sellerComment,
    setSellerComment,
    reqPageQ,
    setReqPageQ,
    piQ,
    setSellerText,
  } = props;

  const [textComment, setTextComment] = useState(false);
  const [viewTextComment, setViewTextComment] = useState(false);
  // 판매자 답글이 있으면 답글 보기 없으면 답글 달기
  const addComment = (roomQnaNo) => {
    setTextComment((prev) => ({
      ...prev,
      [roomQnaNo]: !prev[roomQnaNo], // 현재 reviewNo에 해당하는 값만 토글
    }));
  };

  // 판매자
  const viewComment = (roomQnaNo) => {
    setViewTextComment((prev) => ({
      ...prev,
      [roomQnaNo]: !prev[roomQnaNo], // 현재 reviewNo에 해당하는 값만 토글
    }));
  };

  return (
    <>
      <Box
        sx={{
          width: "100%",
          display: "grid",
          gap: 2,
        }}
      >
        {qnaList.map((qna, i) => {
          const qnaListArr = qna.commentList;
          const dateString = qna.qnaDate;
          const formattedDate = dayjs(dateString).format("YYYY-MM-DD");
          return (
            <Card variant="outlined" key={i}>
              <CardContent>
                <Typography component="div" level="title-md">
                  {qna.memberId}
                </Typography>
                <Typography component="div">{qna.qnaContent}</Typography>
                <Typography variant="caption" component="div">
                  {formattedDate}
                </Typography>
                {
                  qnaListArr.length === 0 ? (
                    <>
                      <Button
                        variant={
                          textComment[qna.roomQnaNo] ? "contained" : "outlined"
                        }
                        onClick={() => viewComment(qna.roomQnaNo)}
                      >
                        {viewTextComment[qna.roomQnaNo]
                          ? "답글 취소"
                          : "답글 달기"}
                      </Button>
                      {viewTextComment[qna.roomQnaNo] && (
                        <TextQna
                          setSellerText={setSellerText}
                          sellerComment={sellerComment}
                          setSellerComment={setSellerComment}
                          //oneComment={oneComment}
                          //setOneComment={setOneComment}
                          qnaNo={qna.roomQnaNo}

                          // 필요한 props 추가
                        />
                      )}
                    </>
                  ) : (
                    <>
                      <Button
                        variant={
                          textComment[qna.roomQnaNo] ? "contained" : "outlined"
                        }
                        onClick={() => addComment(qna.roomQnaNo)}
                      >
                        {textComment[qna.roomQnaNo] ? "답글 닫기" : "답글 보기"}
                      </Button>

                      {/* 답글 보기(true)때만 보여줌*/}
                      {textComment[qna.roomQnaNo] ? (
                        <>
                          <TextQna
                            setSellerText={setSellerText}
                            sellerComment={sellerComment}
                            setSellerComment={setSellerComment}
                            qnaNo={qna.roomQnaNo}
                          />
                        </>
                      ) : (
                        ""
                      )}
                      {textComment[qna.roomQnaNo] &&
                        qnaListArr.map((qnaList, i) => (
                          <Card key={i}>
                            <CardContent>
                              <Typography component="div" level="title-md">
                                [판매자]
                              </Typography>
                              <Typography component="div" level="title-md">
                                {qnaList.comContent}
                              </Typography>
                            </CardContent>
                          </Card>
                        ))}
                    </>
                  ) // 답글이 없으면 답글 달기 -> 있으면 답글 보기
                }
              </CardContent>
            </Card>
          );
        })}
        <div className="seller-comment-list-r">
          <PageNavi pi={piQ} reqPage={reqPageQ} setReqPage={setReqPageQ} />
        </div>
      </Box>
    </>
  );
};

// 판매자 문의 답글 달기
const TextQna = (props) => {
  const { setSellerText, sellerComment, setSellerComment, qnaNo } = props;
  const [oneComment, setOneComment] = useState(""); // 상태를 같이 관리하니까 여러 input에 한 번에 쳐져서 개별로 관리.,..
  const backServer = process.env.REACT_APP_BACK_SERVER;
  const navigate = useNavigate();
  return (
    <Box
      sx={{
        py: 2,
        display: "flex", // Flexbox 사용
        flexDirection: "row", // 가로로 배치
        gap: 2, // 요소 간 간격
        alignItems: "center", // 수직 가운데 정렬
        flexWrap: "nowrap", // 요소가 한 줄에 유지되도록 설정
        width: "100%",
      }}
    >
      <form
        onSubmit={(event) => {
          event.preventDefault();

          // 댓글 1자 이상 100자 이하로..!
          const commentRegex = /^.{1,100}$/;

          if (!commentRegex.test(oneComment)) {
            Swal.fire({
              text: "1~100자 이하로 입력해주세요",
              icon: "error",
            });
            return;
          }
          setSellerComment(oneComment);

          Swal.fire({
            icon: "warning",
            title: "제출 하시겠습니까?",
            text: " 수정 삭제가 불가하오니 신중하게 작성해주시길 바랍니다.",
            showCancelButton: true,
            confirmButtonText: "제출하기",
            cancelButtonText: "취소",
          }).then((r) => {
            if (r.isConfirmed) {
              const form = new FormData();
              form.append("comContent", oneComment);
              form.append("roomQnaNo", qnaNo);
              axios
                .patch(`${backServer}/seller/addSellerComment`, form, {
                  headers: {
                    contentType: "multipart/form-data",
                    processData: false,
                  },
                })
                .then((res) => {
                  if (res.data !== 0) {
                    setSellerText("ok");
                    setOneComment("");
                  }
                })
                .catch((err) => {});
            }
          });
        }}
        style={{ display: "flex", alignItems: "center", width: "100%" }}
      >
        <Textarea
          placeholder="100자 이내로 적어주세요!"
          value={oneComment}
          sx={{
            mb: 1,
            flexGrow: 1,
            minHeight: "40px", // 최소 높이
            maxHeight: "150px", // 최대 높이 (원하는 만큼 조정 가능)
            overflow: "hidden", // 콘텐츠가 넘칠 경우 스크롤 표시
            resize: "vertical", // 수직으로 크기 조절 가능
          }}
          onChange={(e) => {
            setOneComment(e.target.value);
          }}
          onKeyDown={(e) => {
            if (e.key === "Enter" && !e.shiftKey) {
              // 엔터
              e.preventDefault();
              e.target.form.requestSubmit();
            }
          }}
        />
        <Button
          type="submit"
          sx={{
            ml: 2,
            height: "40px",
            padding: "8px 16px",
            marginTop: "-10px",
          }}
          variant="contained"
          color="success"
        >
          작성 완료
        </Button>
      </form>
    </Box>
  );
};

export { Comment, QnaComment };
