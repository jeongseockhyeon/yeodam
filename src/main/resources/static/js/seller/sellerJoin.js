const emailInput = document.getElementById("email");
const emailValidationResult = document.getElementById("emailValidationResult");
const checkEmailBtn = document.getElementById("checkEmailBtn");

const passwordInput = document.getElementById("password");
const confirmPasswordInput = document.getElementById("confirmPassword");
const passwordValidationResult = document.getElementById("passwordValidationResult");

const joinButton = document.getElementById("joinButton");

let isEmailValid = false;
let isPasswordValid = false;

// 이메일 중복체크 및 유효성 검사
checkEmailBtn.addEventListener("click", function () {
    const email = emailInput.value;
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!emailRegex.test(email)) {
        emailValidationResult.textContent = "유효한 이메일 형식이 아닙니다.";
        emailValidationResult.className = "error";
        isEmailValid = false;
        updateJoinButtonState();
        return;
    }

    fetch(`/api/sellers/check-email?email=${email}`)
        .then(response => response.json())
        .then(isDuplicate => {
            if (isDuplicate) {
                emailValidationResult.textContent = "이미 사용 중인 이메일입니다.";
                emailValidationResult.className = "error";
                isEmailValid = false;
            } else {
                emailValidationResult.textContent = "사용 가능한 이메일입니다.";
                emailValidationResult.className = "success";
                isEmailValid = true;
            }
            updateJoinButtonState();
        })
        .catch(error => {
            emailValidationResult.textContent = "중복체크 중 오류가 발생했습니다.";
            emailValidationResult.className = "error";
            isEmailValid = false;
            updateJoinButtonState();
        });
});

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
    updateJoinButtonState();
});

// 가입 폼 제출 처리
document.getElementById("sellerJoinForm").addEventListener("submit", function (event) {
    event.preventDefault(); // 기본 제출 동작 방지

    const formData = new FormData(this);
    const jsonData = {};
    formData.forEach((value, key) => {
        jsonData[key] = value;
    });

    fetch("/api/sellers/join", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(jsonData),
    })
        .then(response => {
            if (response.ok) {
                alert("회원가입이 완료되었습니다.");
                window.location.href = "/login";
            } else {
                alert("가입에 실패했습니다. 다시 시도해주세요.");
            }
        })
        .catch(error => {
            alert("오류가 발생했습니다. 다시 시도해주세요.");
        });
});

// 조건 미충족 시 가입 버튼 비활성화
function updateJoinButtonState() {
    joinButton.disabled = !(isEmailValid && isPasswordValid);
}

const checkCancel = () => {
    if (window.confirm("회원가입을 취소하시겠습니까?")) {
        location.href='/';
    }
}