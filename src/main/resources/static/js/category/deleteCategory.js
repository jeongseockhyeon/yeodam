// 삭제 버튼 클릭 이벤트 처리
function handleDeleteCategory(categoryId, categoryName) {
    if (confirm(`카테고리 ID ${categoryId} (${categoryName})를 삭제하시겠습니까?`)) {
        // 삭제 API 호출 (예: DELETE 요청)
        fetch(`/api/categories/${categoryId}`, { method: 'DELETE' })
            .then((response) => {
                if (response.ok) {
                    alert('카테고리가 삭제되었습니다.');
                    fetchCategories(); // 목록 갱신
                } else {
                    alert('삭제에 실패했습니다.');
                }
            })
            .catch((error) => console.error('Error:', error));
    }
}

// 페이지 로드 시 카테고리 목록 불러오기
document.addEventListener('DOMContentLoaded', fetchCategories);