//const isLoggedIn = ![[${anonymous}]];

// 로컬 스토리지 장바구니 초기화
function initializeLocalCart() {
    if (!isLoggedIn) {
        const cartItems = JSON.parse(localStorage.getItem('cartItems')) || [];
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

    let totalPrice = 0;
    items.forEach(item => {
        const itemElement = document.createElement('div');
        itemElement.className = 'cart-item';
        itemElement.innerHTML = `
                <input type="checkbox" class="item-checkbox" value="${item.itemId}">
                <div class="item-info">
                    <h3>${item.itemName}</h3>
                    <p>₩${(item.price * item.count).toLocaleString()}</p>
                    ${item.reservation ? '<span class="badge bg-primary">예약상품</span>' : ''}
                </div>
                ${!item.reservation ? `
                    <div class="quantity-control">
                        <button class="quantity-btn" onclick="updateLocalCount(${item.itemId}, ${item.count - 1})">-</button>
                        <span>${item.count}</span>
                        <button class="quantity-btn" onclick="updateLocalCount(${item.itemId}, ${item.count + 1})">+</button>
                    </div>
                ` : `
                    <div class="me-3">
                        <span class="badge bg-secondary">수량: 1</span>
                    </div>
                `}
                <button class="btn btn-danger" onclick="removeLocalItem(${item.itemId})">삭제</button>
            `;
        container.appendChild(itemElement);
        totalPrice += item.price * item.count;
    });

    document.getElementById('totalPrice').textContent = totalPrice.toLocaleString();
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

    const cartItems = JSON.parse(localStorage.getItem('cartItems')) || [];
    const itemIndex = cartItems.findIndex(item => item.itemId === itemId);

    if (itemIndex !== -1 && !cartItems[itemIndex].reservation) {
        cartItems[itemIndex].count = newCount;
        localStorage.setItem('cartItems', JSON.stringify(cartItems));
        renderLocalCart(cartItems);
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
    const cartItems = JSON.parse(localStorage.getItem('cartItems')) || [];
    const updatedItems = cartItems.filter(item => item.itemId !== itemId);
    localStorage.setItem('cartItems', JSON.stringify(updatedItems));
    renderLocalCart(updatedItems);
}

// 로그인 시 로컬 스토리지 장바구니 동기화
async function syncLocalCartToServer() {
    const localCart = JSON.parse(localStorage.getItem('cartItems')) || [];
    if (isLoggedIn && localCart.length > 0) {
        try {
            const response = await fetch('/api/carts/sync', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(localCart)
            });

            if (!response.ok) throw new Error('동기화 실패');
            localStorage.removeItem('cartItems');
            location.reload();
        } catch (error) {
            console.error('장바구니 동기화 실패:', error);
        }
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
}

// 선택된 상품 가격 계산
function calculateSelectedPrice() {
    const checkedItems = document.querySelectorAll('.item-checkbox:checked');
    const cartIds = Array.from(checkedItems).map(item => item.value);

    if (cartIds.length > 0) {
        if (isLoggedIn) {
            // GET 요청을 쿼리 파라미터로 변경
            const queryString = cartIds.map(id => `cartIds=${id}`).join('&');
            fetch(`/carts/selected-price?${queryString}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            })
                .then(response => response.json())
                .then(data => {
                    document.getElementById('totalPrice').textContent =
                        data.selectedPrice.totalPrice.toLocaleString();
                });
        } else {
            const localCart = JSON.parse(localStorage.getItem('cartItems')) || [];
            const total = localCart
                .filter(item => cartIds.includes(item.itemId.toString()))
                .reduce((sum, item) => sum + (item.price * item.count), 0);
            document.getElementById('totalPrice').textContent = total.toLocaleString();
        }
    } else {
        // 선택된 항목이 없을 경우 0으로 표시
        document.getElementById('totalPrice').textContent = '0';
    }
}
// 주문 진행 함수 추가
function proceedToCheckout() {
    const checkedItems = document.querySelectorAll('.item-checkbox:checked');
    if (checkedItems.length === 0) {
        alert('선택된 상품이 없습니다.');
        return;
    }

    const cartIds = Array.from(checkedItems).map(item => item.value);
    // 주문 페이지로 리다이렉트 (구현 예정)
    const queryString = cartIds.map(id => `cartIds=${id}`).join('&');
    window.location.href = `/orders/new?${queryString}`;
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
        const cartItems = JSON.parse(localStorage.getItem('cartItems')) || [];
        const selectedIds = Array.from(checkedItems).map(item =>
            parseInt(item.value));
        const updatedItems = cartItems.filter(item =>
            !selectedIds.includes(item.itemId));
        localStorage.setItem('cartItems', JSON.stringify(updatedItems));
        renderLocalCart(updatedItems);
    }
}

// 로컬 스토리지 렌더링 함수 수정
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
                        <div class="check">
                            <input type="checkbox" class="item-checkbox" value="${item.itemId}">
                        </div>
                        <div class="info">
                            <h3>${item.itemName}</h3>
                        </div>
                        <div class="quantity">
                            ${!item.reservation ? `
                                <div class="quantity-control">
                                    <button class="quantity-btn" onclick="updateLocalCount(${item.itemId}, ${item.count - 1})">-</button>
                                    <span>${item.count}</span>
                                    <button class="quantity-btn" onclick="updateLocalCount(${item.itemId}, ${item.count + 1})">+</button>
                                </div>
                            ` : `
                                <div class="badge bg-secondary">예약상품</div>
                            `}
                        </div>
                        <div class="price">₩${(item.price * item.count).toLocaleString()}</div>
                        <div class="select">
                            <button class="delete-btn" onclick="removeLocalItem(${item.itemId})">×</button>
                        </div>
                    `;
        container.appendChild(itemElement);
    });
}

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', () => {
    if (!isLoggedIn) {
        initializeLocalCart();
    } else {
        syncLocalCartToServer();
    }

    // 체크박스 이벤트 리스너
    document.querySelectorAll('.item-checkbox').forEach(checkbox => {
        checkbox.addEventListener('change', () => {
            // 모든 상품이 선택되었는지 확인하여 전체 선택 체크박스 상태 변경
            const allCheckboxes = document.querySelectorAll('.item-checkbox');
            const selectAllCheckbox = document.getElementById('selectAll');
            selectAllCheckbox.checked = Array.from(allCheckboxes).every(cb => cb.checked);

            calculateSelectedPrice();
        });
    });
});