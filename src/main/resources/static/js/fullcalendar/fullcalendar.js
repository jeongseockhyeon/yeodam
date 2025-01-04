function initializeCalendar(tourData) {
    console.log('캘린더 초기화 시작', tourData); //캘린더 초기화 확인 - Cart

    const calendarEl = document.getElementById('calendar');
    if (!calendarEl) {
        console.error('calendar element를 찾을 수 없습니다');
        return;
    }

    // 이전 선택이 남은 경우 저장된 날짜 정보 삭제
    localStorage.removeItem('selectedStartDate');
    localStorage.removeItem('selectedEndDate');

    calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        locale: 'ko',
        selectable: true,
        select: function(info) {
            console.log('날짜 선택됨:', info);

            const startDateStr = info.startStr;
            const isHalfDay = tourData.tourPeriod.includes('반나절');
            // 투어 기간에 따른 종료일 계산
            let endDateStr;
            if (isHalfDay) {
                endDateStr = startDateStr; // 반나절은 날짜 동일
            } else {
                const days = parseInt(tourData.tourPeriod.replace("일", "").trim());
                const endDate = new Date(startDateStr);
                endDate.setDate(endDate.getDate() + days - 1);
                endDateStr = endDate.toISOString().split('T')[0];
            }

            // localStorage에 날짜 저장
            localStorage.setItem('selectedStartDate', startDateStr);
            localStorage.setItem('selectedEndDate', endDateStr);

            console.log('localStorage 저장 직후 확인:', {
                savedStart: localStorage.getItem('selectedStartDate'),
                savedEnd: localStorage.getItem('selectedEndDate')
            });

            // 기존 이벤트 제거
            calendar.getEvents().forEach(event => {
                if (event.title === '예약 선택') event.remove();
            });

            // 새 이벤트 추가
            calendar.addEvent({
                title: '예약 선택',
                start: startDateStr,
                end: isHalfDay ? startDateStr :
                    new Date(new Date(endDateStr).getTime() + 24 * 60 * 60 * 1000).toISOString().split('T')[0],
                backgroundColor: '#28a745',
                borderColor: '#28a745',
            });

            console.log('선택된 날짜가 저장되었습니다:', {
                startDate: startDateStr,
                endDate: endDateStr
            });
        },
        events: [],
    });

    calendar.render();
    console.log('캘린더 렌더링 완료');
}