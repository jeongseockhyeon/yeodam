const periods = ["1일", "2일", "3일", "4일","5일","6일","7일"]
const regions = [
    "서울", "인천", "대전", "대구", "울산", "부산", "광주", "세종",
    "경기북부", "경기남부", "강원도", "충청북도", "충청남도",
    "경상북도", "경상남도", "전라북도", "전라남도", "제주"
];

let selectedRegion = null; // 지역 단일 선택
let selectedPeriod = null; // 기간 단일 선택
let selectedThemes = []; // 테마 다중 선택

const MAX_SELECTIONS = 3;

// 로컬스토리지 초기화 함수
function clearLocalStorageForSelections() {
    localStorage.removeItem("selectedRegion");
    localStorage.removeItem("selectedPeriod");
    localStorage.removeItem("selectedThemes");
}

// 선택값 처리 함수 (단일 선택)
function toggleSelectionSingle(option, currentSelection, list, containerId) {
    const container = document.getElementById(containerId);

    if (currentSelection === option) {
        // 이미 선택된 값 클릭 시 선택 해제
        currentSelection = null;
    } else {
        // 새 값 선택
        currentSelection = option;
    }

    // 버튼 시각적 갱신
    Array.from(container.children).forEach((button) => {
        button.classList.toggle("selected", button.textContent === currentSelection);
    });

    return currentSelection;
}

// 선택값 처리 함수 (다중 선택)
function toggleSelectionMultiple(option, list, buttonElement) {
    if (list.length >= MAX_SELECTIONS && !list.includes(option)) {
        alert(`최대 ${MAX_SELECTIONS}개까지만 선택할 수 있습니다.`);
        return; // 최대 선택 수를 초과하면 선택을 추가하지 않음
    }

    const index = list.indexOf(option);
    if (index > -1) {
        list.splice(index, 1); // 이미 선택된 값 제거
    } else {
        list.push(option); // 선택되지 않은 값 추가
    }
    buttonElement.classList.toggle("selected");
}

// 단계 이동 함수
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

// 옵션 렌더링
function renderOptions(options, containerId, list, singleSelect = false) {
    const container = document.getElementById(containerId);
    options.forEach(option => {
        const buttonElement = document.createElement("button");
        buttonElement.className = "option";
        buttonElement.textContent = option;

        buttonElement.onclick = () => {
            if (singleSelect) {
                if (containerId === "region-options") {
                    selectedRegion = toggleSelectionSingle(option, selectedRegion, list, containerId);
                } else if (containerId === "period-options") {
                    selectedPeriod = toggleSelectionSingle(option, selectedPeriod, list, containerId);
                }
            } else {
                toggleSelectionMultiple(option, list, buttonElement);
            }
        };

        // 선택된 값이 있으면 미리 표시
        if (singleSelect && option === selectedRegion) {
            buttonElement.classList.add("selected");
        }
        if (!singleSelect && list.includes(option)) {
            buttonElement.classList.add("selected");
        }

        container.appendChild(buttonElement);
    });
}

// 로컬 스토리지에서 값 가져오기
function loadSelectionsFromLocalStorage() {
    selectedRegion = localStorage.getItem("selectedRegion");
    selectedPeriod = localStorage.getItem("selectedPeriod");
    selectedThemes = localStorage.getItem("selectedThemes") ? localStorage.getItem("selectedThemes").split(",") : [];

    // 이미 저장된 값에 따라 버튼 상태 업데이트
    renderOptions(regions, "region-options", selectedRegion, true); // 지역: 단일 선택
    renderOptions(periods, "period-options", selectedPeriod, true); // 기간: 단일 선택

    fetch("/api/categories")
        .then(response => response.json())
        .then(categories => {
            const themeNames = categories.map(category => category.name);
            renderOptions(themeNames, "theme-options", selectedThemes, false); // 테마: 다중 선택
        })
        .catch(error => console.error("카테고리 불러오기 실패:", error));
}

// 선택된 값으로 Query String 구성 후 이동
function goToTourList() {
    const regionParam = selectedRegion || "";
    const themeParam = selectedThemes.join(",");
    const periodParam = selectedPeriod || "";

    // 선택된 데이터를 localStorage에 저장
    localStorage.setItem("selectedRegion", regionParam);
    localStorage.setItem("selectedThemes", themeParam);
    localStorage.setItem("selectedPeriod", periodParam);

    // 페이지 이동
    window.location.href = "/tours";
}

// 페이지 로드 시 로컬 스토리지 값으로 상태 초기화
document.addEventListener("DOMContentLoaded",()=> {
    clearLocalStorageForSelections()
    loadSelectionsFromLocalStorage();
});