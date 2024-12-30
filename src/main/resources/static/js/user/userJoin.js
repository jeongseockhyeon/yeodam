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
                    showErrorMessages(errorMessages);
                    throw new Error("HTTP error " + response.status);
                });
            }

            alert("회원가입이 완료되었습니다!");
            window.location.href = "/login";
        })
        .catch(error => {
            console.error("Error:", error);
        });
})