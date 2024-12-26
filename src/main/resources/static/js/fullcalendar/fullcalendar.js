document.addEventListener('DOMContentLoaded', function () {
    const calendarEl = document.getElementById('calendar');
    const calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        locale: 'ko',
        events: [
            // 서버에서 예약 불가능한 날짜를 전달받아 렌더링
            /* 샘플
            {
                title: '예약 불가',
                start: '2025-01-15',
                end: '2025-01-20',
                backgroundColor: '#ff0000',
                borderColor: '#ff0000',
            },
            */
        ],
    });
    calendar.render();
});