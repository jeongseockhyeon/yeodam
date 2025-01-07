document.addEventListener('DOMContentLoaded', function () {
    const travelLink = document.querySelector('.nav-link[href="/tours"]');
    if (travelLink) {
        travelLink.addEventListener('click', function () {
            // 삭제할 로컬 스토리지 키 목록
            const keysToRemove = ['filters', 'selectedRegion', 'selectedPeriod'];

            // 각 키 삭제
            keysToRemove.forEach(key => localStorage.removeItem(key));
        });
    }
});