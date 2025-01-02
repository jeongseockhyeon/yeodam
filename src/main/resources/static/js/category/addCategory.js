// 카테고리 생성 이벤트
document.getElementById('categoryForm').addEventListener('submit', function (event) {
    event.preventDefault(); // 폼 기본 동작 방지

    const categoryName = document.getElementById('categoryName').value.trim();

    if (!categoryName) {
        alert('카테고리 이름을 입력하세요!');
        return;
    }

    // 카테고리 생성 API 요청
    fetch('/api/categories', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ categoryName: categoryName }),
    })
        .then(response => {
            if (response.ok) {
                alert('카테고리가 성공적으로 생성되었습니다.');
                location.reload(); // 새로고침
            } else {
                return response.json().then(err => {
                    throw new Error(err.message || '카테고리 생성 실패');
                });
            }
        })
        .catch(error => {
            document.getElementById('responseMessage').innerHTML =
                `<div class="alert alert-danger">${error.message}</div>`;
        });
});