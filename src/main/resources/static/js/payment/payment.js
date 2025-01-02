const validateUrl = "/api/payments/validate";
const failApiUrl = "/api/payments/fail?orderUid=";


function requestPay(itemName, price, orderUid, username, email, phone) {
    IMP.request_pay(
        {
            pg: "html5_inicis",		// KG이니시스
            pay_method: "card",		// 결제 방법
            merchant_uid: orderUid, // 주문번호
            name: itemName,		    // 상품명
            amount: price,			// 금액
            buyer_name: username,   // 주문자
            buyer_email: email,     // 이메일
            buyer_tel: phone        // 전화번호
        },
        function (rsp) {
            if (rsp.success) {
                fetch(validateUrl, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({
                        "paymentUid": rsp.imp_uid,
                        "orderUid": rsp.merchant_uid
                    })
                })
                    .then(response => response.json())
                    .then(data => {
                        console.log(data);
                        window.location.href = "/payments/" + data.orderUid + "/success";
                    });
            } else {
                alert("결제에 실패하였습니다. 에러 내용: " + rsp.error_msg);
                fetch(failApiUrl, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({
                        "paymentUid": rsp.imp_uid,
                        "orderUid": rsp.merchant_uid
                    })
                })
                    .then(response => response.json())
                    .then(data => {
                        window.location.href = "/payments/" + data.orderUid + "/fail";
                    })
            }
        }
    );
}

async function retryOrder() {
    const path = window.location.pathname;
    const parts = path.split('/');
    const orderUid = parts[2];

    if (orderUid) {
        window.location.href = '/orders/' + orderUid + '/continue';
    } else {
        alert("정보가 없습니다");
    }
}

