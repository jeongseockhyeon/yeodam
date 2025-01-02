const id = getTourItemIdFromUrl();

async function fetchTourData() {
    try {
        const response = await fetch(`/api/tours/${id}`);
        if (!response.ok) {
            throw new Error('데이터를 가져오는데 실패했습니다.');
        }
        const data = await response.json();
        renderTourData(data);
        initializeCalendar(data)
    } catch (error) {
        console.error(error.message);
    }
}

function renderTourData(data) {
    document.getElementById('tourName').textContent = data.tourName;
    document.getElementById('mainImage').src = data.itemImgResDtoList[0]?.imgUrl || "/images/default-thumbnail.jpg";
    document.getElementById('additionalImage').style.display = data.itemImgResDtoList.length > 1 ? 'block' : 'none';
    if (data.itemImgResDtoList[1]) {
        document.getElementById('additionalImage').src = data.itemImgResDtoList[1].imgUrl;
    }

    document.getElementById('guideName').textContent = data.guideInTourResDtos.length > 0 ? data.guideInTourResDtos[0].name : '없음';
    document.getElementById('tourPeriod').textContent = data.tourPeriod;
    document.getElementById('maximum').textContent = data.maximum;

    const categoryContainer = document.getElementById('categories');
    categoryContainer.textContent = data.categoryResDtoList.map(category => category.name).join(', ');

    document.getElementById('tourDesc').textContent = data.tourDesc;
    document.getElementById('tourPrice').textContent = `가격: ${data.tourPrice}원`;

    const guideList = document.getElementById('guideList');
    guideList.innerHTML = ''; // 기존 가이드를 초기화
    data.guideInTourResDtos.forEach(guide => {
        const listItem = document.createElement('li');
        const button = document.createElement('button');
        button.textContent = guide.name;
        button.setAttribute('data-name', guide.name);  // 가이드 이름
        button.setAttribute('data-id', guide.id);  // 가이드 ID
        button.classList.add('guide-option');
        button.style.cssText = 'display: block; width: 100%; text-align: left; padding: 5px;';
        button.onclick = () => selectGuide(button);  // 클릭 시 selectGuide 호출
        listItem.appendChild(button);
        guideList.appendChild(listItem);
    });
}

function toggleGuideDropdown() {
    const dropdown = document.getElementById('guideDropdown');
    dropdown.style.display = dropdown.style.display === 'none' ? 'block' : 'none';
}
function selectGuide(button) {
    const guideId = button.getAttribute('data-id'); // 클릭한 버튼의 가이드 ID
    const guideName = button.getAttribute('data-name'); // 클릭한 버튼의 가이드 이름

    if (!guideId || !guideName) {
        alert('유효하지 않은 가이드 정보입니다.');
        return;
    }

    // 선택된 가이드 표시 및 데이터 설정
    const selectedGuideElement = document.getElementById('selectedGuide');
    selectedGuideElement.innerText = `선택된 가이드: ${guideName}`;
    selectedGuideElement.setAttribute('data-guide-id', guideId); // 선택된 가이드 ID 설정

    // 예약 일정 불러오기
    fetchAndDisplayReservations(guideId); // 해당 가이드의 예약 일정을 가져와서 달력에 표시

    // 선택된 버튼 스타일 업데이트
    document.querySelectorAll('.guide-option').forEach(btn => {
        btn.classList.remove('selected-guide'); // 기존 선택 스타일 제거
    });
    button.classList.add('selected-guide'); // 선택된 가이드 스타일 적용
}

// 데이터 가져오기 실행
fetchTourData();

function getTourItemIdFromUrl() {
    const pathSegments = window.location.pathname.split('/');
    return pathSegments[2];
}

function formatLocalDate(date) {
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0'); // 월은 0부터 시작하므로 +1 필요
    const day = date.getDate().toString().padStart(2, '0'); // 1자리 숫자일 경우 0으로 채움
    return `${year}-${month}-${day}`;
}
let calendar;

function fetchAndDisplayReservations(guideId) {
    // 예약 데이터를 가져오기
    fetch(`/api/tours/${guideId}/reservation`)
        .then(response => response.json())
        .then(reservations => {
            const events = reservations.map(reservation => ({
                title: '예약 불가',
                start: reservation.startDate,
                end: new Date(new Date(reservation.endDate).getTime() + 24 * 60 * 60 * 1000), // FullCalendar는 end 날짜를 포함하지 않으므로 하루 추가
                backgroundColor: '#dc3545',
                borderColor: '#dc3545',
                rendering: 'background', // 배경으로 표시
                overlap: false, // 다른 이벤트와 겹치지 않음
            }));

            // 기존 이벤트 제거
            calendar.getEvents().forEach(event => event.remove());

            // 새 이벤트 추가
            events.forEach(event => calendar.addEvent(event));
        })
        .catch(error => console.error('예약 데이터를 가져오는 중 오류 발생:', error));
}

function initializeCalendar(data) {
    const calendarEl = document.getElementById('calendar');
    calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        locale: 'ko',
        selectable: true,
        dateClick: function (info) {
            const selectedStartDate = new Date(info.date);
            const days = parseInt(data.tourPeriod.replace("일", "").trim());
            const selectedEndDate = new Date(selectedStartDate);
            selectedEndDate.setDate(selectedStartDate.getDate() + days - 1);

            // 예약 선택 가능 여부 확인
            const isUnavailable = calendar.getEvents().some(event =>
                event.rendering === 'background' &&
                event.start <= selectedEndDate &&
                event.end >= selectedStartDate
            );
            if (isUnavailable) {
                alert('선택한 날짜에 예약이 불가능합니다.');
                return;
            }

            // 기존 예약 선택 제거
            calendar.getEvents().forEach(event => {
                if (event.title === '예약 선택') event.remove();
            });

            // 새로운 예약 선택 추가
            calendar.addEvent({
                title: '예약 선택',
                start: formatLocalDate(selectedStartDate),
                end: formatLocalDate(new Date(selectedEndDate.getTime() + 24 * 60 * 60 * 1000)),
                backgroundColor: '#28a745',
                borderColor: '#28a745',
            });
        },
        events: [], // 초기 이벤트 비워둠
    });
    calendar.render();
}

