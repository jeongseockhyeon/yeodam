function formatLocalDate(date) {
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0'); // 월은 0부터 시작하므로 +1 필요
    const day = date.getDate().toString().padStart(2, '0'); // 1자리 숫자일 경우 0으로 채움
    return `${year}-${month}-${day}`;
}
let calendar;
let selectedStartDate;
let selectedEndDate;
function fetchAndDisplayReservations(guideId) {
    fetch(`/api/tours/${guideId}/reservation`)
        .then(response => response.json())
        .then(reservations => {
            if (!reservations || reservations.length === 0) {
                console.warn('예약 데이터가 비어 있습니다.');
                return;
            }
            const events = reservations.map(reservation => ({
                title: '예약 불가',
                start: reservation.startDate,
                end: new Date(new Date(reservation.endDate).getTime()),
                backgroundColor: '#dc3545',
                borderColor: '#dc3545',
                rendering: 'background',
                overlap: false,
                allDay: true,
            }));
            // 이전 선택이 남은 경우 저장된 날짜 정보 삭제
            localStorage.removeItem('selectedStartDate');
            localStorage.removeItem('selectedEndDate');

            // 기존 이벤트 제거 및 새 이벤트 추가
            calendar.getEvents().forEach(event => event.remove());
            events.forEach(event => calendar.addEvent(event));
        })
        .catch(error => console.error('예약 데이터를 가져오는 중 오류 발생:', error));
}


// 달력 초기화 함수
function initializeCalendar(data) {
    const calendarEl = document.getElementById('calendar');
    const today = new Date();
    today.setHours(0, 0, 0, 0); // 시간을 00:00:00으로 설정하여 날짜만 비교

    // 현재 날짜 기준으로 2개월 전, 후 범위 설정
    const twoMonthsBefore = new Date(today);
    twoMonthsBefore.setMonth(today.getMonth() - 2);

    const twoMonthsAfter = new Date(today);
    twoMonthsAfter.setMonth(today.getMonth() + 2);





    calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        locale: 'ko',
        selectable: true,
        validRange: {
            start: formatLocalDate(twoMonthsBefore),  // 2개월 전부터
            end: formatLocalDate(twoMonthsAfter),     // 2개월 후까지
        },
        headerToolbar: {
            left: 'prev,next today',  // 이전, 다음, 오늘 버튼
            center: 'title',          // 월 제목
            right: '',
        },


        dateClick: function (info) {
            console.log(guideId)
            // 가이드 선택 여부 확인
            if (!guideId) {
                alert('가이드를 선택해주세요.');  // 가이드가 선택되지 않으면 알림
                return; // 가이드가 선택되지 않으면 날짜 클릭 이벤트 종료
            }

            const clickedDate = new Date(info.date);
            clickedDate.setHours(0, 0, 0, 0); // 시간을 00:00:00으로 설정하여 날짜만 비교

            // 과거 날짜 선택 차단
            if (clickedDate < today) {
                alert('과거 날짜는 선택할 수 없습니다.');
                return; // 클릭 이벤트 처리하지 않음
            }

            // 오늘 날짜 클릭 시 알림
            if (clickedDate.toDateString() === today.toDateString()) {
                alert('당일 예약은 불가능합니다.');
                return; // 클릭 이벤트 처리하지 않음
            }

            let markedStartDate = new Date(info.date);
            const days = parseInt(data.tourPeriod.replace("일", "").trim());
            let markedEndDate = new Date(markedStartDate);
            markedEndDate.setDate(markedStartDate.getDate() + days - 1)


            selectedStartDate = formatLocalDate(new Date(markedStartDate.getTime()));
            selectedEndDate = formatLocalDate(new Date(markedEndDate.getTime()));

            // localStorage에 날짜 저장
            localStorage.setItem('selectedStartDate', selectedStartDate);
            localStorage.setItem('selectedEndDate', selectedEndDate);


            console.log('localStorage 저장 직후 확인:', {
                savedStart: localStorage.getItem('selectedStartDate'),
                savedEnd: localStorage.getItem('selectedEndDate')
            });

            // 예약 불가 여부 확인 (title이 '예약 불가'인 경우에만 확인)
            const isUnavailable = calendar.getEvents().some(event => {
                if (event.title === '예약 불가') {
                    const eventStart = new Date(event.start);
                    const eventEnd = new Date(event.end);
                    return (
                        (selectedStartDate >= eventStart && selectedStartDate < eventEnd) || // 시작 날짜 겹침
                        (selectedEndDate >= eventStart && selectedEndDate < eventEnd) ||    // 종료 날짜 겹침
                        (selectedStartDate <= eventStart && selectedEndDate >= eventEnd)   // 전체 포함
                    );
                }
                return false;
            });

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
                start: formatLocalDate(markedStartDate),
                end: formatLocalDate(new Date(markedEndDate.getTime() + 24 * 60 * 60 * 1000)),
                backgroundColor: '#28a745',
                borderColor: '#28a745',
            });
        },
        events: [], // 초기 이벤트 비워둠
        dayCellDidMount: function(info) {
            // 과거 날짜를 회색으로 처리하고 클릭을 비활성화
            if (info.date < today) {
                info.el.style.backgroundColor = '#d6d6d6'; // 회색 배경
                info.el.style.pointerEvents = 'none'; // 클릭 비활성화
                info.el.style.opacity = '0.5'; // 시각적으로 클릭 불가능함을 강조
            }

            // 오늘 날짜를 황색으로 처리하고 클릭 비활성화
            if (info.date.toDateString() === today.toDateString()) {
                info.el.style.backgroundColor = '#ffcc00'; // 황색 배경
                info.el.style.pointerEvents = 'none'; // 클릭 비활성화
                info.el.style.opacity = '0.5'; // 시각적으로 클릭 불가능함을 강조
            }
        },
    });

    // 스타일 적용 (과거 날짜 회색 처리, 오늘 날짜 황색 처리)
    const style = document.createElement('style');
    style.innerHTML = `
        .fc-day {
            cursor: pointer;
        }
        .fc-day.fc-day-disabled {
            background-color: #d6d6d6 !important; /* 과거 날짜 회색 배경 */
            pointer-events: none !important;
            opacity: 0.5 !important;
        }
        .fc-day.fc-day-today {
            background-color: #ffcc00 !important; /* 오늘 날짜 황색 배경 */
            pointer-events: none !important;
            opacity: 0.5 !important;
        }
    `;
    document.head.appendChild(style);

    calendar.render();
}