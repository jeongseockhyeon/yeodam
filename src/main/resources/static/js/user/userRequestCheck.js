const submitBtn = document.getElementById("submitButton");
let isEmailValid = false;
let isPasswordValid = false;
let isRePasswordValid = false;
let isNicknameValid = false;

const emailCheck = () => {
    const email = document.getElementById("userEmail").value;
    const checkResult = document.getElementById("email-check-result");

    if (email === "") {
        checkResult.innerHTML = "이메일을 입력해주세요";
        checkResult.className = "error-message";
        document.getElementById("userEmail").focus();
        isEmailValid = false;
        updateSubmitBtn();
        return false;
    }

    const emailRegex = /^[a-zA-Z0-9]+@[a-zA-Z0-9]+(\.[a-z]+)+$/;

    if (!emailRegex.test(email)) {
        checkResult.innerHTML = "유효한 이메일 형식이 아닙니다";
        checkResult.className = "error-message";
        isEmailValid = false;
        updateSubmitBtn();
        return false;
    }

    fetch("/api/users/email-check", {
        method: "POST",
        body: email,
    })
        .then(response => {
            if (!response.ok) {
                throw new Error("HTTP error " + response.status)
            }
            return response.json();
        })
        .then(isDuplicated => {
            if (isDuplicated) {
                checkResult.innerHTML = "이미 사용 중입니다";
                checkResult.className = "error-message";
                isEmailValid = false;
                updateSubmitBtn();
            } else {
                checkResult.innerHTML = "사용 가능한 이메일입니다";
                checkResult.className = "success-message";
                isEmailValid = true;
                updateSubmitBtn();
            }
        })
        .catch(error => {
            alert("이메일 중복 체크 중 오류가 발생했습니다.");
            console.error("Error:", error);
        })
};

const passwordCheck = () => {
    const password = document.getElementById("password").value;
    const pwdCheckResult = document.getElementById("password-check-result");

    if(password === "") {
        pwdCheckResult.innerHTML = "비밀번호를 입력해주세요";
        pwdCheckResult.className = "error-message";
        isPasswordValid = false;
        updateSubmitBtn();
        return false;
    }

    const passwordRegex = /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*\W)(?!.* ).{8,16}$/;
    if (passwordRegex.test(password)) {
        pwdCheckResult.innerHTML = "사용가능한 비밀번호입니다";
        pwdCheckResult.className = "success-message";
        isPasswordValid = true;
        updateSubmitBtn();
    } else {
        pwdCheckResult.innerHTML = "영대소문자, 특수문자, 숫자를 포함해 입력해주세요 (8~16자)";
        pwdCheckResult.className = "error-message";
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
        updateJoinSubmitBtn();
        return false;
    }

    if (rePassword === document.getElementById("password").value) {
        rePwdCheckResult.innerHTML = "비밀번호가 일치합니다";
        rePwdCheckResult.className = "success-message";
        isRePasswordValid = true;
        updateJoinSubmitBtn();
    } else {
        rePwdCheckResult.innerHTML = "비밀번호가 틀렸습니다";
        rePwdCheckResult.className = "error-message";
        isRePasswordValid = false;
        updateJoinSubmitBtn();
    }
}

const nicknameCheck = () => {
    const nickname = document.getElementById("nickname").value;
    const checkResult = document.getElementById("nickname-check-result");

    if (nickname === "") {
        checkResult.innerHTML = "닉네임을 입력해주세요";
        checkResult.className = "error-message";
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
            if(!response.ok) {
                throw new Error("HTTP error " + response.status)
            }
            return response.json();
        })
        .then(isDuplicated => {
            if(isDuplicated) {
                checkResult.innerHTML = "이미 사용 중입니다";
                checkResult.className = "error-message";
                isNicknameValid = false;
                updateSubmitBtn();
            } else {
                checkResult.innerHTML = "사용 가능한 닉네임입니다";
                checkResult.className = "success-message";
                isNicknameValid = true;
                updateSubmitBtn();
            }
        })
        .catch(error => {
            alert("닉네임 중복 체크 중 오류가 발생했습니다.");
            console.error("Error:", error);
        })
}