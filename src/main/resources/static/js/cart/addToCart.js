document.addEventListener("DOMContentLoaded", () => {
    const addToCartBtn = document.querySelector(".btn-secondary");

    addToCartBtn.addEventListener("click", async () => {
        try {
            const cartData = {
                itemId: parseInt(getTourItemIdFromUrl()),
                itemName: document.getElementById("tourName").textContent,
                description: document.getElementById("tourDesc").textContent,
                period: document.getElementById("tourPeriod").textContent,
                region: document.querySelector(".detail-value").textContent,
                price: parseInt(document.getElementById("tourPrice").textContent.replace(/[^0-9]/g, '')),
                count: 1,
                reservation: true,
                guideId: null,
                startDate: null,
                endDate: null
            };

            if (isLoggedIn) {
                // 로그인 상태: 서버 API 호출
                const response = await fetch('/api/carts', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(cartData)
                });

                if (!response.ok) {
                    const errorData = await response.json();
                    throw new Error(errorData.message || '장바구니 담기 실패');
                }
            } else {
                // 비로그인 상태: 로컬 스토리지에 저장
                const cartItems = JSON.parse(localStorage.getItem('cartItems') || '[]');
                const existingItemIndex = cartItems.findIndex(item => item.itemId === cartData.itemId);

                if (existingItemIndex > -1) {
                    // 이미 있는 상품이면 수량만 증가
                    if (!cartItems[existingItemIndex].reservation) {
                        cartItems[existingItemIndex].count += 1;
                    }
                } else {
                    // 새 상품 추가
                    cartItems.push(cartData);
                }

                localStorage.setItem('cartItems', JSON.stringify(cartItems));
            }

            alert("장바구니에 상품이 담겼습니다.");
        } catch (error) {
            console.error('상세 에러:', error);
            alert(error.message || "장바구니 담기 중 오류가 발생했습니다.");
        }
    });
});

function getTourItemIdFromUrl() {
    const pathSegments = window.location.pathname.split('/');
    return pathSegments[pathSegments.length - 1];
}


