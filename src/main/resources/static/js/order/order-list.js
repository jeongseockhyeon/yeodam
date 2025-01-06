document.addEventListener('DOMContentLoaded', () => {
    const cancelOrderURL = "/api/orders/";

    document.querySelectorAll('.toggle-btn').forEach((btn) => {
        btn.addEventListener('click', () => {
            let card = btn.closest('.custom-card');
            let info = btn.closest('.custom-card').nextElementSibling;
            if (info && info.classList.contains('info')) {
                if (info.classList.contains('info-close')) {
                    info.classList.remove('info-close');
                    card.style.marginBottom = '0';
                } else {
                    info.classList.add('info-close');
                    card.style.marginBottom = '10px';
                }
            }
        });
    });

    document.querySelectorAll('.cancel-btn').forEach((btn) => {
        btn.addEventListener('click', async () => {
            try {

                if (!confirm("정말 취소하시겠습니까?")) {
                    return;
                }

                const parentDiv = btn.closest('.info');
                const orderUid = parentDiv.querySelector('p span').textContent;

                // 분할 취소를 할 수 없어서 필요 없음
                // const itemId = Number(parentDiv.querySelector('input[type="hidden"]').value);

                const cancelOrderDto = {
                    orderUid: orderUid,
                    // itemIds: [itemId],
                }

                // 주문 취소 요청
                const cancelOrderPaymentResponse = await fetch(cancelOrderURL + orderUid, {
                    method: "PATCH",
                    headers: {
                        'Content-Type': 'application/json',
                        'Accept': 'application/json'
                    },
                    body: JSON.stringify(cancelOrderDto)
                });

                if (!cancelOrderPaymentResponse.ok) {
                    alert("주문 취소 요청 실패");
                    return;
                }
                window.location.href = "/orders";
            } catch (error) {
                console.error("에러 ", error);
            }
        });
    });
})