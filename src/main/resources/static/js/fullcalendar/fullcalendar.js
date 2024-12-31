function initializeCalendar(tourData) {
    const calendarEl = document.getElementById('calendar');
    const calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        locale: 'ko',
        selectable: true,
        select: function (info) {
            const selectedStartDate = new Date(info.start);
            const days = parseInt(tourData.tourPeriod.replace("일", "").trim());
            const selectedEndDate = new Date(selectedStartDate);
            selectedEndDate.setDate(selectedStartDate.getDate() + days - 1);

            calendar.addEvent({
                title: '예약 선택',
                start: selectedStartDate.toISOString().split('T')[0],
                end: selectedEndDate.toISOString().split('T')[0],
                backgroundColor: '#28a745',
                borderColor: '#28a745',
            });
        },
        events: [],
    });
    calendar.render();
}