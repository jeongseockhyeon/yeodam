document.addEventListener("categoriesLoaded", () => {
    // 카테고리 체크박스 가져오기
    const categoryCheckboxes = document.querySelectorAll('input[name="categories"]');

    categoryCheckboxes.forEach(checkbox => {
        checkbox.addEventListener("change", () => {
            // 선택된 체크박스 개수 계산
            const selectedCount = Array.from(categoryCheckboxes).filter(cb => cb.checked).length;

            if (selectedCount > 3) {
                alert("최대 3개의 카테고리만 선택할 수 있습니다.");
                checkbox.checked = false; // 선택 취소
            }
        });
    });
});