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
        checkNameResult.style.display = "inline-block";
        checkPhoneResult.innerHTML = errorMessages.phone;
        checkPhoneResult.className = "error-message";
    } else {
        checkPhoneResult.style.display = "none";
    }

    submitBtn.disabled = true;
}

submitBtn.addEventListener("click", () => {

    const joinFormData = new FormData();

    joinFormData.append("email", document.getElementById("userEmail").value);
    joinFormData.append("password", document.getElementById("password").value);
    joinFormData.append("name", document.getElementById("name").value);
    joinFormData.append("nickname", document.getElementById("nickname").value);
    joinFormData.append("phone", document.getElementById("phone").value);
    joinFormData.append("birthDate", document.getElementById("birthDate").value);
    joinFormData.append("gender", document.querySelector('input[name="gender"]:checked').value);

    fetch("/api/users", {
        method: "POST",
        body: joinFormData,
    })
        .then(response => {
            if (!response.ok) {
                return response.json().then(errorMessages => {
                    console.log(errorMessages);
                    showErrorMessages(errorMessages);
                    throw new Error("HTTP error " + response.status);
                });
            }
            // console.log(response.json()); // Response: pending ?
            return response.json();
        })
        .then(data => {
            alert("회원가입이 완료되었습니다!");
            window.location.href = "/login";
            console.log("Response: ", data);
        })
        .catch(error => {
            console.error("Error:", error);
        });
})