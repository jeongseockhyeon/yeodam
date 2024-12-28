const activeBtn = document.getElementById('activeBtn')
const id = getTourItemIdFromUrl();
// 버튼 상태 업데이트 함수
function updateButton(isActive) {
    if (isActive) {
        activeBtn.textContent = '비활성화'; // 활성화 상태 -> 비활성화로 변경 가능
        activeBtn.classList.add('active');
        activeBtn.classList.remove('inactive');
    } else {
        activeBtn.textContent = '활성화'; // 비활성화 상태 -> 활성화로 변경 가능
        activeBtn.classList.add('inactive');
        activeBtn.classList.remove('active');
    }
}

// 활성화 상태 변경 요청
async function toggleActiveStatus() {
    try {
        const currentActive = activeBtn.textContent === '비활성화'; // 현재 상태 확인
        const response = await fetch(`/api/items/${id}/active`, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ active: !currentActive }), // 반대 상태로 변경 요청
        });

        if (!response.ok) throw new Error('Failed to update active status');

        const updatedActive = await response.json(); // 서버에서 반환된 boolean 값
        updateButton(updatedActive); // 버튼 상태 업데이트
        alert(`상품 상태가 ${updatedActive ? '활성화' : '비활성화'}되었습니다.`);
    } catch (error) {
        console.error(error);
        alert('상태 변경에 실패했습니다.');
    }
}

// 초기 로딩 및 이벤트 리스너
activeBtn.addEventListener('click', toggleActiveStatus);