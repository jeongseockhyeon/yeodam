function toggleWishlist() {
    if (!isLoggedIn) {
        window.location.href = '/members/login';
        return;
    }

    const itemId = window.location.pathname.split('/tours/')[1];
    const heartIcon = document.getElementById('heartIcon');
    const isCurrentlyWished = heartIcon.classList.contains('bi-heart-fill');

    // 현재 찜 상태에 따라 요청 메소드 결정
    const method = isCurrentlyWished ? 'DELETE' : 'POST';

    fetch(`/api/wishes/${itemId}`, {
        method: method,
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(data => {
            if (data.code === 'SUCCESS') {
                // 아이콘 상태 토글
                if (isCurrentlyWished) {
                    heartIcon.classList.remove('bi-heart-fill');
                    heartIcon.classList.add('bi-heart');
                } else {
                    heartIcon.classList.remove('bi-heart');
                    heartIcon.classList.add('bi-heart-fill');
                }
            }
        })
        .catch(error => console.error('Error:', error));
}

// 페이지 로드 시 찜 상태 체크
window.addEventListener('load', () => {
    if (isLoggedIn) {
        const itemId = window.location.pathname.split('/tours/')[1];
        fetch(`/api/wishes/check/${itemId}`)
            .then(response => response.json())
            .then(data => {
                const heartIcon = document.getElementById('heartIcon');
                const isWished = data.message === 'true';
                heartIcon.classList.add(isWished ? 'bi-heart-fill' : 'bi-heart');
            })
            .catch(error => console.error('Error:', error));
    } else {
        const heartIcon = document.getElementById('heartIcon');
        heartIcon.classList.add('bi-heart');
    }
});