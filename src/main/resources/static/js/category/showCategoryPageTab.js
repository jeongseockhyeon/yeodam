// 탭 전환 이벤트
document.getElementById('categoryTab').addEventListener('shown.bs.tab', function (event) {
    const targetTab = event.target.getAttribute('data-bs-target');
    if (targetTab === '#list') {
        document.getElementById('create').classList.remove('show');
        document.getElementById('create').classList.remove('active');
    }
});