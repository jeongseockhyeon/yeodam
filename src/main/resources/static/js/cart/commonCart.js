// 로그인 시 로컬 스토리지 장바구니 동기화
async function syncLocalCartToServer() {
    const cartItems = getLocalCartItems();

    if (isLoggedIn && cartItems.length > 0) {
        try {
            const cartData = cartItems.map(item => ({
                itemId: item.itemId,
                tourName: item.tourName,
                tourRegion: item.tourRegion,
                tourPeriod: item.tourPeriod,
                tourPrice: item.tourPrice,
                maximum: item.maximum,
                guideId: item.guideId,
                imgUrl: item.imgUrl
            }));

            const response = await fetch('/api/carts/sync', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                credentials: 'include',
                body: JSON.stringify(cartData)
            });

            if (!response.ok) {
                throw new Error(`동기화 실패: ${response.status}`);
            }

            localStorage.removeItem('cartItems');
            location.reload();
        } catch (error) {
            console.error('장바구니 동기화 실패:', error);
            throw error;
        }
    }
}

// 로그인 후 장바구니 동기화 이벤트 리스너 추가
document.addEventListener('DOMContentLoaded', async () => {
    if (isLoggedIn && localStorage.getItem('syncCartAfterLogin')) {
        localStorage.removeItem('syncCartAfterLogin');
        await syncLocalCartToServer();
    }
});

// 페이지 로드 시 이벤트 리스너 추가
document.addEventListener('DOMContentLoaded', () => {
    if (!isLoggedIn) {
        initializeLocalCart();
    }
    // 로그인 상태여도 초기에는 금액을 0으로 설정
    document.getElementById('totalPrice').textContent = '0';
    document.getElementById('subtotal').textContent = '0';

    addCheckboxEventListeners();
    updateSelectedCount();
});

// 페이지 로드 시 초기화 - 가격 계산 다시 실행
document.addEventListener('DOMContentLoaded', () => {
    if (!isLoggedIn) {
        initializeLocalCart();
    }
    // 로그인 상태에서도 초기에는 0으로 표시
    document.getElementById('totalPrice').textContent = '0';
    document.getElementById('subtotal').textContent = '0';

    addCheckboxEventListeners();
    updateSelectedCount();

    // 전체 선택 체크박스의 초기 상태를 해제로 설정
    const selectAllCheckbox = document.getElementById('selectAll');
    if (selectAllCheckbox) {
        selectAllCheckbox.checked = false;
    }

    // 모든 개별 체크박스의 초기 상태를 해제로 설정
    const allCheckboxes = document.querySelectorAll('.item-checkbox');
    allCheckboxes.forEach(checkbox => {
        checkbox.checked = false;
    });
});

// 선택된 상품 개수 업데이트
function updateSelectedCount() {
    const checkedItems = document.querySelectorAll('.item-checkbox:checked');
    document.getElementById('selectedCount').textContent = checkedItems.length;
}

// 전체 선택/해제 토글 함수
function toggleAllCheckboxes(checkbox) {
    const allCheckboxes = document.querySelectorAll('.item-checkbox');
    allCheckboxes.forEach(item => {
        item.checked = checkbox.checked;
    });
    // 체크박스 변경 시 선택된 상품 금액 계산
    calculateSelectedPrice();
    updateSelectedCount();
}

// 선택된 상품만 가격 계산
function calculateSelectedPrice() {
    const checkedItems = document.querySelectorAll('.item-checkbox:checked');
    const cartIds = Array.from(checkedItems).map(item => item.value);

    if (checkedItems.length > 0) {
        if (isLoggedIn) {
            const queryString = cartIds.map(id => `cartIds=${id}`).join('&');
            fetch(`/carts/selected-price?${queryString}`, {
                method: 'GET',
                headers: { 'Content-Type': 'application/json' }
            })
                .then(response => response.json())
                .then(data => {
                    document.getElementById('totalPrice').textContent = data.tourPrice.toLocaleString();
                    document.getElementById('subtotal').textContent = data.tourPrice.toLocaleString();
                });
        } else {
            // 비로그인 상태: 로컬스토리지 데이터 사용
            const cartItems = getLocalCartItems();
            const total = cartItems
                .filter(item => cartIds.includes(item.itemId.toString()))
                .reduce((sum, item) => sum + (item.price * item.count), 0);

            document.getElementById('totalPrice').textContent = total.toLocaleString();
            document.getElementById('subtotal').textContent = total.toLocaleString();
        }
    } else {
        document.getElementById('totalPrice').textContent = '0';
        document.getElementById('subtotal').textContent = '0';
    }
}

// 가격 계산 함수
function calculateTotalPrice() {
    const checkedItems = document.querySelectorAll('.item-checkbox:checked');
    const cartItems = getLocalCartItems();

    if (checkedItems.length > 0) {
        // 선택된 상품들의 total 계산
        const total = Array.from(checkedItems)
            .map(checkbox => parseInt(checkbox.value))
            .reduce((sum, itemId) => {
                const item = cartItems.find(item => item.itemId === itemId);
                return sum + (item ? item.price * item.count : 0);
            }, 0);

        document.getElementById('subtotal').textContent = total.toLocaleString();
        document.getElementById('totalPrice').textContent = total.toLocaleString();
    } else {
        document.getElementById('subtotal').textContent = '0';
        document.getElementById('totalPrice').textContent = '0';
    }
}

// 선택 상품 삭제 함수 추가
async function deleteSelectedItems() {
    const checkedItems = document.querySelectorAll('.item-checkbox:checked');
    if (checkedItems.length === 0) {
        alert('선택된 상품이 없습니다.');
        return;
    }

    if (!confirm('선택한 상품을 삭제하시겠습니까?')) {
        return;
    }

    if (isLoggedIn) {
        for (const item of checkedItems) {
            try {
                await removeCart(item.value);
            } catch (error) {
                console.error('상품 삭제 실패:', error);
            }
        }
        location.reload();
    } else {
        const cartItems = getLocalCartItems();
        const selectedIds = Array.from(checkedItems).map(item =>
            parseInt(item.value));
        const updatedItems = cartItems.filter(item =>
            !selectedIds.includes(item.itemId));
        localStorage.setItem('cartItems', JSON.stringify(updatedItems));
        renderLocalCart(updatedItems);
        calculateSelectedPrice();
        updateSelectedCount();
    }
}

// 체크박스 이벤트 리스너
function addCheckboxEventListeners() {
    const allCheckboxes = document.querySelectorAll('.item-checkbox');
    const selectAllCheckbox = document.getElementById('selectAll');

    // 개별 체크박스 변경 이벤트
    allCheckboxes.forEach(checkbox => {
        checkbox.addEventListener('change', () => {
            if (selectAllCheckbox) {
                selectAllCheckbox.checked = Array.from(allCheckboxes).every(cb => cb.checked);
            }
            calculateSelectedPrice();  // 선택된 상품만의 가격 계산
            updateSelectedCount();
        });
    });

    // 전체 선택 체크박스 변경 이벤트
    if (selectAllCheckbox) {
        selectAllCheckbox.addEventListener('change', () => {
            allCheckboxes.forEach(checkbox => {
                checkbox.checked = selectAllCheckbox.checked;
            });
            calculateSelectedPrice();  // 선택된 상품만의 가격 계산
            updateSelectedCount();
        });
    }
}



// 주문 진행 함수
document.addEventListener('DOMContentLoaded', function() {
    window.proceedToCheckout = function () {
        const checkedItems = document.querySelectorAll('.item-checkbox:checked');
        if (checkedItems.length === 0) {
            alert('선택된 상품이 없습니다.');
            return;
        }

        //비로그인 시 로그인 페이지로 이동
        if (!isLoggedIn) {
            window.location.href = '/login';
            return;
        }

        const form = document.getElementById('orderForm');
        if (!form) {
            console.error('orderForm을 찾을 수 없습니다');
            return;
        }

        // 기존의 hidden input 제거
        form.querySelectorAll('input[type="hidden"]').forEach(input => input.remove());

        // 선택 상품 ID를 hidden input으로 추가
        checkedItems.forEach(item => {
            const input = document.createElement('input');
            input.type = 'hidden';
            input.name = 'cartIds';
            input.value = item.value;
            form.appendChild(input);
        });

        form.submit();
    };
});


// 쇼핑 계속하기 함수
function continueShopping() {
    // 이전 페이지의 URL 가져오기
    const previousPage = document.referrer;

    // 이전 페이지가 존재하고 같은 도메인인지 확인
    if (previousPage && new URL(previousPage).hostname === window.location.hostname) {
        history.back();
    } else {
        // 같은 도메인이 아니거나 이전 페이지가 없는 경우 상품 목록으로 이동
        window.location.href = '/tours';
    }
}
