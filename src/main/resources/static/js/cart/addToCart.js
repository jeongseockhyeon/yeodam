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

async function addToCart() {
    try {
        const itemId = parseInt(getTourItemIdFromUrl());

        // 선택된 가이드 정보 가져오기
        const selectedGuideElement = document.getElementById('selectedGuide');
        const selectedGuideId = selectedGuideElement.getAttribute('data-guide-id');
        const selectedGuideName = selectedGuideElement.innerText.replace('선택된 가이드: ', '');

        console.log('장바구니 담기 시도:', {
            selectedGuideId,
            selectedGuideName,
            fullElement: selectedGuideElement.outerHTML
        });

        if (!selectedGuideId) {
            alert('가이드를 선택해주세요.');
            return;
        }

        const tourResponse = await fetch(`/api/tours/${itemId}`);
        const tourDetail = await tourResponse.json();

        if (!tourResponse.ok) {
            throw new Error('상품 정보를 불러올 수 없습니다.');
        }
        console.log('tourDetail:', tourDetail);


        console.log('localStorage 값:', {
            selectedStartDate: localStorage.getItem('selectedStartDate'),
            selectedEndDate: localStorage.getItem('selectedEndDate')
        });

        // cartData 생성 직전에 날짜 정보 가져오기
        const startDate = localStorage.getItem('selectedStartDate');
        const endDate = localStorage.getItem('selectedEndDate');

        if (!startDate || !endDate) {
            console.log('날짜 확인:', { startDate, endDate });
            alert('날짜를 선택해주세요.');
            return;
        }

        const cartData = {
            itemId: tourDetail.id,
            tourName: tourDetail.tourName,
            tourRegion: tourDetail.tourRegion,
            tourPeriod: tourDetail.tourPeriod,
            tourPrice: tourDetail.tourPrice,
            maximum: tourDetail.maximum,
            guideId: parseInt(selectedGuideId),
            guideName: selectedGuideName,
            imgUrl: tourDetail.itemImgResDtoList.length > 0
                ? (tourDetail.itemImgResDtoList.find(img => img.isThumbnail)?.imgUrl || tourDetail.itemImgResDtoList[0].imgUrl)
                : null,
            reservation: true,
            startDate: startDate,
            endDate: endDate
        };
        console.log('cartData의 날짜 정보:', {
            startDate: cartData.startDate,
            endDate: cartData.endDate
        });

        if (isLoggedIn) {
            try {
                console.log('서버로 전송할 cartData:', cartData);

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
                localStorage.removeItem('selectedStartDate');
                localStorage.removeItem('selectedEndDate');
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
            localStorage.removeItem('selectedStartDate');
            localStorage.removeItem('selectedEndDate');
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