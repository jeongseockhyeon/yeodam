function handleReserveButtonClick() {
    // 상품 이름 가져오기
    const tourName = document.getElementById('tourName').textContent; // 상품 이름

    // 선택된 가이드 ID 가져오기
    const selectedGuideId = document.getElementById('selectedGuide').getAttribute('data-id'); // 선택된 가이드의 ID

    // calendar 객체에서 getEvents() 호출
    const event = calendar.getEvents()[0];  // "예약 선택" 이벤트 가져오기
    const startDate = event ? event.startStr : '';  // 시작 날짜
    const endDate = event ? event.endStr : '';     // 종료 날짜

    // 가격 가져오기
    const priceText = document.getElementById('tourPrice').textContent;
    const tourPrice = priceText.replace(/[^0-9]/g, '');  // 정규식으로 숫자만 추출

    //최대 인원 가져오기
    const count = document.getElementById('maximum').textContent;

    // 상품 ID 가져오기
    const itemId = getTourItemIdFromUrl();

    // URL 구성 (상품 이름과 가이드 ID를 포함)
    const url = `/order-test?itemId=${itemId}&name=${encodeURIComponent(tourName)}&guideId=${encodeURIComponent(selectedGuideId)}&startDate=${startDate}&endDate=${endDate}&tourPrice=${tourPrice}&count=${count}`;

    // 예약하기 버튼 클릭 시 해당 URL로 이동
    window.location.href = url;
}

// 예약하기 버튼에 클릭 이벤트 등록
document.querySelector('.btn-primary').addEventListener('click', handleReserveButtonClick);