const periods = ["반나절", "1일", "2일", "3일", "4~7일", "7일 이상"];
const regions = [
    "서울", "인천", "대전", "대구", "울산", "부산",
    "광주", "세종", "경기북부", "경기남부", "강원도",
    "충청북도", "충청남도", "경상북도", "경상남도",
    "전라북도", "전라남도", "제주"
];

function populateDropdown(id, options) {
    const dropdown = document.getElementById(id);
    options.forEach(option => {
        const opt = document.createElement("option");
        opt.value = option;
        opt.textContent = option;
        dropdown.appendChild(opt);
    });
}

document.addEventListener("DOMContentLoaded", () => {
    populateDropdown("period", periods);
    populateDropdown("region", regions);
});