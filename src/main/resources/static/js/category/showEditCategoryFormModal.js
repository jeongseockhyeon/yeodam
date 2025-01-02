// 수정 버튼 클릭 이벤트 처리
function handleEditCategory(categoryId) {
    // 카테고리 정보를 불러오기
    fetch(`/api/categories/${categoryId}`)
        .then((response) => response.json())
        .then((category) => {
            // 모달에 카테고리 정보 삽입
            document.getElementById('editCategoryId').value = category.id;
            document.getElementById('editCategoryName').value = category.name;

            // 모달 창 띄우기
            const editModal = new bootstrap.Modal(document.getElementById('editCategoryModal'));
            editModal.show();
        })
        .catch((error) => console.error('Error fetching category:', error));
}