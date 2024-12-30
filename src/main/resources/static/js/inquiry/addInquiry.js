function openModal(modalId) {
    document.getElementById(modalId).style.display = 'block';
}

function closeModal(modalId) {
    document.getElementById(modalId).style.display = 'none';
}

// 문의 전체 보기 클릭
document.getElementById('inquiryAllButton').addEventListener('click', () => {
    openModal('inquiryAllModal');
});

// 문의하기 클릭
document.getElementById('inquiryButton').addEventListener('click', () => {
    openModal('inquiryModal');
});

// 문의 제출 기능
async function submitInquiry() {
    const title = document.getElementById('inquiryTitle').value;
    const content = document.getElementById('inquiryContent').value;
    const payload = { itemId: id, title, content, parentId: null };

    try {
        const response = await fetch('/inquiries/add', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(payload),
        });

        if (response.ok) {
            alert('문의가 성공적으로 등록되었습니다.');
            closeModal('inquiryModal');
        } else {
            const errorData = await response.json();
            alert(`문의 등록 실패: ${errorData.message}`);
        }
    } catch (error) {
        console.error('문의 등록 중 에러 발생:', error);
    }
}

// 문의 제출 버튼 클릭 이벤트
document.getElementById('submitInquiryButton').addEventListener('click', submitInquiry);