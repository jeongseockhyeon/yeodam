const checkNameResult = document.getElementById("name-check-result");
const checkPhoneResult = document.getElementById("phone-check-result");

const showErrorMessages = (errorMessages) => {
    if(errorMessages.email) {
        checkEmailResult.innerHTML = errorMessages.email;
        checkEmailResult.className = "error-message";
    }

    if(errorMessages.password) {
        checkPwdResult.innerHTML = errorMessages.password;
        checkPwdResult.className = "error-message";
    }

    if(errorMessages.name) {
        checkNameResult.style.display = "inline-block";
        checkNameResult.innerHTML = errorMessages.name;
        checkNameResult.className = "error-message";
    } else {
        checkNameResult.style.display = "none";
    }

    if(errorMessages.nickname) {
        checkNicknameResult.innerHTML = errorMessages.nickname;
        checkNicknameResult.className = "error-message";
    }

    if(errorMessages.phone) {
        checkPhoneResult.style.display = "inline-block";
        checkPhoneResult.innerHTML = errorMessages.phone;
        checkPhoneResult.className = "error-message";
    } else {
        checkPhoneResult.style.display = "none";
    }

    submitBtn.disabled = true;
}