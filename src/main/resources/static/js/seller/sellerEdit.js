const passwordInput = document.getElementById("password");
const confirmPasswordInput = document.getElementById("confirmPassword");
const passwordValidationResult = document.getElementById("passwordValidationResult");
const updateButton = document.getElementById("updateButton");

let isPasswordValid = true;

// 비밀번호 확인 검사
confirmPasswordInput.addEventListener("input", function () {
    const password = passwordInput.value;
    const confirmPassword = confirmPasswordInput.value;

    if (password === confirmPassword) {
        passwordValidationResult.textContent = "비밀번호가 일치합니다.";
        passwordValidationResult.className = "success";
        isPasswordValid = true;
    } else {
        passwordValidationResult.textContent = "비밀번호가 일치하지 않습니다.";
        passwordValidationResult.className = "error";
        isPasswordValid = false;
    }

    updateUpdateButtonState();
});

// 수정 폼 제출 처리
document.querySelector("#sellerEditForm").addEventListener("submit", function (event) {
    event.preventDefault(); // 기본 제출 동작 방지

    if (!isPasswordValid) {
        alert("비밀번호가 일치하지 않습니다. 다시 확인해주세요.");
        return;
    }

    const formData = new FormData(this);
    const jsonData = {};
    formData.forEach((value, key) => {
        jsonData[key] = value;
    });

    const companyId = document.getElementById('companyId').value;
    console.log(companyId);
    fetch(`/api/sellers/edit/${companyId}`, {
        method: 'PUT',
        headers: {
            "Content-Type": 'application/json',
        },
        body: JSON.stringify(jsonData),
    })
    .then(response => {
        if (response.ok) {
            alert('수정이 완료되었습니다!');
            window.location.href = '/sellers/myPage';
        } else {
            alert('수정 중 오류가 발생했습니다.');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert("오류가 발생했습니다. 다시 시도해주세요.");
    });
});

// 조건 미충족 시 수정 버튼 비활성화
function updateUpdateButtonState() {
    updateButton.disabled = !isPasswordValid;
}

const checkCancel = () => {
    if (window.confirm("파트너 정보 수정을 취소하시겠습니까?")) {
        location.href='/sellers/myPage';
    }
}