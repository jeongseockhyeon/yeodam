const periods = ["반나절", "하루", "2일", "3일", "4~7일", "7일"];
const regions = [
    "서울", "인천", "대전", "대구", "울산", "부산", "광주", "세종",
    "경기북부", "경기남부", "강원도", "충청북도", "충청남도",
    "경상북도", "경상남도", "전라북도", "전라남도", "제주"
];

let currentStep = 0;
const steps = ["step-region", "step-theme", "step-period"];

function nextStep() {
    if (currentStep < steps.length - 1) {
        document.getElementById(steps[currentStep]).classList.remove("active");
        currentStep++;
        document.getElementById(steps[currentStep]).classList.add("active");
    }
}
function prevStep() {
    if (currentStep > 0) {
        document.getElementById(steps[currentStep]).classList.remove("active");
        currentStep--;
        document.getElementById(steps[currentStep]).classList.add("active");
    }
}

function submitSurvey() {
    alert("설문 완료!");
}

function renderOptions(options, containerId) {
    const container = document.getElementById(containerId);
    options.forEach(option => {
        const buttonElement = document.createElement("button");
        buttonElement.className = "option";
        buttonElement.textContent = option;
        container.appendChild(buttonElement);
    });
}

renderOptions(regions, "region-options");
renderOptions(periods, "period-options");

fetch("/api/categories")
    .then(response => response.json())
    .then(categories => {
        const themeNames = categories.map(category => category.name);
        renderOptions(themeNames, "theme-options");
    })
    .catch(error => console.error("카테고리 불러오기 실패:", error));
