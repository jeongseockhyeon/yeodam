function showTab(tabName) {
    document.querySelectorAll('.tab').forEach(tab => tab.classList.remove('active-tab'));
    document.querySelector(`#${tabName}`).classList.add('active-tab');
    document.querySelectorAll('.menu-button').forEach(btn => btn.classList.remove('active'));
    document.querySelector(`[data-tab=${tabName}]`).classList.add('active');
}