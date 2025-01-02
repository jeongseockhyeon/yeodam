const submitBtn = document.getElementById("submitButton");
let isEmailValid = false;
let isPasswordValid = false;
let isRePasswordValid = false;
let isNicknameValid = false;

const checkEmailResult = document.getElementById("email-check-result");
const checkPwdResult = document.getElementById("password-check-result");
const checkNicknameResult = document.getElementById("nickname-check-result");

const emailCheck = () => {
    const email = document.getElementById("userEmail").value;

    if (email === "") {
        checkEmailResult.innerHTML = "이메일을 입력해주세요";
        checkEmailResult.className = "error-message";
        document.getElementById("userEmail").focus();
        isEmailValid = false;
        updateSubmitBtn();
        return false;
    }

    const emailRegex = /^[a-zA-Z0-9]+@[a-zA-Z0-9]+(\.[a-z]+)+$/;

    if (!emailRegex.test(email)) {
        checkEmailResult.innerHTML = "유효한 이메일 형식이 아닙니다";
        checkEmailResult.className = "error-message";
        isEmailValid = false;
        updateSubmitBtn();
        return false;
    }

    fetch("/api/auth/email-check", {
        method: "POST",
        body: email,
    })
        .then(response => {
            if (response.status === 400) {
                checkEmailResult.innerHTML = "이미 사용 중입니다";
                checkEmailResult.className = "error-message";
                isEmailValid = false;
                updateSubmitBtn();
            } else if (response.ok) {
                checkEmailResult.innerHTML = "사용 가능한 이메일입니다";
                checkEmailResult.className = "success-message";
                isEmailValid = true;
                updateSubmitBtn();
            } else {
                throw new Error("HTTP error " + response.status);
            }
        })
        .catch(error => {
            alert(`이메일 중복 체크 중 오류가 발생했습니다 ${error}`);
        })
    ;
};

const passwordCheck = () => {
    const password = document.getElementById("password").value;

    if(password === "") {
        checkPwdResult.innerHTML = "비밀번호를 입력해주세요";
        checkPwdResult.className = "error-message";
        isPasswordValid = false;
        updateSubmitBtn();
        return false;
    }

    const passwordRegex = /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*\W)(?!.* ).{8,16}$/;
    if (passwordRegex.test(password)) {
        checkPwdResult.innerHTML = "사용가능한 비밀번호입니다";
        checkPwdResult.className = "success-message";
        isPasswordValid = true;
        updateSubmitBtn();
    } else {
        checkPwdResult.innerHTML = "영대소문자, 특수문자, 숫자를 포함해 입력해주세요 (8~16자)";
        checkPwdResult.className = "error-message";
        isPasswordValid = false;
        updateSubmitBtn();
    }
}

const rePasswordCheck = () => {

    const rePassword = document.getElementById("rePassword").value;
    const rePwdCheckResult = document.getElementById("rePassword-check-result");

    if (rePassword === "") {
        rePwdCheckResult.innerHTML = "비밀번호를 입력해주세요";
        rePwdCheckResult.className = "error-message";
        isRePasswordValid = false;
        updateSubmitBtn();
        return false;
    }

    if (rePassword === document.getElementById("password").value) {
        rePwdCheckResult.innerHTML = "비밀번호가 일치합니다";
        rePwdCheckResult.className = "success-message";
        isRePasswordValid = true;
        updateSubmitBtn();
    } else {
        rePwdCheckResult.innerHTML = "비밀번호가 틀렸습니다";
        rePwdCheckResult.className = "error-message";
        isRePasswordValid = false;
        updateSubmitBtn();
    }
}

const nicknameCheck = () => {
    const nickname = document.getElementById("nickname").value;

    if (nickname === "") {
        checkNicknameResult.innerHTML = "닉네임을 입력해주세요";
        checkNicknameResult.className = "error-message";
        isNicknameValid = false;
        updateSubmitBtn();
        document.getElementById("nickname").focus();
        return false;
    }

    fetch("/api/users/nickname-check", {
        method: "POST",
        body: nickname,
    })
        .then(response => {
            if (response.status === 400) {
                checkNicknameResult.innerHTML = "이미 사용 중입니다";
                checkNicknameResult.className = "error-message";
                isNicknameValid = false;
                updateSubmitBtn();
            } else if (response.ok) {
                checkNicknameResult.innerHTML = "사용 가능한 닉네임입니다";
                checkNicknameResult.className = "success-message";
                isNicknameValid = true;
                updateSubmitBtn();
            } else {
                throw new Error("HTTP error " + response.status);
            }
        })
        .catch(error => {
            alert(`닉네임 중복 체크 중 오류가 발생했습니다 ${error}`);
        })
    ;
}