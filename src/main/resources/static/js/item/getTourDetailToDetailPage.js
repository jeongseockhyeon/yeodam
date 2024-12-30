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
    document.getElementById('mainImage').src = data.itemImgResDtoList[0]?.imgUrl || '';
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
        button.setAttribute('data-name', guide.name);
        button.setAttribute('data-id', guide.id);
        button.classList.add('guide-option');
        button.style.cssText = 'display: block; width: 100%; text-align: left; padding: 5px;';
        button.onclick = () => selectGuide(button);
        listItem.appendChild(button);
        guideList.appendChild(listItem);
    });
}

function toggleGuideDropdown() {
    const dropdown = document.getElementById('guideDropdown');
    dropdown.style.display = dropdown.style.display === 'none' ? 'block' : 'none';
}

function selectGuide(button) {
    const selectedGuide = document.getElementById('selectedGuide');
    selectedGuide.textContent = `선택된 가이드: ${button.dataset.name}`;

    selectedGuide.setAttribute('data-id', button.dataset.id);
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

function initializeCalendar(data) {
    const calendarEl = document.getElementById('calendar');
    calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        locale: 'ko',
        selectable: true,
        dateClick: function (info) {
            // 클릭한 날짜 기준으로 선택 기간 설정
            const selectedStartDate = new Date(info.date);
            const days = parseInt(data.tourPeriod.replace("일", "").trim());
            const selectedEndDate = new Date(selectedStartDate);

            // 날짜 계산 시, 'days' 만큼 추가
            selectedEndDate.setDate(selectedStartDate.getDate() + days - 1); // 기간 끝 날짜를 정확하게 설정

            // 이미 존재하는 "예약 선택" 이벤트 제거
            calendar.getEvents().forEach(event => {
                if (event.title === '예약 선택') {
                    event.remove();
                }
            });

            // 로컬 시간대 기준으로 날짜를 포맷팅
            const startDateString = formatLocalDate(selectedStartDate);  // "2024-12-20"
            const endDateString = formatLocalDate(new Date(selectedEndDate.getTime() + 24 * 60 * 60 * 1000));  // "2024-12-21"

            // 새로운 "예약 선택" 이벤트 추가
            calendar.addEvent({
                title: '예약 선택',
                start: startDateString,
                end: endDateString, // FullCalendar는 end 날짜를 포함하지 않음
                backgroundColor: '#28a745',
                borderColor: '#28a745',
            });
        },
        events: [],
    });
    calendar.render();
}

