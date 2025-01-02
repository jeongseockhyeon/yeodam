let isEventListenerAdded = false;

document.addEventListener("DOMContentLoaded", () => {
    if (!isEventListenerAdded) {
        const addToCartBtn = document.querySelector("#addToCartBtn");
        addToCartBtn.addEventListener("click", async () => {
            await addToCart();
        });
        isEventListenerAdded = true;
    }
});

//  실제 상품 데이터로 변경 되면 사용 예정
async function addToCart() {
    try {
        const itemId = parseInt(getTourItemIdFromUrl());

        const tourResponse = await fetch(`/api/tours/${itemId}`);
        const tourDetail = await tourResponse.json();

        if (!tourResponse.ok) {
            throw new Error('상품 정보를 불러올 수 없습니다.');
        }
        console.log('tourDetail:', tourDetail);

        const cartData = {
            itemId: tourDetail.id,
            tourName: tourDetail.tourName,
            tourRegion: tourDetail.tourRegion,
            tourPeriod: tourDetail.tourPeriod,
            tourPrice: tourDetail.tourPrice,
            maximum: tourDetail.maximum,
            guideId: tourDetail.guideInTourResDtos[0]?.id || null,
            imgUrl: tourDetail.itemImgResDtoList[0]?.storePath || null
        };
        console.log('cartData:', cartData);
        if (isLoggedIn) {
            try {
                const response = await fetch('/api/carts', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    credentials: 'include',
                    body: JSON.stringify(cartData)
                });

                if (response.status === 401) {
                    alert('로그인이 필요합니다.');
                    window.location.href = '/login';
                    return;
                }

                if (!response.ok) {
                    throw new Error(`서버 에러: ${response.status}`);
                }

                alert("장바구니에 상품이 담겼습니다.");
                return;
            } catch (error) {
                console.error('서버 장바구니 추가 실패:', error);
                throw error;
            }
        } else {
            const cartItems = JSON.parse(localStorage.getItem('cartItems') || '[]');

            if (cartItems.some(item => item.itemId === itemId)) {
                alert('이미 장바구니에 존재하는 상품입니다.');
                return;
            }

            cartItems.push(cartData);
            localStorage.setItem('cartItems', JSON.stringify(cartItems));
            alert("장바구니에 상품이 담겼습니다.");
        }
    } catch (error) {
        console.error('장바구니 담기 실패:', error);
        alert(error.message || "장바구니 담기에 실패했습니다.");
    }
}

// 상품 Id 추출
function getTourItemIdFromUrl() {
    const pathArray = window.location.pathname.split('/');
    const itemId = parseInt(pathArray[pathArray.length - 1]);
    return !isNaN(itemId) ? itemId : null;
}