// Fetch API를 사용해 카테고리 목록 조회
async function fetchCategories() {
    try {
        const response = await fetch('/api/categories');
        if (!response.ok) {
            throw new Error('카테고리 데이터를 불러오는 데 실패했습니다.');
        }

        const categories = await response.json(); // JSON 데이터를 파싱
        renderCategories(categories);
    } catch (error) {
        console.error('Error:', error);
    }
}

// 카테고리 렌더링
function renderCategories(categories) {
    const tableBody = document.getElementById('categoryTableBody');
    tableBody.innerHTML = ''; // 기존 내용을 초기화

    categories.forEach((category) => {
        const row = document.createElement('tr');

        // 카테고리 ID
        const idCell = document.createElement('td');
        idCell.textContent = category.id;

        // 카테고리 이름
        const nameCell = document.createElement('td');
        nameCell.textContent = category.name;

        // 수정 버튼
        const editCell = document.createElement('td');
        const editButton = document.createElement('button');
        editButton.textContent = '수정';
        editButton.className = 'btn btn-warning btn-sm';
        editButton.onclick = () => handleEditCategory(category.id);
        editCell.appendChild(editButton);

        // 삭제 버튼
        const deleteCell = document.createElement('td');
        const deleteButton = document.createElement('button');
        deleteButton.textContent = '삭제';
        deleteButton.className = 'btn btn-danger btn-sm';
        deleteButton.onclick = () => handleDeleteCategory(category.id, category.name); // 이름 전달
        deleteCell.appendChild(deleteButton);

        // 행에 셀 추가
        row.appendChild(idCell);
        row.appendChild(nameCell);
        row.appendChild(editCell);
        row.appendChild(deleteCell);

        // 테이블에 행 추가
        tableBody.appendChild(row);
    });
}