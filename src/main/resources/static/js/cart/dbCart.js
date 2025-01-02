// DB 아이템 삭제
async function removeCart(cartId) {
    if (!isLoggedIn) return;

    try {
        console.log('삭제 요청 시작:', cartId); // 디버깅용
        const response = await fetch(`/api/carts/${cartId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]')?.content
            },
            credentials: 'same-origin'
        });

        console.log('응답 상태:', response.status); // 디버깅용
        if (!response.ok) throw new Error(`삭제 실패: ${response.status}`);
        location.reload();
    } catch (error) {
        console.error('삭제 실패:', error);
        throw error;
    }
}