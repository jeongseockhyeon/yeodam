function getLocalCartItems() {
    try {
        return JSON.parse(localStorage.getItem('cartItems')) || [];
    } catch (error) {
        console.error('장바구니 데이터 파싱 실패:', error);
        return [];
    }
}

// 로컬 스토리지 장바구니 초기화
function initializeLocalCart() {
    if (!isLoggedIn) {
        const cartItems = getLocalCartItems();
        renderLocalCart(cartItems);
    }
}

// 로컬 스토리지 장바구니 렌더링
function renderLocalCart(items) {
    const container = document.getElementById('localCartItems');
    container.innerHTML = '';

    if (items.length === 0) {
        container.innerHTML = '<div class="text-center p-5">장바구니가 비어있습니다.</div>';
        return;
    }

    items.forEach(item => {
        const itemElement = document.createElement('div');
        itemElement.className = 'cart-item';
        itemElement.innerHTML = `
            <div class="cart-item-header">
                <div class="header-left">
                    <input type="checkbox" class="item-checkbox" value="${item.itemId}">
                    <h3 class="item-title">${item.itemName}</h3>
                </div>
                <div><button class="delete-btn" onclick="removeLocalItem(${item.itemId})">×</button></div>
            </div>
            <div class="cart-item-content">
                <div class="item-image">
                    ${item.imagePath ?
            `<img src="${item.imagePath}" alt="${item.itemName}" class="product-image">` :
            `<div class="no-image"><i class="bi bi-image text-secondary"></i></div>`}
                </div>
                <div class="item-info">
                    ${item.reservation ? `
                        <div class="tour-info">
                            <p>지역: ${item.region || ''}</p>
                            <p>기간: ${item.period || ''}</p>
                            <p>가이드: ${item.guideName || '선택된 가이드 없음'}</p>
                            <p>예약일: ${item.startDate || ''} - ${item.endDate || ''}</p>
                        </div>
                    ` : ''}
                </div>
                <div>
                    ${!item.reservation ? `
                        <div class="quantity-control">
                            <button class="quantity-btn" onclick="updateLocalCount(${item.itemId}, ${item.count - 1})">-</button>
                            <span>${item.count}</span>
                            <button class="quantity-btn" onclick="updateLocalCount(${item.itemId}, ${item.count + 1})">+</button>
                        </div>
                    ` : `
                        <div class="badge-reserved">예약상품</div>
                    `}
                </div>
                <div class="price">₩${(item.price * item.count).toLocaleString()}</div>
            </div>
        `;
        container.appendChild(itemElement);
    });
}

// DB 수량 업데이트
async function updateCartCount(cartId, newCount) {
    if (newCount < 1) return;

    try {
        const response = await fetch(`/api/carts/${cartId}/count`, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                count: newCount,
                reservation: false
            })
        });

        if (!response.ok) throw new Error('수량 업데이트 실패');
        location.reload();
    } catch (error) {
        console.error('수량 업데이트 실패:', error);
        alert('수량 변경에 실패했습니다.');
    }
}

// 로컬 스토리지 수량 업데이트
function updateLocalCount(itemId, newCount) {
    if (newCount < 1) return;

    const cartItems = getLocalCartItems();
    const itemIndex = cartItems.findIndex(item => item.itemId === itemId);

    if (itemIndex !== -1 && !cartItems[itemIndex].reservation) {
        cartItems[itemIndex].count = newCount;
        localStorage.setItem('cartItems', JSON.stringify(cartItems));
        renderLocalCart(cartItems);
        calculateTotalPrice();
        updateSelectedCount();
    }
}

// DB 아이템 삭제
async function removeCart(cartId) {
    try {
        const response = await fetch(`/api/carts/${cartId}`, {
            method: 'DELETE'
        });

        if (!response.ok) throw new Error('삭제 실패');
        location.reload();
    } catch (error) {
        console.error('삭제 실패:', error);
        alert('상품 삭제에 실패했습니다.');
    }
}

// 로컬 스토리지 상품 삭제
function removeLocalItem(itemId) {
    const cartItems = getLocalCartItems();
    const updatedItems = cartItems.filter(item => item.itemId !== itemId);
    localStorage.setItem('cartItems', JSON.stringify(updatedItems));
    renderLocalCart(updatedItems);
    calculateSelectedPrice();
    updateSelectedCount();
}

// 로그인 시 로컬 스토리지 장바구니 동기화
async function syncLocalCartToServer() {
    const cartItems = getLocalCartItems();
    if (isLoggedIn && cartItems.length > 0) {
        const cartData = cartItems.map(item => ({
            itemId: item.itemId,
            count: item.count,
            reservation: item.reservation,
            guideId: item.guideId,
            startDate: item.startDate,
            endDate: item.endDate
        }));

        try {
            const response = await fetch('/api/carts/sync', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(cartData)
            });

            if (!response.ok) throw new Error('동기화 실패');
            localStorage.removeItem('cartItems');
            location.reload();
        } catch (error) {
            console.error('장바구니 동기화 실패:', error);
        }
    }
}

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


// 페이지 로드 시 이벤트 리스너 추가
document.addEventListener('DOMContentLoaded', () => {
    if (!isLoggedIn) {
        initializeLocalCart();
    }
    addCheckboxEventListeners();
    calculateSelectedPrice();
    updateSelectedCount();
});

// 선택된 상품만 가격 계산
function calculateSelectedPrice() {
    const checkedItems = document.querySelectorAll('.item-checkbox:checked');
    const cartIds = Array.from(checkedItems).map(item => item.value);

    if (checkedItems.length > 0) {
        if (isLoggedIn) {
            // 로그인 상태: DB 데이터 사용
            const queryString = cartIds.map(id => `cartIds=${id}`).join('&');

            fetch(`/carts/selected-price?${queryString}`, {
                method: 'GET',
                headers: { 'Content-Type': 'application/json' }
            })
                .then(response => response.json())
                .then(data => {
                    document.getElementById('totalPrice').textContent = data.totalPrice.toLocaleString();
                    document.getElementById('subtotal').textContent = data.totalPrice.toLocaleString();
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

// 선택된 상품 개수 업데이트
function updateSelectedCount() {
    const checkedItems = document.querySelectorAll('.item-checkbox:checked');
    document.getElementById('selectedCount').textContent = checkedItems.length;
}

// 주문 진행 함수
function proceedToCheckout() {
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

    // form submit으로 선택된 cartIds 전송
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = '/order';

    checkedItems.forEach(item => {
        const input = document.createElement('input');
        input.type = 'hidden';
        input.name = 'cartIds';
        input.value = item.value;
        form.appendChild(input);
    })

    document.body.appendChild(form);
    form.submit();
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

// 페이지 로드 시 초기화 - 가격 계산 다시 실행
document.addEventListener('DOMContentLoaded', () => {
    if (!isLoggedIn) {
        initializeLocalCart();
    }
    calculateTotalPrice();
    updateSelectedCount();     // 초기 선택 개수 업데이트
});