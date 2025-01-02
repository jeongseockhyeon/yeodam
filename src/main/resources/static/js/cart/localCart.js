//비로그인 사용자 로컬 스토리지 데이터 가져오기
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

// 로컬 스토리지 상품 삭제
function removeLocalItem(itemId) {
    const cartItems = getLocalCartItems();
    const updatedItems = cartItems.filter(item => item.itemId !== itemId);
    localStorage.setItem('cartItems', JSON.stringify(updatedItems));
    renderLocalCart(updatedItems);
    calculateSelectedPrice();
    updateSelectedCount();
}






