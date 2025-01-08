function handleReserveButtonClick() {
    // 상품 이름 가져오기
    const tourName = document.getElementById('tourName').textContent; // 상품 이름

    // 선택된 가이드 ID 가져오기
    const selectedGuideId = document.getElementById('selectedGuide').getAttribute('data-guide-id'); // 선택된 가이드의 ID

    if (!selectedGuideId) {
        alert('가이드를 선택해주세요.');
        return;
    }

    // calendar 객체에서 getEvents() 호출
    const event = calendar.getEvents().find(event => event.title === '예약 선택'); // "예약 선택" 이벤트 가져오기
    if (!event) {
        alert('예약 날짜를 선택해주세요.');
        return;
    }

    // 시작 날짜와 종료 날짜를 간단한 형식으로 변환
    const startDate = selectedStartDate; // YYYY-MM-DD 형식
    const endDate = selectedEndDate; // YYYY-MM-DD 형식

    // 가격 가져오기
    const priceText = document.getElementById('tourPrice').textContent;
    const tourPrice = priceText.replace(/[^0-9]/g, ''); // 정규식으로 숫자만 추출

    // 최대 인원 가져오기
    const count = document.getElementById('maximum').textContent;

    // 상품 ID 가져오기
    const itemId = getTourItemIdFromUrl();

    // 예약하기 버튼 클릭 시 해당 URL로 이동
    window.location.href = `/order-test?itemId=${itemId}&name=${encodeURIComponent(tourName)}&guideId=${encodeURIComponent(selectedGuideId)}&startDate=${startDate}&endDate=${endDate}&tourPrice=${tourPrice}&count=${count}`;
}

// 예약하기 버튼에 클릭 이벤트 등록
document.querySelector('.btn-primary').addEventListener('click', handleReserveButtonClick);

