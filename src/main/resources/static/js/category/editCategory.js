// 수정된 카테고리 저장 처리
document.getElementById('saveCategoryButton').addEventListener('click', () => {
    const categoryId = document.getElementById('editCategoryId').value;
    const categoryName = document.getElementById('editCategoryName').value;

    // 수정 API 호출
    fetch(`/api/categories/${categoryId}`, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ categoryName: categoryName }),
    })
        .then((response) => {
            if (response.ok) {
                alert('카테고리가 수정되었습니다.');
                const editModal = bootstrap.Modal.getInstance(document.getElementById('editCategoryModal'));
                editModal.hide();
                fetchCategories(); // 목록 갱신
            } else {
                alert('수정에 실패했습니다.');
            }
        })
        .catch((error) => console.error('Error:', error));
});